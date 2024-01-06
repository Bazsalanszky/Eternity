package eu.toldi.infinityforlemmy.multicommunity

import androidx.paging.ListenableFuturePagingSource
import androidx.paging.PagingState
import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import eu.toldi.infinityforlemmy.SortType
import eu.toldi.infinityforlemmy.apis.LemmyAPI
import eu.toldi.infinityforlemmy.post.ParsePost
import eu.toldi.infinityforlemmy.post.Post
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher
import eu.toldi.infinityforlemmy.postfilter.PostFilter
import eu.toldi.infinityforlemmy.utils.MultiCommunityUtils.sortByNewest
import eu.toldi.infinityforlemmy.utils.MultiCommunityUtils.sortByOldest
import eu.toldi.infinityforlemmy.utils.MultiCommunityUtils.sortByScore
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor


class MulticommunityPagingSource(
    private val api: LemmyAPI,
    private val communities: List<String>,
    private val accessToken: String?,
    private val sortType: SortType,
    private val executor: Executor,
    private val postFilter: PostFilter,
    private val readPostList: List<String>, private val postEnricher: PostEnricher
) : ListenableFuturePagingSource<Map<String, Int>, Post>() {

    private val postLinkedHashSet: LinkedHashSet<Post> = LinkedHashSet()
    private val undisplayedPosts = mutableMapOf<String, MutableList<Post>>()

    override fun loadFuture(loadParams: LoadParams<Map<String, Int>>): ListenableFuture<LoadResult<Map<String, Int>, Post>> {
        val currentPageMap = loadParams.key ?: communities.associateWith { 1 }

        // Initialize a list to hold futures of post loading results
        val futuresList = mutableListOf<ListenableFuture<LoadResult<Int, Post>>>()
        val combinedPosts = mutableListOf<Post>()
        val wasCached = mutableMapOf<String, Boolean>()
        // Loop through each community and fetch posts
        for ((community, pageNumber) in currentPageMap) {
            if (community == "all") continue // Skip all
            if (undisplayedPosts.containsKey(community) && undisplayedPosts[community]!!.size > 10) {
                val posts: List<Post> = undisplayedPosts[community] ?: listOf()
                combinedPosts.addAll(posts)
                undisplayedPosts[community]!!.clear() // Clear used posts
                wasCached[community] = true
                // Add a dummy future to the list
                futuresList.add(Futures.immediateFuture(LoadResult.Page(posts, null, null)))
            } else {
                val future = fetchPostsFromCommunity(api, pageNumber, community)
                futuresList.add(future)
                wasCached[community] = false
            }
        }

        val combinedFuture: ListenableFuture<LoadResult<Map<String, Int>, Post>> =
            Futures.transform(Futures.successfulAsList(futuresList), { individualResults ->
                // Combine the results from individual communities

                val nextPageMap = mutableMapOf<String, Int>()

                for ((index, result) in individualResults.withIndex()) {
                    if (result is LoadResult.Page<Int, Post>) {
                        combinedPosts.addAll(result.data)
                        val addition = if (wasCached[communities[index]] == true) 0 else 1
                        nextPageMap[communities[index]] =
                            result.nextKey ?: (currentPageMap[communities[index]]?.plus(addition))
                                    ?: 1
                    }
                    // Handle other cases like LoadResult.Error
                }


                val sorted = when (sortType.type) {
                    SortType.Type.NEW -> {
                        sortByNewest(combinedPosts)
                    }

                    SortType.Type.OLD -> {
                        sortByOldest(combinedPosts)
                    }

                    SortType.Type.TOP_ALL, SortType.Type.TOP_YEAR, SortType.Type.TOP_NINE_MONTHS, SortType.Type.TOP_SIX_MONTHS, SortType.Type.TOP_THREE_MONTHS, SortType.Type.TOP_MONTH, SortType.Type.TOP_WEEK, SortType.Type.TOP_DAY, SortType.Type.TOP_TWELVE_HOURS, SortType.Type.TOP_SIX_HOURS, SortType.Type.TOP_HOUR
                    -> sortByScore(combinedPosts)

                    else -> {
                        sortByNewest(combinedPosts)
                    }
                }

                val filteredPosts = sorted.take(25)

                // Store undisplayed posts
                for (post in combinedPosts) {
                    if (!filteredPosts.contains(post)) {
                        if (undisplayedPosts.containsKey(post.subredditNamePrefixed)) {
                            undisplayedPosts[post.subredditNamePrefixed]?.add(post)
                        } else {
                            undisplayedPosts[post.subredditNamePrefixed] = mutableListOf(post)
                        }
                    }
                }

                if (currentPageMap.containsKey("all")) {
                    nextPageMap["all"] = filteredPosts.size + currentPageMap["all"]!!
                } else {
                    nextPageMap["all"] = filteredPosts.size
                }



                LoadResult.Page(
                    data = filteredPosts,
                    prevKey = null, // Define prevKey logic if needed
                    nextKey = if (filteredPosts.isNotEmpty()) nextPageMap else null
                )
            }, executor)
        val partialLoadResultFuture =
            Futures.catching<LoadResult<Map<String, Int>, Post>, HttpException>(
                combinedFuture,
                HttpException::class.java,
                Function<HttpException, LoadResult<Map<String, Int>, Post>> { throwable: HttpException ->
                    LoadResult.Error(throwable)
                }, executor
            )

        return partialLoadResultFuture

    }

    private fun fetchPostsFromCommunity(
        api: LemmyAPI,
        pageNumber: Int,
        community: String
    ): ListenableFuture<LoadResult<Int, Post>> {
        val subredditPost: ListenableFuture<Response<String>>
        subredditPost = api.getPostsListenableFuture(
            null,
            sortType.getType().value,
            pageNumber,
            25,
            null,
            community,
            false,
            accessToken
        )
        val communityFuture: ListenableFuture<LoadResult<Int, Post>> = Futures.transform(
            subredditPost,
            { response -> transformData(response, pageNumber) }, executor
        )

        val partialLoadResultFuture: ListenableFuture<LoadResult<Int, Post>> =
            Futures.catching(
                communityFuture,
                HttpException::class.java,
                { throwable -> LoadResult.Error<Int, Post>(throwable) },
                executor
            )

        return Futures.catching(
            partialLoadResultFuture,
            IOException::class.java,
            Function<IOException, LoadResult<Int, Post>> { throwable ->
                LoadResult.Error<Int, Post>(throwable)
            }, executor
        )
    }


    fun transformData(response: Response<String>, pageNumber: Int): LoadResult<Int, Post> {
        if (!response.isSuccessful) return LoadResult.Error(Exception("Response failed"))

        val responseString =
            response.body() ?: return LoadResult.Error(Exception("Empty response body"))
        val newPosts =
            ParsePost.parsePostsSync(responseString, -1, postFilter, readPostList, postEnricher)
                ?: return LoadResult.Error(Exception("Error parsing posts"))

        // Filter out already linked posts
        val trulyNewPosts = newPosts.filterNot(postLinkedHashSet::contains)
        postLinkedHashSet.addAll(trulyNewPosts)

        val nextKey = if (trulyNewPosts.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(trulyNewPosts, null, nextKey)
    }


    override fun getRefreshKey(pagingState: PagingState<Map<String, Int>, Post>): Map<String, Int>? {
        return null
    }

    // Additional methods and logic...
}
