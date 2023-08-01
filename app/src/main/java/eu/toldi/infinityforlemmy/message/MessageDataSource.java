package eu.toldi.infinityforlemmy.message;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.toldi.infinityforlemmy.NetworkState;
import retrofit2.Retrofit;

class MessageDataSource extends PageKeyedDataSource<Integer, CommentInteraction> {
    private Retrofit retrofit;
    private Locale locale;
    private String accessToken;
    private String where;
    private int messageType;

    private MutableLiveData<NetworkState> paginationNetworkStateLiveData;
    private MutableLiveData<NetworkState> initialLoadStateLiveData;
    private MutableLiveData<Boolean> hasPostLiveData;

    private LoadParams<Integer> params;
    private LoadCallback<Integer, CommentInteraction> callback;

    private int page = 0;

    MessageDataSource(Retrofit oauthRetrofit, Locale locale, String accessToken, String where) {
        this.retrofit = oauthRetrofit;
        this.locale = locale;
        this.accessToken = accessToken;
        this.where = where;
        if (where.equals(FetchMessage.WHERE_MESSAGES)) {
            messageType = FetchMessage.MESSAGE_TYPE_PRIVATE_MESSAGE;
        } else if (where.equals(FetchMessage.WHERE_REPLIES)) {
            messageType = FetchMessage.MESSAGE_TYPE_REPLIES;
        } else {
            messageType = FetchMessage.MESSAGE_TYPE_MENTIONS;
        }
        paginationNetworkStateLiveData = new MutableLiveData<>();
        initialLoadStateLiveData = new MutableLiveData<>();
        hasPostLiveData = new MutableLiveData<>();
    }

    MutableLiveData<NetworkState> getPaginationNetworkStateLiveData() {
        return paginationNetworkStateLiveData;
    }

    MutableLiveData<NetworkState> getInitialLoadStateLiveData() {
        return initialLoadStateLiveData;
    }

    MutableLiveData<Boolean> hasPostLiveData() {
        return hasPostLiveData;
    }

    void retryLoadingMore() {
        loadAfter(params, callback);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, CommentInteraction> callback) {
        initialLoadStateLiveData.postValue(NetworkState.LOADING);
        if (messageType == FetchMessage.MESSAGE_TYPE_REPLIES) {
            FetchCommentInteractions.fetchReplies(retrofit, 1, false, accessToken, new FetchCommentInteractions.FetchCommentInteractionsListener() {
                @Override
                public void fetchSuccess(List<CommentInteraction> commentInteractions) {
                    hasPostLiveData.postValue(true);
                    if (commentInteractions.size() == 0) {
                        callback.onResult(commentInteractions, null, null);
                    } else {
                        callback.onResult(commentInteractions, null, 2);
                    }

                    initialLoadStateLiveData.postValue(NetworkState.LOADED);
                }

                @Override
                public void fetchFailed() {
                    initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetch messages"));
                }
            });
        } else if (messageType == FetchMessage.MESSAGE_TYPE_MENTIONS) {
            FetchCommentInteractions.fetchMentions(retrofit, 1, false, accessToken, new FetchCommentInteractions.FetchCommentInteractionsListener() {
                @Override
                public void fetchSuccess(List<CommentInteraction> commentInteractions) {
                    hasPostLiveData.postValue(true);
                    if (commentInteractions.size() == 0) {
                        callback.onResult(commentInteractions, null, null);
                    } else {
                        callback.onResult(commentInteractions, null, 2);
                    }

                    initialLoadStateLiveData.postValue(NetworkState.LOADED);
                }

                @Override
                public void fetchFailed() {
                    initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetch messages"));
                }
            });
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, CommentInteraction> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, CommentInteraction> callback) {
        this.params = params;
        this.callback = callback;

        paginationNetworkStateLiveData.postValue(NetworkState.LOADING);
        if (messageType == FetchMessage.MESSAGE_TYPE_REPLIES) {
            FetchCommentInteractions.fetchReplies(retrofit, params.key, false, accessToken, new FetchCommentInteractions.FetchCommentInteractionsListener() {
                @Override
                public void fetchSuccess(List<CommentInteraction> commentInteractions) {
                    hasPostLiveData.postValue(true);
                    if (commentInteractions.size() == 0) {
                        callback.onResult(new ArrayList<>(), null);
                    } else {
                        callback.onResult(commentInteractions, params.key + 1);
                    }

                    paginationNetworkStateLiveData.postValue(NetworkState.LOADED);
                }

                @Override
                public void fetchFailed() {
                    paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetch messages"));
                }
            });
        } else if (messageType == FetchMessage.MESSAGE_TYPE_MENTIONS) {
            FetchCommentInteractions.fetchMentions(retrofit, params.key, false, accessToken, new FetchCommentInteractions.FetchCommentInteractionsListener() {
                @Override
                public void fetchSuccess(List<CommentInteraction> commentInteractions) {
                    hasPostLiveData.postValue(true);
                    if (commentInteractions.size() == 0) {
                        callback.onResult(new ArrayList<>(), null);
                    } else {
                        callback.onResult(commentInteractions, params.key + 1);
                    }
                    paginationNetworkStateLiveData.postValue(NetworkState.LOADED);
                }

                @Override
                public void fetchFailed() {
                    paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetch messages"));
                }
            });
        }
    }
}
