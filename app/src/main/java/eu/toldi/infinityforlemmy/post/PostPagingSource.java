package eu.toldi.infinityforlemmy.post;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.utils.MultiCommunityUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import retrofit2.HttpException;
import retrofit2.Response;

public class PostPagingSource extends ListenableFuturePagingSource<Integer, Post> {
    public static final int TYPE_FRONT_PAGE = 0;
    public static final int TYPE_SUBREDDIT = 1;
    public static final int TYPE_USER = 2;
    public static final int TYPE_SEARCH = 3;
    public static final int TYPE_MULTI_REDDIT = 4;
    public static final int TYPE_ANONYMOUS_FRONT_PAGE = 5;
    public static final int TYPE_ANONYMOUS_MULTIREDDIT = 6;

    public static final String USER_WHERE_SUBMITTED = "submitted";
    public static final String USER_WHERE_UPVOTED = "upvoted";
    public static final String USER_WHERE_DOWNVOTED = "downvoted";
    public static final String USER_WHERE_HIDDEN = "hidden";
    public static final String USER_WHERE_SAVED = "saved";
    public static final String USER_WHERE_GILDED = "gilded";

    private Executor executor;
    private RetrofitHolder retrofit;
    private String accessToken;
    private String accountName;
    private SharedPreferences sharedPreferences;
    private SharedPreferences postFeedScrolledPositionSharedPreferences;
    private String subredditOrUserName;
    private String query;
    private String trendingSource;
    private int postType;
    private SortType sortType;
    private PostFilter postFilter;
    private List<String> readPostList;
    private String userWhere;
    private String multiRedditPath;
    private final PostEnricher postEnricher;
    private LinkedHashSet<Post> postLinkedHashSet;

    private int page = 1;

    PostPagingSource(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences,
                     SharedPreferences postFeedScrolledPositionSharedPreferences, int postType,
                     SortType sortType, PostFilter postFilter, List<String> readPostList, String option,
                     PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.subredditOrUserName = option;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.postType = postType;
        this.sortType = sortType == null ? new SortType(SortType.Type.ACTIVE) : sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.postEnricher = postEnricher;
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String path, int postType, SortType sortType, PostFilter postFilter,
                     List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        if (postType == TYPE_SUBREDDIT || postType == TYPE_ANONYMOUS_FRONT_PAGE) {
            this.subredditOrUserName = path;
        } else {
            multiRedditPath = path;
        }
        this.postType = postType;
        if (sortType == null) {
            if (path.equals("local") || path.equals("all")) {
                this.sortType = new SortType(SortType.Type.HOT);
            } else {
                this.sortType = new SortType(SortType.Type.ACTIVE);
            }
        } else {
            this.sortType = sortType;
        }
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.postEnricher = postEnricher;
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String subredditOrUserName, int postType, SortType sortType, PostFilter postFilter,
                     String where, List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.subredditOrUserName = subredditOrUserName;
        this.postType = postType;
        this.sortType = sortType == null ? new SortType(SortType.Type.NEW) : sortType;
        this.postFilter = postFilter;
        userWhere = where;
        this.readPostList = readPostList;
        this.postEnricher = postEnricher;
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String subredditOrUserName, String query, String trendingSource, int postType,
                     SortType sortType, PostFilter postFilter, List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.subredditOrUserName = subredditOrUserName;
        this.query = query;
        this.trendingSource = trendingSource;
        this.postType = postType;
        this.sortType = sortType == null ? new SortType(SortType.Type.TOP) : sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.postEnricher = postEnricher;
        postLinkedHashSet = new LinkedHashSet<>();
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Post> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Post>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        LemmyAPI api = retrofit.getRetrofit().create(LemmyAPI.class);
        switch (postType) {

            case TYPE_FRONT_PAGE:
                return loadHomePosts(loadParams, api);
            case TYPE_USER:
                return loadUserPosts(loadParams, api);
            case TYPE_SEARCH:
                return loadSearchPosts(loadParams, api);
            case TYPE_SUBREDDIT:
                return loadSubredditPosts(loadParams, api);
            case TYPE_MULTI_REDDIT:
                return loadMultipleSubredditPosts(loadParams, api, List.of(multiRedditPath.split(Pattern.quote(","))));
            case TYPE_ANONYMOUS_FRONT_PAGE:
            default:
                return loadAnonymousHomePosts(loadParams, api);
        }
    }

    public LoadResult<Integer, Post> transformData(Response<String> response) {
        if (response.isSuccessful()) {
            String responseString = response.body();
            LinkedHashSet<Post> newPosts = ParsePost.parsePostsSync(responseString, -1, postFilter, readPostList, postEnricher);

            if (newPosts == null) {
                return new LoadResult.Error<>(new Exception("Error parsing posts"));
            } else {
                List<Post> trulyNewPosts = new ArrayList<>();
                for (Post post : newPosts) {
                    if (!postLinkedHashSet.contains(post)) {
                        trulyNewPosts.add(post);
                    }
                }
                int currentPostsSize = postLinkedHashSet.size();
                postLinkedHashSet.addAll(trulyNewPosts);
                int nextKey = ++page;
                if (trulyNewPosts.size() == 0) {
                    return new LoadResult.Page<>(new ArrayList<>(), null, null);
                }

                if (currentPostsSize == postLinkedHashSet.size()) {
                    return new LoadResult.Page<>(new ArrayList<>(), null, nextKey);
                } else {
                    return new LoadResult.Page<>(new ArrayList<>(postLinkedHashSet).subList(currentPostsSize, postLinkedHashSet.size()), null, nextKey);
                }
            }
        } else {
            return new LoadResult.Error<>(new Exception("Response failed"));
        }
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadHomePosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> bestPost;
        Integer page;
        if (loadParams.getKey() == null) {
            boolean savePostFeedScrolledPosition = sortType != null && sortType.getType() == SortType.Type.ACTIVE && sharedPreferences.getBoolean(SharedPreferencesUtils.SAVE_FRONT_PAGE_SCROLLED_POSITION, false);
            if (savePostFeedScrolledPosition) {
                String accountNameForCache = accountName == null ? SharedPreferencesUtils.FRONT_PAGE_SCROLLED_POSITION_ANONYMOUS : accountName;
                // TODO: Fix this. Save the page number?
                page = null; // postFeedScrolledPositionSharedPreferences.getString(accountNameForCache + SharedPreferencesUtils.FRONT_PAGE_SCROLLED_POSITION_FRONT_PAGE_BASE, null);
            } else {
                page = null;
            }
        } else {
            page = loadParams.getKey();
        }
        String feed_type = Objects.equals(subredditOrUserName, "all") ? "All" : Objects.equals(subredditOrUserName, "local") ? "Local" : "Subscribed";

        bestPost = api.getPostsListenableFuture(feed_type, sortType.getType().value, page, 25, null, null, false, accessToken);

        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(bestPost, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadSubredditPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> subredditPost;

        subredditPost = api.getPostsListenableFuture(null, sortType.getType().value, loadParams.getKey(), 25, null, subredditOrUserName, false, accessToken);


        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(subredditPost, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadUserPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> userPosts;
        userPosts = api.getUserPostsListenableFuture(subredditOrUserName, sortType.getType().value, loadParams.getKey(), 25, userWhere.equals(USER_WHERE_SAVED), accessToken);


        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(userPosts, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadSearchPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> searchPosts;

        searchPosts = api.searchLive(query, null, subredditOrUserName, null, "Posts", sortType.getType().value, "All", loadParams.getKey(), 25, accessToken);


        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(searchPosts, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    Map<String, List<Post>> undisplayedPosts = new HashMap<>();

    private ListenableFuture<LoadResult<Integer, Post>> loadMultipleSubredditPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api, List<String> communities) {
        List<ListenableFuture<LoadResult<Integer, Post>>> futures = new ArrayList<>();
        List<Post> combinedPostsFromCache = new ArrayList<>();

        // Check and use undisplayed posts first
        for (String community : communities) {
            futures.add(fetchPostsFromCommunity(api, loadParams, community));
        }

        // Combine and sort posts from cache and fetched
        return Futures.transform(Futures.successfulAsList(futures),
                results -> {
                    for (LoadResult<Integer, Post> result : results) {
                        if (result instanceof LoadResult.Page) {
                            combinedPostsFromCache.addAll(((LoadResult.Page<Integer, Post>) result).getData());
                        }
                    }
                    switch (sortType.getType()) {
                        default:
                        case NEW:
                            MultiCommunityUtils.INSTANCE.sortByNewest(combinedPostsFromCache);
                        case TOP_ALL:
                        case TOP_YEAR:
                        case TOP_NINE_MONTHS:
                        case TOP_SIX_MONTHS:
                        case TOP_THREE_MONTHS:
                        case TOP_MONTH:
                        case TOP_WEEK:
                        case TOP_DAY:
                        case TOP_TWELVE_HOURS:
                        case TOP_SIX_HOURS:
                        case TOP_HOUR:
                            MultiCommunityUtils.INSTANCE.sortByScore(combinedPostsFromCache);

                    }

                    List<Post> result = MultiCommunityUtils.INSTANCE.takeFirstN(combinedPostsFromCache, 25);
                    // Gather undisplayed posts
                    for (int i = result.size(); i < combinedPostsFromCache.size(); i++) {
                        Post post = combinedPostsFromCache.get(i);
                        if (undisplayedPosts.containsKey(post.getCommunityInfo().getQualifiedName())) {
                            undisplayedPosts.get(post.getCommunityInfo().getQualifiedName()).add(post);
                        } else {
                            undisplayedPosts.put(post.getCommunityInfo().getQualifiedName(), new ArrayList<>(Arrays.asList(post)));
                        }
                    }

                    Integer prevKey = result.size() > 0 ? (loadParams.getKey() != null) ? loadParams.getKey() : 1 : null;
                    Integer nextKey = (prevKey != null) ? prevKey + 1 : null;

                    return new LoadResult.Page<>(result, prevKey, nextKey);
                }, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> fetchPostsFromCommunity(LemmyAPI api, LoadParams<Integer> loadParams, String community) {

        ListenableFuture<Response<String>> subredditPost;

        subredditPost = api.getPostsListenableFuture(null, sortType.getType().value, loadParams.getKey(), 25, null, community, false, accessToken);

        ListenableFuture<LoadResult<Integer, Post>> communityFuture = Futures.transform(subredditPost,
                this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(communityFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }


/*
    private ListenableFuture<LoadResult<String, Post>> loadMultiRedditPosts(@NonNull LoadParams<String> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> multiRedditPosts;
        if (accessToken == null) {
            multiRedditPosts = api.getMultiRedditPostsListenableFuture(multiRedditPath, loadParams.getKey(), sortType.getTime());
        } else {
            multiRedditPosts = api.getMultiRedditPostsOauthListenableFuture(multiRedditPath, loadParams.getKey(),
                    sortType.getTime(), APIUtils.getOAuthHeader(accessToken));
        }

        ListenableFuture<LoadResult<String, Post>> pageFuture = Futures.transform(multiRedditPosts, this::transformData, executor);

        ListenableFuture<LoadResult<String, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }*/

    private ListenableFuture<LoadResult<Integer, Post>> loadAnonymousHomePosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        if (subredditOrUserName == null) {
            // Return empty list
            return Futures.immediateFuture(new LoadResult.Page<>(new ArrayList<>(), null, null));
        }
        return loadMultipleSubredditPosts(loadParams, api, Arrays.asList(subredditOrUserName.split(Pattern.quote(","))));
    }

    @Override
    public boolean getKeyReuseSupported() {
        //TODO: Figure out why this is needed
        return true;
    }
}
