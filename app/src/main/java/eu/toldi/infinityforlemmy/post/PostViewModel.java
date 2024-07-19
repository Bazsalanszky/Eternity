package eu.toldi.infinityforlemmy.post;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingDataTransforms;
import androidx.paging.PagingLiveData;
import androidx.paging.PagingSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.multicommunity.MulticommunityPagingSource;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class PostViewModel extends ViewModel {
    private Executor executor;
    private RetrofitHolder retrofit;
    private String accessToken;
    private String accountName;
    private SharedPreferences sharedPreferences;
    private SharedPreferences postFeedScrolledPositionSharedPreferences;
    private String name;
    private String query;
    private String trendingSource;
    private int postType;
    private SortType sortType;
    private PostFilter postFilter;
    private String userWhere;
    private List<String> readPostList;
    private PostEnricher postEnricher;
    private MutableLiveData<Boolean> currentlyReadPostIdsLiveData = new MutableLiveData<>();

    private LiveData<PagingData<Post>> posts;
    private LiveData<PagingData<Post>> postsWithReadPostsHidden;

    private MutableLiveData<SortType> sortTypeLiveData;
    private MutableLiveData<PostFilter> postFilterLiveData;
    private SortTypeAndPostFilterLiveData sortTypeAndPostFilterLiveData;

    public PostViewModel(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                         SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                         @Nullable SharedPreferences postHistorySharedPreferences, int postType,
                         SortType sortType, PostFilter postFilter, List<String> readPostList, String option, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.postType = postType;
        this.sortType = sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.name = option;
        this.postEnricher = postEnricher;

        sortTypeLiveData = new MutableLiveData<>(sortType);
        postFilterLiveData = new MutableLiveData<>(postFilter);

        sortTypeAndPostFilterLiveData = new SortTypeAndPostFilterLiveData(sortTypeLiveData, postFilterLiveData);

        Pager<Integer, Post> pager = new Pager<>(new PagingConfig(25, 25, false), this::returnPagingSoruce);

        posts = Transformations.switchMap(sortTypeAndPostFilterLiveData, sortAndPostFilter -> {
            changeSortTypeAndPostFilter(
                    sortTypeLiveData.getValue(), postFilterLiveData.getValue());
            return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), ViewModelKt.getViewModelScope(this));
        });

        postsWithReadPostsHidden = PagingLiveData.cachedIn(Transformations.switchMap(currentlyReadPostIdsLiveData,
                currentlyReadPostIds -> Transformations.map(
                        posts,
                        postPagingData -> PagingDataTransforms.filter(
                                postPagingData, executor,
                                post -> !post.isRead() || !currentlyReadPostIdsLiveData.getValue()))), ViewModelKt.getViewModelScope(this));

        currentlyReadPostIdsLiveData.setValue(postHistorySharedPreferences != null
                && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.HIDE_READ_POSTS_AUTOMATICALLY_BASE, false));
    }

    public PostViewModel(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                         SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                         @Nullable SharedPreferences postHistorySharedPreferences, String subredditName, int postType,
                         SortType sortType, PostFilter postFilter, List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.postType = postType;
        this.sortType = sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.name = subredditName;
        this.postEnricher = postEnricher;

        sortTypeLiveData = new MutableLiveData<>(sortType);
        postFilterLiveData = new MutableLiveData<>(postFilter);

        sortTypeAndPostFilterLiveData = new SortTypeAndPostFilterLiveData(sortTypeLiveData, postFilterLiveData);

        Pager<Integer, Post> pager = new Pager<>(new PagingConfig(25, 25, false), this::returnPagingSoruce);

        posts = Transformations.switchMap(sortTypeAndPostFilterLiveData, sortAndPostFilter -> {
            changeSortTypeAndPostFilter(
                    sortTypeLiveData.getValue(), postFilterLiveData.getValue());
            return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), ViewModelKt.getViewModelScope(this));
        });

        postsWithReadPostsHidden = PagingLiveData.cachedIn(Transformations.switchMap(currentlyReadPostIdsLiveData,
                currentlyReadPostIds -> Transformations.map(
                        posts,
                        postPagingData -> PagingDataTransforms.filter(
                                postPagingData, executor,
                                post -> !post.isRead() || !currentlyReadPostIdsLiveData.getValue()))), ViewModelKt.getViewModelScope(this));

        currentlyReadPostIdsLiveData.setValue(postHistorySharedPreferences != null
                && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.HIDE_READ_POSTS_AUTOMATICALLY_BASE, false));
    }

    public PostViewModel(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                         SharedPreferences sharedPreferences,
                         SharedPreferences postFeedScrolledPositionSharedPreferences,
                         @Nullable SharedPreferences postHistorySharedPreferences, String username,
                         int postType, SortType sortType, PostFilter postFilter, String userWhere,
                         List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.postType = postType;
        this.sortType = sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.name = username;
        this.userWhere = userWhere;
        this.postEnricher = postEnricher;

        sortTypeLiveData = new MutableLiveData<>(sortType);
        postFilterLiveData = new MutableLiveData<>(postFilter);

        sortTypeAndPostFilterLiveData = new SortTypeAndPostFilterLiveData(sortTypeLiveData, postFilterLiveData);

        Pager<Integer, Post> pager = new Pager<>(new PagingConfig(25, 25, false), this::returnPagingSoruce);

        posts = Transformations.switchMap(sortTypeAndPostFilterLiveData, sortAndPostFilter -> {
            changeSortTypeAndPostFilter(
                    sortTypeLiveData.getValue(), postFilterLiveData.getValue());
            return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), ViewModelKt.getViewModelScope(this));
        });

        postsWithReadPostsHidden = PagingLiveData.cachedIn(Transformations.switchMap(currentlyReadPostIdsLiveData,
                currentlyReadPostIds -> Transformations.map(
                        posts,
                        postPagingData -> PagingDataTransforms.filter(
                                postPagingData, executor,
                                post -> !post.isRead() || !currentlyReadPostIdsLiveData.getValue()))), ViewModelKt.getViewModelScope(this));
        boolean dontHideSaved = postHistorySharedPreferences != null && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.DONT_HIDE_SAVED_READ_POSTS, false) && userWhere.equals(PostPagingSource.USER_WHERE_SAVED);
        boolean dontHideOwn = postHistorySharedPreferences != null && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.DONT_HIDE_OWN_READ_POSTS, false) && name.equals(accountName);
        currentlyReadPostIdsLiveData.setValue(postHistorySharedPreferences != null
                && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.HIDE_READ_POSTS_AUTOMATICALLY_BASE, false) && !dontHideSaved && !dontHideOwn);
    }

    public PostViewModel(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                         SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                         @Nullable SharedPreferences postHistorySharedPreferences, String subredditName, String query,
                         String trendingSource, int postType, SortType sortType, PostFilter postFilter,
                         List<String> readPostList, PostEnricher postEnricher) {
        this.executor = executor;
        this.retrofit = retrofit;
        this.accessToken = accessToken;
        this.accountName = accountName;
        this.sharedPreferences = sharedPreferences;
        this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
        this.postType = postType;
        this.sortType = sortType;
        this.postFilter = postFilter;
        this.readPostList = readPostList;
        this.name = subredditName;
        this.query = query;
        this.trendingSource = trendingSource;
        this.postEnricher = postEnricher;

        sortTypeLiveData = new MutableLiveData<>(sortType);
        postFilterLiveData = new MutableLiveData<>(postFilter);

        sortTypeAndPostFilterLiveData = new SortTypeAndPostFilterLiveData(sortTypeLiveData, postFilterLiveData);

        Pager<Integer, Post> pager = new Pager<>(new PagingConfig(25, 25, false), this::returnPagingSoruce);

        posts = Transformations.switchMap(sortTypeAndPostFilterLiveData, sortAndPostFilter -> {
            changeSortTypeAndPostFilter(
                    sortTypeLiveData.getValue(), postFilterLiveData.getValue());
            return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), ViewModelKt.getViewModelScope(this));
        });

        postsWithReadPostsHidden = PagingLiveData.cachedIn(Transformations.switchMap(currentlyReadPostIdsLiveData,
                currentlyReadPostIds -> Transformations.map(
                        posts,
                        postPagingData -> PagingDataTransforms.filter(
                                postPagingData, executor,
                                post -> !post.isRead() || !currentlyReadPostIdsLiveData.getValue()))), ViewModelKt.getViewModelScope(this));

        currentlyReadPostIdsLiveData.setValue(postHistorySharedPreferences != null
                && postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.HIDE_READ_POSTS_AUTOMATICALLY_BASE, false));
    }

    public LiveData<PagingData<Post>> getPosts() {
        return postsWithReadPostsHidden;
    }

    public void hideReadPosts() {
        currentlyReadPostIdsLiveData.setValue(true);
    }

    public PagingSource returnPagingSoruce() {
        PagingSource paging3PagingSource;
        switch (postType) {
            case PostPagingSource.TYPE_FRONT_PAGE:
                paging3PagingSource = new PostPagingSource(executor, retrofit, accessToken, accountName,
                        sharedPreferences, postFeedScrolledPositionSharedPreferences, postType, sortType,
                        postFilter, readPostList, name, postEnricher);
                break;
            case PostPagingSource.TYPE_SUBREDDIT:
                paging3PagingSource = new PostPagingSource(executor, retrofit, accessToken, accountName,
                        sharedPreferences, postFeedScrolledPositionSharedPreferences, name, postType,
                        sortType, postFilter, readPostList, postEnricher);
                break;
            case PostPagingSource.TYPE_MULTI_REDDIT:
            case PostPagingSource.TYPE_ANONYMOUS_FRONT_PAGE:
                String[] communities = (name != null) ? name.split(Pattern.quote(",")) : new String[0];
                paging3PagingSource = new MulticommunityPagingSource(retrofit.getRetrofit().create(LemmyAPI.class), List.of(communities), accessToken,
                        sortType, executor, postFilter, (readPostList != null) ? readPostList : new ArrayList<>(), postEnricher);
                break;
            case PostPagingSource.TYPE_SEARCH:
                paging3PagingSource = new PostPagingSource(executor, retrofit, accessToken, accountName,
                        sharedPreferences, postFeedScrolledPositionSharedPreferences, name, query, trendingSource,
                        postType, sortType, postFilter, readPostList, postEnricher);
                break;
            default:
                //User
                paging3PagingSource = new PostPagingSource(executor, retrofit, accessToken, accountName,
                        sharedPreferences, postFeedScrolledPositionSharedPreferences, name, postType,
                        sortType, postFilter, userWhere, readPostList, postEnricher);
                break;
        }
        return paging3PagingSource;
    }

    private void changeSortTypeAndPostFilter(SortType sortType, PostFilter postFilter) {
        this.sortType = sortType;
        this.postFilter = postFilter;
    }

    public void changeSortType(SortType sortType) {
        sortTypeLiveData.postValue(sortType);
    }

    public void changePostFilter(PostFilter postFilter) {
        postFilterLiveData.postValue(postFilter);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Executor executor;
        private RetrofitHolder retrofit;
        private String accessToken;
        private String accountName;
        private SharedPreferences sharedPreferences;
        private SharedPreferences postFeedScrolledPositionSharedPreferences;
        private SharedPreferences postHistorySharedPreferences;
        private String name;
        private String query;
        private String trendingSource;
        private int postType;
        private SortType sortType;
        private PostFilter postFilter;
        private String userWhere;
        private List<String> readPostList;
        private PostEnricher postEnricher;

        public Factory(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                       SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                       SharedPreferences postHistorySharedPreferences, int postType, SortType sortType,
                       PostFilter postFilter, List<String> readPostList, String option, PostEnricher postEnricher) {
            this.executor = executor;
            this.retrofit = retrofit;
            this.accessToken = accessToken;
            this.accountName = accountName;
            this.sharedPreferences = sharedPreferences;
            this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
            this.postHistorySharedPreferences = postHistorySharedPreferences;
            this.postType = postType;
            this.sortType = sortType;
            this.postFilter = postFilter;
            this.readPostList = readPostList;
            this.name = option;
            this.postEnricher = postEnricher;
        }

        public Factory(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                       SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                       SharedPreferences postHistorySharedPreferences, String name, int postType, SortType sortType,
                       PostFilter postFilter, List<String> readPostList, PostEnricher postEnricher) {
            this.executor = executor;
            this.retrofit = retrofit;
            this.accessToken = accessToken;
            this.accountName = accountName;
            this.sharedPreferences = sharedPreferences;
            this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
            this.postHistorySharedPreferences = postHistorySharedPreferences;
            this.name = name;
            this.postType = postType;
            this.sortType = sortType;
            this.postFilter = postFilter;
            this.readPostList = readPostList;
            this.postEnricher = postEnricher;
        }

        //User posts
        public Factory(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                       SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                       SharedPreferences postHistorySharedPreferences, String username, int postType,
                       SortType sortType, PostFilter postFilter, String where, List<String> readPostList,
                       PostEnricher postEnricher) {
            this.executor = executor;
            this.retrofit = retrofit;
            this.accessToken = accessToken;
            this.accountName = accountName;
            this.sharedPreferences = sharedPreferences;
            this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
            this.postHistorySharedPreferences = postHistorySharedPreferences;
            this.name = username;
            this.postType = postType;
            this.sortType = sortType;
            this.postFilter = postFilter;
            userWhere = where;
            this.readPostList = readPostList;
            this.postEnricher = postEnricher;
        }

        public Factory(Executor executor, RetrofitHolder retrofit, String accessToken, String accountName,
                       SharedPreferences sharedPreferences, SharedPreferences postFeedScrolledPositionSharedPreferences,
                       SharedPreferences postHistorySharedPreferences, String name, String query, String trendingSource,
                       int postType, SortType sortType, PostFilter postFilter, List<String> readPostList,
                       PostEnricher postEnricher) {
            this.executor = executor;
            this.retrofit = retrofit;
            this.accessToken = accessToken;
            this.accountName = accountName;
            this.sharedPreferences = sharedPreferences;
            this.postFeedScrolledPositionSharedPreferences = postFeedScrolledPositionSharedPreferences;
            this.postHistorySharedPreferences = postHistorySharedPreferences;
            this.name = name;
            this.query = query;
            this.trendingSource = trendingSource;
            this.postType = postType;
            this.sortType = sortType;
            this.postFilter = postFilter;
            this.readPostList = readPostList;
            this.postEnricher = postEnricher;
        }

        //Anonymous Front Page
        public Factory(Executor executor, RetrofitHolder retrofit, SharedPreferences sharedPreferences,
                       String concatenatedSubredditNames, int postType, SortType sortType, PostFilter postFilter,
                       String opt, PostEnricher postEnricher) {
            this.executor = executor;
            this.retrofit = retrofit;
            this.sharedPreferences = sharedPreferences;

            this.name = concatenatedSubredditNames;
            this.postType = postType;
            this.sortType = sortType;
            this.postFilter = postFilter;

            this.postEnricher = postEnricher;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (postType == PostPagingSource.TYPE_FRONT_PAGE) {
                return (T) new PostViewModel(executor, retrofit, accessToken, accountName, sharedPreferences,
                        postFeedScrolledPositionSharedPreferences, postHistorySharedPreferences, postType,
                        sortType, postFilter, readPostList,name, postEnricher);
            } else if (postType == PostPagingSource.TYPE_SEARCH) {
                return (T) new PostViewModel(executor, retrofit, accessToken, accountName, sharedPreferences,
                        postFeedScrolledPositionSharedPreferences, postHistorySharedPreferences, name, query,
                        trendingSource, postType, sortType, postFilter, readPostList, postEnricher);
            } else if (postType == PostPagingSource.TYPE_SUBREDDIT || postType == PostPagingSource.TYPE_MULTI_REDDIT) {
                return (T) new PostViewModel(executor, retrofit, accessToken, accountName, sharedPreferences,
                        postFeedScrolledPositionSharedPreferences, postHistorySharedPreferences, name,
                        postType, sortType, postFilter, readPostList, postEnricher);
            } else if (postType == PostPagingSource.TYPE_ANONYMOUS_FRONT_PAGE) {
                return (T) new PostViewModel(executor, retrofit, null, null, sharedPreferences,
                        null, null, name, postType, sortType,
                        postFilter, null, postEnricher);
            } else {
                return (T) new PostViewModel(executor, retrofit, accessToken, accountName, sharedPreferences,
                        postFeedScrolledPositionSharedPreferences, postHistorySharedPreferences, name,
                        postType, sortType, postFilter, userWhere, readPostList, postEnricher);
            }
        }
    }

    private static class SortTypeAndPostFilterLiveData extends MediatorLiveData<Pair<PostFilter, SortType>> {
        public SortTypeAndPostFilterLiveData(LiveData<SortType> sortTypeLiveData, LiveData<PostFilter> postFilterLiveData) {
            addSource(sortTypeLiveData, sortType -> setValue(Pair.create(postFilterLiveData.getValue(), sortType)));
            addSource(postFilterLiveData, postFilter -> setValue(Pair.create(postFilter, sortTypeLiveData.getValue())));
        }
    }
}
