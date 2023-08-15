package eu.toldi.infinityforlemmy;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FetchMyInfo {

    public static void fetchAccountInfo(final Retrofit retrofit, RedditDataRoomDatabase redditDataRoomDatabase,
                                        String username,String accessToken, final FetchMyInfoListener fetchMyInfoListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);

        Call<String> userInfo = api.userInfo(username,accessToken);
        userInfo.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    new ParseAndSaveAccountInfoAsyncTask(response.body(), redditDataRoomDatabase, fetchMyInfoListener).execute();
                } else {
                    fetchMyInfoListener.onFetchMyInfoFailed(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchMyInfoListener.onFetchMyInfoFailed(false);
            }
        });
    }

    public interface FetchMyInfoListener {
        void onFetchMyInfoSuccess(String name, String display_name,String profileImageUrl, String bannerImageUrl);

        void onFetchMyInfoFailed(boolean parseFailed);
    }

    private static class ParseAndSaveAccountInfoAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONObject jsonResponse;
        private RedditDataRoomDatabase redditDataRoomDatabase;
        private FetchMyInfoListener fetchMyInfoListener;
        private boolean parseFailed;

        private String name;
        private String profileImageUrl;
        private String bannerImageUrl;

        private String display_name;

        ParseAndSaveAccountInfoAsyncTask(String response, RedditDataRoomDatabase redditDataRoomDatabase,
                                         FetchMyInfoListener fetchMyInfoListener) {
            try {
                jsonResponse = new JSONObject(response);
                this.redditDataRoomDatabase = redditDataRoomDatabase;
                this.fetchMyInfoListener = fetchMyInfoListener;
                parseFailed = false;
            } catch (JSONException e) {
                fetchMyInfoListener.onFetchMyInfoFailed(true);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject person = jsonResponse.getJSONObject("person_view").getJSONObject("person");

                name = LemmyUtils.actorID2FullName(person.getString("actor_id"));
                if (!person.isNull("avatar")) {
                    profileImageUrl = person.getString("avatar");
                }
                if (!person.isNull("banner")) {
                    bannerImageUrl = person.getString("banner");
                }
                display_name = (person.has("display_name")) ? person.getString("display_name") : person.getString("name");
                redditDataRoomDatabase.accountDao().updateAccountInfo(name, profileImageUrl, bannerImageUrl);
            } catch (JSONException e) {
                parseFailed = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!parseFailed) {
                fetchMyInfoListener.onFetchMyInfoSuccess(name,display_name, profileImageUrl, bannerImageUrl);
            } else {
                fetchMyInfoListener.onFetchMyInfoFailed(true);
            }
        }
    }
}
