package eu.toldi.infinityforlemmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.ActivityToolbarInterface;
import eu.toldi.infinityforlemmy.FragmentCommunicator;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.account.FetchBlockedThings;
import eu.toldi.infinityforlemmy.asynctasks.InsertBlockedThings;
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityData;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserData;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.ViewPagerBugFixed;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.events.GoBackToMainPageEvent;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.fragments.BlockedCommunitiesListingFragment;
import eu.toldi.infinityforlemmy.fragments.BlockedUsersListingFragment;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import retrofit2.Retrofit;


public class BlockedThingListingActivity extends BaseActivity implements ActivityToolbarInterface {

    public static final String EXTRA_SHOW_MULTIREDDITS = "ESM";
    private static final String INSERT_SUBSCRIBED_SUBREDDIT_STATE = "ISSS";
    private static final String INSERT_MULTIREDDIT_STATE = "IMS";

    @BindView(R.id.coordinator_layout_subscribed_thing_listing_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_subscribed_thing_listing_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout_subscribed_thing_listing_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_subscribed_thing_listing_activity)
    Toolbar toolbar;
    @BindView(R.id.search_edit_text_subscribed_thing_listing_activity)
    EditText searchEditText;
    @BindView(R.id.tab_layout_subscribed_thing_listing_activity)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_subscribed_thing_listing_activity)
    ViewPagerBugFixed viewPager;
    @BindView(R.id.fab_subscribed_thing_listing_activity)
    FloatingActionButton fab;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
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
    private String mAccessToken;
    private String mAccountName;

    private String mAccountQualifiedName;
    private boolean mInsertSuccess = false;
    private boolean mInsertMultiredditSuccess = false;
    private boolean showMultiReddits = false;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscribed_thing_listing);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            mSliderPanel = Slidr.attach(this);
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
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    params.bottomMargin += navBarHeight;
                    fab.setLayoutParams(params);
                }
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarGoToTop(toolbar);

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, "-");
        mAccountQualifiedName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, "-");

        if (savedInstanceState != null) {
            mInsertSuccess = savedInstanceState.getBoolean(INSERT_SUBSCRIBED_SUBREDDIT_STATE);
            mInsertMultiredditSuccess = savedInstanceState.getBoolean(INSERT_MULTIREDDIT_STATE);
        } else {
            showMultiReddits = getIntent().getBooleanExtra(EXTRA_SHOW_MULTIREDDITS, false);
        }

        if (mAccessToken == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchEditText.setImeOptions(searchEditText.getImeOptions() | EditorInfoCompat.IME_FLAG_NO_PERSONALIZED_LEARNING);
        }

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sectionsPagerAdapter.changeSearchQuery(editable.toString());
            }
        });
        initializeViewPagerAndLoadSubscriptions();
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
        applyTabLayoutTheme(tabLayout);
        applyFABTheme(fab, mSharedPreferences.getBoolean(SharedPreferencesUtils.USE_CIRCULAR_FAB, false));
        searchEditText.setTextColor(mCustomThemeWrapper.getToolbarPrimaryTextAndIconColor());
        searchEditText.setHintTextColor(mCustomThemeWrapper.getToolbarSecondaryTextColor());
    }

    private void initializeViewPagerAndLoadSubscriptions() {
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateMultiRedditActivity.class);
            startActivity(intent);
        });
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        if (viewPager.getCurrentItem() != 2) {
            fab.hide();
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    unlockSwipeRightToGoBack();
                    fab.hide();
                } else {
                    lockSwipeRightToGoBack();
                    if (position != 2) {
                        fab.hide();
                    } else {
                        fab.show();
                    }
                }
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        if (showMultiReddits) {
            viewPager.setCurrentItem(2, false);
        }

        loadBlocks(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscribed_thing_listing_activity, menu);
        mMenu = menu;
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search_subscribed_thing_listing_activity) {
            item.setVisible(false);
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
            if (searchEditText.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (searchEditText.getVisibility() == View.VISIBLE) {
                Utils.hideKeyboard(this);
                searchEditText.setVisibility(View.GONE);
                searchEditText.setText("");
                mMenu.findItem(R.id.action_search_subscribed_thing_listing_activity).setVisible(true);
                sectionsPagerAdapter.changeSearchQuery("");
                return true;
            }
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (searchEditText.getVisibility() == View.VISIBLE) {
            Utils.hideKeyboard(this);
            searchEditText.setVisibility(View.GONE);
            searchEditText.setText("");
            mMenu.findItem(R.id.action_search_subscribed_thing_listing_activity).setVisible(true);
            sectionsPagerAdapter.changeSearchQuery("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INSERT_SUBSCRIBED_SUBREDDIT_STATE, mInsertSuccess);
        outState.putBoolean(INSERT_MULTIREDDIT_STATE, mInsertMultiredditSuccess);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void loadBlocks(boolean forceLoad) {
        if (mAccessToken != null && !(!forceLoad && mInsertSuccess)) {
            FetchBlockedThings.fetchBlockedThings(mRetrofit.getRetrofit(), mAccessToken, mAccountQualifiedName, new FetchBlockedThings.FetchBlockedThingsListener() {
                @Override
                public void onFetchBlockedThingsSuccess(List<BlockedUserData> blockedUsers, List<BlockedCommunityData> blockedCommunities) {
                    InsertBlockedThings.insertBlockedThings(mExecutor, new Handler(), mRedditDataRoomDatabase, mAccountQualifiedName,
                            blockedCommunities, blockedUsers, () -> {
                                mInsertSuccess = true;
                                sectionsPagerAdapter.stopRefreshProgressbar();
                            });
                }

                @Override
                public void onFetchBlockedThingsFailure() {

                }
            });
        }
    }


    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }

    @Subscribe
    public void goBackToMainPageEvent(GoBackToMainPageEvent event) {
        finish();
    }

    @Override
    public void onLongPress() {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.goBackToTop();
        }
    }

    @Override
    public void lockSwipeRightToGoBack() {
        if (mSliderPanel != null) {
            mSliderPanel.lock();
        }
    }

    @Override
    public void unlockSwipeRightToGoBack() {
        if (mSliderPanel != null) {
            mSliderPanel.unlock();
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private BlockedCommunitiesListingFragment blockedCommunitiesListingFragment;
        private BlockedUsersListingFragment followedUsersListingFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0: {
                    BlockedCommunitiesListingFragment fragment = new BlockedCommunitiesListingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BlockedCommunitiesListingFragment.EXTRA_IS_SUBREDDIT_SELECTION, false);
                    bundle.putString(BlockedCommunitiesListingFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                    bundle.putString(BlockedCommunitiesListingFragment.EXTRA_ACCOUNT_QUALIFIED_NAME, mAccountQualifiedName);
                    bundle.putString(BlockedCommunitiesListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    fragment.setArguments(bundle);
                    return fragment;
                }
                case 1: {
                    BlockedUsersListingFragment fragment = new BlockedUsersListingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BlockedUsersListingFragment.EXTRA_ACCOUNT_NAME, mAccountQualifiedName);
                    bundle.putString(BlockedUsersListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    fragment.setArguments(bundle);
                    return fragment;
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return Utils.getTabTextWithCustomFont(typeface, getString(R.string.communities));
                case 1:
                    return Utils.getTabTextWithCustomFont(typeface, getString(R.string.users));
                case 2:
                    return Utils.getTabTextWithCustomFont(typeface, getString(R.string.multi_reddits));
            }

            return null;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if (position == 0) {
                blockedCommunitiesListingFragment = (BlockedCommunitiesListingFragment) fragment;
            } else if (position == 1) {
                followedUsersListingFragment = (BlockedUsersListingFragment) fragment;
            }

            return fragment;
        }

        void stopRefreshProgressbar() {
            if (blockedCommunitiesListingFragment != null) {
                ((FragmentCommunicator) blockedCommunitiesListingFragment).stopRefreshProgressbar();
            }
            if (followedUsersListingFragment != null) {
                ((FragmentCommunicator) followedUsersListingFragment).stopRefreshProgressbar();
            }
        }


        void goBackToTop() {
            if (viewPager.getCurrentItem() == 0) {
                blockedCommunitiesListingFragment.goBackToTop();
            } else if (viewPager.getCurrentItem() == 1) {
                followedUsersListingFragment.goBackToTop();
            }
        }

        void changeSearchQuery(String searchQuery) {
            if (blockedCommunitiesListingFragment != null) {
                blockedCommunitiesListingFragment.changeSearchQuery(searchQuery);
            }
            if (followedUsersListingFragment != null) {
                followedUsersListingFragment.changeSearchQuery(searchQuery);
            }
        }
    }
}

