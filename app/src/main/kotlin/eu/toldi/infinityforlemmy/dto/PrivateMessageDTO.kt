package eu.toldi.infinityforlemmy.dto

data class PrivateMessageDTO(val recipient_id: Int, val content: String, val auth: String)

data class PrivateMessageUpdateDTO(
    val private_message_id: Int,
    val auth: String,
    val content: String
)

data class PrivateMessageDeleteDTO(
    val private_message_id: Int,
    val auth: String,
    val deleted: Boolean
)

data class PrivateMessageReadDTO(val private_message_id: Int, val auth: String, val read: Boolean)

data class PrivateMessageReportDTO(
    val private_message_id: Int,
    val auth: String,
    val reason: String
)