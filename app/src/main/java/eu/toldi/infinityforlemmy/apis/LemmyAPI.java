package eu.toldi.infinityforlemmy.apis;

import androidx.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;

import eu.toldi.infinityforlemmy.dto.AccountLoginDTO;
import eu.toldi.infinityforlemmy.dto.AuthDTO;
import eu.toldi.infinityforlemmy.dto.BlockCommunityDTO;
import eu.toldi.infinityforlemmy.dto.BlockInstanceDTO;
import eu.toldi.infinityforlemmy.dto.CommentDTO;
import eu.toldi.infinityforlemmy.dto.CommentVoteDTO;
import eu.toldi.infinityforlemmy.dto.DeleteCommentDTO;
import eu.toldi.infinityforlemmy.dto.DeletePostDTO;
import eu.toldi.infinityforlemmy.dto.EditCommentDTO;
import eu.toldi.infinityforlemmy.dto.EditPostDTO;
import eu.toldi.infinityforlemmy.dto.FollowCommunityDTO;
import eu.toldi.infinityforlemmy.dto.PostVoteDTO;
import eu.toldi.infinityforlemmy.dto.PrivateMessageDTO;
import eu.toldi.infinityforlemmy.dto.PrivateMessageReadDTO;
import eu.toldi.infinityforlemmy.dto.PrivateMessageReportDTO;
import eu.toldi.infinityforlemmy.dto.PrivateMessageUpdateDTO;
import eu.toldi.infinityforlemmy.dto.ReadCommentDTO;
import eu.toldi.infinityforlemmy.dto.ReadMessageDTO;
import eu.toldi.infinityforlemmy.dto.ReadPostDTO;
import eu.toldi.infinityforlemmy.dto.ReportCommentDTO;
import eu.toldi.infinityforlemmy.dto.ReportPostDTO;
import eu.toldi.infinityforlemmy.dto.SaveCommentDTO;
import eu.toldi.infinityforlemmy.dto.SavePostDTO;
import eu.toldi.infinityforlemmy.dto.SubmitPostDTO;
import eu.toldi.infinityforlemmy.dto.UserBlockDTO;
import eu.toldi.infinityforlemmy.message.MessageCount;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface LemmyAPI {
    @Headers("Content-Type: application/json")
    @POST("api/v3/user/login")
    Call<String> userLogin(@Body AccountLoginDTO params);

    @GET("api/v3/user")
    Call<String> userInfo(@Query("username") String username, @Query("auth") String access_token);

    @Headers("Content-Type: application/json")
    @POST("api/v3/user/block")
    Call<String> userBlock(@Body UserBlockDTO params);

    @GET("api/v3/user/mention")
    Call<String> userMentions(@Query("sort") String sort, @Query("page") Integer page, @Query("limit") Integer limit, @Query("unread_only") boolean unread_only, @Query("auth") String access_token);

    @GET("api/v3/user/replies")
    Call<String> userReplies(@Query("sort") String sort, @Query("page") Integer page, @Query("limit") Integer limit, @Query("unread_only") boolean unread_only, @Query("auth") String access_token);

    @GET("api/v3/private_message/list")
    Call<String> privateMessageList(@Query("page") Integer page, @Query("limit") Integer limit, @Query("unread_only") boolean unread_only, @Query("auth") String access_token);

    @GET("api/v3/user/unread_count")
    Call<MessageCount> userUnreadCount(@NonNull @Query("auth") String access_token);

    @GET("api/v3/user/validate_auth")
    Call<String> userValidateAuth();

    @Headers("Content-Type: application/json")
    @POST("api/v3/user/mention/mark_as_read")
    Call<String> userMentionMarkAsRead(@Body ReadMessageDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/comment/mark_as_read")
    Call<String> commentMarkAsRead(@Body ReadCommentDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/user/mark_all_as_read")
    Call<String> userMarkAllAsRead(@Body AuthDTO params);

    @GET("api/v3/community")
    Call<String> communityInfo(@Query("name") String name, @Query("auth") String access_token);

    @GET("api/v3/post")
    Call<String> postInfo(@Query("id") Integer postID, @Query("comment_id") Integer comment_id, @Query("auth") String access_token);

    @Headers("Content-Type: application/json")
    @POST("api/v3/post")
    Call<String> postCreate(@Body SubmitPostDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/post/report")
    Call<String> postReport(@Body ReportPostDTO params);

    @Headers("Content-Type: application/json")
    @PUT("api/v3/post")
    Call<String> postUpdate(@Body EditPostDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/post/delete")
    Call<String> postDelete(@Body DeletePostDTO params);

    @GET("api/v3/user")
    ListenableFuture<Response<String>> getUserPostsListenableFuture(
            @Query("username") String username,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("saved_only") Boolean saved_only,
            @Query("auth") String access_token);

    @GET("api/v3/user")
    Call<String> getUserPosts(
            @Query("username") String username,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("saved_only") Boolean saved_only,
            @Query("auth") String access_token);

    @GET("api/v3/user")
    Call<String> getUserComments(
            @Query("username") String username,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("saved_only") Boolean saved_only,
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
    ListenableFuture<Response<String>> getPostsListenableFuture(
            @Query("type_") String type_,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("community_id") Integer community_id,
            @Query("community_name") String community_name,
            @Query("saved_only") Boolean saved_only,
            @Query("auth") String auth
    );

    @GET("api/v3/post/list")
    Call<String> getPosts(
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
    Call<String> postLike(@Body PostVoteDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/comment/like")
    Call<String> commentLike(@Body CommentVoteDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/comment/report")
    Call<String> commentReport(@Body ReportCommentDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/community/follow")
    Call<String> communityFollow(@Body FollowCommunityDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/community/block")
    Call<String> communityBlock(@Body BlockCommunityDTO params);

    @Headers("Content-Type: application/json")
    @PUT("api/v3/post/save")
    Call<String> postSave(@Body SavePostDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/post/mark_as_read")
    Call<String> postRead(@Body ReadPostDTO params);

    @Headers("Content-Type: application/json")
    @PUT("api/v3/comment/save")
    Call<String> commentSave(@Body SaveCommentDTO params);

    @GET("api/v3/comment/list")
    Call<String> getComments(
            @Query("type_") String type,
            @Query("sort") String sort,
            @Query("max_depth") Integer maxDepth,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("community_id") Integer communityId,
            @Query("community_name") String communityName,
            @Query("post_id") Integer postId,
            @Query("parent_id") Integer parentId,
            @Query("saved_only") Boolean savedOnly,
            @Query("auth") String auth
    );

    @GET("api/v3/search")
    ListenableFuture<Response<String>> searchLive(
            @Query("q") String q,
            @Query("community_id") Integer communityId,
            @Query("community_name") String communityName,
            @Query("creator_id") Integer creatorId,
            @Query("type_") String type,
            @Query("sort") String sort,
            @Query("listing_type") String listingType,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("auth") String auth
    );

    @GET("api/v3/search")
    Call<String> search(
            @Query("q") String q,
            @Query("community_id") Integer communityId,
            @Query("community_name") String communityName,
            @Query("creator_id") Integer creatorId,
            @Query("type_") String type,
            @Query("sort") String sort,
            @Query("listing_type") String listingType,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("auth") String auth
    );

    @Headers("Content-Type: application/json")
    @POST("api/v3/comment")
    Call<String> postComment(@Body CommentDTO params);

    @Headers("Content-Type: application/json")
    @POST("api/v3/comment/delete")
    Call<String> commentDelete(@Body DeleteCommentDTO params);

    @Headers("Content-Type: application/json")
    @PUT("api/v3/comment")
    Call<String> commentEdit(@Body EditCommentDTO params);

    @Multipart
    @POST("/pictrs/image")
    Call<String> uploadImage(
            @Header("Cookie") String token,
            @Part MultipartBody.Part filePart
    );

    @GET("api/v3/resolve_object")
    Call<String> resolveObject(
            @Query("q") String query,
            @Query("auth") String auth
    );

    @GET("api/v3/comment")
    Call<String> getComment(
            @Query("id") int commentId,
            @Query("auth") String auth
    );

    @GET("api/v3/site")
    Call<String> getSiteInfo(
            @Query("auth") String auth
    );

    @POST("/api/v3/site/block")
    Call<String> blockInstance(
            @Body BlockInstanceDTO params
    );

    @GET("api/v3/private_message/list")
    Call<String> privateMessagesList(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("unread_only") Boolean unread_only,
            @NonNull @Query("auth") String auth
    );

    @POST("api/v3/private_message")
    Call<String> privateMessageSend(
            @Body PrivateMessageDTO params
    );

    @PUT("api/v3/private_message")
    Call<String> privateMessageEdit(
            @Body PrivateMessageUpdateDTO params
    );

    @POST("api/v3/private_message/delete")
    Call<String> privateMessageDelete(
            @Body PrivateMessageUpdateDTO params
    );

    @POST("api/v3/private_message/mark_as_read")
    Call<String> privateMessageMarkAsRead(
            @Body PrivateMessageReadDTO params
    );

    @POST("api/v3/private_message/report")
    Call<String> privateMessageReport(
            @Body PrivateMessageReportDTO params
    );
}
