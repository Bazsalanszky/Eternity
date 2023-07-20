package eu.toldi.infinityforlemmy.subreddit;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.Utils;

public class ParseSubredditData {
    public static void parseSubredditData(String response, ParseSubredditDataListener parseSubredditDataListener) {
        new ParseSubredditDataAsyncTask(response, parseSubredditDataListener).execute();
    }

    public static void parseSubredditListingData(String response, boolean nsfw, ParseSubredditListingDataListener parseSubredditListingDataListener) {
        new ParseSubredditListingDataAsyncTask(response, nsfw, parseSubredditListingDataListener).execute();
    }

    @Nullable
    private static SubredditData parseSubredditData(JSONObject subredditDataJsonObject, boolean nsfw) throws JSONException {
        JSONObject community = subredditDataJsonObject.getJSONObject("community");
        boolean isNSFW = community.getBoolean("nsfw");
        if (!nsfw && isNSFW) {
            return null;
        }

        String title = community.getString(JSONUtils.TITLE_KEY);
        String bannerImageUrl = "";
        if(!community.isNull("banner")){
            bannerImageUrl = community.getString("banner");
        }

        String iconUrl = "";
        if(!community.isNull("banner")){
            bannerImageUrl = community.getString("icon");
        }
        int id = community.getInt("id");
        String name = community.getString("name");
        String description = community.getString("description");
        boolean removed = community.getBoolean("removed");
        String published = community.getString("published");
        String updated = community.getString("updated");
        boolean deleted = community.getBoolean("deleted");

        String actorId = community.getString("actor_id");
        boolean local = community.getBoolean("local");
        String icon = community.getString("icon");
        String banner = community.getString("banner");
        boolean hidden = community.getBoolean("hidden");
        boolean postingRestrictedToMods = community.getBoolean("posting_restricted_to_mods");
        int instanceId = community.getInt("instance_id");
        int subscribers = subredditDataJsonObject.getJSONObject("counts").getInt("subscribers");

        return new SubredditData(id,name,title,description,removed,published,updated,deleted,nsfw,actorId,local,iconUrl,bannerImageUrl,hidden,postingRestrictedToMods,instanceId,subscribers);
    }

    interface ParseSubredditDataListener {
        void onParseSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers);

        void onParseSubredditDataFail();
    }

    public interface ParseSubredditListingDataListener {
        void onParseSubredditListingDataSuccess(ArrayList<SubredditData> subredditData, String after);

        void onParseSubredditListingDataFail();
    }

    private static class ParseSubredditDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONObject jsonResponse;
        private boolean parseFailed;
        private ParseSubredditDataListener parseSubredditDataListener;
        private SubredditData subredditData;
        private int mNCurrentOnlineSubscribers;

        ParseSubredditDataAsyncTask(String response, ParseSubredditDataListener parseSubredditDataListener) {
            this.parseSubredditDataListener = parseSubredditDataListener;
            try {
                jsonResponse = new JSONObject(response);
                parseFailed = false;
            } catch (JSONException e) {
                e.printStackTrace();
                parseSubredditDataListener.onParseSubredditDataFail();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject data = jsonResponse.getJSONObject("community_view");
                mNCurrentOnlineSubscribers = 0;// data.getInt(JSONUtils.ACTIVE_USER_COUNT_KEY);
                subredditData = parseSubredditData(data, true);
            } catch (JSONException e) {
                parseFailed = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!parseFailed) {
                parseSubredditDataListener.onParseSubredditDataSuccess(subredditData, mNCurrentOnlineSubscribers);
            } else {
                parseSubredditDataListener.onParseSubredditDataFail();
            }
        }
    }

    private static class ParseSubredditListingDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONObject jsonResponse;
        private boolean nsfw;
        private boolean parseFailed;
        private ParseSubredditListingDataListener parseSubredditListingDataListener;
        private ArrayList<SubredditData> subredditListingData;
        private String after;

        ParseSubredditListingDataAsyncTask(String response, boolean nsfw, ParseSubredditListingDataListener parseSubredditListingDataListener) {
            this.parseSubredditListingDataListener = parseSubredditListingDataListener;
            try {
                jsonResponse = new JSONObject(response);
                this.nsfw = nsfw;
                parseFailed = false;
                subredditListingData = new ArrayList<>();
            } catch (JSONException e) {
                e.printStackTrace();
                parseFailed = true;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (!parseFailed) {
                    JSONArray children = jsonResponse.getJSONObject(JSONUtils.DATA_KEY)
                            .getJSONArray(JSONUtils.CHILDREN_KEY);
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject data = children.getJSONObject(i).getJSONObject(JSONUtils.DATA_KEY);
                        SubredditData subredditData = parseSubredditData(data, nsfw);
                        if (subredditData != null) {
                            subredditListingData.add(subredditData);
                        }
                    }
                    after = jsonResponse.getJSONObject(JSONUtils.DATA_KEY).getString(JSONUtils.AFTER_KEY);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                parseFailed = true;
                parseSubredditListingDataListener.onParseSubredditListingDataFail();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!parseFailed) {
                parseSubredditListingDataListener.onParseSubredditListingDataSuccess(subredditListingData, after);
            } else {
                parseSubredditListingDataListener.onParseSubredditListingDataFail();
            }
        }
    }
}
