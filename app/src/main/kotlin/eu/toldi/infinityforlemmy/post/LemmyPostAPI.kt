package eu.toldi.infinityforlemmy.post

import eu.toldi.infinityforlemmy.RetrofitHolder
import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.dto.ReportPostDTO
import retrofit2.Call
import retrofit2.Callback

class LemmyPostAPI(val retrofitHolder: RetrofitHolder) {

    fun reportPost(postId: Int, reason: String, auth: String, callback: ReportPostCallback) {
        val api = retrofitHolder.retrofit.create(LemmyAPI::class.java)
        api.postReport(ReportPostDTO(postId, reason, auth)).enqueue(object : Callback<String> {
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

    public interface ReportPostCallback {
        fun onSuccess()
        fun onFailure()
    }
}