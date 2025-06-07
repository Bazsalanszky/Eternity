package eu.toldi.infinityforlemmy.apis.apihandler

import android.os.Handler
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase
import eu.toldi.infinityforlemmy.SortType
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityData
import eu.toldi.infinityforlemmy.blockedinstances.BlockedInstanceData
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserData
import eu.toldi.infinityforlemmy.comment.Comment
import eu.toldi.infinityforlemmy.commentfilter.CommentFilter
import eu.toldi.infinityforlemmy.post.Post
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher
import eu.toldi.infinityforlemmy.site.SiteInfo
import eu.toldi.infinityforlemmy.subreddit.SubredditData
import eu.toldi.infinityforlemmy.user.MyUserInfo
import eu.toldi.infinityforlemmy.user.UserData
import retrofit2.Retrofit
import java.util.concurrent.Executor

interface ApiHandler {
    // Community-related
    fun blockCommunity(
        communityId: Int,
        auth: String,
        listener: BlockCommunityListener
    )
    fun unblockCommunity(
        communityId: Int,
        auth: String,
        listener: BlockCommunityListener
    )

    // Instance-related
    fun blockInstance(
        instanceId: Int,
        block: Boolean,
        listener: BlockInstanceListener
    )
    fun unblockInstance(
        instanceId: Int,
        listener: BlockInstanceListener
    )

    // User-related
    fun blockUser(
        userId: Int,
        auth: String,
        listener: BlockUserListener
    )
    fun unblockUser(
        userId: Int,
        auth: String,
        listener: BlockUserListener
    )

    // Post-related
    fun reportPost(
        postId: Int,
        reason: String,
        auth: String,
        listener: ReportPostListener
    )
    fun savePost(
        postId: Int,
        auth: String,
        listener: SavePostListener
    )
    fun unsavePost(
        postId: Int,
        auth: String,
        listener: SavePostListener
    )

    // Comment-related
    fun reportComment(
        commentId: Int,
        reason: String,
        auth: String,
        listener: ReportCommentListener
    )
    fun saveComment(
        commentId: Int,
        auth: String,
        listener: SaveCommentListener
    )
    fun unsaveComment(
        commentId: Int,
        auth: String,
        listener: SaveCommentListener
    )

    // Post-related (submit, edit, delete)
    fun submitPost(
        executor: Executor,
        handler: Handler,
        accessToken: String,
        communityId: Int,
        title: String,
        content: String,
        isNSFW: Boolean,
        receivePostReplyNotifications: Boolean,
        kind: String,
        posterUrl: String?,
        postEnricher: PostEnricher,
        submitPostListener: SubmitPostListener
    )
    fun editPost(
        postId: Int,
        title: String,
        content: String,
        posterUrl: String,
        isNSFW: Boolean,
        auth: String,
        listener: ApiHandler.EditPostListener
    )
    fun deletePost(
        postId: Int,
        auth: String,
        listener: DeletePostListener
    )

    // Comment-related (submit, edit, delete)
    fun submitComment(
        executor:Executor,
        handler: Handler,
        postId: Int,
        content: String,
        parentId: Int?,
        auth: String,
        listener: SubmitCommentListener
    )
    fun editComment(
        commentId: Int,
        content: String,
        auth: String,
        listener: EditCommentListener
    )
    fun deleteComment(
        commentId: Int,
        auth: String,
        listener: DeleteCommentListener
    )

    // Fetch-related
    fun fetchComments(
        executor: Executor,
        handler: Handler,
        retrofit: Retrofit,
        accessToken: String?,
        article: Int,
        commentId: Int,
        sortType: SortType.Type,
        expandChildren: Boolean,
        page: Int,
        commentFilter: CommentFilter,
        listener: FetchCommentListListener
    )


    fun fetchSubscribedThings(
        retrofit: Retrofit,
        accessToken: String,
        accountName: String,
        listener: FetchSubscribedThingsListener
    )
    fun fetchCommunityData(
        retrofit: Retrofit,
        subredditName: String,
        accessToken: String,
        listener: FetchCommunityDataListener
    )
    fun fetchPost(
        executor: Executor,
        handler: Handler,
        postId: Int,
        accessToken: String,
        postEnricher: PostEnricher,
        listener: ApiHandler.FetchPostListener
    )
    fun fetchUser(
        redditDataRoomDatabase: RedditDataRoomDatabase,
        username: String,
        auth: String?,
        listener: ApiHandler.FetchUserListener
    )
    fun fetchSiteInfo(
        retrofit: Retrofit,
        accessToken: String,
        listener: FetchSiteInfoListener
    )
    fun fetchComment(
        commentId: Int,
        accessToken: String,
        listener: FetchCommentListener
    )

    // Other operations
    fun markPostAsRead(
        postId: Int,
        isRead: Boolean,
        auth: String,
        listener: MarkPostAsReadListener
    )
    fun markCommentAsRead(
        commentId: Int,
        isRead: Boolean,
        auth: String,
        listener: MarkCommentAsReadListener
    )

    //Voting
    fun votePost(
        postId: Int,
        point: Int,
        accessToken: String,
        listener: VoteListener
    )


    fun voteComment(
        commentId: Int,
        point: Int,
        accessToken: String,
        listener: VoteListener
    )

    interface BlockCommunityListener {
        fun onSuccess()
        fun onFailure()
    }

    interface BlockInstanceListener {
        fun onResponse()
        fun onFailure()
    }

    interface BlockUserListener {
        fun success()
        fun failed()
    }

    interface ReportPostListener {
        fun success()
        fun onFailure()
    }

    interface SavePostListener {
        fun success()
        fun onFailure()
    }

    interface SaveCommentListener {
        fun success()
        fun onFailure()
    }

    interface SubmitPostListener {
        fun success(post: Post)
        fun onFailure(error: String?)
    }

    interface SubmitCommentListener {
        fun success(comment: Comment)
        fun onFailure(error: String?)
    }

    interface EditPostListener {
        fun success()
        fun onFailure()
    }

    interface EditCommentListener {
        fun success(comment: Comment)
        fun onFailure()
    }

    interface DeletePostListener {
        fun success()
        fun onFailure()
    }

    interface DeleteCommentListener {
        fun success()
        fun onFailure()
    }

    interface FetchCommentsListener {
        fun onSuccess(comments: List<Comment>)
        fun onFailure()
    }

    interface FetchSubscribedThingsListener {
        fun onSuccess(blockedUsers: List<BlockedUserData>, blockedCommunities: List<BlockedCommunityData>, blockedInstances: List<BlockedInstanceData>)
        fun onFailure()
    }

    interface FetchCommunityDataListener {
        fun onSuccess(subredditData: SubredditData)
        fun onFailure()
    }

    interface FetchPostListener {
        fun onSuccess(post: Post)
        fun onFailure()
    }

    interface FetchUserListener {
        fun onSuccess(userData: UserData)
        fun onFailure()
    }

    interface FetchSiteInfoListener {
        fun onSuccess(siteInfo: SiteInfo, myUserInfo: MyUserInfo)
        fun onFailure(parseFailed: Boolean)
    }

    interface FetchCommentListListener {
        fun onSuccess(topLevelComments: ArrayList<Comment>,
                      expandedComments: ArrayList<Comment>,
                      parentId: Int, moreChildrenIds: ArrayList<Int>)
        fun onFailure()
    }

    interface FetchCommentListener {
        fun onSuccess(comment: Comment)
        fun onFailure()
    }

    interface MarkPostAsReadListener {
        fun onSuccess()
        fun onFailure()
    }

    interface MarkCommentAsReadListener {
        fun onSuccess()
        fun onFailure()
    }

    interface VoteListener {
        fun onVoteThingSuccess()
        fun onVoteThingFail()
    }


    interface ReportCommentListener {
        fun onSuccess()
        fun onFailure()
    }
}


