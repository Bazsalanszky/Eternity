package eu.toldi.infinityforlemmy.comment

import eu.toldi.infinityforlemmy.RetrofitHolder
import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.dto.ReportCommentDTO
import retrofit2.Call
import retrofit2.Callback

class LemmyCommentAPI(val retrofitHolder: RetrofitHolder) {

    fun reportComment(id: Int, reason: String, auth: String, callback: ReportCommentCallback) {
        val api = retrofitHolder.retrofit.create(LemmyAPI::class.java)
        api.commentReport(ReportCommentDTO(id, reason, auth)).enqueue(object : Callback<String> {
            override fun onResponse(
                call: retrofit2.Call<String>,
                response: retrofit2.Response<String>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess()
                } else {
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.onFailure()
            }
        })
    }

    public interface ReportCommentCallback {
        fun onSuccess()
        fun onFailure()
    }
}