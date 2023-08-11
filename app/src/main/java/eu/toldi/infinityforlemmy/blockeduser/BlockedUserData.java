package eu.toldi.infinityforlemmy.blockeduser;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import eu.toldi.infinityforlemmy.user.UserData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

@Entity(tableName = "blocked_users", primaryKeys = {"id", "account_name"})
public class BlockedUserData {

    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "avatar")
    private String avatar;

    @ColumnInfo(name = "qualified_name")
    private String qualifiedName;

    @NonNull
    @ColumnInfo(name = "account_name")
    private String accountName;

    public BlockedUserData(int id, String name, String avatar, String qualifiedName, String accountName) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.qualifiedName = qualifiedName;
        this.accountName = accountName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BlockedUserData(UserData userData, @NonNull String accountName) {
        this.id = userData.getId();
        this.name = userData.getName();
        this.avatar = userData.getAvatar();
        this.qualifiedName = LemmyUtils.actorID2FullName(userData.getActorId());
        this.accountName = accountName;
    }
}
