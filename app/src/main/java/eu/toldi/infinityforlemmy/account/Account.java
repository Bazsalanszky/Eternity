package eu.toldi.infinityforlemmy.account;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    private String accountName;

    @ColumnInfo(name = "display_name")
    private String display_name;
    @ColumnInfo(name = "profile_image_url")
    private String profileImageUrl;
    @ColumnInfo(name = "banner_image_url")
    private String bannerImageUrl;
    @ColumnInfo(name = "access_token")
    private String accessToken;
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "is_current_user")
    private boolean isCurrentUser;

    @ColumnInfo(name = "instance_url")
    private String instance_url;

    @Ignore
    protected Account(Parcel in) {
        accountName = in.readString();
        display_name = in.readString();
        profileImageUrl = in.readString();
        bannerImageUrl = in.readString();
        accessToken = in.readString();
        code = in.readString();
        isCurrentUser = in.readByte() != 0;
        instance_url = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Ignore
    public static Account getAnonymousAccount() {
        return new Account("-",null, null, null, null, null, false,null);
    }

    public Account(@NonNull String accountName, String display_name, String accessToken, String code,
                   String profileImageUrl, String bannerImageUrl, boolean isCurrentUser,String instance_url) {
        this.accountName = accountName;
        this.display_name = display_name;
        this.accessToken = accessToken;
        this.code = code;
        this.profileImageUrl = profileImageUrl;
        this.bannerImageUrl = bannerImageUrl;
        this.isCurrentUser = isCurrentUser;
        this.instance_url = instance_url;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCode() {
        return code;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getInstance_url() {
        return instance_url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountName);
        dest.writeString(display_name);
        dest.writeString(profileImageUrl);
        dest.writeString(bannerImageUrl);
        dest.writeString(accessToken);
        dest.writeString(code);
        dest.writeByte((byte) (isCurrentUser ? 1 : 0));
        dest.writeString(instance_url);
    }
}
