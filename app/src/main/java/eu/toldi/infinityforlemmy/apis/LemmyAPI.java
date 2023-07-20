package eu.toldi.infinityforlemmy.apis;

import com.google.common.util.concurrent.ListenableFuture;

import eu.toldi.infinityforlemmy.dto.AccountLoginDTO;
import eu.toldi.infinityforlemmy.dto.VoteDTO;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LemmyAPI {
    @Headers("Content-Type: application/json")
    @POST("api/v3/user/login")
    Call<String> userLogin(@Body AccountLoginDTO params);

    @GET("api/v3/user")
    Call<String> userInfo(@Query("username") String username,@Query("auth") String access_token);

    @GET("api/v3/community")
    Call<String> communityInfo(@Query("name") String name,@Query("auth") String access_token);

    @GET("api/v3/user")
    ListenableFuture<Response<String>> getUserPosts(
            @Query("username") String username,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("auth") String access_token);

    @GET("api/v3/community/list")
    Call<String> listCommunities(
            @Query("type_") String type_,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("auth") String auth
    );

    @GET("api/v3/post/list")
    ListenableFuture<Response<String>> getPosts(
            @Query("type_") String type_,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("community_id") Integer community_id,
            @Query("community_name") String community_name,
            @Query("saved_only") Boolean saved_only,
            @Query("auth") String auth
    );

    @Headers("Content-Type: application/json")
    @POST("api/v3/post/like")
    Call<String> postLike(@Body VoteDTO params);
}
