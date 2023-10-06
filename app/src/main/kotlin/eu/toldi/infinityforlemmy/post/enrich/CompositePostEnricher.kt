package eu.toldi.infinityforlemmy.post.enrich

import eu.toldi.infinityforlemmy.post.Post

class CompositePostEnricher(private val enrichers: Set<PostEnricher>) : PostEnricher {
    override fun enrich(posts: Collection<Post>) {
        for (enricher in enrichers) {
            enricher.enrich(posts)
        }
    }
}
