package eu.toldi.infinityforlemmy.user;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.UserBlockDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BlockUser {
    public interface BlockUserListener {
        void success();

        void failed();
    }

    public static void blockUser(Retrofit retrofit, String accessToken, int userID, boolean block, BlockUserListener blockUserListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);
        api.userBlock(new UserBlockDTO(userID, block, accessToken)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    blockUserListener.success();
                } else {
                    blockUserListener.failed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                blockUserListener.failed();
            }
        });
    }
}
