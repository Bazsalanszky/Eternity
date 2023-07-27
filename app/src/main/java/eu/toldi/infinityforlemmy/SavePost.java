package eu.toldi.infinityforlemmy;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.SavePostDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SavePost implements SaveThing {

    @Override
    public void saveThing(Retrofit retrofit, String accessToken, int id, SaveThingListener saveThingListener) {


        retrofit.create(LemmyAPI.class).postSave(new SavePostDTO(id, true, accessToken)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    saveThingListener.success();
                } else {
                    saveThingListener.failed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                saveThingListener.failed();
            }
        });

    }

    @Override
    public void unsaveThing(Retrofit retrofit, String accessToken, int id, SaveThingListener saveThingListener) {

        retrofit.create(LemmyAPI.class).postSave(new SavePostDTO(id, false, accessToken)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    saveThingListener.success();
                } else {
                    saveThingListener.failed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                saveThingListener.failed();
            }
        });
    }
}
