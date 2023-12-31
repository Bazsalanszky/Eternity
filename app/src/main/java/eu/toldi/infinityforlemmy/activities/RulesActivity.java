package eu.toldi.infinityforlemmy.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.Rule;
import eu.toldi.infinityforlemmy.adapters.RulesRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.customviews.slidr.widget.SliderPanel;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.subreddit.FetchSubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import retrofit2.Retrofit;

public class RulesActivity extends BaseActivity {

    static final String EXTRA_SUBREDDIT_NAME = "ESN";

    @BindView(R.id.coordinator_layout_rules_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_rules_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout_rules_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_rules_activity)
    Toolbar toolbar;
    @BindView(R.id.progress_bar_rules_activity)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view_rules_activity)
    RecyclerView recyclerView;
    @BindView(R.id.error_text_view_rules_activity)
    TextView errorTextView;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;

    private String mAccessToken;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;

    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private String mSubredditName;
    private RulesRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rules);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        SliderPanel sliderPanel = null;
        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            sliderPanel = Slidr.attach(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();

            if (isChangeStatusBarIconColor()) {
                addOnOffsetChangedListener(appBarLayout);
            }

            if (isImmersiveInterface()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false);
                } else {
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
                adjustToolbar(toolbar);

                int navBarHeight = getNavBarHeight();
                if (navBarHeight > 0) {
                    recyclerView.setPadding(0, 0, 0, navBarHeight);
                }
            }
        }
        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);

        appBarLayout.setBackgroundColor(mCustomThemeWrapper.getColorPrimary());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubredditName = getIntent().getExtras().getString(EXTRA_SUBREDDIT_NAME);

        mAdapter = new RulesRecyclerViewAdapter(this, mCustomThemeWrapper, sliderPanel, mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW, false));
        recyclerView.setAdapter(mAdapter);
        FetchSubredditData.fetchSubredditData(mRetrofit.getRetrofit(), mSubredditName, mAccessToken, new FetchSubredditData.FetchSubredditDataListener() {
            @Override
            public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                progressBar.setVisibility(View.GONE);
                String description = subredditData.getDescription();
                if (description == null || description.isEmpty()) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(R.string.no_rule);
                    errorTextView.setOnClickListener(view -> {
                    });
                }
                ArrayList<Rule> rules = new ArrayList<>();
                rules.add(new Rule("Rules", description));
                mAdapter.changeDataset(rules);
            }

            @Override
            public void onFetchSubredditDataFail(boolean isQuarantined) {
                displayError();
            }
        });
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
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(appBarLayout, collapsingToolbarLayout, toolbar);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(mCustomThemeWrapper.getColorAccent()));
        errorTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
        if (typeface != null) {
            errorTextView.setTypeface(typeface);
        }
    }

    private void displayError() {
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(R.string.error_loading_rules);
        errorTextView.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.GONE);
            FetchSubredditData.fetchSubredditData(mRetrofit.getRetrofit(), mSubredditName, mAccessToken, new FetchSubredditData.FetchSubredditDataListener() {
                @Override
                public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                    progressBar.setVisibility(View.GONE);
                    String description = subredditData.getDescription();
                    if (description == null || description.isEmpty()) {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText(R.string.no_rule);
                        errorTextView.setOnClickListener(view -> {
                        });
                    }
                    ArrayList<Rule> rules = new ArrayList<>();
                    rules.add(new Rule("Rules", description));
                    mAdapter.changeDataset(rules);
                }

                @Override
                public void onFetchSubredditDataFail(boolean isQuarantined) {
                    displayError();
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }
}
