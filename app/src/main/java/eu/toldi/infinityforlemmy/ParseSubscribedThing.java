package eu.toldi.infinityforlemmy;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.subscribeduser.SubscribedUserData;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

class ParseSubscribedThing {
    static void parseSubscribedSubreddits(String response, String accountName,
                                          ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                          ArrayList<SubscribedUserData> subscribedUserData,
                                          ArrayList<SubredditData> subredditData,
                                          ParseSubscribedSubredditsListener parseSubscribedSubredditsListener) {
        new ParseSubscribedSubredditsAsyncTask(response, accountName, subscribedSubredditData, subscribedUserData, subredditData,
                parseSubscribedSubredditsListener).execute();
    }

    interface ParseSubscribedSubredditsListener {
        void onParseSubscribedSubredditsSuccess(ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                                ArrayList<SubscribedUserData> subscribedUserData,
                                                ArrayList<SubredditData> subredditData,
                                                boolean lastItem);

        void onParseSubscribedSubredditsFail();
    }

    private static class ParseSubscribedSubredditsAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONObject jsonResponse;
        private String accountName;
        private boolean parseFailed;
        private boolean lastItem;
        private ArrayList<SubscribedSubredditData> subscribedSubredditData;
        private ArrayList<SubscribedUserData> subscribedUserData;
        private ArrayList<SubredditData> subredditData;
        private ArrayList<SubscribedSubredditData> newSubscribedSubredditData;
        private ArrayList<SubscribedUserData> newSubscribedUserData;
        private ArrayList<SubredditData> newSubredditData;
        private ParseSubscribedSubredditsListener parseSubscribedSubredditsListener;

        ParseSubscribedSubredditsAsyncTask(String response, String accountName, ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                           ArrayList<SubscribedUserData> subscribedUserData,
                                           ArrayList<SubredditData> subredditData,
                                           ParseSubscribedSubredditsListener parseSubscribedSubredditsListener) {
            this.parseSubscribedSubredditsListener = parseSubscribedSubredditsListener;
            try {
                jsonResponse = new JSONObject(response);
                this.accountName = accountName;
                parseFailed = false;
                this.subscribedSubredditData = subscribedSubredditData;
                this.subscribedUserData = subscribedUserData;
                this.subredditData = subredditData;
                newSubscribedSubredditData = new ArrayList<>();
                newSubscribedUserData = new ArrayList<>();
                newSubredditData = new ArrayList<>();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (jsonResponse == null) {
                    parseFailed = true;
                    return null;
                }
                JSONArray children = jsonResponse.getJSONArray("communities");
                if(children.length() ==0){
                    lastItem = true;
                }
                for (int i = 0; i < children.length(); i++) {
                    JSONObject data = children.getJSONObject(i);
                    JSONObject community = data.getJSONObject("community");
                    String title = community.getString(JSONUtils.TITLE_KEY);
                    String bannerImageUrl = "";
                    if(!community.isNull("banner")){
                        bannerImageUrl = community.getString("banner");
                    }

                    String iconUrl = "";
                    if(!community.isNull("icon")){
                        iconUrl = community.getString("icon");
                    }
                    int id = community.getInt("id");
                    String name = community.getString("name");
                    String description = "";
                    if(!community.isNull("description")) {
                        description = community.getString("description");
                    }
                    boolean removed = community.getBoolean("removed");
                    String published = community.getString("published");
                    String updated = "";
                    if(!community.isNull("updated")) {
                        updated = community.getString("updated");
                    }
                    boolean deleted = community.getBoolean("deleted");
                    boolean nsfw = community.getBoolean("nsfw");
                    String actorId = community.getString("actor_id");
                    boolean local = community.getBoolean("local");
                    boolean hidden = community.getBoolean("hidden");
                    boolean postingRestrictedToMods = community.getBoolean("posting_restricted_to_mods");
                    int instanceId = community.getInt("instance_id");
                    int subscribers = data.getJSONObject("counts").getInt("subscribers");
                    boolean isBlocked = data.getBoolean("blocked");
                    newSubscribedSubredditData.add(new SubscribedSubredditData(id, title, LemmyUtils.actorID2FullName(actorId), iconUrl, accountName, false));
                    newSubredditData.add(new SubredditData(id, name, title, description, removed, published, updated, deleted, nsfw, actorId, local, iconUrl, bannerImageUrl, hidden, postingRestrictedToMods, instanceId, subscribers, isBlocked));

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
                subscribedSubredditData.addAll(newSubscribedSubredditData);
                subscribedUserData.addAll(newSubscribedUserData);
                subredditData.addAll(newSubredditData);
                parseSubscribedSubredditsListener.onParseSubscribedSubredditsSuccess(subscribedSubredditData,
                        subscribedUserData, subredditData, lastItem);
            } else {
                parseSubscribedSubredditsListener.onParseSubscribedSubredditsFail();
            }
        }
    }
}
