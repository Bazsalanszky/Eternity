package eu.toldi.infinityforlemmy.comment;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.apis.RedditAPI;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchComment {
    public static void fetchComments(Executor executor, Handler handler, Retrofit retrofit,
                                     @Nullable String accessToken, Integer article,
                                     Integer commentId, SortType.Type sortType, boolean expandChildren,
                                     Integer page, FetchCommentListener fetchCommentListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        Call<String> comments;

        comments = api.getComments("All", sortType.value, 5, page, 25, null, null, article, commentId, false, accessToken);


        comments.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseComment.parseComments(executor, handler, response.body(),
                            expandChildren, new ParseComment.ParseCommentListener() {
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
                                        @Nullable String accessToken,
                                        ArrayList<Integer> allChildren,
                                        boolean expandChildren, String postFullName,
                                        SortType.Type sortType,
                                        FetchMoreCommentListener fetchMoreCommentListener) {
        if (allChildren == null) {
            return;
        }

        String childrenIds = "";

        if (childrenIds.isEmpty()) {
            return;
        }

        RedditAPI api = retrofit.create(RedditAPI.class);
        Call<String> moreComments;
        if (accessToken == null) {
            moreComments = api.moreChildren(postFullName, childrenIds, sortType);
        } else {
            moreComments = api.moreChildrenOauth(postFullName, childrenIds,
                    sortType, APIUtils.getOAuthHeader(accessToken));
        }

        moreComments.enqueue(new Callback<String>() {
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
