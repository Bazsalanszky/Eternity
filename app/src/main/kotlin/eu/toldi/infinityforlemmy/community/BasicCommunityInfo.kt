package eu.toldi.infinityforlemmy.community

import android.os.Parcel
import android.os.Parcelable

data class BasicCommunityInfo(
    val id: Int,
    val name: String,
    val qualifiedName: String,
    val icon: String?,
    val displayName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(qualifiedName)
        parcel.writeString(icon)
        parcel.writeString(displayName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BasicCommunityInfo> {
        override fun createFromParcel(parcel: Parcel): BasicCommunityInfo {
            return BasicCommunityInfo(parcel)
        }

        override fun newArray(size: Int): Array<BasicCommunityInfo?> {
            return arrayOfNulls(size)
        }
    }
}
