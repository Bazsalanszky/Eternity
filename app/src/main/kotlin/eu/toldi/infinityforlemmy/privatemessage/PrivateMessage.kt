package eu.toldi.infinityforlemmy.privatemessage

data class PrivateMessage(
    val id: Int,
    val creatorId: Int,
    val recipientId: Int,
    val content: String,
    val deleted: Boolean,
    var read: Boolean,
    val published: String,
    val updated: String,
    val creatorName: String,
    val creatorAvatar: String,
    val creatorQualifiedName: String,
    val recipientName: String,
    val recipientAvatar: String,
    val recipientQualifiedName: String
)
