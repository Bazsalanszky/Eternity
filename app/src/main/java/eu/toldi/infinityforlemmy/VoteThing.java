package eu.toldi.infinityforlemmy;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.CommentVoteDTO;
import eu.toldi.infinityforlemmy.dto.PostVoteDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by alex on 3/14/18.
 */

public class VoteThing {

    public static void votePost(Context context, final Retrofit retrofit, String accessToken,
                                final VoteThingListener voteThingListener, final int postID,
                                final int point, final int position) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> voteThingCall = api.postLike(new PostVoteDTO(postID, point, accessToken));
        voteThingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    voteThingListener.onVoteThingSuccess(position);
                } else {
                    voteThingListener.onVoteThingFail(position);
                    Toast.makeText(context, "Code " + response.code() + " Body: " + response.body(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                voteThingListener.onVoteThingFail(position);
                Toast.makeText(context, "Network error " + "Body: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void votePost(Context context, final Retrofit retrofit, String accessToken,
                                final VoteThingWithoutPositionListener voteThingWithoutPositionListener,
                                final int postID, final int point) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> voteThingCall = api.postLike(new PostVoteDTO(postID, point, accessToken));
        voteThingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    voteThingWithoutPositionListener.onVoteThingSuccess();
                } else {
                    voteThingWithoutPositionListener.onVoteThingFail();
                    Toast.makeText(context, "Code " + response.code() + " Body: " + response.body(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                voteThingWithoutPositionListener.onVoteThingFail();
                Toast.makeText(context, "Network error " + "Body: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void voteComment(Context context, final Retrofit retrofit, String accessToken,
                                   final VoteThingListener voteThingListener, final int commentId,
                                   final int point, final int position) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> voteThingCall = api.commentLike(new CommentVoteDTO(commentId, point, accessToken));
        voteThingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    voteThingListener.onVoteThingSuccess(position);
                } else {
                    voteThingListener.onVoteThingFail(position);
                    Toast.makeText(context, "Code " + response.code() + " Body: " + response.body(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                voteThingListener.onVoteThingFail(position);
                Toast.makeText(context, "Network error " + "Body: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void voteComment(Context context, final Retrofit retrofit, String accessToken,
                                   final VoteThingWithoutPositionListener voteThingWithoutPositionListener,
                                   final int commentId, final int point) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> voteThingCall = api.commentLike(new CommentVoteDTO(commentId, point, accessToken));
        voteThingCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    voteThingWithoutPositionListener.onVoteThingSuccess();
                } else {
                    voteThingWithoutPositionListener.onVoteThingFail();
                    Toast.makeText(context, "Code " + response.code() + " Body: " + response.body(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                voteThingWithoutPositionListener.onVoteThingFail();
                Toast.makeText(context, "Network error " + "Body: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface VoteThingListener {
        void onVoteThingSuccess(int position);

        void onVoteThingFail(int position);
    }

    public interface VoteThingWithoutPositionListener {
        void onVoteThingSuccess();

        void onVoteThingFail();
    }
}
