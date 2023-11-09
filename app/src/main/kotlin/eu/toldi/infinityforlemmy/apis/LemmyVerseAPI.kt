package eu.toldi.infinityforlemmy.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers


interface LemmyVerseAPI {

    @Headers("Content-Type: application/json")
    @GET("/data/instance.min.json")
    fun getInstanceList(): Call<String?>?
}