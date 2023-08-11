package eu.toldi.infinityforlemmy.blockedcommunity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

@Entity(tableName = "blocked_communities", primaryKeys = {"id", "account_name"})
public class BlockedCommunityData {

    @ColumnInfo(name = "id")
    private final int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "qualified_name")
    private String qualified_name;
    @ColumnInfo(name = "icon")
    private String iconUrl;

    @NonNull
    @ColumnInfo(name = "account_name")
    private String accountName;

    public BlockedCommunityData(int id, String name, String qualified_name, String iconUrl, @NonNull String accountName) {
        this.id = id;
        this.name = name;
        this.qualified_name = qualified_name;
        this.iconUrl = iconUrl;
        this.accountName = accountName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQualified_name() {
        return qualified_name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQualified_name(String qualified_name) {
        this.qualified_name = qualified_name;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setAccountName(@NonNull String accountName) {
        this.accountName = accountName;
    }

    public BlockedCommunityData(SubredditData subredditData, @NonNull String accountName) {
        this.id = subredditData.getId();
        this.name = subredditData.getName();
        this.qualified_name = LemmyUtils.actorID2FullName(subredditData.getActorId());
        this.iconUrl = subredditData.getIconUrl();
        this.accountName = accountName;
    }
}
