package eu.toldi.infinityforlemmy.message;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.AuthDTO;
import eu.toldi.infinityforlemmy.dto.ReadCommentDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReadMessage {
    public static void readMessage(Retrofit oauthRetrofit, String accessToken, int messageId,
                                   ReadMessageListener readMessageListener) {
        oauthRetrofit.create(LemmyAPI.class).commentMarkAsRead(new ReadCommentDTO(messageId, true, accessToken))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            readMessageListener.readSuccess();
                        } else {
                            readMessageListener.readFailed();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        readMessageListener.readFailed();
                    }
                });
    }

    public static void readAllMessages(Retrofit retrofit, String accessToken,
                                       ReadMessageListener readMessageListener) {
        retrofit.create(LemmyAPI.class).userMarkAllAsRead(new AuthDTO(accessToken))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            readMessageListener.readSuccess();
                        } else {
                            readMessageListener.readFailed();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        readMessageListener.readFailed();
                    }
                });
    }

    public interface ReadMessageListener {
        void readSuccess();

        void readFailed();
    }
}
