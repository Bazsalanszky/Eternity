package eu.toldi.infinityforlemmy.comment;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import eu.toldi.infinityforlemmy.NetworkState;
import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentDataSource extends PageKeyedDataSource<Integer, Comment> {

    private Retrofit retrofit;
    private Locale locale;
    @Nullable
    private String accessToken;
    private String username;
    private SortType sortType;
    private boolean areSavedComments;

    private MutableLiveData<NetworkState> paginationNetworkStateLiveData;
    private MutableLiveData<NetworkState> initialLoadStateLiveData;
    private MutableLiveData<Boolean> hasPostLiveData;

    private LoadParams<Integer> params;
    private LoadCallback<Integer, Comment> callback;

    CommentDataSource(Retrofit retrofit, Locale locale, @Nullable String accessToken, String username, SortType sortType,
                      boolean areSavedComments) {
        this.retrofit = retrofit;
        this.locale = locale;
        this.accessToken = accessToken;
        this.username = username;
        this.sortType = sortType;
        this.areSavedComments = areSavedComments;
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
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Comment> callback) {
        initialLoadStateLiveData.postValue(NetworkState.LOADING);

        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> commentsCall = api.getUserComments(username, sortType.getType().value, 1, 25, areSavedComments, accessToken);

        commentsCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    new ParseCommentAsyncTask(response.body(), locale, new ParseCommentAsyncTask.ParseCommentAsyncTaskListener() {
                        @Override
                        public void parseSuccessful(ArrayList<Comment> comments, Integer after) {
                            if (comments.isEmpty()) {
                                callback.onResult(comments, null, null);
                                hasPostLiveData.postValue(false);
                            } else {
                                hasPostLiveData.postValue(true);
                                callback.onResult(comments, null, 2);
                            }

                            initialLoadStateLiveData.postValue(NetworkState.LOADED);
                        }

                        @Override
                        public void parseFailed() {
                            initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error parsing data"));
                        }
                    }).execute();
                } else {
                    initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error parsing data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error parsing data"));
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Comment> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Comment> callback) {
        this.params = params;
        this.callback = callback;

        paginationNetworkStateLiveData.postValue(NetworkState.LOADING);

        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> commentsCall = api.getUserComments(username, sortType.getType().value, params.key, 25, areSavedComments, accessToken);
        commentsCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    new ParseCommentAsyncTask(response.body(), locale, new ParseCommentAsyncTask.ParseCommentAsyncTaskListener() {
                        @Override
                        public void parseSuccessful(ArrayList<Comment> comments, Integer after) {
                            if (comments.isEmpty()) {
                                callback.onResult(comments, null);
                            } else {
                                callback.onResult(comments, after);
                            }
                            paginationNetworkStateLiveData.postValue(NetworkState.LOADED);
                        }

                        @Override
                        public void parseFailed() {
                            paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error parsing data"));
                        }
                    }).execute();
                } else {
                    paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetching data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error fetching data"));
            }
        });
    }

    private static class ParseCommentAsyncTask extends AsyncTask<Void, ArrayList<Comment>, ArrayList<Comment>> {
        private Integer after;
        private Locale locale;
        private JSONArray commentsJSONArray;
        private boolean parseFailed;
        private ParseCommentAsyncTaskListener parseCommentAsyncTaskListener;

        ParseCommentAsyncTask(String response, Locale locale, ParseCommentAsyncTaskListener parseCommentAsyncTaskListener) {
            this.locale = locale;
            this.parseCommentAsyncTaskListener = parseCommentAsyncTaskListener;
            try {
                JSONObject data = new JSONObject(response);
                commentsJSONArray = data.getJSONArray("comments");
                parseFailed = false;
            } catch (JSONException e) {
                parseFailed = true;
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<Comment> doInBackground(Void... voids) {
            if (parseFailed) {
                return null;
            }

            ArrayList<Comment> comments = new ArrayList<>();
            for (int i = 0; i < commentsJSONArray.length(); i++) {
                try {
                    JSONObject commentJSON = commentsJSONArray.getJSONObject(i);
                    comments.add(ParseComment.parseSingleComment(commentJSON));
                } catch (JSONException ignored) {
                }
            }
            return comments;
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> commentData) {
            super.onPostExecute(commentData);
            if (commentData != null) {
                parseCommentAsyncTaskListener.parseSuccessful(commentData, after);
            } else {
                parseCommentAsyncTaskListener.parseFailed();
            }
        }

        interface ParseCommentAsyncTaskListener {
            void parseSuccessful(ArrayList<Comment> comments, Integer page);

            void parseFailed();
        }
    }
}
