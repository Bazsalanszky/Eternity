package eu.toldi.infinityforlemmy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.adapters.PrivateMessageRecycleViewAdapter;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.events.RepliedToPrivateMessageEvent;
import eu.toldi.infinityforlemmy.privatemessage.LemmyPrivateMessageAPI;
import eu.toldi.infinityforlemmy.privatemessage.PrivateMessage;
import eu.toldi.infinityforlemmy.privatemessage.PrivateMessageViewModel;

public class PrivateMessageFragment extends Fragment implements FragmentCommunicator {

    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_MESSAGE_WHERE = "EMT";
    @BindView(R.id.swipe_refresh_layout_inbox_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_inbox_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.fetch_messages_info_linear_layout_inbox_fragment)
    LinearLayout mFetchMessageInfoLinearLayout;
    @BindView(R.id.fetch_messages_info_image_view_inbox_fragment)
    ImageView mFetchMessageInfoImageView;
    @BindView(R.id.fetch_messages_info_text_view_inbox_fragment)
    TextView mFetchMessageInfoTextView;
    PrivateMessageViewModel mMessageViewModel;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;

    @Inject
    LemmyPrivateMessageAPI mLemmyPrivateMessageAPI;
    private String mAccessToken;
    private PrivateMessageRecycleViewAdapter mAdapter;
    private RequestManager mGlide;
    private LinearLayoutManagerBugFixed mLinearLayoutManager;
    private BaseActivity mActivity;

    public PrivateMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("PrivateMessageFragment", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        ((Infinity) mActivity.getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this, rootView);

        EventBus.getDefault().register(this);

        applyTheme();

        Bundle arguments = getArguments();
        if (arguments == null) {
            return rootView;
        }
        mAccessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        mGlide = Glide.with(this);

        if (mActivity.isImmersiveInterface()) {
            mRecyclerView.setPadding(0, 0, 0, mActivity.getNavBarHeight());
        }


        mAdapter = new PrivateMessageRecycleViewAdapter(mActivity, mRetrofit.getRetrofit(), mCustomThemeWrapper, mAccessToken, mLemmyPrivateMessageAPI, () -> mMessageViewModel.refresh());
        mLinearLayoutManager = new LinearLayoutManagerBugFixed(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if (mActivity instanceof RecyclerViewContentScrollingInterface) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        PrivateMessageViewModel.Factory factory = new PrivateMessageViewModel.Factory(mRetrofit.getRetrofit(),
                getResources().getConfiguration().locale, mAccessToken, mLemmyPrivateMessageAPI);
        mMessageViewModel = new ViewModelProvider(this, factory).get(PrivateMessageViewModel.class);
        mMessageViewModel.getPrivateMessages().observe(getViewLifecycleOwner(), messages -> mAdapter.submitList(messages));


        mMessageViewModel.getInitialLoadState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState.getStatus().equals(NetworkState.Status.SUCCESS)) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (networkState.getStatus().equals(NetworkState.Status.FAILED)) {
                mSwipeRefreshLayout.setRefreshing(false);
                mFetchMessageInfoLinearLayout.setOnClickListener(view -> {
                    mFetchMessageInfoLinearLayout.setVisibility(View.GONE);
                    mMessageViewModel.refresh();
                    mAdapter.setNetworkState(null);
                });
                showErrorView(R.string.load_messages_failed);
            } else {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        return rootView;
    }

    private void showErrorView(int stringResId) {
        mSwipeRefreshLayout.setRefreshing(false);
        mFetchMessageInfoLinearLayout.setVisibility(View.VISIBLE);
        mFetchMessageInfoTextView.setText(stringResId);
        mGlide.load(R.mipmap.ic_launcher_round).into(mFetchMessageInfoImageView);
    }

    @Override
    public void applyTheme() {
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        mSwipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        mFetchMessageInfoTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
        if (mActivity.typeface != null) {
            mFetchMessageInfoTextView.setTypeface(mActivity.typeface);
        }
    }

    public void goBackToTop() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public void markAllMessagesRead() {
        if (mAdapter != null) {
            mAdapter.setMarkAllMessagesAsRead(true);

            int previousPosition = -1;
            if (mLinearLayoutManager != null) {
                previousPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
            }

            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            mRecyclerView.setAdapter(null);
            mRecyclerView.setLayoutManager(null);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(layoutManager);

            if (previousPosition > 0) {
                mRecyclerView.scrollToPosition(previousPosition);
            }
        }
    }

    private void onRefresh() {
        mMessageViewModel.refresh();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public PrivateMessage getMessageByIndex(int index) {
        if (mMessageViewModel == null || index < 0) {
            return null;
        }
        PagedList<PrivateMessage> messages = mMessageViewModel.getPrivateMessages().getValue();
        if (messages == null) {
            return null;
        }
        if (index >= messages.size()) {
            return null;
        }

        return messages.get(index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Subscribe
    public void onRepliedToPrivateMessageEvent(RepliedToPrivateMessageEvent repliedToPrivateMessageEvent) {
       /* if (mAdapter != null && mWhere.equals(FetchMessage.WHERE_MESSAGES)) {
            mAdapter.updateMessageReply(repliedToPrivateMessageEvent.newReply, repliedToPrivateMessageEvent.messagePosition);
        }*/
    }
}
