package eu.toldi.infinityforlemmy.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "display_name")
    private String displayName;

    @ColumnInfo(name = "avatar")
    private String avatar;

    @ColumnInfo(name = "banned")
    private boolean banned;

    @ColumnInfo(name = "published")
    private String published;  // Consider using a converter if you want to store it as a different type

    @ColumnInfo(name = "actor_id")
    private String actorId;

    @ColumnInfo(name = "local")
    private boolean local;

    @ColumnInfo(name = "deleted")
    private boolean deleted;

    @ColumnInfo(name = "admin")
    private boolean admin;

    @ColumnInfo(name = "bot_account")
    private boolean botAccount;

    @ColumnInfo(name = "instance_id")
    private int instanceId;

    @Ignore
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getIconUrl() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isBotAccount() {
        return botAccount;
    }

    public void setBotAccount(boolean botAccount) {
        this.botAccount = botAccount;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public UserData(int id, String name, String displayName, String avatar, boolean banned, String published, String actorId, boolean local, boolean deleted, boolean admin, boolean botAccount, int instanceId) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.avatar = avatar;
        this.banned = banned;
        this.published = published;
        this.actorId = actorId;
        this.local = local;
        this.deleted = deleted;
        this.admin = admin;
        this.botAccount = botAccount;
        this.instanceId = instanceId;
    }

    public boolean isCanBeFollowed() {
        return false;
    }


    public String getBanner() {
        return "";
    }

    public Object getCakeday() {
        return published;
    }

    public String getDescription() {
        return null;
    }

    public String getTitle() {
        return displayName;
    }
}
