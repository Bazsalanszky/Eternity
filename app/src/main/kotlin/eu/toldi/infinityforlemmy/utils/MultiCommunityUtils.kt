package eu.toldi.infinityforlemmy.utils

import eu.toldi.infinityforlemmy.post.Post

object MultiCommunityUtils {

    fun takeFirstN(posts: List<Post>, take: Int = 25): List<Post> {
        return posts.take(take)
    }

    fun sortByNewest(posts: List<Post>): List<Post> {
        return posts.sortedByDescending { it.postTimeMillis }
    }

    fun sortByOldest(posts: List<Post>): List<Post> {
        return posts.sortedBy { it.postTimeMillis }
    }

    fun sortByMostComments(posts: List<Post>): List<Post> {
        return posts.sortedByDescending { it.nComments }
    }

    fun sortByScore(posts: List<Post>): List<Post> {
        return posts.sortedByDescending { it.score }
    }

}