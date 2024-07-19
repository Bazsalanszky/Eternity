package eu.toldi.infinityforlemmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.RedditAPI;
import eu.toldi.infinityforlemmy.bottomsheetfragments.UrlMenuBottomSheetFragment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.customviews.SwipeLockInterface;
import eu.toldi.infinityforlemmy.customviews.SwipeLockLinearLayoutManager;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.markdown.MarkdownUtils;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.recycler.MarkwonAdapter;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WikiActivity extends BaseActivity {

    public static final String EXTRA_SUBREDDIT_NAME = "ESN";
    public static final String EXTRA_WIKI_PATH = "EWP";
    private static final String WIKI_MARKDOWN_STATE = "WMS";

    @BindView(R.id.coordinator_layout_comment_wiki_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_comment_wiki_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout_wiki_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_comment_wiki_activity)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout_wiki_activity)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.content_markdown_view_comment_wiki_activity)
    RecyclerView markdownRecyclerView;
    @BindView(R.id.fetch_wiki_linear_layout_wiki_activity)
    LinearLayout mFetchWikiInfoLinearLayout;
    @BindView(R.id.fetch_wiki_image_view_wiki_activity)
    ImageView mFetchWikiInfoImageView;
    @BindView(R.id.fetch_wiki_text_view_wiki_activity)
    TextView mFetchWikiInfoTextView;

    @Inject
    @Named("no_oauth")
    RetrofitHolder retrofit;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private String wikiMarkdown;
    private Markwon markwon;
    private MarkwonAdapter markwonAdapter;
    private boolean isRefreshing = false;
    private RequestManager mGlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                markdownRecyclerView.setPadding(markdownRecyclerView.getPaddingLeft(), 0, markdownRecyclerView.getPaddingRight(), getNavBarHeight());
            }
        }

        mGlide = Glide.with(getApplication());

        swipeRefreshLayout.setEnabled(mSharedPreferences.getBoolean(SharedPreferencesUtils.PULL_TO_REFRESH, true));
        swipeRefreshLayout.setOnRefreshListener(this::loadWiki);

        int markdownColor = mCustomThemeWrapper.getPrimaryTextColor();
        int spoilerBackgroundColor = markdownColor | 0xFF000000;
        int linkColor = mCustomThemeWrapper.getLinkColor();
        MarkwonPlugin miscPlugin = new AbstractMarkwonPlugin() {
            @Override
            public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                if (contentTypeface != null) {
                    textView.setTypeface(contentTypeface);
                }
                textView.setTextColor(markdownColor);
            }

            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> {
                    Intent intent = new Intent(WikiActivity.this, LinkResolverActivity.class);
                    Uri uri = Uri.parse(link);
                    intent.setData(uri);
                    startActivity(intent);
                });
            }

            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder.linkColor(linkColor);
            }
        };
        BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener = (textView, url) -> {
            UrlMenuBottomSheetFragment urlMenuBottomSheetFragment = UrlMenuBottomSheetFragment.newInstance(url);
            urlMenuBottomSheetFragment.show(getSupportFragmentManager(), null);
            return true;
        };
        markwon = MarkdownUtils.createFullRedditMarkwon(this,
                miscPlugin, markdownColor, spoilerBackgroundColor, mGlide, onLinkLongClickListener, mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW, false));

        markwonAdapter = MarkdownUtils.createTablesAdapter();
        LinearLayoutManagerBugFixed linearLayoutManager = new SwipeLockLinearLayoutManager(this, new SwipeLockInterface() {
            @Override
            public void lockSwipe() {
                if (mSliderPanel != null) {
                    mSliderPanel.lock();
                }
            }

            @Override
            public void unlockSwipe() {
                if (mSliderPanel != null) {
                    mSliderPanel.unlock();
                }
            }
        });
        markdownRecyclerView.setLayoutManager(linearLayoutManager);
        markdownRecyclerView.setAdapter(markwonAdapter);

        if (savedInstanceState != null) {
            wikiMarkdown = savedInstanceState.getString(WIKI_MARKDOWN_STATE);
        }

        if (wikiMarkdown == null) {
            loadWiki();
        } else {
            markwonAdapter.setMarkdown(markwon, wikiMarkdown);
            // noinspection NotifyDataSetChanged
            markwonAdapter.notifyDataSetChanged();
        }
    }

    private void loadWiki() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        swipeRefreshLayout.setRefreshing(true);

        Glide.with(getApplication()).clear(mFetchWikiInfoImageView);
        mFetchWikiInfoLinearLayout.setVisibility(View.GONE);

        retrofit.getRetrofit().create(RedditAPI.class).getWikiPage(getIntent().getStringExtra(EXTRA_SUBREDDIT_NAME), getIntent().getStringExtra(EXTRA_WIKI_PATH)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        String markdown = new JSONObject(response.body())
                                .getJSONObject(JSONUtils.DATA_KEY).getString(JSONUtils.CONTENT_MD_KEY);
                        markwonAdapter.setMarkdown(markwon, Utils.modifyMarkdown(markdown));
                        // noinspection NotifyDataSetChanged
                        markwonAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorView(R.string.error_loading_wiki);
                    }
                } else {
                    if (response.code() == 404 || response.code() == 403) {
                        showErrorView(R.string.no_wiki);
                    } else {
                        showErrorView(R.string.error_loading_wiki);
                    }
                }
                isRefreshing = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showErrorView(R.string.error_loading_wiki);
                isRefreshing = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showErrorView(int stringResId) {
        swipeRefreshLayout.setRefreshing(false);
        mFetchWikiInfoLinearLayout.setVisibility(View.VISIBLE);
        mFetchWikiInfoTextView.setText(stringResId);
        mGlide.load(R.mipmap.ic_launcher_round).into(mFetchWikiInfoImageView);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WIKI_MARKDOWN_STATE, wikiMarkdown);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(appBarLayout, collapsingToolbarLayout, toolbar);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        swipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        mFetchWikiInfoTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
        if (typeface != null) {
            mFetchWikiInfoTextView.setTypeface(typeface);
        }
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        if (!getClass().getName().equals(event.excludeActivityClassName)) {
            finish();
        }
    }
}