package eu.toldi.infinityforlemmy.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityData;
import eu.toldi.infinityforlemmy.blockedinstances.BlockedInstanceData;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserData;
import eu.toldi.infinityforlemmy.subreddit.ParseSubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FetchBlockedThings {

    public static void fetchBlockedThings(Retrofit mRetrofit, String accessToken, String accountName, FetchBlockedThingsListener fetchBlockedThingsListener) {
        LemmyAPI lemmyAPI = mRetrofit.create(LemmyAPI.class);

        Call<String> call = lemmyAPI.getSiteInfo(accessToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    String siteInfo = response.body();
                    if (siteInfo != null) {
                        List<BlockedUserData> blockedUsers = new ArrayList<>();
                        List<BlockedCommunityData> blockedCommunities = new ArrayList<>();
                        List<BlockedInstanceData> blockedInstances = new ArrayList<>();

                        try {
                            JSONObject siteInfoJson = new JSONObject(siteInfo).getJSONObject("my_user");
                            JSONArray blockedUsersJson = (siteInfoJson.has("person_blocks")) ? siteInfoJson.getJSONArray("person_blocks") : null;
                            JSONArray blockedCommunitiesJson = (siteInfoJson.has("community_blocks")) ? siteInfoJson.getJSONArray("community_blocks") : null;
                            JSONArray blockedInstancesJson = (siteInfoJson.has("instance_blocks")) ? siteInfoJson.getJSONArray("instance_blocks") : null;
                            if (blockedUsersJson != null) {
                                for (int i = 0; i < blockedUsersJson.length(); i++) {
                                    JSONObject blockedUserJson = blockedUsersJson.getJSONObject(i).getJSONObject("target");
                                    int id = blockedUserJson.getInt("id");
                                    String name = blockedUserJson.getString("name");
                                    String avatar = "";
                                    if (blockedUserJson.has("avatar"))
                                        avatar = blockedUserJson.getString("avatar");

                                    String qualifiedName = LemmyUtils.actorID2FullName(blockedUserJson.getString("actor_id"));
                                    BlockedUserData blockedUserData = new BlockedUserData(id, name, avatar, qualifiedName, accountName);

                                    blockedUsers.add(blockedUserData);
                                }
                            }
                            if (blockedCommunitiesJson != null) {
                                for (int i = 0; i < blockedCommunitiesJson.length(); i++) {
                                    JSONObject blockedCommunityJson = blockedCommunitiesJson.getJSONObject(i);
                                    SubredditData blockedCommunityData = ParseSubredditData.parseSubredditData(blockedCommunityJson, true);
                                    blockedCommunities.add(new BlockedCommunityData(blockedCommunityData, accountName));
                                }
                            }
                            if (blockedInstancesJson != null) {
                                for (int i = 0; i < blockedInstancesJson.length(); i++) {
                                    JSONObject blockedInstanceJson = blockedInstancesJson.getJSONObject(i);
                                    // Get the "instance" object if it exists
                                    JSONObject instanceJson = blockedInstanceJson.has("instance") ? blockedInstanceJson.getJSONObject("instance") : null;
                                    if (instanceJson == null) {
                                        continue;
                                    }
                                    int id = instanceJson.getInt("id");
                                    String domain = instanceJson.getString("domain");
                                    JSONObject siteJson = blockedInstanceJson.has("site") ? blockedInstanceJson.getJSONObject("site") : null;
                                    if (siteJson == null) {
                                        blockedInstances.add(new BlockedInstanceData(id, domain, null, null, accountName));
                                        continue;
                                    }

                                    String name = siteJson.getString("name");
                                    String icon = siteJson.optString("icon");
                                    blockedInstances.add(new BlockedInstanceData(id, domain, name, icon, accountName));
                                }
                            }

                            fetchBlockedThingsListener.onFetchBlockedThingsSuccess(blockedUsers, blockedCommunities, blockedInstances);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            fetchBlockedThingsListener.onFetchBlockedThingsFailure();
                        }
                    } else {
                        fetchBlockedThingsListener.onFetchBlockedThingsFailure();
                    }
                } else {
                    fetchBlockedThingsListener.onFetchBlockedThingsFailure();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                fetchBlockedThingsListener.onFetchBlockedThingsFailure();
            }
        });
    }

    public interface FetchBlockedThingsListener {
        void onFetchBlockedThingsSuccess(List<BlockedUserData> blockedUsers, List<BlockedCommunityData> blockedCommunities, List<BlockedInstanceData> blockedInstances);

        void onFetchBlockedThingsFailure();
    }
}
