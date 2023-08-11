package eu.toldi.infinityforlemmy.subreddit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "subreddits")
public class SubredditData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "removed")
    private boolean removed;

    @ColumnInfo(name = "published")
    private String published;  // Consider using a converter if you want to store it as a different type

    @ColumnInfo(name = "updated")
    private String updated;  // Consider using a converter if you want to store it as a different type

    @ColumnInfo(name = "deleted")
    private boolean deleted;

    @ColumnInfo(name = "nsfw")
    private boolean nsfw;

    @ColumnInfo(name = "actor_id")
    private String actorId;

    @ColumnInfo(name = "local")
    private boolean local;

    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "banner")
    private String banner;

    @ColumnInfo(name = "hidden")
    private boolean hidden;

    @ColumnInfo(name = "posting_restricted_to_mods")
    private boolean postingRestrictedToMods;

    @ColumnInfo(name = "instance_id")
    private int instanceId;

    @ColumnInfo(name = "subscribers")
    private int subscribers;

    @ColumnInfo(name = "blocked")
    private boolean blocked;


    @Ignore
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isPostingRestrictedToMods() {
        return postingRestrictedToMods;
    }

    public void setPostingRestrictedToMods(boolean postingRestrictedToMods) {
        this.postingRestrictedToMods = postingRestrictedToMods;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public SubredditData(int id, String name, String title, String description, boolean removed, String published, String updated, boolean deleted, boolean nsfw, String actorId, boolean local, String icon, String banner, boolean hidden, boolean postingRestrictedToMods, int instanceId, int subscribers, boolean blocked) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.removed = removed;
        this.published = published;
        this.updated = updated;
        this.deleted = deleted;
        this.nsfw = nsfw;
        this.actorId = actorId;
        this.local = local;
        this.icon = icon;
        this.banner = banner;
        this.hidden = hidden;
        this.postingRestrictedToMods = postingRestrictedToMods;
        this.instanceId = instanceId;
        this.subscribers = subscribers;
        this.blocked = blocked;
    }

    public boolean isNSFW() {
        return nsfw;
    }

    public String getBannerUrl() {
        return banner;
    }

    public String getIconUrl() {
        return icon;
    }

    public int getNSubscribers() {
        return subscribers;
    }

    public String getCreatedUTC() {
        return published;
    }

    public String getSidebarDescription() {
        return description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean b) {
        isSelected = b;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
