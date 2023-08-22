package eu.toldi.infinityforlemmy.site;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.toldi.infinityforlemmy.user.BasicUserInfo;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

public class SiteInfo {

    private int id;
    private String name;
    private String sidebar;
    private String description;
    private boolean enable_downvotes;
    private boolean enable_nsfw;
    private boolean community_creation_admin_only;

    private List<BasicUserInfo> admins;

    SiteStatistics siteStatistics;

    public SiteInfo(int id, String name, String sidebar, String description, boolean enable_downvotes, boolean enable_nsfw, boolean community_creation_admin_only, List<BasicUserInfo> admins, SiteStatistics siteStatistics) {
        this.id = id;
        this.name = name;
        this.sidebar = sidebar;
        this.description = description;
        this.enable_downvotes = enable_downvotes;
        this.enable_nsfw = enable_nsfw;
        this.community_creation_admin_only = community_creation_admin_only;
        this.admins = admins;
        this.siteStatistics = siteStatistics;
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

    public List<BasicUserInfo> getAdmins() {
        return admins;
    }

    public SiteStatistics getSiteStatistics() {
        return siteStatistics;
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

            JSONObject counts = siteView.getJSONObject("counts");
            List<BasicUserInfo> admins = new ArrayList<>();
            if (siteInfo.has("admins")) {
                JSONArray adminsJson = siteInfo.getJSONArray("admins");
                for (int i = 0; i < adminsJson.length(); i++) {
                    JSONObject adminJson = adminsJson.getJSONObject(i).getJSONObject("person");
                    admins.add(new BasicUserInfo(adminJson.getInt("id"), adminJson.getString("name"),
                            LemmyUtils.actorID2FullName(adminJson.getString("actor_id")), adminJson.optString("avatar ", ""),
                            adminJson.optString("display_name", adminJson.getString("name")))
                    );
                }
            }

            return new SiteInfo(id, name, sidebar, description, enable_downvotes, enable_nsfw, community_creation_admin_only, admins, SiteStatistics.parseSiteStatistics(counts));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
