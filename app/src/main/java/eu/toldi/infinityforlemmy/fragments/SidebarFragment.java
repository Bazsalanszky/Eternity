package eu.toldi.infinityforlemmy.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.evernote.android.state.State;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.ViewSubredditDetailActivity;
import eu.toldi.infinityforlemmy.adapters.ModeratorRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.asynctasks.InsertSubredditData;
import eu.toldi.infinityforlemmy.bottomsheetfragments.CopyTextBottomSheetFragment;
import eu.toldi.infinityforlemmy.bottomsheetfragments.UrlMenuBottomSheetFragment;
import eu.toldi.infinityforlemmy.community.CommunityStats;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.markdown.MarkdownUtils;
import eu.toldi.infinityforlemmy.subreddit.FetchSubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditViewModel;
import eu.toldi.infinityforlemmy.user.BasicUserRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.recycler.MarkwonAdapter;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import retrofit2.Retrofit;

public class SidebarFragment extends Fragment {

    public static final String EXTRA_SUBREDDIT_NAME = "ESN";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_COMMUNITY_QUALIFIED_NAME = "ECQN";

    public static final String EXTRA_SHOW_STATISTICS = "ESS";
    public static final String EXTRA_DISABLE_IMAGE_PREVIEW = "EDIP";
    public SubredditViewModel mSubredditViewModel;
    @BindView(R.id.swipe_refresh_layout_sidebar_fragment)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.markdown_recycler_view_sidebar_fragment)
    RecyclerView recyclerView;

    @BindView(R.id.recycler_view_moderators_side_fragment)
    RecyclerView moderatorsRecyclerView;

    @BindView(R.id.subscriber_count_text_view_sidebar_fragment)
    TextView nSubscribersTextView;
    @BindView(R.id.active_user_count_text_view_sidebar_fragment)
    TextView nActiveUsersTextView;
    @BindView(R.id.post_count_text_view_sidebar_fragment)
    TextView nPostsTextView;
    @BindView(R.id.comment_count_text_view_sidebar_fragment)
    TextView nCommentsTextView;

    @BindView(R.id.subscriber_count_image_view_sidebar_fragment)
    ImageView nSubscribersImageView;
    @BindView(R.id.active_user_count_image_view_sidebar_fragment)
    ImageView nActiveUsersImageView;
    @BindView(R.id.post_count_image_view_sidebar_fragment)
    ImageView nPostsImageView;
    @BindView(R.id.comment_count_image_view_sidebar_fragment)
    ImageView nCommentsImageView;

    @BindView(R.id.community_statistics_block_sidebar_fragment)
    ConstraintLayout communityStatisticsBlock;

    @BindView(R.id.moderators_text_view_sidebar_fragment)
    TextView moderatorsTextView;

    @BindView(R.id.moderators_card_sidebar_fragment)
    MaterialCardView moderatorsCard;

    @BindView(R.id.description_card_sidebar_fragment)
    MaterialCardView descriptionCard;

    @BindView(R.id.statistics_card_sidebar_fragment)
    MaterialCardView statisticsCard;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private ViewSubredditDetailActivity activity;
    private String mAccessToken;
    private String subredditName;

    private boolean mShowStatistics;

    private boolean mDisableImagePreview;

    private String communityQualifiedName;

    private boolean isKbinMagazine = false;
    private LinearLayoutManagerBugFixed linearLayoutManager;
    private int markdownColor;
    private String sidebarDescription;

    @State
    CommunityStats mCommunityStats;
    private BasicUserRecyclerViewAdapter moderatorAdapter;
    private Markwon markwon;
    private MarkwonAdapter markwonAdapter;

    public SidebarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sidebar, container, false);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this, rootView);

        mAccessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        subredditName = getArguments().getString(EXTRA_SUBREDDIT_NAME);
        communityQualifiedName = getArguments().getString(EXTRA_COMMUNITY_QUALIFIED_NAME);
        mShowStatistics = getArguments().getBoolean(EXTRA_SHOW_STATISTICS, true);
        mDisableImagePreview = getArguments().getBoolean(EXTRA_DISABLE_IMAGE_PREVIEW, false);
        if (communityQualifiedName == null) {
            Toast.makeText(activity, R.string.error_getting_community_name, Toast.LENGTH_SHORT).show();
            return rootView;
        }

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        swipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        int mCardViewBackgroundColor = mCustomThemeWrapper.getCardViewBackgroundColor();
        int primaryTextColor = mCustomThemeWrapper.getPrimaryTextColor();
        nSubscribersTextView.setTextColor(primaryTextColor);
        nActiveUsersTextView.setTextColor(primaryTextColor);
        nPostsTextView.setTextColor(primaryTextColor);
        nCommentsTextView.setTextColor(primaryTextColor);
        moderatorsTextView.setTextColor(primaryTextColor);
        moderatorsTextView.setTypeface(activity.contentTypeface);
        nSubscribersImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        nActiveUsersImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        nPostsImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        nCommentsImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        moderatorsCard.setCardBackgroundColor(mCardViewBackgroundColor);
        descriptionCard.setCardBackgroundColor(mCardViewBackgroundColor);
        if (mShowStatistics) {
            statisticsCard.setCardBackgroundColor(mCardViewBackgroundColor);
        } else {
            statisticsCard.setVisibility(View.GONE);
        }


        markdownColor = mCustomThemeWrapper.getPrimaryTextColor();
        int spoilerBackgroundColor = markdownColor | 0xFF000000;

        MarkwonPlugin miscPlugin = new AbstractMarkwonPlugin() {
            @Override
            public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                if (activity.contentTypeface != null) {
                    textView.setTypeface(activity.contentTypeface);
                }
                textView.setTextColor(markdownColor);
                textView.setOnLongClickListener(view -> {
                    if (sidebarDescription != null && !sidebarDescription.equals("") && textView.getSelectionStart() == -1 && textView.getSelectionEnd() == -1) {
                        Bundle bundle = new Bundle();
                        bundle.putString(CopyTextBottomSheetFragment.EXTRA_MARKDOWN, sidebarDescription);
                        CopyTextBottomSheetFragment copyTextBottomSheetFragment = new CopyTextBottomSheetFragment();
                        copyTextBottomSheetFragment.setArguments(bundle);
                        copyTextBottomSheetFragment.show(getChildFragmentManager(), copyTextBottomSheetFragment.getTag());
                    }
                    return true;
                });
            }

            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder.linkColor(mCustomThemeWrapper.getLinkColor());
            }

            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> {
                    Intent intent = new Intent(activity, LinkResolverActivity.class);
                    Uri uri = Uri.parse(link);
                    intent.setData(uri);
                    startActivity(intent);
                });
            }
        };
        BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener = (textView, url) -> {
            UrlMenuBottomSheetFragment urlMenuBottomSheetFragment = UrlMenuBottomSheetFragment.newInstance(url);
            urlMenuBottomSheetFragment.show(getChildFragmentManager(), null);
            return true;
        };
        markwon = MarkdownUtils.createFullRedditMarkwon(activity,
                miscPlugin, markdownColor, spoilerBackgroundColor, Glide.with(activity.getApplication()), onLinkLongClickListener, mDisableImagePreview);
        markwonAdapter = MarkdownUtils.createTablesAdapter();

        linearLayoutManager = new LinearLayoutManagerBugFixed(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(markwonAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((ViewSubredditDetailActivity) activity).contentScrollDown();
                } else if (dy < 0) {
                    ((ViewSubredditDetailActivity) activity).contentScrollUp();
                }

            }
        });

        moderatorsRecyclerView.setLayoutManager(new LinearLayoutManagerBugFixed(activity));
        moderatorAdapter = new ModeratorRecyclerViewAdapter(activity,
                mCustomThemeWrapper);
        moderatorsRecyclerView.setAdapter(moderatorAdapter);

        mSubredditViewModel = new ViewModelProvider(activity,
                new SubredditViewModel.Factory(activity.getApplication(), mRedditDataRoomDatabase, (!isKbinMagazine) ? LemmyUtils.qualifiedCommunityName2ActorId(communityQualifiedName) :
                        LemmyUtils.qualifiedMagazineName2ActorId(communityQualifiedName)))
                .get(SubredditViewModel.class);
        addLiveDataObserver();

        swipeRefreshLayout.setOnRefreshListener(this::fetchSubredditData);

        return rootView;
    }

    private void addLiveDataObserver() {
        mSubredditViewModel.getSubredditLiveData().observe(getViewLifecycleOwner(), subredditData -> {
            if (subredditData != null) {
                sidebarDescription = subredditData.getSidebarDescription();
                if (sidebarDescription != null && !sidebarDescription.equals("")) {
                    markwonAdapter.setMarkdown(markwon, sidebarDescription);
                    // noinspection NotifyDataSetChanged
                    markwonAdapter.notifyDataSetChanged();
                }
            } else {
                fetchSubredditData();
            }

            if (mCommunityStats != null) {
                communityStatisticsBlock.setVisibility(View.VISIBLE);
                nSubscribersTextView.setText(getString(R.string.subscribers_number_detail, mCommunityStats.getSubscribers()));
                nActiveUsersTextView.setText(getString(R.string.active_users_number_detail, mCommunityStats.getActiveUsers()));
                nPostsTextView.setText(getString(R.string.post_count_detail, mCommunityStats.getPosts()));
                nCommentsTextView.setText(getString(R.string.comment_count_detail, mCommunityStats.getComments()));
            } else {
                fetchSubredditData();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (ViewSubredditDetailActivity) context;
    }

    public void fetchSubredditData() {
        swipeRefreshLayout.setRefreshing(true);
        FetchSubredditData.fetchSubredditData(mRetrofit.getRetrofit(), communityQualifiedName, mAccessToken, new FetchSubredditData.FetchSubredditDataListener() {
            @Override
            public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                swipeRefreshLayout.setRefreshing(false);
                mCommunityStats = subredditData.getCommunityStats();
                moderatorAdapter.setUsers(subredditData.getModerators());
                if (subredditData.getActorId().contains("/m/")) {
                    isKbinMagazine = true;
                    mSubredditViewModel.getSubredditLiveData().removeObservers(getViewLifecycleOwner());
                    mSubredditViewModel.setSubredditName(LemmyUtils.qualifiedMagazineName2ActorId(communityQualifiedName));
                    addLiveDataObserver();
                }
                InsertSubredditData.insertSubredditData(mExecutor, new Handler(), mRedditDataRoomDatabase,
                        subredditData, () -> swipeRefreshLayout.setRefreshing(false));
            }

            @Override
            public void onFetchSubredditDataFail(boolean isQuarantined) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(activity, R.string.cannot_fetch_sidebar, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goBackToTop() {
        if (linearLayoutManager != null) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }
}
