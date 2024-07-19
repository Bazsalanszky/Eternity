package eu.toldi.infinityforlemmy.markdown

import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.target.Target
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin.GlideStore

class GlideMarkdownLoader(val requestManager: RequestManager) : GlideStore {
    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
        return requestManager.load(drawable.destination).override(2048)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
    }

    override fun cancel(target: Target<*>) {
        requestManager.clear(target);
    }
}