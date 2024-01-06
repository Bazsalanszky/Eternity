package eu.toldi.infinityforlemmy.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;

public class SubredditWithSelection implements Parcelable {
    private final String name;
    private final String iconUrl;

    private final String qualifiedName;
    private boolean selected;

    public SubredditWithSelection(String name, String iconUrl, String qualifiedName) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.qualifiedName = qualifiedName;
        selected = false;
    }

    protected SubredditWithSelection(Parcel in) {
        name = in.readString();
        iconUrl = in.readString();
        qualifiedName = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<SubredditWithSelection> CREATOR = new Creator<SubredditWithSelection>() {
        @Override
        public SubredditWithSelection createFromParcel(Parcel in) {
            return new SubredditWithSelection(in);
        }

        @Override
        public SubredditWithSelection[] newArray(int size) {
            return new SubredditWithSelection[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static ArrayList<SubredditWithSelection> convertSubscribedSubreddits(
            List<SubscribedSubredditData> subscribedSubredditData) {
        ArrayList<SubredditWithSelection> subredditWithSelections = new ArrayList<>();
        for (SubscribedSubredditData s : subscribedSubredditData) {
            subredditWithSelections.add(new SubredditWithSelection(s.getName(), s.getIconUrl(), s.getQualified_name()));
        }

        return subredditWithSelections;
    }

    public static SubredditWithSelection convertSubreddit(SubredditData subreddit) {
        return new SubredditWithSelection(subreddit.getName(), subreddit.getIconUrl(), LemmyUtils.actorID2FullName(subreddit.getActorId()));
    }

    public int compareName(SubredditWithSelection subredditWithSelection) {
        if (subredditWithSelection != null) {
            return name.compareToIgnoreCase(subredditWithSelection.getName());
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SubredditWithSelection)) {
            return false;
        } else {
            return this.getQualifiedName().compareToIgnoreCase(((SubredditWithSelection) obj).getQualifiedName()) == 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(iconUrl);
        parcel.writeString(qualifiedName);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
