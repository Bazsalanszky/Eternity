package eu.toldi.infinityforlemmy.community

import android.os.Parcel
import android.os.Parcelable

data class CommunityStats(
    val subscribers: Int,
    val activeUsers: Int,
    val posts: Int,
    val comments: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(subscribers)
        parcel.writeInt(activeUsers)
        parcel.writeInt(posts)
        parcel.writeInt(comments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommunityStats> {
        override fun createFromParcel(parcel: Parcel): CommunityStats {
            return CommunityStats(parcel)
        }

        override fun newArray(size: Int): Array<CommunityStats?> {
            return arrayOfNulls(size)
        }
    }
}
