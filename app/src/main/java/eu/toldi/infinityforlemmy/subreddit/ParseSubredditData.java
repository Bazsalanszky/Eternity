package eu.toldi.infinityforlemmy.subreddit;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import eu.toldi.infinityforlemmy.community.CommunityStats;
import eu.toldi.infinityforlemmy.user.BasicUserInfo;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

public class ParseSubredditData {
    public static void parseSubredditData(String response, ParseSubredditDataListener parseSubredditDataListener) {
        new ParseSubredditDataAsyncTask(response, parseSubredditDataListener).execute();
    }

    public static void parseSubredditListingData(String response, boolean nsfw, ParseSubredditListingDataListener parseSubredditListingDataListener) {
        new ParseSubredditListingDataAsyncTask(response, nsfw, parseSubredditListingDataListener).execute();
    }

    @Nullable
    public static SubredditData parseSubredditData(JSONObject subredditDataJsonObject, boolean nsfw) throws JSONException {
        JSONObject community = subredditDataJsonObject.getJSONObject("community");
        boolean isNSFW = community.getBoolean("nsfw");
        if (!nsfw && isNSFW) {
            return null;
        }

        String title = community.getString(JSONUtils.TITLE_KEY).replace("&amp;", "&");
        String bannerImageUrl = "";
        if (!community.isNull("banner")) {
            bannerImageUrl = community.getString("banner");
        }

        String iconUrl = "";
        if (!community.isNull("icon")) {
            iconUrl = community.getString("icon");
        }
        int id = community.getInt("id");
        String name = community.getString("name");
        String description = "";
        if (!community.isNull("description")) {
            description = community.getString("description").trim();
        }

        boolean removed = community.getBoolean("removed");
        String published_raw = community.getString("published");
        String published = formatISOTime(published_raw);
        if(published == null){
            published = published_raw;
        }
        String updated = "";
        if (!community.isNull("updated")) {
            updated = community.getString("updated");
        }

        boolean deleted = community.getBoolean("deleted");

        String actorId = community.getString("actor_id");
        boolean local = community.getBoolean("local");
        boolean hidden = community.getBoolean("hidden");
        boolean postingRestrictedToMods = community.getBoolean("posting_restricted_to_mods");
        int instanceId = community.getInt("instance_id");
        int subscribers = (subredditDataJsonObject.has("counts")) ? subredditDataJsonObject.getJSONObject("counts").getInt("subscribers") : 0;
        boolean blocked = (subredditDataJsonObject.has("blocked")) ? subredditDataJsonObject.getBoolean("blocked") : true;
        CommunityStats stats = null;
        if (subredditDataJsonObject.has("counts")) {
            JSONObject counts = subredditDataJsonObject.getJSONObject("counts");
            int activeUserCount = counts.getInt("users_active_month");
            int postCount = counts.getInt("posts");
            int commentCount = counts.getInt("comments");
            stats = new CommunityStats(subscribers, activeUserCount, postCount, commentCount);
        }
        return new SubredditData(id, name, title, description, removed, published, updated, deleted, isNSFW, actorId, local, iconUrl, bannerImageUrl, hidden, postingRestrictedToMods, instanceId, subscribers, blocked, stats);
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
                JSONArray moderators = jsonResponse.getJSONArray("moderators");
                for (int i = 0; i < moderators.length(); i++) {
                    JSONObject moderator = moderators.getJSONObject(i).getJSONObject("moderator");
                    int mod_id = moderator.getInt("id");
                    String mod_name = moderator.getString("name");
                    String mod_displayName = moderator.optString("display_name", mod_name);
                    String mod_qualified_name = LemmyUtils.actorID2FullName(moderator.getString("actor_id"));
                    String avatarUrl = moderator.optString("avatar", "");
                    subredditData.addModerator(new BasicUserInfo(mod_id, mod_name, mod_qualified_name, avatarUrl, mod_displayName));
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
                    JSONArray children = jsonResponse.getJSONArray("communities");
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject data = children.getJSONObject(i);
                        SubredditData subredditData = parseSubredditData(data, nsfw);
                        if (subredditData != null) {
                            subredditListingData.add(subredditData);
                        }
                    }
                    //after = jsonResponse.getJSONObject(JSONUtils.DATA_KEY).getString(JSONUtils.AFTER_KEY);
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

    public static String formatISOTime(String isoTime) {
        // Truncate the time to millisecond precision
        //String truncatedTime = isoTime.substring(0, 23);

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = isoFormat.parse(isoTime);

            // Set your desired output format here
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);
            outputFormat.setTimeZone(TimeZone.getDefault()); // Set to device's default timezone

            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
