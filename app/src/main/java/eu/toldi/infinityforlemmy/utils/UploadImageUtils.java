package eu.toldi.infinityforlemmy.utils;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadImageUtils {
    @Nullable
    public static String uploadImage(RetrofitHolder mRetrofit,
                                     String accessToken, Bitmap image) throws IOException, JSONException, XmlPullParserException {
        return uploadImage(mRetrofit, accessToken, image, false);
    }

    @Nullable
    public static String uploadImage(RetrofitHolder mRetrofit,
                                     String accessToken, Bitmap image, boolean returnResponseForGallerySubmission) throws IOException, JSONException, XmlPullParserException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody fileBody = RequestBody.create(byteArray, MediaType.parse("application/octet-stream"));
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("images[]", "post_image.jpg", fileBody);

        LemmyAPI api = mRetrofit.getRetrofit().create(LemmyAPI.class);
        Call<String> uploadMedia = api.uploadImage("jwt=" + accessToken, fileToUpload);
        Response<String> uploadMediaResponse = uploadMedia.execute();
        if (uploadMediaResponse.isSuccessful()) {
            JSONObject responseObject = new JSONObject(uploadMediaResponse.body());
            String fileName = responseObject.getJSONArray("files").getJSONObject(0).getString("file");
            return mRetrofit.getBaseURL() + "/pictrs/image/" + fileName;
        } else {
            return "Error: " + uploadMediaResponse.code();
        }
    }

    @Nullable
    public static String parseXMLResponseFromAWS(String response) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
        xmlPullParser.setInput(new StringReader(response));

        boolean isLocationTag = false;
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xmlPullParser.getName().equals("Location")) {
                    isLocationTag = true;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                if (isLocationTag) {
                    return xmlPullParser.getText();
                }
            }
            eventType = xmlPullParser.next();
        }

        return null;
    }

    public static Map<String, RequestBody> parseJSONResponseFromAWS(String response) throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        JSONArray nameValuePairs = responseObject.getJSONObject(JSONUtils.ARGS_KEY).getJSONArray(JSONUtils.FIELDS_KEY);

        Map<String, RequestBody> nameValuePairsMap = new HashMap<>();
        for (int i = 0; i < nameValuePairs.length(); i++) {
            nameValuePairsMap.put(nameValuePairs.getJSONObject(i).getString(JSONUtils.NAME_KEY),
                    APIUtils.getRequestBody(nameValuePairs.getJSONObject(i).getString(JSONUtils.VALUE_KEY)));
        }

        return nameValuePairsMap;
    }
}
