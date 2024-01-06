package eu.toldi.infinityforlemmy.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.InflateException;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.adapters.CustomArrayAdapter;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.asynctasks.ParseAndInsertNewAccount;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.dto.AccountLoginDTO;
import eu.toldi.infinityforlemmy.lemmyverse.FetchInstancesListener;
import eu.toldi.infinityforlemmy.lemmyverse.LemmyInstance;
import eu.toldi.infinityforlemmy.lemmyverse.LemmyVerseFetchInstances;
import eu.toldi.infinityforlemmy.site.FetchSiteInfo;
import eu.toldi.infinityforlemmy.site.SiteInfo;
import eu.toldi.infinityforlemmy.user.MyUserInfo;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity {

    private static final String ENABLE_DOM_STATE = "EDS";
    private static final String IS_AGREE_TO_USER_AGGREMENT_STATE = "IATUAS";

    public static final String EXTRA_INPUT_USERNAME = "INPUT_USERNAME";
    public static final String EXTRA_INPUT_INSTANCE = "INPUT_INSTANCE";

    @BindView(R.id.coordinator_layout_login_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_login_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_login_activity)
    Toolbar toolbar;
    @BindView(R.id.two_fa_infO_text_view_login_activity)
    TextView twoFAInfoTextView;

    @BindView(R.id.instance_url_input)
    AppCompatAutoCompleteTextView instance_input;
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
    @Named("lemmyVerse")
    Retrofit mLemmyVerseRetrofit;

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

        // Get username and instance from intent
        Intent intent = getIntent();
        String username_intent = intent.getStringExtra(EXTRA_INPUT_USERNAME);
        String instance_intent = intent.getStringExtra(EXTRA_INPUT_INSTANCE);
        if (username_intent != null) {
            username_input.setText(username_intent);
        }
        if (instance_intent != null) {
            instance_input.setText(instance_intent);
        }

        LemmyVerseFetchInstances.INSTANCE.fetchInstances(mLemmyVerseRetrofit, new FetchInstancesListener() {

            @Override
            public void onFetchInstancesSuccess(@NonNull List<LemmyInstance> instances) {
                ArrayList<String> instanceNames = new ArrayList<>();
                for (LemmyInstance instance : instances) {
                    instanceNames.add(instance.getFqdn());
                }
                ArrayAdapter<String> adapter = new CustomArrayAdapter(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, instanceNames, mCustomThemeWrapper);
                instance_input.setAdapter(adapter);
            }
        });


        loginButton.setOnClickListener(view -> {
            Log.i("LoginActivity", "Login button clicked");
            if (!checkFields())
                return;
            loginButton.setEnabled(false);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            String username = username_input.getText().toString().trim();
            String instance = correctURL(instance_input.getText().toString().trim());
            try {
                URL urlObj = new URL(instance);
                instance = urlObj.getProtocol() + "://" + urlObj.getHost() + "/";
            } catch (MalformedURLException e) {
                instance_input.setError("Invalid URL");
                Toast.makeText(LoginActivity.this, "Invalid instance URL", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("LoginActivity", "Instance: " + instance);
            AccountLoginDTO accountLoginDTO = new AccountLoginDTO(username, password_input.getText().toString(), token_2fa_input.getText().toString());
            mRetrofit.setBaseURL(instance);
            LemmyAPI api = mRetrofit.getRetrofit().create(LemmyAPI.class);
            Call<String> accessTokenCall = api.userLogin(accountLoginDTO);
            String finalInstance = instance;
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
                            mRetrofit.setAccessToken(accessToken);

                            FetchSiteInfo.fetchSiteInfo(mRetrofit.getRetrofit(), accessToken, new FetchSiteInfo.FetchSiteInfoListener() {
                                @Override
                                public void onFetchSiteInfoSuccess(SiteInfo siteInfo, MyUserInfo myUserInfo) {
                                    if (myUserInfo == null) {
                                        finish();
                                        Toast.makeText(LoginActivity.this, R.string.parse_user_info_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    boolean canDownvote = siteInfo.isEnable_downvotes();
                                    ParseAndInsertNewAccount.parseAndInsertNewAccount(mExecutor, new Handler(), myUserInfo.getQualifiedName(), myUserInfo.getDisplayName(), accessToken, myUserInfo.getProfileImageUrl(), myUserInfo.getBannerImageUrl(), authCode, finalInstance, canDownvote, mRedditDataRoomDatabase.accountDao(),
                                            () -> {
                                                Intent resultIntent = new Intent();
                                                setResult(Activity.RESULT_OK, resultIntent);
                                                finish();
                                            });
                                    mCurrentAccountSharedPreferences.edit()
                                            .putString(SharedPreferencesUtils.ACCESS_TOKEN, accessToken)
                                            .putString(SharedPreferencesUtils.ACCOUNT_NAME, myUserInfo.getDisplayName())
                                            .putString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, myUserInfo.getQualifiedName())
                                            .putString(SharedPreferencesUtils.ACCOUNT_INSTANCE,finalInstance)
                                            .putString(SharedPreferencesUtils.ACCOUNT_IMAGE_URL, myUserInfo.getProfileImageUrl())
                                            .putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, canDownvote).apply();
                                    String[] version = siteInfo.getVersion().split("\\.");
                                    if (version.length > 0) {
                                        Log.d("SwitchAccount", "Lemmy Version: " + version[0] + "." + version[1]);
                                        int majorVersion = Integer.parseInt(version[0]);
                                        int minorVersion = Integer.parseInt(version[1]);
                                        if (majorVersion > 0 || (majorVersion == 0 && minorVersion >= 19)) {
                                            mRetrofit.setAccessToken(accessToken);
                                            mCurrentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.BEARER_TOKEN_AUTH, true).apply();
                                        } else {
                                            mRetrofit.setAccessToken(null);
                                            mCurrentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.BEARER_TOKEN_AUTH, false).apply();
                                        }
                                    }
                                }

                                @Override
                                public void onFetchSiteInfoFailed(boolean parseFailed) {
                                    if (parseFailed) {
                                        finish();
                                        Toast.makeText(LoginActivity.this, R.string.parse_user_info_error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(ProgressBar.GONE);
                                        loginButton.setEnabled(true);
                                        Toast.makeText(LoginActivity.this, R.string.cannot_fetch_user_info, Toast.LENGTH_SHORT).show();
                                    }
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
        Editable username = username_input.getText();
        Editable password = password_input.getText();
        Editable instance = instance_input.getText();
        if(instance == null || instance.toString().isEmpty()) {
            instance_input.setError(getString(R.string.instance_cannot_be_empty));
            result = false;
        }
        if(username == null || username.toString().isEmpty()) {
            username_input.setError(getString(R.string.username_cannot_be_empty));
            result = false;
        }
        if(password == null || password.toString().isEmpty()) {
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
        instance_input.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
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
