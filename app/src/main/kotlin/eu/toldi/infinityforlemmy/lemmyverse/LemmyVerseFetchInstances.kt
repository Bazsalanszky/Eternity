package eu.toldi.infinityforlemmy.lemmyverse

import eu.toldi.infinityforlemmy.apis.LemmyVerseAPI
import retrofit2.Retrofit

object LemmyVerseFetchInstances {

    fun fetchInstances(
        lemmyVerseRetrofit: Retrofit,
        fetchInstancesListener: FetchInstancesListener
    ) {
        val lemmyVerseAPI = lemmyVerseRetrofit.create(LemmyVerseAPI::class.java)
        val call = lemmyVerseAPI.getInstanceList()
        call?.enqueue(object : retrofit2.Callback<String?> {
            override fun onResponse(
                call: retrofit2.Call<String?>,
                response: retrofit2.Response<String?>
            ) {
                if (response.isSuccessful) {
                    val instances = LemmyVerseParseInstances.parseInstances(response.body())
                    fetchInstancesListener.onFetchInstancesSuccess(instances)
                } else {
                    fetchInstancesListener.onFetchInstancesSuccess(listOf())
                }
            }

            override fun onFailure(call: retrofit2.Call<String?>, t: Throwable) {
                fetchInstancesListener.onFetchInstancesSuccess(listOf())
            }
        })
    }


}

fun interface FetchInstancesListener {

    fun onFetchInstancesSuccess(instances: List<LemmyInstance>)
}