package eu.toldi.infinityforlemmy;

import retrofit2.Retrofit;

public interface SaveThing {
    void saveThing(Retrofit oauthRetrofit, String accessToken, int id,
                   SaveThingListener saveThingListener);

    void unsaveThing(Retrofit oauthRetrofit, String accessToken, int id,
                     SaveThingListener saveThingListener);

    public interface SaveThingListener {
        void success();

        void failed();
    }
}
