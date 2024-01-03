package eu.toldi.infinityforlemmy.post.enrich

import android.util.Log
import eu.toldi.infinityforlemmy.apis.RedgifsAPI
import eu.toldi.infinityforlemmy.post.Post
import eu.toldi.infinityforlemmy.post.Post.Preview
import eu.toldi.infinityforlemmy.utils.JSONUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RedGifsPostEnricher(private val redgifsAPI: RedgifsAPI) : PostEnricher {
    override fun enrich(posts: Collection<Post>) {
        val redGifsPosts = posts.filter { it.isRedgifs && it.previews.isEmpty() }
            .groupBy { it.redgifsId }

        if (redGifsPosts.isEmpty()) {
            return
        }

        try {
            val response = redgifsAPI.getRedgifsMultipleData(redGifsPosts.keys.joinToString(",")).execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                try {
                    val gifsJson = JSONObject(body).getJSONArray(JSONUtils.GIFS_KEY)
                    for (i in 0 until gifsJson.length()) {
                        val gifJson = gifsJson.getJSONObject(i)
                        val id = gifJson.getString(JSONUtils.ID_KEY)
                        val width = gifJson.getInt(JSONUtils.WIDTH_KEY)
                        val height = gifJson.getInt(JSONUtils.HEIGHT_KEY)
                        val thumbnail = gifJson.getJSONObject(JSONUtils.URLS_KEY)
                            .getString(JSONUtils.THUMBNAIL_KEY)

                        val previews = ArrayList(listOf(Preview(thumbnail, width, height, null, null)))

                        redGifsPosts[id]?.forEach {
                            it.previews = previews
                        }
                    }

                } catch (e: JSONException) {
                    Log.w(TAG, "Failed to parse JSON", e)
                }
            } else {
                Log.w(TAG, "Failed fetch data. Status code ${response.code()}")
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed fetch data", e)
        }
    }

    companion object {
        private const val TAG = "RedGifsPostEnricher"
    }
}
