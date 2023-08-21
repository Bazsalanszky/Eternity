package eu.toldi.infinityforlemmy.dto

data class ReportPostDTO(val post_id: Int, val reason: String, val auth: String)

data class ReportCommentDTO(val comment_id: Int, val reason: String, val auth: String)