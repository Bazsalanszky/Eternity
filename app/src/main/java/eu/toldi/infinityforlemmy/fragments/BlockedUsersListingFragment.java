package eu.toldi.infinityforlemmy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.FragmentCommunicator;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.BlockedThingListingActivity;
import eu.toldi.infinityforlemmy.adapters.BlockedUsersRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserViewModel;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import retrofit2.Retrofit;

public class BlockedUsersListingFragment extends Fragment implements FragmentCommunicator {
    public static final String EXTRA_ACCOUNT_NAME = "EAN";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";

    @BindView(R.id.swipe_refresh_layout_followed_users_listing_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_followed_users_listing_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_subscriptions_linear_layout_followed_users_listing_fragment)
    LinearLayout mLinearLayout;
    @BindView(R.id.no_subscriptions_image_view_followed_users_listing_fragment)
    ImageView mImageView;
    @BindView(R.id.error_text_view_followed_users_listing_fragment)
    TextView mErrorTextView;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    BlockedUserViewModel blockedUserViewModel;
    private BaseActivity mActivity;
    private RequestManager mGlide;
    private LinearLayoutManagerBugFixed mLinearLayoutManager;

    public BlockedUsersListingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_followed_users_listing, container, false);

        ButterKnife.bind(this, rootView);

        ((Infinity) mActivity.getApplication()).getAppComponent().inject(this);

        applyTheme();

        Resources resources = getResources();

        if ((mActivity instanceof BaseActivity && ((BaseActivity) mActivity).isImmersiveInterface())) {
            mRecyclerView.setPadding(0, 0, 0, ((BaseActivity) mActivity).getNavBarHeight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && mSharedPreferences.getBoolean(SharedPreferencesUtils.IMMERSIVE_INTERFACE_KEY, true)) {
            int navBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (navBarResourceId > 0) {
                mRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(navBarResourceId));
            }
        }

        mGlide = Glide.with(this);

        String accessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        if (accessToken == null) {
            mSwipeRefreshLayout.setEnabled(false);
        }
        mLinearLayoutManager = new LinearLayoutManagerBugFixed(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        BlockedUsersRecyclerViewAdapter adapter = new BlockedUsersRecyclerViewAdapter(mActivity,
                mExecutor, mOauthRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, accessToken);
        mRecyclerView.setAdapter(adapter);
        new FastScrollerBuilder(mRecyclerView).useMd2Style().build();

        blockedUserViewModel = new ViewModelProvider(this,
                new BlockedUserViewModel.Factory(mActivity.getApplication(), mRedditDataRoomDatabase, getArguments().getString(EXTRA_ACCOUNT_NAME)))
                .get(BlockedUserViewModel.class);

        blockedUserViewModel.getAllSubscribedUsers().observe(getViewLifecycleOwner(), subscribedUserData -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (subscribedUserData == null || subscribedUserData.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.VISIBLE);
                mGlide.load(R.mipmap.ic_launcher_round).into(mImageView);
            } else {
                mLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mGlide.clear(mImageView);
            }
            adapter.setSubscribedUsers(subscribedUserData);
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void stopRefreshProgressbar() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void applyTheme() {
        if (mActivity instanceof BlockedThingListingActivity) {
            mSwipeRefreshLayout.setOnRefreshListener(() -> ((BlockedThingListingActivity) mActivity).loadBlocks(true));
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
            mSwipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
        mErrorTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
        if (mActivity.typeface != null) {
            mErrorTextView.setTypeface(mActivity.typeface);
        }
    }

    public void goBackToTop() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public void changeSearchQuery(String searchQuery) {
        blockedUserViewModel.setSearchQuery(searchQuery);
    }
}
