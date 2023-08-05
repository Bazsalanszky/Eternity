package eu.toldi.infinityforlemmy.site;

import org.json.JSONException;
import org.json.JSONObject;

public class SiteInfo {

    private int id;
    private String name;
    private String sidebar;
    private String description;
    private boolean enable_downvotes;
    private boolean enable_nsfw;
    private boolean community_creation_admin_only;

    public SiteInfo(int id, String name, String sidebar, String description, boolean enable_downvotes, boolean enable_nsfw, boolean community_creation_admin_only) {
        this.id = id;
        this.name = name;
        this.sidebar = sidebar;
        this.description = description;
        this.enable_downvotes = enable_downvotes;
        this.enable_nsfw = enable_nsfw;
        this.community_creation_admin_only = community_creation_admin_only;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSidebar() {
        return sidebar;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnable_downvotes() {
        return enable_downvotes;
    }

    public boolean isEnable_nsfw() {
        return enable_nsfw;
    }

    public boolean isCommunity_creation_admin_only() {
        return community_creation_admin_only;
    }

    public static SiteInfo parseSiteInfo(String siteInfoJson) {
        try {
            JSONObject siteInfo = new JSONObject(siteInfoJson);
            JSONObject siteView = siteInfo.getJSONObject("site_view");
            JSONObject site = siteView.getJSONObject("site");
            JSONObject localSite = siteView.getJSONObject("local_site");

            int id = site.getInt("id");
            String name = site.getString("name");
            String sidebar = null;
            if (site.has("sidebar"))
                sidebar = site.getString("sidebar");

            String description = null;
            if (site.has("description"))
                description = site.getString("description");

            boolean enable_downvotes = localSite.getBoolean("enable_downvotes");
            boolean enable_nsfw = localSite.getBoolean("enable_nsfw");
            boolean community_creation_admin_only = localSite.getBoolean("community_creation_admin_only");

            SiteInfo si = new SiteInfo(id, name, sidebar, description, enable_downvotes, enable_nsfw, community_creation_admin_only);

            return si;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
