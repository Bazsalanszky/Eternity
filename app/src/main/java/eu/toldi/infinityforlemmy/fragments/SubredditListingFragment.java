package eu.toldi.infinityforlemmy.fragments;


import android.content.Context;
import android.content.Intent;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.FragmentCommunicator;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.NetworkState;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RecyclerViewContentScrollingInterface;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.SearchSubredditsResultActivity;
import eu.toldi.infinityforlemmy.activities.ViewSubredditDetailActivity;
import eu.toldi.infinityforlemmy.adapters.SubredditListingRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditListingViewModel;
import eu.toldi.infinityforlemmy.subreddit.SubredditWithSelection;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubredditListingFragment extends Fragment implements FragmentCommunicator {

    public static final String EXTRA_QUERY = "EQ";
    public static final String EXTRA_IS_GETTING_SUBREDDIT_INFO = "EIGSI";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_ACCOUNT_NAME = "EAN";
    public static final String EXTRA_ACCOUNT_QUALIFIED_NAME = "EAQN";
    public static final String EXTRA_IS_MULTI_SELECTION = "EIMS";

    @BindView(R.id.coordinator_layout_subreddit_listing_fragment)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.recycler_view_subreddit_listing_fragment)
    RecyclerView mSubredditListingRecyclerView;
    @BindView(R.id.swipe_refresh_layout_subreddit_listing_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fetch_subreddit_listing_info_linear_layout_subreddit_listing_fragment)
    LinearLayout mFetchSubredditListingInfoLinearLayout;
    @BindView(R.id.fetch_subreddit_listing_info_image_view_subreddit_listing_fragment)
    ImageView mFetchSubredditListingInfoImageView;
    @BindView(R.id.fetch_subreddit_listing_info_text_view_subreddit_listing_fragment)
    TextView mFetchSubredditListingInfoTextView;
    SubredditListingViewModel mSubredditListingViewModel;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("sort_type")
    SharedPreferences mSortTypeSharedPreferences;
    @Inject
    @Named("nsfw_and_spoiler")
    SharedPreferences mNsfwAndSpoilerSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private LinearLayoutManagerBugFixed mLinearLayoutManager;
    private SubredditListingRecyclerViewAdapter mAdapter;
    private BaseActivity mActivity;
    private SortType sortType;

    public SubredditListingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_subreddit_listing, container, false);

        ((Infinity) mActivity.getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this, rootView);

        applyTheme();

        Resources resources = getResources();

        if ((mActivity != null && ((BaseActivity) mActivity).isImmersiveInterface())) {
            mSubredditListingRecyclerView.setPadding(0, 0, 0, ((BaseActivity) mActivity).getNavBarHeight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && mSharedPreferences.getBoolean(SharedPreferencesUtils.IMMERSIVE_INTERFACE_KEY, true)) {
            int navBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (navBarResourceId > 0) {
                mSubredditListingRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(navBarResourceId));
            }
        }

        mLinearLayoutManager = new LinearLayoutManagerBugFixed(getActivity());
        mSubredditListingRecyclerView.setLayoutManager(mLinearLayoutManager);

        String query = getArguments().getString(EXTRA_QUERY);
        boolean isGettingSubredditInfo = getArguments().getBoolean(EXTRA_IS_GETTING_SUBREDDIT_INFO);
        String accessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        String accountName = getArguments().getString(EXTRA_ACCOUNT_NAME);
        String accountQualifiedName = getArguments().getString(EXTRA_ACCOUNT_QUALIFIED_NAME);

        String sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_SEARCH_SUBREDDIT, SortType.Type.TOP_ALL.value);
        sortType = new SortType(SortType.Type.fromValue(sort));
        boolean nsfw = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.NSFW_BASE, false);

        mAdapter = new SubredditListingRecyclerViewAdapter(mActivity, mExecutor, mRetrofit.getRetrofit(), mRetrofit.getRetrofit(),
                mCustomThemeWrapper, accessToken, accountQualifiedName,
                mRedditDataRoomDatabase, getArguments().getBoolean(EXTRA_IS_MULTI_SELECTION, false),
                new SubredditListingRecyclerViewAdapter.Callback() {
                    @Override
                    public void retryLoadingMore() {
                        mSubredditListingViewModel.retryLoadingMore();
                    }

                    @Override
                    public void subredditSelected(SubredditData communityData) {
                        if (isGettingSubredditInfo) {
                            ((SearchSubredditsResultActivity) mActivity).getSelectedSubreddit(communityData);
                        } else {
                            Intent intent = new Intent(mActivity, ViewSubredditDetailActivity.class);
                            intent.putExtra(ViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, communityData.getName());
                            intent.putExtra(ViewSubredditDetailActivity.EXTRA_COMMUNITY_FULL_NAME_KEY, LemmyUtils.actorID2FullName(communityData.getActorId()));
                            mActivity.startActivity(intent);
                        }
                    }
                });

        mSubredditListingRecyclerView.setAdapter(mAdapter);

        if (mActivity instanceof RecyclerViewContentScrollingInterface) {
            mSubredditListingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        ((RecyclerViewContentScrollingInterface) mActivity).contentScrollDown();
                    } else if (dy < 0) {
                        ((RecyclerViewContentScrollingInterface) mActivity).contentScrollUp();
                    }
                }
            });
        }

        SubredditListingViewModel.Factory factory = new SubredditListingViewModel.Factory(
                mRetrofit.getRetrofit(), query, sortType, accessToken, nsfw);
        mSubredditListingViewModel = new ViewModelProvider(this, factory).get(SubredditListingViewModel.class);
        mSubredditListingViewModel.getSubreddits().observe(getViewLifecycleOwner(), subredditData -> {
            if (subredditData != null && !subredditData.isEmpty()) {
                mAdapter.submitList(subredditData);
            } else {
                mAdapter.submitList(null);
            }

        });

        mSubredditListingViewModel.hasSubredditLiveData().observe(getViewLifecycleOwner(), hasSubreddit -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (hasSubreddit) {
                mFetchSubredditListingInfoLinearLayout.setVisibility(View.GONE);
            } else {
                mFetchSubredditListingInfoLinearLayout.setOnClickListener(null);
                showErrorView(R.string.no_communities);
            }
        });

        mSubredditListingViewModel.getInitialLoadingState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState.getStatus().equals(NetworkState.Status.SUCCESS)) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (networkState.getStatus().equals(NetworkState.Status.FAILED)) {
                mSwipeRefreshLayout.setRefreshing(false);
                mFetchSubredditListingInfoLinearLayout.setOnClickListener(view -> refresh());
                showErrorView(R.string.search_communities_error);
            } else {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSubredditListingViewModel.getPaginationNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            mAdapter.setNetworkState(networkState);
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSubredditListingViewModel.refresh());

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    private void showErrorView(int stringResId) {
        if (getActivity() != null && isAdded()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mFetchSubredditListingInfoLinearLayout.setVisibility(View.VISIBLE);
            mFetchSubredditListingInfoTextView.setText(stringResId);
            Glide.with(this).load(R.drawable.error_image).into(mFetchSubredditListingInfoImageView);
        }
    }

    public void changeSortType(SortType sortType) {
        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_SEARCH_SUBREDDIT, sortType.getType().name()).apply();
        mSubredditListingViewModel.changeSortType(sortType);
        this.sortType = sortType;
    }

    @Override
    public void refresh() {
        mFetchSubredditListingInfoLinearLayout.setVisibility(View.GONE);
        mSubredditListingViewModel.refresh();
        mAdapter.setNetworkState(null);
    }

    @Override
    public void applyTheme() {
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        mSwipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        mFetchSubredditListingInfoTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
        if (mActivity.typeface != null) {
            mFetchSubredditListingInfoTextView.setTypeface(mActivity.contentTypeface);
        }
    }

    public void goBackToTop() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public SortType getSortType() {
        return sortType;
    }

    public ArrayList<SubredditWithSelection> getSelectedSubredditNames() {
        if (mSubredditListingViewModel != null) {
            List<SubredditData> allSubreddits = mSubredditListingViewModel.getSubreddits().getValue();
            if (allSubreddits == null) {
                return null;
            }

            ArrayList<SubredditWithSelection> selectedSubreddits = new ArrayList<>();
            for (SubredditData s : allSubreddits) {
                if (s.isSelected()) {
                    selectedSubreddits.add(new SubredditWithSelection(s.getName(), s.getIconUrl(), LemmyUtils.actorID2FullName(s.getActorId())));
                }
            }
            return selectedSubreddits;
        }

        return null;
    }
}
