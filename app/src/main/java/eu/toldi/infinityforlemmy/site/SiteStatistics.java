package eu.toldi.infinityforlemmy.site;

import org.json.JSONException;
import org.json.JSONObject;

public class SiteStatistics {

    private final int users;
    private final int posts;
    private final int comments;
    private final int communities;
    private final int users_active;

    public SiteStatistics(int users, int posts, int comments, int communities, int users_active) {
        this.users = users;
        this.posts = posts;
        this.comments = comments;
        this.communities = communities;
        this.users_active = users_active;
    }

    public int getUsers() {
        return users;
    }

    public int getPosts() {
        return posts;
    }

    public int getComments() {
        return comments;
    }

    public int getCommunities() {
        return communities;
    }

    public int getUsers_active() {
        return users_active;
    }

    public static SiteStatistics parseSiteStatistics(JSONObject countsJson) {
        try {
            int users = countsJson.getInt("users");
            int posts = countsJson.getInt("posts");
            int comments = countsJson.getInt("comments");
            int communities = countsJson.getInt("communities");
            int users_active = countsJson.getInt("users_active_month");
            return new SiteStatistics(users, posts, comments, communities, users_active);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
