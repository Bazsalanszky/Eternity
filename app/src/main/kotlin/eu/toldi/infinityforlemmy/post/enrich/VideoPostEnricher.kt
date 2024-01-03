package eu.toldi.infinityforlemmy.post.enrich

import android.media.MediaMetadataRetriever
import android.util.Log
import eu.toldi.infinityforlemmy.post.Post

class VideoPostEnricher : PostEnricher {
    override fun enrich(posts: Collection<Post>) {
        for (post in posts) {
            if (post.postType != Post.VIDEO_TYPE || post.previews.isNotEmpty()) {
                continue
            }

            val url = post.videoUrl ?: continue
            if (!url.endsWith(".mp4", ignoreCase = true) &&
                !url.endsWith(".webm", ignoreCase = true)
            ) {
                continue
            }

            val retriever = try {
                MediaMetadataRetriever().apply {
                    setDataSource(url);
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error retrieving metadata", e)
                continue
            }

            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: -1
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: -1

            // Glide can extract thumbnails from video URLs (it uses MediaMetadataRetriever too)
            post.previews = ArrayList(listOf(Post.Preview(url, width, height, null, null)))
        }
    }

    companion object {
        private const val TAG = "VideoPostEnricher"
    }
}
