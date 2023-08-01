package eu.toldi.infinityforlemmy;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.DeleteCommentDTO;
import eu.toldi.infinityforlemmy.dto.DeletePostDTO;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeleteThing {

    public static void deletePost(Retrofit retrofit, int post_id, String accessToken, DeleteThingListener deleteThingListener) {
        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.ID_KEY, String.valueOf(post_id));
        retrofit.create(LemmyAPI.class).postDelete(new DeletePostDTO(post_id, true, accessToken)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    deleteThingListener.deleteSuccess();
                } else {
                    deleteThingListener.deleteFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                deleteThingListener.deleteFailed();
            }
        });
    }

    public static void deleteComment(Retrofit retrofit, int comment_id, String accessToken, DeleteThingListener deleteThingListener) {
        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.ID_KEY, String.valueOf(comment_id));
        retrofit.create(LemmyAPI.class).commentDelete(new DeleteCommentDTO(comment_id, true, accessToken)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    deleteThingListener.deleteSuccess();
                } else {
                    deleteThingListener.deleteFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                deleteThingListener.deleteFailed();
            }
        });
    }


    public interface DeleteThingListener {
        void deleteSuccess();

        void deleteFailed();
    }
}
