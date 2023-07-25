package eu.toldi.infinityforlemmy.comment;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.apis.RedditAPI;
import eu.toldi.infinityforlemmy.dto.CommentDTO;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SendComment {
    public static void sendComment(Executor executor, Handler handler, String commentMarkdown,
                                   Integer post_id, Integer parent_id,
                                   Retrofit retrofit, Account account,
                                   SendCommentListener sendCommentListener) {



        retrofit.create(LemmyAPI.class).postComment(new CommentDTO(commentMarkdown, post_id,parent_id, null,null,account.getAccessToken())).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseComment.parseSentComment(executor, handler, response.body(), new ParseComment.ParseSentCommentListener() {
                        @Override
                        public void onParseSentCommentSuccess(Comment comment) {
                            sendCommentListener.sendCommentSuccess(comment);
                        }

                        @Override
                        public void onParseSentCommentFailed(@Nullable String errorMessage) {
                            sendCommentListener.sendCommentFailed(errorMessage);
                        }
                    });
                } else {
                    sendCommentListener.sendCommentFailed(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                sendCommentListener.sendCommentFailed(t.getMessage());
            }
        });
    }

    public interface SendCommentListener {
        void sendCommentSuccess(Comment comment);

        void sendCommentFailed(String errorMessage);
    }
}
