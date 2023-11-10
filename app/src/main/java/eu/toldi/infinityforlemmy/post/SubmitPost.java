package eu.toldi.infinityforlemmy.post;

import android.graphics.Bitmap;
import android.os.Handler;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.Flair;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.SubmitPostDTO;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import eu.toldi.infinityforlemmy.utils.UploadImageUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubmitPost {
    public static void submitTextOrLinkPost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                            int communityId, String title, String body, String url,
                                            Flair flair, boolean isSpoiler, boolean isNSFW,
                                            boolean receivePostReplyNotifications, String kind, PostEnricher postEnricher,
                                            SubmitPostListener submitPostListener) {
        submitPost(executor, handler, oauthRetrofit, accessToken, communityId, title, body,
                isNSFW, receivePostReplyNotifications, kind, url, postEnricher, submitPostListener);
    }

    public static void submitImagePost(Executor executor, Handler handler, RetrofitHolder mRetrofit,
                                       String accessToken, int communityId, String title, String body, Bitmap image,
                                       Flair flair, boolean isSpoiler, boolean isNSFW,
                                       boolean receivePostReplyNotifications, PostEnricher postEnricher,
                                       SubmitPostListener submitPostListener) {
        try {
            String imageUrlOrError = UploadImageUtils.uploadImage(mRetrofit, accessToken, image);
            if (imageUrlOrError != null && !imageUrlOrError.startsWith("Error: ")) {
                submitPost(executor, handler, mRetrofit.getRetrofit(), accessToken,
                        communityId, title, body, isNSFW,
                        receivePostReplyNotifications, APIUtils.KIND_IMAGE, imageUrlOrError,
                        postEnricher, submitPostListener);
            } else {
                submitPostListener.submitFailed(imageUrlOrError);
            }
        } catch (IOException | JSONException | XmlPullParserException e) {
            e.printStackTrace();
            submitPostListener.submitFailed(e.getMessage());
        }
    }

    public static void submitCrosspost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                       int communityId, String title, String crosspostFullname,
                                       String url, boolean isSpoiler, boolean isNSFW,
                                       boolean receivePostReplyNotifications, String kind, PostEnricher postEnricher,
                                       SubmitPostListener submitPostListener) {
        submitPost(executor, handler, oauthRetrofit, accessToken, communityId, title, crosspostFullname,
                isNSFW, receivePostReplyNotifications, kind, url, postEnricher, submitPostListener);
    }

    private static void submitPost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                   int communityId, String title, String content,
                                   boolean isNSFW,
                                   boolean receivePostReplyNotifications, String kind,
                                   @Nullable String posterUrl, PostEnricher postEnricher,
                                   SubmitPostListener submitPostListener) {
        LemmyAPI api = oauthRetrofit.create(LemmyAPI.class);


        Call<String> submitPostCall = api.postCreate(new SubmitPostDTO(title, communityId, posterUrl, content, null, isNSFW, null, accessToken));

        try {
            Response<String> response = submitPostCall.execute();
            if (response.isSuccessful()) {
                getSubmittedPost(executor, handler, response.body(), kind, oauthRetrofit, accessToken,
                        postEnricher, submitPostListener);
            } else {
                submitPostListener.submitFailed(response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            submitPostListener.submitFailed(e.getMessage());
        }
    }

    private static void getSubmittedPost(Executor executor, Handler handler, String response, String kind,
                                         Retrofit oauthRetrofit, String accessToken, PostEnricher postEnricher,
                                         SubmitPostListener submitPostListener) throws JSONException, IOException {

        ParsePost.parsePost(executor, handler, postEnricher, response, new ParsePost.ParsePostListener() {
            @Override
            public void onParsePostSuccess(Post post) {
                submitPostListener.submitSuccessful(post);
            }

            @Override
            public void onParsePostFail() {
                submitPostListener.submitFailed(null);
            }
        });
    }

    public interface SubmitPostListener {
        void submitSuccessful(Post post);

        void submitFailed(@Nullable String errorMessage);
    }
}
