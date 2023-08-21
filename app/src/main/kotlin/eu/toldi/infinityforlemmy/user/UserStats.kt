package eu.toldi.infinityforlemmy.user

import android.os.Parcel
import android.os.Parcelable

data class UserStats(
    val postCount: Int,
    val postScore: Int,
    val commentCount: Int,
    val commentScore: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(postCount)
        parcel.writeInt(postScore)
        parcel.writeInt(commentCount)
        parcel.writeInt(commentScore)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserStats> {
        override fun createFromParcel(parcel: Parcel): UserStats {
            return UserStats(parcel)
        }

        override fun newArray(size: Int): Array<UserStats?> {
            return arrayOfNulls(size)
        }
    }
}
