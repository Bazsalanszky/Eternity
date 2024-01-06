package eu.toldi.infinityforlemmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.adapters.AdminRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.databinding.ActivityInstanceInfoBinding;
import eu.toldi.infinityforlemmy.markdown.MarkdownUtils;
import eu.toldi.infinityforlemmy.site.FetchSiteInfo;
import eu.toldi.infinityforlemmy.site.SiteInfo;
import eu.toldi.infinityforlemmy.site.SiteStatistics;
import eu.toldi.infinityforlemmy.user.BasicUserInfo;
import eu.toldi.infinityforlemmy.user.MyUserInfo;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.recycler.MarkwonAdapter;

public class InstanceInfoActivity extends BaseActivity {

    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;

    @Inject
    CustomThemeWrapper mCustomThemeWrapper;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetorifitHolder;

    ActivityInstanceInfoBinding mInstanceInfoActivityViewBinding;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    private ConstraintLayout mLoadingConstraintLayout;

    private MaterialCardView mStatisticsCardView;

    private TextView mUsersTextView;
    private TextView mCommunitiesTextView;
    private TextView mPostsTextView;
    private TextView mCommentsTextView;
    private TextView mActiveUsersTextView;
    private ImageView mUsersImageView;
    private ImageView mCommunitiesImageView;
    private ImageView mPostsImageView;
    private ImageView mCommentsImageView;
    private ImageView mActiveUsersImageView;

    private MaterialCardView mDescriptionCardView;

    private RecyclerView mContentMarkdownView;
    private RecyclerView mAdminsRecyclerView;

    private MaterialCardView mAdminsCardView;
    private MarkwonAdapter mMarkwonAdapter;
    private Markwon mPostDetailMarkwon;
    private AdminRecyclerViewAdapter mAdminAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        mInstanceInfoActivityViewBinding = ActivityInstanceInfoBinding.inflate(getLayoutInflater());
        View view = mInstanceInfoActivityViewBinding.getRoot();
        setImmersiveModeNotApplicable();

        setContentView(view);
        setSupportActionBar(mInstanceInfoActivityViewBinding.toolbarInstanceInfoActivity);

        setUpBindings();
        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Remove transparency from navigation bar
        getWindow().setNavigationBarColor(mCustomThemeWrapper.getBackgroundColor());

        int markdownColor = customThemeWrapper.getPostContentColor();
        int postSpoilerBackgroundColor = markdownColor | 0xFF000000;
        int linkColor = customThemeWrapper.getLinkColor();


        MarkwonPlugin miscPlugin = new AbstractMarkwonPlugin() {
            @Override
            public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                if (InstanceInfoActivity.this.contentTypeface != null) {
                    textView.setTypeface(InstanceInfoActivity.this.contentTypeface);
                }
                textView.setTextColor(markdownColor);
                textView.setHighlightColor(Color.TRANSPARENT);
            }

            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> {
                    Intent intent = new Intent(InstanceInfoActivity.this, LinkResolverActivity.class);
                    Uri uri = Uri.parse(link);
                    intent.setData(uri);
                    InstanceInfoActivity.this.startActivity(intent);
                });
            }

            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder.linkColor(linkColor);
            }
        };

        mPostDetailMarkwon = MarkdownUtils.createFullRedditMarkwon(this,
                miscPlugin, markdownColor, postSpoilerBackgroundColor, null, mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW, false));
        mMarkwonAdapter = MarkdownUtils.createTablesAdapter();
        mContentMarkdownView.setAdapter(mMarkwonAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mContentMarkdownView.setLayoutManager(linearLayoutManager);
        mAdminAdapter = new AdminRecyclerViewAdapter(InstanceInfoActivity.this, customThemeWrapper);
        mAdminsRecyclerView.setLayoutManager(new LinearLayoutManagerBugFixed(InstanceInfoActivity.this));
        mAdminsRecyclerView.setAdapter(mAdminAdapter);

        fetchInstanceInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchInstanceInfo();
    }

    private void fetchInstanceInfo() {
        FetchSiteInfo.fetchSiteInfo(mRetorifitHolder.getRetrofit(), null, new FetchSiteInfo.FetchSiteInfoListener() {
            @Override
            public void onFetchSiteInfoSuccess(SiteInfo siteInfo, MyUserInfo myUserInfo) {
                mLoadingConstraintLayout.setVisibility(View.GONE);
                toolbar.setTitle(siteInfo.getName());
                if (siteInfo.getSidebar() != null) {
                    mMarkwonAdapter.setMarkdown(mPostDetailMarkwon, siteInfo.getSidebar());
                    Log.i("SiteInfo", "onFetchSiteInfoSuccess: " + siteInfo.getSidebar());
                    // noinspection NotifyDataSetChanged
                    mMarkwonAdapter.notifyDataSetChanged();
                    mDescriptionCardView.setVisibility(View.VISIBLE);
                }
                List<BasicUserInfo> admins = siteInfo.getAdmins();
                if (admins != null && !admins.isEmpty()) {
                    mAdminsCardView.setVisibility(View.VISIBLE);
                    mAdminAdapter.setUsers(admins);
                }
                SiteStatistics siteStatistics = siteInfo.getSiteStatistics();
                if (siteStatistics != null) {
                    mStatisticsCardView.setVisibility(View.VISIBLE);
                    mUsersTextView.setText(getString(R.string.user_number_detail, siteStatistics.getUsers()));
                    mCommunitiesTextView.setText(getString(R.string.community_number_detail, siteStatistics.getCommunities()));
                    mPostsTextView.setText(getString(R.string.post_count_detail, siteStatistics.getPosts()));
                    mCommentsTextView.setText(getString(R.string.comment_count_detail, siteStatistics.getComments()));
                    mActiveUsersTextView.setText(getString(R.string.active_users_number_detail, siteStatistics.getUsers_active()));
                }
            }

            @Override
            public void onFetchSiteInfoFailed(boolean parseFailed) {

            }
        });
    }

    protected void setUpBindings() {
        coordinatorLayout = mInstanceInfoActivityViewBinding.coordinatorLayoutInstanceInfoActivity;
        toolbar = mInstanceInfoActivityViewBinding.toolbarInstanceInfoActivity;
        appBarLayout = mInstanceInfoActivityViewBinding.appbarLayoutInstanceInfoActivity;
        mStatisticsCardView = mInstanceInfoActivityViewBinding.statisticsCardInstanceInfoActivity;
        mDescriptionCardView = mInstanceInfoActivityViewBinding.descriptionCardInstanceInfoActivity;
        mContentMarkdownView = mInstanceInfoActivityViewBinding.markdownRecyclerViewInstanceInfoActivity;
        mAdminsCardView = mInstanceInfoActivityViewBinding.moderatorsCardInstanceInfoActivity;
        mLoadingConstraintLayout = mInstanceInfoActivityViewBinding.loadingLayoutInstanceInfoActivity;
        mAdminsRecyclerView = mInstanceInfoActivityViewBinding.recyclerViewAdminsInstanceInfoActivity;
        mUsersTextView = mInstanceInfoActivityViewBinding.registeredUserCountTextViewInstanceInfoActivity;
        mCommunitiesTextView = mInstanceInfoActivityViewBinding.communityCountInstanceInfoActivity;
        mPostsTextView = mInstanceInfoActivityViewBinding.postCountTextViewInstanceInfoActivity;
        mCommentsTextView = mInstanceInfoActivityViewBinding.commentCountTextViewInstanceInfoActivity;
        mActiveUsersTextView = mInstanceInfoActivityViewBinding.activeUserCountTextViewInstanceInfoActivity;
        mUsersImageView = mInstanceInfoActivityViewBinding.registeredUserCountImageViewInstanceInfoActivity;
        mCommunitiesImageView = mInstanceInfoActivityViewBinding.communitiesIconImageViewInstanceInfoActivity;
        mPostsImageView = mInstanceInfoActivityViewBinding.postCountImageViewInstanceInfoActivity;
        mCommentsImageView = mInstanceInfoActivityViewBinding.commentCountImageViewInstanceInfoActivity;
        mActiveUsersImageView = mInstanceInfoActivityViewBinding.activeUserCountImageViewInstanceInfoActivity;
    }

    @Override
    protected SharedPreferences getDefaultSharedPreferences() {
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
        mStatisticsCardView.setCardBackgroundColor(mCustomThemeWrapper.getCardViewBackgroundColor());
        mDescriptionCardView.setCardBackgroundColor(mCustomThemeWrapper.getCardViewBackgroundColor());
        mAdminsCardView.setCardBackgroundColor(mCustomThemeWrapper.getCardViewBackgroundColor());
        mUsersImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        mCommunitiesImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        mPostsImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        mCommentsImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        mActiveUsersImageView.setColorFilter(mCustomThemeWrapper.getPrimaryTextColor(), PorterDuff.Mode.SRC_IN);
        mUsersTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        mCommunitiesTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        mPostsTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        mCommentsTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        mActiveUsersTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());

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
