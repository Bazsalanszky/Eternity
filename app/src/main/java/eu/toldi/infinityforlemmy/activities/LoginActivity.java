package eu.toldi.infinityforlemmy.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.InflateException;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.FetchMyInfo;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.asynctasks.ParseAndInsertNewAccount;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.dto.AccountLoginDTO;
import eu.toldi.infinityforlemmy.site.FetchSiteInfo;
import eu.toldi.infinityforlemmy.site.SiteInfo;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity {

    private static final String ENABLE_DOM_STATE = "EDS";
    private static final String IS_AGREE_TO_USER_AGGREMENT_STATE = "IATUAS";

    @BindView(R.id.coordinator_layout_login_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_login_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_login_activity)
    Toolbar toolbar;
    @BindView(R.id.two_fa_infO_text_view_login_activity)
    TextView twoFAInfoTextView;

    @BindView(R.id.instance_url_input)
    TextInputEditText instance_input;
    @BindView(R.id.username_input)
    TextInputEditText username_input;
    @BindView(R.id.user_password_input)
    TextInputEditText password_input;
    @BindView(R.id.user_2fa_token_input)
    TextInputEditText token_2fa_input;

    @BindView(R.id.user_login_button)
    Button loginButton;

    @BindView(R.id.login_progress)
    ProgressBar progressBar;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private String authCode;
    private boolean enableDom = false;
    private boolean isAgreeToUserAgreement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);
        } catch (InflateException ie) {
            Log.e("LoginActivity", "Failed to inflate LoginActivity: " + ie.getMessage());
            Toast.makeText(LoginActivity.this, R.string.no_system_webview_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ButterKnife.bind(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            enableDom = savedInstanceState.getBoolean(ENABLE_DOM_STATE);
            isAgreeToUserAgreement = savedInstanceState.getBoolean(IS_AGREE_TO_USER_AGGREMENT_STATE);
        }

        loginButton.setOnClickListener(view -> {
            Log.i("LoginActivity", "Login button clicked");
            if(!checkFields())
                return;
            loginButton.setEnabled(false);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            String username = username_input.getText().toString().trim();
            String instance = correctURL(instance_input.getText().toString().trim());
            if (!Patterns.WEB_URL.matcher(instance).matches()) {
                instance_input.setError("Invalid instance URL");
                Toast.makeText(LoginActivity.this, "Invalid instance URL", Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
                progressBar.setVisibility(ProgressBar.GONE);
                return;
            }
            Log.i("LoginActivity", "Instance: " + instance);
            AccountLoginDTO accountLoginDTO = new AccountLoginDTO(username, password_input.getText().toString(), token_2fa_input.getText().toString());
            mRetrofit.setBaseURL(instance);
            LemmyAPI api = mRetrofit.getRetrofit().create(LemmyAPI.class);
            Call<String> accessTokenCall = api.userLogin(accountLoginDTO);
            accessTokenCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    loginButton.setEnabled(true);


                    if (response.isSuccessful()) {
                        String accountResponse = response.body();
                        if (accountResponse == null) {
                            Log.e("LoginActivity", "Account response is null");
                            Toast.makeText(LoginActivity.this, R.string.invalid_response, Toast.LENGTH_SHORT).show();
                            //Handle error
                            return;
                        }
                        try {
                            JSONObject responseJSON = new JSONObject(accountResponse);
                            String accessToken = responseJSON.getString("jwt");

                            FetchMyInfo.fetchAccountInfo(mRetrofit.getRetrofit(), mRedditDataRoomDatabase, username,
                                    accessToken, new FetchMyInfo.FetchMyInfoListener() {
                                        @Override
                                        public void onFetchMyInfoSuccess(String name, String display_name, String profileImageUrl, String bannerImageUrl) {
                                            FetchSiteInfo.fetchSiteInfo(mRetrofit.getRetrofit(), accessToken, new FetchSiteInfo.FetchSiteInfoListener() {
                                                @Override
                                                public void onFetchSiteInfoSuccess(SiteInfo siteInfo) {
                                                    boolean canDownvote = siteInfo.isEnable_downvotes();
                                                    ParseAndInsertNewAccount.parseAndInsertNewAccount(mExecutor, new Handler(), name,display_name, accessToken,  profileImageUrl, bannerImageUrl, authCode,instance,canDownvote, mRedditDataRoomDatabase.accountDao(),
                                                            () -> {
                                                                Intent resultIntent = new Intent();
                                                                setResult(Activity.RESULT_OK, resultIntent);
                                                                finish();
                                                            });
                                                    mCurrentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, canDownvote).apply();
                                                }

                                                @Override
                                                public void onFetchSiteInfoFailed() {
                                                    ParseAndInsertNewAccount.parseAndInsertNewAccount(mExecutor, new Handler(), name,display_name, accessToken,  profileImageUrl, bannerImageUrl, authCode,instance,true, mRedditDataRoomDatabase.accountDao(),
                                                            () -> {
                                                                Intent resultIntent = new Intent();
                                                                setResult(Activity.RESULT_OK, resultIntent);
                                                                finish();
                                                            });
                                                    mCurrentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, true).apply();
                                                }
                                            });
                                            mCurrentAccountSharedPreferences.edit().putString(SharedPreferencesUtils.ACCESS_TOKEN, accessToken)
                                                    .putString(SharedPreferencesUtils.ACCOUNT_NAME, display_name)
                                                    .putString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, name)
                                                    .putString(SharedPreferencesUtils.ACCOUNT_INSTANCE,instance)
                                                    .putString(SharedPreferencesUtils.ACCOUNT_IMAGE_URL, profileImageUrl).apply();
                                        }

                                        @Override
                                        public void onFetchMyInfoFailed(boolean parseFailed) {
                                            if (parseFailed) {
                                                Toast.makeText(LoginActivity.this, R.string.parse_user_info_error, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                                            }

                                            finish();
                                        }
                                    });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        return;
                    }

                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("LoginActivity", "Error body: " + errorBody.trim());
                        JSONObject responseObject = new JSONObject(errorBody.trim());
                        if (responseObject.has("error")) {
                            if (responseObject.getString("error").equals("incorrect_login")) {
                                Toast.makeText(LoginActivity.this, R.string.invalid_username_or_password, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error:" + responseObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                    }

                    Log.e("LoginActivity", "Failed to get access token: " + response.code() + " " + response.message());

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private boolean checkFields() {
        boolean result = true;
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        String instance = instance_input.getText().toString();
        if(instance == null || instance.isEmpty()) {
            instance_input.setError(getString(R.string.instance_cannot_be_empty));
            result = false;
        }
        if(username == null || username.isEmpty()) {
            username_input.setError(getString(R.string.username_cannot_be_empty));
            result = false;
        }
        if(password == null || password.isEmpty()) {
            password_input.setError(getString(R.string.password_cannot_be_empty));
            result = false;
        }
        return result;
    }
    private static String correctURL(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return url;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ENABLE_DOM_STATE, enableDom);
        outState.putBoolean(IS_AGREE_TO_USER_AGGREMENT_STATE, isAgreeToUserAgreement);
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(appBarLayout, null, toolbar);
        twoFAInfoTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        Drawable infoDrawable = Utils.getTintedDrawable(this, R.drawable.ic_info_preference_24dp, mCustomThemeWrapper.getPrimaryIconColor());
        twoFAInfoTextView.setCompoundDrawablesWithIntrinsicBounds(infoDrawable, null, null, null);
        applyButtonTheme(loginButton);
        if (typeface != null) {
            twoFAInfoTextView.setTypeface(typeface);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
