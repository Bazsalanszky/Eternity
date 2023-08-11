package eu.toldi.infinityforlemmy.community

import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.dto.BlockCommunityDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

object BlockCommunity {

    fun blockCommunity(
        retrofit: Retrofit,
        communityId: Int,
        auth: String,
        blockCommunityListener: BlockCommunityListener
    ) {
        val api = retrofit.create(LemmyAPI::class.java)
        api.communityBlock(BlockCommunityDTO(communityId, true, auth))?.enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        blockCommunityListener.onBlockCommunitySuccess()
                    } else {
                        blockCommunityListener.onBlockCommunityError()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    blockCommunityListener.onBlockCommunityError()
                }
            }
        )
    }

    fun unBlockCommunity(
        retrofit: Retrofit,
        communityId: Int,
        auth: String,
        blockCommunityListener: BlockCommunityListener
    ) {
        val api = retrofit.create(LemmyAPI::class.java)
        api.communityBlock(BlockCommunityDTO(communityId, false, auth))?.enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        blockCommunityListener.onBlockCommunitySuccess()
                    } else {
                        blockCommunityListener.onBlockCommunityError()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    blockCommunityListener.onBlockCommunityError()
                }
            }
        )
    }

    interface BlockCommunityListener {
        fun onBlockCommunitySuccess()
        fun onBlockCommunityError()
    }
}