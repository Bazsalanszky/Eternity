package eu.toldi.infinityforlemmy.privatemessage

import eu.toldi.infinityforlemmy.RetrofitHolder
import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.dto.PrivateMessageReadDTO
import eu.toldi.infinityforlemmy.utils.LemmyUtils
import org.json.JSONObject

class LemmyPrivateMessageAPI(val retrofitHolder: RetrofitHolder) {


    fun fetchPrivateMessages(
        auth: String,
        page: Int,
        listener: PrivateMessageFetchedListener,
        limit: Int = 25,
        unreadOnly: Boolean = false
    ) {
        val api = retrofitHolder.retrofit.create(LemmyAPI::class.java)
        api.privateMessagesList(page, limit, unreadOnly, auth).enqueue(
            object : retrofit2.Callback<String> {
                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                    if (response.isSuccessful) {
                        val jresponse = JSONObject(response.body()!!);
                        val privateMessages = jresponse.getJSONArray("private_messages")
                        val privateMessageList = mutableListOf<PrivateMessage>()
                        for (i in 0 until privateMessages.length()) {
                            val privateMessage =
                                parsePrivateMessage(privateMessages.getJSONObject(i))
                            privateMessageList.add(privateMessage)
                        }
                        listener.onPrivateMessageFetchedSuccess(privateMessageList)
                    } else {
                        listener.onPrivateMessageFetchedError()
                    }
                }

                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    listener.onPrivateMessageFetchedError()
                }
            }
        )
    }

    fun markPrivateMessageAsRead(
        auth: String,
        privateMessageId: Int,
        listener: PrivateMessageMarkedAsReadListener
    ) {
        val api = retrofitHolder.retrofit.create(LemmyAPI::class.java)
        api.privateMessageMarkAsRead(PrivateMessageReadDTO(privateMessageId, auth, true)).enqueue(
            object : retrofit2.Callback<String> {
                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onPrivateMessageMarkedAsReadSuccess()
                    } else {
                        listener.onPrivateMessageMarkedAsReadError()
                    }
                }

                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    listener.onPrivateMessageMarkedAsReadError()
                }
            }
        )
    }

    interface PrivateMessageMarkedAsReadListener {
        fun onPrivateMessageMarkedAsReadSuccess()
        fun onPrivateMessageMarkedAsReadError()
    }


    interface PrivateMessageFetchedListener {
        fun onPrivateMessageFetchedSuccess(privateMessage: List<PrivateMessage>)
        fun onPrivateMessageFetchedError()
    }

    private fun parsePrivateMessage(jsonObject: JSONObject): PrivateMessage {

        val privateMessage = jsonObject.getJSONObject("private_message")
        val creator = jsonObject.getJSONObject("creator")
        val recipient = jsonObject.getJSONObject("recipient")

        return PrivateMessage(
            id = privateMessage.getInt("id"),
            creatorId = privateMessage.getInt("creator_id"),
            recipientId = privateMessage.getInt("recipient_id"),
            content = privateMessage.getString("content"),
            deleted = privateMessage.getBoolean("deleted"),
            read = privateMessage.getBoolean("read"),
            published = privateMessage.getString("published"),
            updated = privateMessage.optString("updated", ""),
            creatorName = creator.getString("name"),
            creatorAvatar = creator.optString("avatar", ""),
            creatorQualifiedName = LemmyUtils.actorID2FullName(creator.getString("actor_id")),
            recipientName = recipient.getString("name"),
            recipientAvatar = recipient.optString("avatar", ""),
            recipientQualifiedName = LemmyUtils.actorID2FullName(recipient.getString("actor_id"))
        )
    }
}