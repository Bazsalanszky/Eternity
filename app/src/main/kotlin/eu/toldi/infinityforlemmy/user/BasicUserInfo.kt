package eu.toldi.infinityforlemmy.user

import android.os.Parcel
import android.os.Parcelable

data class BasicUserInfo(
    val id: Int,
    val username: String,
    val qualifiedName: String,
    val avatar: String?,
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
        parcel.writeString(username)
        parcel.writeString(qualifiedName)
        parcel.writeString(avatar)
        parcel.writeString(displayName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BasicUserInfo> {
        override fun createFromParcel(parcel: Parcel): BasicUserInfo {
            return BasicUserInfo(parcel)
        }

        override fun newArray(size: Int): Array<BasicUserInfo?> {
            return arrayOfNulls(size)
        }
    }
}
