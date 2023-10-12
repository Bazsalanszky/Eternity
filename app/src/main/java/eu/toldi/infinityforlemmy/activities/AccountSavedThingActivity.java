package eu.toldi.infinityforlemmy.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import eu.toldi.infinityforlemmy.ActivityToolbarInterface;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.MarkPostAsReadInterface;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.bottomsheetfragments.PostLayoutBottomSheetFragment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.databinding.ActivityAccountSavedThingBinding;
import eu.toldi.infinityforlemmy.events.ChangeNSFWEvent;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.fragments.CommentsListingFragment;
import eu.toldi.infinityforlemmy.fragments.PostFragment;
import eu.toldi.infinityforlemmy.post.MarkPostAsRead;
import eu.toldi.infinityforlemmy.post.Post;
import eu.toldi.infinityforlemmy.post.PostPagingSource;
import eu.toldi.infinityforlemmy.readpost.InsertReadPost;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;

public class AccountSavedThingActivity extends BaseActivity implements ActivityToolbarInterface,
        PostLayoutBottomSheetFragment.PostLayoutSelectionCallback, MarkPostAsReadInterface {

    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("post_layout")
    SharedPreferences mPostLayoutSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;

    @Inject
    MarkPostAsRead markPostAsRead;
    private FragmentManager fragmentManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private String mAccessToken;
    private String mAccountName;

    private String mAccountQualifiedName;
    private PostLayoutBottomSheetFragment postLayoutBottomSheetFragment;
    private ActivityAccountSavedThingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        binding = ActivityAccountSavedThingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            mSliderPanel = Slidr.attach(this);
        }

        mViewPager2 = binding.accountSavedThingViewPager2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();

            if (isChangeStatusBarIconColor()) {
                addOnOffsetChangedListener(binding.accountSavedThingAppbarLayout);
            }

            if (isImmersiveInterface()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.setDecorFitsSystemWindows(false);
                } else {
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
                adjustToolbar(binding.accountSavedThingToolbar);
            }
        }

        setSupportActionBar(binding.accountSavedThingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarGoToTop(binding.accountSavedThingToolbar);

        postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();

        fragmentManager = getSupportFragmentManager();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);
        mAccountQualifiedName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, null);

        initializeViewPager();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (sectionsPagerAdapter != null) {
            return sectionsPagerAdapter.handleKeyDown(keyCode) || super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
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
        binding.accountSavedThingCoordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(
                binding.accountSavedThingAppbarLayout,
                binding.accountSavedThingCollapsingToolbarLayout,
                binding.accountSavedThingToolbar);
        applyTabLayoutTheme(binding.accountSavedThingTabLayout);
    }

    private void initializeViewPager() {
        sectionsPagerAdapter = new SectionsPagerAdapter(this);
        binding.accountSavedThingViewPager2.setAdapter(sectionsPagerAdapter);
        binding.accountSavedThingViewPager2.setOffscreenPageLimit(2);
        binding.accountSavedThingViewPager2.setUserInputEnabled(!mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_SWIPING_BETWEEN_TABS, false));
        new TabLayoutMediator(binding.accountSavedThingTabLayout, binding.accountSavedThingViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    Utils.setTitleWithCustomFontToTab(typeface, tab, getString(R.string.posts));
                    break;
                case 1:
                    Utils.setTitleWithCustomFontToTab(typeface, tab, getString(R.string.comments));
                    break;
            }
        }).attach();

        binding.accountSavedThingViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    unlockSwipeRightToGoBack();
                } else {
                    lockSwipeRightToGoBack();
                }
            }
        });

        fixViewPager2Sensitivity(binding.accountSavedThingViewPager2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_saved_thing_activity, menu);
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_refresh_account_saved_thing_activity) {
            sectionsPagerAdapter.refresh();
            return true;
        } else if (itemId == R.id.action_change_post_layout_account_saved_thing_activity) {
            postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
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

    @Subscribe
    public void onChangeNSFWEvent(ChangeNSFWEvent changeNSFWEvent) {
        sectionsPagerAdapter.changeNSFW(changeNSFWEvent.nsfw);
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

    @Override
    public void postLayoutSelected(int postLayout) {
        if (sectionsPagerAdapter != null) {
            mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + mAccountName, postLayout).apply();
            sectionsPagerAdapter.changePostLayout(postLayout);
        }
    }

    @Override
    public void markPostAsRead(Post post) {
        markPostAsRead.markPostAsRead(post.getId(), mAccessToken, new MarkPostAsRead.MarkPostAsReadListener() {
            @Override
            public void onMarkPostAsReadSuccess() {
                InsertReadPost.insertReadPost(mRedditDataRoomDatabase, mExecutor, mAccountQualifiedName, post.getId());
            }

            @Override
            public void onMarkPostAsReadFailed() {
                Toast.makeText(AccountSavedThingActivity.this, R.string.mark_post_as_read_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentStateAdapter {

        SectionsPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                PostFragment fragment = new PostFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(PostFragment.EXTRA_POST_TYPE, PostPagingSource.TYPE_USER);
                bundle.putString(PostFragment.EXTRA_USER_NAME, mAccountQualifiedName);
                bundle.putString(PostFragment.EXTRA_USER_WHERE, PostPagingSource.USER_WHERE_SAVED);
                bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                bundle.putBoolean(PostFragment.EXTRA_DISABLE_READ_POSTS, true);
                fragment.setArguments(bundle);
                return fragment;
            }
            CommentsListingFragment fragment = new CommentsListingFragment();
            Bundle bundle = new Bundle();
            bundle.putString(CommentsListingFragment.EXTRA_USERNAME, mAccountQualifiedName);
            bundle.putString(CommentsListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
            bundle.putString(CommentsListingFragment.EXTRA_ACCOUNT_NAME, mAccountName);
            bundle.putBoolean(CommentsListingFragment.EXTRA_ARE_SAVED_COMMENTS, true);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Nullable
        private Fragment getCurrentFragment() {
            if (fragmentManager == null) {
                return null;
            }
            return fragmentManager.findFragmentByTag("f" + binding.accountSavedThingViewPager2.getCurrentItem());
        }

        public boolean handleKeyDown(int keyCode) {
            if (binding.accountSavedThingViewPager2.getCurrentItem() == 0) {
                Fragment fragment = getCurrentFragment();
                if (fragment instanceof PostFragment) {
                    return ((PostFragment) fragment).handleKeyDown(keyCode);
                }
            }
            return false;
        }

        public void refresh() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).refresh();
            } else if (fragment instanceof CommentsListingFragment) {
                ((CommentsListingFragment) fragment).refresh();
            }
        }

        public void changeNSFW(boolean nsfw) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changeNSFW(nsfw);
            }
        }

        public void changePostLayout(int postLayout) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changePostLayout(postLayout);
            }
        }


        public void goBackToTop() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).goBackToTop();
            } else if (fragment instanceof CommentsListingFragment) {
                ((CommentsListingFragment) fragment).goBackToTop();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
