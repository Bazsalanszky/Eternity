package eu.toldi.infinityforlemmy.subscribedsubreddit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

@Entity(tableName = "subscribed_subreddits", primaryKeys = {"id", "username"},
        foreignKeys = @ForeignKey(entity = Account.class, parentColumns = "username",
                childColumns = "username", onDelete = ForeignKey.CASCADE))
public class SubscribedSubredditData implements Parcelable {
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

    @ColumnInfo(name = "is_favorite")
    private boolean favorite;

    public SubscribedSubredditData(@NonNull int id, String name, @NonNull String qualified_name, String iconUrl, @NonNull String username, boolean favorite) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.username = username;
        this.qualified_name = qualified_name;
        this.favorite = favorite;
    }

    public SubscribedSubredditData(@NonNull SubredditData communityData) {
        this.id = communityData.getId();
        this.name = communityData.getName();
        this.iconUrl = communityData.getIconUrl();
        this.username = "-";
        this.qualified_name = LemmyUtils.actorID2FullName(communityData.getActorId());
        this.favorite = false;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(iconUrl);
        parcel.writeString(username);
        parcel.writeString(qualified_name);
        parcel.writeByte((byte) (favorite ? 1 : 0));
    }

    public SubscribedSubredditData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        iconUrl = in.readString();
        username = in.readString();
        qualified_name = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<SubscribedSubredditData> CREATOR = new Creator<>() {
        @Override
        public SubscribedSubredditData createFromParcel(Parcel in) {
            return new SubscribedSubredditData(in);
        }

        @Override
        public SubscribedSubredditData[] newArray(int size) {
            return new SubscribedSubredditData[size];
        }
    };

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int hashCode() {
        return id + username.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SubscribedSubredditData) {
            return id == ((SubscribedSubredditData) obj).getId() && username.equalsIgnoreCase(((SubscribedSubredditData) obj).getUsername());
        }
        return false;
    }
}
