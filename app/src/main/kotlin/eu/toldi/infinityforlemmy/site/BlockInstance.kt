package eu.toldi.infinityforlemmy.site

import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.dto.BlockInstanceDTO
import retrofit2.Retrofit

object BlockInstance {
    fun blockInstance(retrofit: Retrofit, instance_id: Int, block: Boolean, blockInstanceResponse: BlockInstanceResponse) {
        val LemmyAPI = retrofit.create(LemmyAPI::class.java)
        val response = LemmyAPI.blockInstance(BlockInstanceDTO(instance_id,block)).enqueue(
            object : retrofit2.Callback<String> {
                override fun onResponse(call: retrofit2.Call<String>, response: retrofit2.Response<String>) {
                    if (response.isSuccessful) {
                        blockInstanceResponse.onResponse()
                    } else {
                        blockInstanceResponse.onFailure()
                    }
                }

                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    blockInstanceResponse.onFailure()
                }
            }
        )
    }
    interface BlockInstanceResponse {
        fun onResponse()
        fun onFailure()
    }
}

