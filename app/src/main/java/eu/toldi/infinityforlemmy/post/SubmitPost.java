package eu.toldi.infinityforlemmy.post;

import android.graphics.Bitmap;
import android.os.Handler;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.Flair;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.RedditAPI;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.UploadImageUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubmitPost {
    public static void submitTextOrLinkPost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                            String subredditName, String title, String content,
                                            Flair flair, boolean isSpoiler, boolean isNSFW,
                                            boolean receivePostReplyNotifications, String kind,
                                            SubmitPostListener submitPostListener) {
        submitPost(executor, handler, oauthRetrofit, accessToken, subredditName, title, content,
                flair, isSpoiler, isNSFW, receivePostReplyNotifications, kind, null, submitPostListener);
    }

    public static void submitImagePost(Executor executor, Handler handler, RetrofitHolder mRetrofit,
                                       String accessToken, String subredditName, String title, Bitmap image,
                                       Flair flair, boolean isSpoiler, boolean isNSFW,
                                       boolean receivePostReplyNotifications, SubmitPostListener submitPostListener) {
        try {
            String imageUrlOrError = UploadImageUtils.uploadImage(mRetrofit, accessToken, image);
            if (imageUrlOrError != null && !imageUrlOrError.startsWith("Error: ")) {
                submitPost(executor, handler, mRetrofit.getRetrofit(), accessToken,
                        subredditName, title, imageUrlOrError, flair, isSpoiler, isNSFW,
                        receivePostReplyNotifications, APIUtils.KIND_IMAGE, null, submitPostListener);
            } else {
                submitPostListener.submitFailed(imageUrlOrError);
            }
        } catch (IOException | JSONException | XmlPullParserException e) {
            e.printStackTrace();
            submitPostListener.submitFailed(e.getMessage());
        }
    }

    public static void submitCrosspost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                       String subredditName, String title, String crosspostFullname,
                                       Flair flair, boolean isSpoiler, boolean isNSFW,
                                       boolean receivePostReplyNotifications, String kind,
                                       SubmitPostListener submitPostListener) {
        submitPost(executor, handler, oauthRetrofit, accessToken, subredditName, title, crosspostFullname,
                flair, isSpoiler, isNSFW, receivePostReplyNotifications, kind, null, submitPostListener);
    }

    private static void submitPost(Executor executor, Handler handler, Retrofit oauthRetrofit, String accessToken,
                                   String subredditName, String title, String content,
                                   Flair flair, boolean isSpoiler, boolean isNSFW,
                                   boolean receivePostReplyNotifications, String kind,
                                   @Nullable String posterUrl, SubmitPostListener submitPostListener) {
        RedditAPI api = oauthRetrofit.create(RedditAPI.class);

        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.API_TYPE_KEY, APIUtils.API_TYPE_JSON);
        params.put(APIUtils.SR_KEY, subredditName);
        params.put(APIUtils.TITLE_KEY, title);
        params.put(APIUtils.KIND_KEY, kind);
        switch (kind) {
            case APIUtils.KIND_SELF:
                params.put(APIUtils.TEXT_KEY, content);
                break;
            case APIUtils.KIND_LINK:
            case APIUtils.KIND_IMAGE:
                params.put(APIUtils.URL_KEY, content);
                break;
            case APIUtils.KIND_VIDEOGIF:
                params.put(APIUtils.KIND_KEY, APIUtils.KIND_IMAGE);
                params.put(APIUtils.URL_KEY, content);
                params.put(APIUtils.VIDEO_POSTER_URL_KEY, posterUrl);
                break;
            case APIUtils.KIND_VIDEO:
                params.put(APIUtils.URL_KEY, content);
                params.put(APIUtils.VIDEO_POSTER_URL_KEY, posterUrl);
                break;
            case APIUtils.KIND_CROSSPOST:
                params.put(APIUtils.CROSSPOST_FULLNAME_KEY, content);
                break;
        }

        if (flair != null) {
            params.put(APIUtils.FLAIR_TEXT_KEY, flair.getText());
            params.put(APIUtils.FLAIR_ID_KEY, flair.getId());
        }
        params.put(APIUtils.SPOILER_KEY, Boolean.toString(isSpoiler));
        params.put(APIUtils.NSFW_KEY, Boolean.toString(isNSFW));
        params.put(APIUtils.SEND_REPLIES_KEY, Boolean.toString(receivePostReplyNotifications));

        Call<String> submitPostCall = api.submit(APIUtils.getOAuthHeader(accessToken), params);

        try {
            Response<String> response = submitPostCall.execute();
            if (response.isSuccessful()) {
                getSubmittedPost(executor, handler, response.body(), kind, oauthRetrofit, accessToken,
                        submitPostListener);
            } else {
                submitPostListener.submitFailed(response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            submitPostListener.submitFailed(e.getMessage());
        }
    }

    private static void getSubmittedPost(Executor executor, Handler handler, String response, String kind,
                                         Retrofit oauthRetrofit, String accessToken,
                                         SubmitPostListener submitPostListener) throws JSONException, IOException {
        JSONObject responseObject = new JSONObject(response).getJSONObject(JSONUtils.JSON_KEY);
        if (responseObject.getJSONArray(JSONUtils.ERRORS_KEY).length() != 0) {
            JSONArray error = responseObject.getJSONArray(JSONUtils.ERRORS_KEY)
                    .getJSONArray(responseObject.getJSONArray(JSONUtils.ERRORS_KEY).length() - 1);
            if (error.length() != 0) {
                String errorString;
                if (error.length() >= 2) {
                    errorString = error.getString(1);
                } else {
                    errorString = error.getString(0);
                }
                submitPostListener.submitFailed(errorString);
            } else {
                submitPostListener.submitFailed(null);
            }

            return;
        }

        if (!kind.equals(APIUtils.KIND_IMAGE) && !kind.equals(APIUtils.KIND_VIDEO) && !kind.equals(APIUtils.KIND_VIDEOGIF)) {
            String postId = responseObject.getJSONObject(JSONUtils.DATA_KEY).getString(JSONUtils.ID_KEY);

            RedditAPI api = oauthRetrofit.create(RedditAPI.class);

            Call<String> getPostCall = api.getPostOauth(postId, APIUtils.getOAuthHeader(accessToken));
            Response<String> getPostCallResponse = getPostCall.execute();
            if (getPostCallResponse.isSuccessful()) {
                ParsePost.parsePost(executor, handler, getPostCallResponse.body(), new ParsePost.ParsePostListener() {
                    @Override
                    public void onParsePostSuccess(Post post) {
                        submitPostListener.submitSuccessful(post);
                    }

                    @Override
                    public void onParsePostFail() {
                        submitPostListener.submitFailed(null);
                    }
                });
            } else {
                submitPostListener.submitFailed(getPostCallResponse.message());
            }
        } else {
            submitPostListener.submitSuccessful(null);
        }
    }

    public interface SubmitPostListener {
        void submitSuccessful(Post post);

        void submitFailed(@Nullable String errorMessage);
    }
}
