package eu.toldi.infinityforlemmy.user;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.utils.JSONUtils;

public class ParseUserData {
    static void parseUserData(RedditDataRoomDatabase redditDataRoomDatabase, String response,
                              ParseUserDataListener parseUserDataListener) {
        new ParseUserDataAsyncTask(redditDataRoomDatabase, response, parseUserDataListener).execute();
    }

    static void parseUserListingData(String response, ParseUserListingDataListener parseUserListingDataListener) {
        new ParseUserListingDataAsyncTask(response, parseUserListingDataListener).execute();
    }

    public static UserData parseUserDataBase(JSONObject userDataJson, boolean parseFullKarma) throws JSONException {
        if (userDataJson == null) {
            return null;
        }


        JSONObject personJson = (userDataJson.has("person_view")) ? userDataJson.getJSONObject("person_view").getJSONObject("person") : userDataJson.getJSONObject("person");
        String userName = personJson.getString(JSONUtils.NAME_KEY);
        String actor_id = personJson.getString("actor_id");
        String iconImageUrl = "";
        String bannerImageUrl = "";
        if (!personJson.isNull("avatar")) {
            iconImageUrl = personJson.getString("avatar");
        }
        if (!personJson.isNull("banner")) {
            bannerImageUrl = personJson.getString("banner");
        }

        int account_id = personJson.getInt("id");
        int instance_id = personJson.getInt("instance_id");

        String cakeday = personJson.getString(JSONUtils.PUBLISHED);
        cakeday = cakeday.substring(0, cakeday.indexOf("T"));
        boolean isBot = personJson.getBoolean("bot_account");
        boolean isBanned = personJson.getBoolean("banned");
        boolean isLocal = personJson.getBoolean("local");
        boolean isAdmin = personJson.getBoolean("admin");
        boolean isDeleted = personJson.getBoolean("deleted");

        String description = "";

        if (!personJson.isNull("bio")) {
            description = personJson.getString("bio");
        }
        String title = "";
        if (!personJson.isNull("display_name")) {
            title = personJson.getString("display_name");
        }


        return new UserData(account_id,userName,title, iconImageUrl,isBanned,cakeday,actor_id,isLocal,isDeleted,isAdmin,isBot,instance_id);
    }

    interface ParseUserDataListener {
        void onParseUserDataSuccess(UserData userData, int inboxCount);

        void onParseUserDataFailed();
    }

    interface ParseUserListingDataListener {
        void onParseUserListingDataSuccess(ArrayList<UserData> userData, String after);

        void onParseUserListingDataFailed();
    }

    private static class ParseUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private RedditDataRoomDatabase redditDataRoomDatabase;
        private JSONObject jsonResponse;
        private ParseUserDataListener parseUserDataListener;
        private boolean parseFailed = false;

        private UserData userData;
        private int inboxCount = -1;

        ParseUserDataAsyncTask(RedditDataRoomDatabase redditDataRoomDatabase, String response, ParseUserDataListener parseUserDataListener) {
            this.redditDataRoomDatabase = redditDataRoomDatabase;
            this.parseUserDataListener = parseUserDataListener;
            try {
                jsonResponse = new JSONObject(response);
            } catch (JSONException e) {
                parseFailed = true;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!parseFailed) {
                try {
                    userData = parseUserDataBase(jsonResponse, true);
                    if (redditDataRoomDatabase != null) {
                        redditDataRoomDatabase.accountDao().updateAccountInfo(userData.getName(), userData.getIconUrl(), userData.getBanner());
                    }
                } catch (JSONException e) {
                    parseFailed = true;
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!parseFailed) {
                parseUserDataListener.onParseUserDataSuccess(userData, inboxCount);
            } else {
                parseUserDataListener.onParseUserDataFailed();
            }
        }
    }

    private static class ParseUserListingDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private String response;
        private JSONObject jsonResponse;
        private ParseUserListingDataListener parseUserListingDataListener;
        private String after;
        private boolean parseFailed;

        private ArrayList<UserData> userDataArrayList;

        ParseUserListingDataAsyncTask(String response, ParseUserListingDataListener parseUserListingDataListener) {
            this.parseUserListingDataListener = parseUserListingDataListener;
            this.response = response;
            try {
                jsonResponse = new JSONObject(response);
                parseFailed = false;
                userDataArrayList = new ArrayList<>();
            } catch (JSONException e) {
                e.printStackTrace();
                parseFailed = true;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (!parseFailed) {

                    JSONArray children = jsonResponse.getJSONArray("users");
                    for (int i = 0; i < children.length(); i++) {
                        try {
                            UserData userData = parseUserDataBase(children.getJSONObject(i), false);
                            userDataArrayList.add(userData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                parseFailed = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!parseFailed) {
                parseUserListingDataListener.onParseUserListingDataSuccess(userDataArrayList, after);
            } else {
                if (response.equals("\"{}\"")) {
                    parseUserListingDataListener.onParseUserListingDataSuccess(new ArrayList<>(), null);
                } else {
                    parseUserListingDataListener.onParseUserListingDataFailed();
                }
            }
        }
    }
}
