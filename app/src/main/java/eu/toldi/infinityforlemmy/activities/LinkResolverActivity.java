package eu.toldi.infinityforlemmy.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsService;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.asynctasks.SwitchAccount;
import eu.toldi.infinityforlemmy.comment.Comment;
import eu.toldi.infinityforlemmy.comment.FetchComment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.post.FetchPost;
import eu.toldi.infinityforlemmy.post.ObjectResolver;
import eu.toldi.infinityforlemmy.post.Post;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class LinkResolverActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_FULLNAME = "ENF";
    public static final String EXTRA_NEW_ACCOUNT_NAME = "ENAN";
    public static final String EXTRA_IS_NSFW = "EIN";

    private static final String POST_PATTERN = "https?:\\/\\/\\S+\\/post\\/\\d+";
    private static final String POST_PATTERN_3 = "/[\\w-]+$";
    private static final String COMMENT_PATTERN = "https?:\\/\\/\\S+\\/comment\\/\\d+";
    private static final String SUBREDDIT_PATTERN = "(?:https?://[\\w.-]+)?/c/[\\w-]+(@[\\w.-]+)?";
    private static final String USER_PATTERN = "(?:https?://[\\w.-]+)?/u(sers)?/[\\w-]+(@[\\w.-]+)?";
    private static final String SIDEBAR_PATTERN = "/[rR]/[\\w-]+/about/sidebar";
    private static final String MULTIREDDIT_PATTERN = "/user/[\\w-]+/m/\\w+/?";
    private static final String MULTIREDDIT_PATTERN_2 = "/[rR]/(\\w+\\+?)+/?";
    private static final String REDD_IT_POST_PATTERN = "/\\w+/?";
    private static final String REDGIFS_PATTERN = "/watch/[\\w-]+$";
    private static final String IMGUR_GALLERY_PATTERN = "/gallery/\\w+/?";
    private static final String IMGUR_ALBUM_PATTERN = "/(album|a)/\\w+/?";
    private static final String IMGUR_IMAGE_PATTERN = "/\\w+/?";
    private static final String WIKI_PATTERN = "/[rR]/[\\w-]+/(wiki|w)(?:/[\\w-]+)*";
    private static final String GOOGLE_AMP_PATTERN = "/amp/s/amp.reddit.com/.*";
    private static final String STREAMABLE_PATTERN = "/\\w+/?";

    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;

    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;

    @Inject
    ObjectResolver mObjectResolver;

    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;

    @Inject
    Executor mExecutor;

    @Inject
    PostEnricher postEnricher;

    private String mAccessToken;
    private String mAccountQualifedName;

    private Uri getRedditUriByPath(String path) {
        if (path.charAt(0) != '/') {
            return Uri.parse(mRetrofit.getBaseURL() + path);
        } else {
            return Uri.parse(mRetrofit.getBaseURL() + path);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Infinity) getApplication()).getAppComponent().inject(this);
        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountQualifedName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME,null);
        if (mAccessToken != null) {
            String instance = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_INSTANCE, null);
            mRetrofit.setBaseURL(instance);
            if (mCurrentAccountSharedPreferences.getBoolean(SharedPreferencesUtils.BEARER_TOKEN_AUTH, true)) {
                mRetrofit.setAccessToken(mAccessToken);
            }
        }

        Uri uri = getIntent().getData();
        if (uri == null) {
            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (!URLUtil.isValidUrl(url)) {
                Toast.makeText(getApplicationContext(), R.string.invalid_link, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            try {
                uri = Uri.parse(url);
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), R.string.invalid_link, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        if (uri.getScheme() == null && uri.getHost() == null) {
            if (uri.toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.invalid_link, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            handleUri(getRedditUriByPath(uri.toString()));
        } else {
            handleUri(uri);
        }
    }

    private void handleUri(Uri uri) {
        if (uri == null) {
            Toast.makeText(getApplicationContext(), R.string.no_link_available, Toast.LENGTH_SHORT).show();
        } else {
            String path = uri.getPath();
            if (path == null) {
                deepLinkError(uri);
            } else {
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }

                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                    Intent intent = new Intent(this, ViewImageOrGifActivity.class);
                    String url = uri.toString();
                    String fileName = FilenameUtils.getName(path);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, url);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, fileName);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, fileName);
                    startActivity(intent);
                } else if (path.endsWith(".gif")) {
                    Intent intent = new Intent(this, ViewImageOrGifActivity.class);
                    String url = uri.toString();
                    String fileName = FilenameUtils.getName(path);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_GIF_URL_KEY, url);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, fileName);
                    intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, fileName);
                    startActivity(intent);
                } else if (path.endsWith(".mp4")) {
                    Intent intent = new Intent(this, ViewVideoActivity.class);
                    intent.putExtra(ViewVideoActivity.EXTRA_VIDEO_TYPE, ViewVideoActivity.VIDEO_TYPE_DIRECT);
                    intent.putExtra(ViewVideoActivity.EXTRA_IS_NSFW, getIntent().getBooleanExtra(EXTRA_IS_NSFW, false));
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    String messageFullname = getIntent().getStringExtra(EXTRA_MESSAGE_FULLNAME);
                    String newAccountName = getIntent().getStringExtra(EXTRA_NEW_ACCOUNT_NAME);

                    String authority = uri.getAuthority();
                    List<String> segments = uri.getPathSegments();

                    if (authority != null) {
                        if (authority.equals("reddit-uploaded-media.s3-accelerate.amazonaws.com")) {
                            String unescapedUrl = uri.toString().replace("%2F", "/");
                            int lastSlashIndex = unescapedUrl.lastIndexOf("/");
                            if (lastSlashIndex < 0 || lastSlashIndex == unescapedUrl.length() - 1) {
                                deepLinkError(uri);
                                return;
                            }
                            String id = unescapedUrl.substring(lastSlashIndex + 1);
                            Intent intent = new Intent(this, ViewImageOrGifActivity.class);
                            intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, uri.toString());
                            intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, id + ".jpg");
                            startActivity(intent);
                        } else if (uri.toString().matches(SUBREDDIT_PATTERN)) {
                            Intent intent = new Intent(this, ViewSubredditDetailActivity.class);
                            intent.putExtra(ViewSubredditDetailActivity.EXTRA_COMMUNITY_FULL_NAME_KEY, uri.getScheme() != null ? LemmyUtils.actorID2FullName(uri.toString()) : path.substring(3));
                            intent.putExtra(ViewSubredditDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                            intent.putExtra(ViewSubredditDetailActivity.EXTRA_NEW_ACCOUNT_NAME, newAccountName);
                            startActivity(intent);
                        } else if (uri.toString().matches(USER_PATTERN)) {
                            Intent intent = new Intent(this, ViewUserDetailActivity.class);
                            intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, uri.getScheme() != null ? LemmyUtils.actorID2FullName(uri.toString()) : path.substring(3));
                            intent.putExtra(ViewUserDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                            intent.putExtra(ViewUserDetailActivity.EXTRA_NEW_ACCOUNT_NAME, newAccountName);
                            startActivity(intent);
                        } else if (uri.toString().matches(POST_PATTERN)) {
                            if (mAccessToken == null) {
                                // switch retrofit to use the current instance for anonymous requests
                                mRetrofit.setBaseURL(uri.getScheme() + "://" + uri.getHost() + "/");
                                Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                                intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, Integer.parseInt(segments.get(segments.size() - 1)));
                                intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                                intent.putExtra(ViewPostDetailActivity.EXTRA_NEW_ACCOUNT_NAME, newAccountName);
                                startActivity(intent);
                            } else {
                                if (mAccountQualifedName == null || newAccountName != null && !mAccountQualifedName.equals(newAccountName)) {
                                    SwitchAccount.switchAccount(mRedditDataRoomDatabase, mRetrofit, mCurrentAccountSharedPreferences,
                                            mExecutor, new Handler(), newAccountName, newAccount -> {
                                                EventBus.getDefault().post(new SwitchAccountEvent(getClass().getName()));
                                                Toast.makeText(this, R.string.account_switched, Toast.LENGTH_SHORT).show();
                                                openPost(uri, segments, messageFullname);
                                            });
                                } else {
                                    openPost(uri, segments, messageFullname);
                                }

                            }
                        } else if (uri.toString().matches(COMMENT_PATTERN)) {
                            if (mAccessToken == null) {
                                mRetrofit.setBaseURL(uri.getScheme() + "://" + uri.getHost() + "/");
                                FetchComment.fetchSingleComment(mRetrofit.getRetrofit(), null, Integer.parseInt(segments.get(segments.size() - 1)), new FetchComment.FetchCommentListener() {
                                    @Override
                                    public void onFetchCommentSuccess(ArrayList<Comment> comments, Integer parentId, ArrayList<Integer> moreChildrenIds) {
                                        Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                                        Comment comment = comments.get(0);
                                        intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, comment.getPostId());
                                        intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                                        intent.putExtra(ViewPostDetailActivity.EXTRA_NEW_ACCOUNT_NAME, newAccountName);
                                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_ID, comment.getId());
                                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_PARENT_ID, comment.getParentId());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFetchCommentFailed() {
                                        Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } else {
                                if (mAccountQualifedName == null || newAccountName != null && !mAccountQualifedName.equals(newAccountName)) {
                                    SwitchAccount.switchAccount(mRedditDataRoomDatabase, mRetrofit, mCurrentAccountSharedPreferences,
                                            mExecutor, new Handler(), newAccountName, newAccount -> {
                                                EventBus.getDefault().post(new SwitchAccountEvent(getClass().getName()));
                                                Toast.makeText(this, R.string.account_switched, Toast.LENGTH_SHORT).show();
                                                openComment(uri, segments, messageFullname);
                                            });
                                } else {
                                    openComment(uri, segments, messageFullname);
                                }

                            }
                        } else if (authority.equals("v.redd.it")) {
                            Intent intent = new Intent(this, ViewVideoActivity.class);
                            intent.putExtra(ViewVideoActivity.EXTRA_VIDEO_TYPE, ViewVideoActivity.VIDEO_TYPE_V_REDD_IT);
                            intent.putExtra(ViewVideoActivity.EXTRA_V_REDD_IT_URL, uri.toString());
                            startActivity(intent);
                        } else if (authority.equals("click.redditmail.com")) {
                            if (path.startsWith("/CL0/")) {
                                handleUri(Uri.parse(path.substring("/CL0/".length())));
                            }
                        } else if (authority.contains("redgifs.com")) {
                            if (path.matches(REDGIFS_PATTERN)) {
                                Intent intent = new Intent(this, ViewVideoActivity.class);
                                intent.putExtra(ViewVideoActivity.EXTRA_REDGIFS_ID, path.substring(path.lastIndexOf("/") + 1));
                                intent.putExtra(ViewVideoActivity.EXTRA_VIDEO_TYPE, ViewVideoActivity.VIDEO_TYPE_REDGIFS);
                                intent.putExtra(ViewVideoActivity.EXTRA_IS_NSFW, true);
                                startActivity(intent);
                            } else {
                                deepLinkError(uri);
                            }
                        } else if (authority.contains("imgur.com")) {
                            if (path.matches(IMGUR_GALLERY_PATTERN)) {
                                Intent intent = new Intent(this, ViewImgurMediaActivity.class);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_TYPE, ViewImgurMediaActivity.IMGUR_TYPE_GALLERY);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_ID, segments.get(1));
                                startActivity(intent);
                            } else if (path.matches(IMGUR_ALBUM_PATTERN)) {
                                Intent intent = new Intent(this, ViewImgurMediaActivity.class);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_TYPE, ViewImgurMediaActivity.IMGUR_TYPE_ALBUM);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_ID, segments.get(1));
                                startActivity(intent);
                            } else if (path.matches(IMGUR_IMAGE_PATTERN)) {
                                Intent intent = new Intent(this, ViewImgurMediaActivity.class);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_TYPE, ViewImgurMediaActivity.IMGUR_TYPE_IMAGE);
                                intent.putExtra(ViewImgurMediaActivity.EXTRA_IMGUR_ID, path.substring(1));
                                startActivity(intent);
                            } else if (path.endsWith("gifv") || path.endsWith("mp4")) {
                                String url = uri.toString();
                                if (path.endsWith("gifv")) {
                                    url = url.substring(0, url.length() - 5) + ".mp4";
                                }
                                Intent intent = new Intent(this, ViewVideoActivity.class);
                                intent.putExtra(ViewVideoActivity.EXTRA_VIDEO_TYPE, ViewVideoActivity.VIDEO_TYPE_IMGUR);
                                intent.putExtra(ViewVideoActivity.EXTRA_IS_NSFW, getIntent().getBooleanExtra(EXTRA_IS_NSFW, false));
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            } else {
                                deepLinkError(uri);
                            }
                        } else if (authority.contains("google.com")) {
                            if (path.matches(GOOGLE_AMP_PATTERN)) {
                                String url = path.substring(11);
                                handleUri(Uri.parse("https://" + url));
                            } else {
                                deepLinkError(uri);
                            }
                        } else if (authority.equals("streamable.com")) {
                            if (path.matches(STREAMABLE_PATTERN)) {
                                String shortCode = segments.get(0);
                                Intent intent = new Intent(this, ViewVideoActivity.class);
                                intent.putExtra(ViewVideoActivity.EXTRA_VIDEO_TYPE, ViewVideoActivity.VIDEO_TYPE_STREAMABLE);
                                intent.putExtra(ViewVideoActivity.EXTRA_STREAMABLE_SHORT_CODE, shortCode);
                                startActivity(intent);
                            } else {
                                deepLinkError(uri);
                            }
                        } else {
                            deepLinkError(uri);
                        }
                    } else {
                        deepLinkError(uri);
                    }
                }
            }

        }
        finish();
    }

    private void openComment(Uri uri, List<String> segments, String messageFullname) {
        boolean local = false;
        try {
            URL baseURL = new URL(mRetrofit.getBaseURL());
            if (baseURL.getHost().equalsIgnoreCase(uri.getHost())) {
                local = true;
                FetchComment.fetchSingleComment(mRetrofit.getRetrofit(), mAccessToken, Integer.parseInt(segments.get(segments.size() - 1)), new FetchComment.FetchCommentListener() {
                    @Override
                    public void onFetchCommentSuccess(ArrayList<Comment> comments, Integer parentId, ArrayList<Integer> moreChildrenIds) {
                        Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                        Comment comment = comments.get(0);
                        intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, comment.getPostId());
                        intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_ID, comment.getId());
                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_PARENT_ID, comment.getParentId());
                        startActivity(intent);
                    }

                    @Override
                    public void onFetchCommentFailed() {
                        Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!local) {
            mObjectResolver.resolveComment(uri.toString(), mAccessToken, new ObjectResolver.ObjectResolverListener() {
                @Override
                public void onResolveObjectSuccess(Object c) {
                    Comment comment = (Comment) c;
                    Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                    intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, comment.getPostId());
                    intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                    intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_ID, comment.getId());
                    intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_PARENT_ID, comment.getParentId());
                    startActivity(intent);
                }

                @Override
                public void onResolveObjectFailed() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void openPost(Uri uri, List<String> segments, String messageFullname) {
        boolean local = false;
        try {
            URL baseURL = new URL(mRetrofit.getBaseURL());
            if (baseURL.getHost().equalsIgnoreCase(uri.getHost())) {
                local = true;
                FetchPost.fetchPost(mExecutor, new Handler(), mRetrofit.getRetrofit(), segments.get(segments.size() - 1), mAccessToken, postEnricher, new FetchPost.FetchPostListener() {
                    @Override
                    public void fetchPostSuccess(Post post) {

                        Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                        intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, post.getId());
                        intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                        startActivity(intent);
                    }

                    @Override
                    public void fetchPostFailed() {
                        Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!local) {
            mObjectResolver.resolvePost(uri.toString(), mAccessToken, new ObjectResolver.ObjectResolverListener() {
                @Override
                public void onResolveObjectSuccess(Object p) {
                    Post post = (Post) p;
                    Intent intent = new Intent(LinkResolverActivity.this, ViewPostDetailActivity.class);
                    intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, post.getId());
                    intent.putExtra(ViewPostDetailActivity.EXTRA_MESSAGE_FULLNAME, messageFullname);
                    startActivity(intent);
                }

                @Override
                public void onResolveObjectFailed() {
                    Toast.makeText(getApplicationContext(), R.string.could_not_resolve_link, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void deepLinkError(Uri uri) {
        PackageManager pm = getPackageManager();

        String authority = uri.getAuthority();
        if(authority != null && (authority.contains("reddit.com") || authority.contains("redd.it") || authority.contains("reddit.app.link"))) {
            openInBrowser(uri, pm, false);
            return;
        }

        int linkHandler = Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.LINK_HANDLER, "0"));
        if (linkHandler == 0) {
            openInBrowser(uri, pm, true);
        } else if (linkHandler == 1) {
            openInCustomTabs(uri, pm, true);
        } else {
            openInWebView(uri);
        }
    }

    private void openInBrowser(Uri uri, PackageManager pm, boolean handleError) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            if (handleError) {
                openInCustomTabs(uri, pm, false);
            } else {
                openInWebView(uri);
            }
        }
    }

    private ArrayList<ResolveInfo> getCustomTabsPackages(PackageManager pm) {
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.fromParts("http", "", null));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList<ResolveInfo> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info);
            }
        }
        return packagesSupportingCustomTabs;
    }

    private void openInCustomTabs(Uri uri, PackageManager pm, boolean handleError) {
        ArrayList<ResolveInfo> resolveInfos = getCustomTabsPackages(pm);
        if (!resolveInfos.isEmpty()) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            // add share action to menu list
            builder.setShareState(CustomTabsIntent.SHARE_STATE_ON);
            builder.setDefaultColorSchemeParams(
                    new CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(mCustomThemeWrapper.getColorPrimary())
                            .build());
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(resolveInfos.get(0).activityInfo.packageName);
            if (uri.getScheme() == null) {
                uri = Uri.parse("http://" + uri.toString());
            }
            try {
                customTabsIntent.launchUrl(this, uri);
            } catch (ActivityNotFoundException e) {
                if (handleError) {
                    openInBrowser(uri, pm, false);
                } else {
                    openInWebView(uri);
                }
            }
        } else {
            if (handleError) {
                openInBrowser(uri, pm, false);
            } else {
                openInWebView(uri);
            }
        }
    }

    private void openInWebView(Uri uri) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }
}
