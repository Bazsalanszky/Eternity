package eu.toldi.infinityforlemmy.user;

import org.json.JSONException;
import org.json.JSONObject;

import eu.toldi.infinityforlemmy.utils.LemmyUtils;

public class MyUserInfo {

    private final String qualifiedName;
    private final String name;
    private final String displayName;
    private final String profileImageUrl;
    private final String bannerImageUrl;

    private MyUserInfo(String qualifiedName, String name, String displayName, String profileImageUrl, String bannerImageUrl) {
        this.qualifiedName = qualifiedName;
        this.name = name;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public static MyUserInfo parseFromSiteInfo(String siteInfoJson) throws JSONException {
        JSONObject siteInfo = new JSONObject(siteInfoJson);
        JSONObject myUser = siteInfo.optJSONObject("my_user");
        if (myUser == null) {
            return null;
        }
        JSONObject localUserView = myUser.getJSONObject("local_user_view");
        JSONObject person = localUserView.getJSONObject("person");

        String qualifiedName = LemmyUtils.actorID2FullName(person.getString("actor_id"));
        String name = person.getString("name");
        String displayName = person.has("display_name") ? person.getString("display_name") : name;
        String avatar = person.optString("avatar");
        String banner = person.optString("banner");

        return new MyUserInfo(qualifiedName, name, displayName, avatar, banner);
    }
}
