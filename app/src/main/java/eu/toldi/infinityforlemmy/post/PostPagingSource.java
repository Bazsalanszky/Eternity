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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private Retrofit retrofit;
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
    private LinkedHashSet<Post> postLinkedHashSet;

    private int page = 1;

    PostPagingSource(Executor executor, Retrofit retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences,
                     SharedPreferences postFeedScrolledPositionSharedPreferences, int postType,
                     SortType sortType, PostFilter postFilter, List<String> readPostList,String option) {
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
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, Retrofit retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String path, int postType, SortType sortType, PostFilter postFilter,
                     List<String> readPostList) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        if (postType == TYPE_SUBREDDIT || postType == TYPE_ANONYMOUS_FRONT_PAGE) {
            this.subredditOrUserName = path;
        } else {
            if (sortType != null) {
                if (path.endsWith("/")) {
                    multiRedditPath = path + sortType.getType().value;
                } else {
                    multiRedditPath = path + "/" + sortType.getType().value;
                }
            } else {
                multiRedditPath = path;
            }
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
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, Retrofit retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String subredditOrUserName, int postType, SortType sortType, PostFilter postFilter,
                     String where, List<String> readPostList) {
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
        postLinkedHashSet = new LinkedHashSet<>();
    }

    PostPagingSource(Executor executor, Retrofit retrofit, String accessToken, String accountName,
                     SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                     String subredditOrUserName, String query, String trendingSource, int postType,
                     SortType sortType, PostFilter postFilter, List<String> readPostList) {
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
        postLinkedHashSet = new LinkedHashSet<>();
        this.readPostList = readPostList;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Post> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Post>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        switch (postType) {
            default:
            case TYPE_FRONT_PAGE:
                return loadHomePosts(loadParams, api);
            case TYPE_SUBREDDIT:
                return loadSubredditPosts(loadParams, api);
            case TYPE_USER:
                return loadUserPosts(loadParams, api);
            case TYPE_SEARCH:
                return loadSearchPosts(loadParams, api);
          /*  case TYPE_MULTI_REDDIT:
                return loadMultiRedditPosts(loadParams, api);
            default:
                return loadAnonymousHomePosts(loadParams, api);*/
        }
    }

    public LoadResult<Integer, Post> transformData(Response<String> response) {
        if (response.isSuccessful()) {
            String responseString = response.body();
            LinkedHashSet<Post> newPosts = ParsePost.parsePostsSync(responseString, -1, postFilter, readPostList);

            if (newPosts == null) {
                return new LoadResult.Error<>(new Exception("Error parsing posts"));
            } else {
                int currentPostsSize = postLinkedHashSet.size();
                postLinkedHashSet.addAll(newPosts);
                int nextKey = (postLinkedHashSet.size()+1) / 25+1;
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
                page = null ; // postFeedScrolledPositionSharedPreferences.getString(accountNameForCache + SharedPreferencesUtils.FRONT_PAGE_SCROLLED_POSITION_FRONT_PAGE_BASE, null);
            } else {
                page = null;
            }
        } else {
            page = loadParams.getKey();
        }
        String feed_type = Objects.equals(subredditOrUserName, "all") ? "All" : Objects.equals(subredditOrUserName, "local") ? "Local" : "Subscribed";

        bestPost = api.getPosts(feed_type,sortType.getType().value,page,25,null,null,false,accessToken);

        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(bestPost, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadSubredditPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> subredditPost;

        subredditPost = api.getPosts(null,sortType.getType().value,loadParams.getKey(),25,null,subredditOrUserName,false,null);


        ListenableFuture<LoadResult<Integer, Post>> pageFuture = Futures.transform(subredditPost, this::transformData, executor);

        ListenableFuture<LoadResult<Integer, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private ListenableFuture<LoadResult<Integer, Post>> loadUserPosts(@NonNull LoadParams<Integer> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> userPosts;
        userPosts = api.getUserPosts(subredditOrUserName, sortType.getType().value, loadParams.getKey(), 25, userWhere.equals(USER_WHERE_SAVED), accessToken);


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
    }

    private ListenableFuture<LoadResult<String, Post>> loadAnonymousHomePosts(@NonNull LoadParams<String> loadParams, LemmyAPI api) {
        ListenableFuture<Response<String>> anonymousHomePosts;
        anonymousHomePosts = api.getSubredditBestPostsListenableFuture(subredditOrUserName, sortType.getType(), sortType.getTime(), loadParams.getKey());

        ListenableFuture<LoadResult<String, Post>> pageFuture = Futures.transform(anonymousHomePosts, this::transformData, executor);

        ListenableFuture<LoadResult<String, Post>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }*/

    @Override
    public boolean getKeyReuseSupported() {
        //TODO: Figure out why this is needed
        return true;
    }
}
