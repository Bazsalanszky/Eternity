package eu.toldi.infinityforlemmy.subscribedsubreddit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import eu.toldi.infinityforlemmy.account.Account;

@Entity(tableName = "subscribed_subreddits", primaryKeys = {"id", "username"},
        foreignKeys = @ForeignKey(entity = Account.class, parentColumns = "username",
        childColumns = "username", onDelete = ForeignKey.CASCADE))
public class SubscribedSubredditData {
    @NonNull
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "qualified_name")
    private String qualified_name;
    @ColumnInfo(name = "icon")
    private String iconUrl;
    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    public SubscribedSubredditData(@NonNull int id, String name,@NonNull String qualified_name, String iconUrl, @NonNull String username) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.username = username;
        this.qualified_name =qualified_name;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getQualified_name() {
        return qualified_name;
    }

    public void setQualified_name(String qualified_name) {
        this.qualified_name = qualified_name;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }
}
