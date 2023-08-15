package eu.toldi.infinityforlemmy.privatemessage

import android.os.Parcel
import android.os.Parcelable

data class PrivateMessage(
    val id: Int,
    val creatorId: Int,
    val recipientId: Int,
    val content: String,
    val deleted: Boolean,
    var read: Boolean,
    val published: Long,
    val updated: Long?,
    val creatorName: String,
    val creatorAvatar: String,
    val creatorQualifiedName: String,
    val recipientName: String,
    val recipientAvatar: String,
    val recipientQualifiedName: String
) : Parcelable {
    fun addReply(reply: PrivateMessage) {
        replies.add(reply)
    }

    val replies = mutableListOf<PrivateMessage>()

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(creatorId)
        parcel.writeInt(recipientId)
        parcel.writeString(content)
        parcel.writeByte(if (deleted) 1 else 0)
        parcel.writeByte(if (read) 1 else 0)
        parcel.writeLong(published)
        parcel.writeValue(updated)
        parcel.writeString(creatorName)
        parcel.writeString(creatorAvatar)
        parcel.writeString(creatorQualifiedName)
        parcel.writeString(recipientName)
        parcel.writeString(recipientAvatar)
        parcel.writeString(recipientQualifiedName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PrivateMessage> {
        override fun createFromParcel(parcel: Parcel): PrivateMessage {
            return PrivateMessage(parcel)
        }

        override fun newArray(size: Int): Array<PrivateMessage?> {
            return arrayOfNulls(size)
        }
    }
}
