package eu.toldi.infinityforlemmy.post.enrich

import eu.toldi.infinityforlemmy.post.Post

interface PostEnricher {
    fun enrich(posts: Collection<Post>)
}
