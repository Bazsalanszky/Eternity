package eu.toldi.infinityforlemmy.apis;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RedgifsAPI {
    @GET("/v2/gifs/{id}")
    Call<String> getRedgifsData(@Path("id") String id);

    @FormUrlEncoded
    @POST("/v2/oauth/client")
    Call<String> getRedgifsAccessToken(@FieldMap Map<String, String> params);
}
