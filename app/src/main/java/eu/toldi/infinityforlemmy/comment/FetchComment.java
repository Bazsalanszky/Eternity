package eu.toldi.infinityforlemmy.comment;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.commentfilter.CommentFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchComment {
    public static void fetchComments(Executor executor, Handler handler, Retrofit retrofit,
                                     @Nullable String accessToken, Integer article,
                                     Integer commentId, SortType.Type sortType, boolean expandChildren,
                                     Integer page, CommentFilter commentFilter, FetchCommentListener fetchCommentListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> comments;

        comments = api.getComments("All", sortType.value, 8, page, 25, null, null, article, commentId, false, accessToken);


        comments.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseComment.parseComments(executor, handler, response.body(), commentId,
                            expandChildren, commentFilter,
                            new ParseComment.ParseCommentListener() {
                                @Override
                                public void onParseCommentSuccess(ArrayList<Comment> topLevelComments,
                                                                  ArrayList<Comment> expandedComments,
                                                                  Integer parentId, ArrayList<Integer> moreChildrenIds) {
                                    fetchCommentListener.onFetchCommentSuccess(expandedComments, parentId,
                                            moreChildrenIds);
                                }

                                @Override
                                public void onParseCommentFailed() {
                                    fetchCommentListener.onFetchCommentFailed();
                                }
                            });
                } else {
                    fetchCommentListener.onFetchCommentFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchCommentListener.onFetchCommentFailed();
            }
        });
    }

    public static void fetchMoreComment(Executor executor, Handler handler, Retrofit retrofit,
                                        @Nullable String accessToken, int article,
                                        int commentId, SortType.Type sortType, boolean expandChildren,
                                        Integer page, FetchMoreCommentListener fetchMoreCommentListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> moreComments;

        moreComments = api.getComments("All", sortType.value, 8, page, 25, null, null, article, commentId, false, accessToken);


        moreComments.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseComment.parseMoreComment(executor, handler, response.body(),
                            expandChildren, new ParseComment.ParseCommentListener() {
                                @Override
                                public void onParseCommentSuccess(ArrayList<Comment> topLevelComments,
                                                                  ArrayList<Comment> expandedComments,
                                                                  Integer parentId, ArrayList<Integer> moreChildrenIds) {
                                    fetchMoreCommentListener.onFetchMoreCommentSuccess(
                                            topLevelComments, expandedComments, moreChildrenIds);
                                }

                                @Override
                                public void onParseCommentFailed() {
                                    fetchMoreCommentListener.onFetchMoreCommentFailed();
                                }
                            });
                } else {
                    fetchMoreCommentListener.onFetchMoreCommentFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchMoreCommentListener.onFetchMoreCommentFailed();
            }
        });
    }

    public static void fetchSingleComment(Retrofit retrofit, @Nullable String accessToken, int commentId,
                                          FetchCommentListener fetchCommentListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> comment = api.getComment(commentId, accessToken);
        comment.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Comment c = ParseComment.parseSingleComment(new JSONObject(response.body()).getJSONObject("comment_view"));
                        ArrayList<Comment> comments = new ArrayList<>();
                        comments.add(c);
                        fetchCommentListener.onFetchCommentSuccess(comments, null, null);
                    } catch (JSONException e) {
                        fetchCommentListener.onFetchCommentFailed();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchCommentListener.onFetchCommentFailed();
            }
        });
    }

    public interface FetchCommentListener {
        void onFetchCommentSuccess(ArrayList<Comment> expandedComments, Integer parentId, ArrayList<Integer> children);

        void onFetchCommentFailed();
    }

    public interface FetchMoreCommentListener {
        void onFetchMoreCommentSuccess(ArrayList<Comment> topLevelComments,
                                       ArrayList<Comment> expandedComments,
                                       ArrayList<Integer> moreChildrenIds);

        void onFetchMoreCommentFailed();
    }
}
