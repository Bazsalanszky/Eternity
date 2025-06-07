package eu.toldi.infinityforlemmy.apis.apihandler

import android.os.Handler
import android.widget.Toast
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase
import eu.toldi.infinityforlemmy.SortType
import eu.toldi.infinityforlemmy.apis.LemmyBetaAPI
import eu.toldi.infinityforlemmy.apis.apihandler.ApiHandler.SubmitPostListener
import eu.toldi.infinityforlemmy.comment.Comment
import eu.toldi.infinityforlemmy.comment.ParseComment
import eu.toldi.infinityforlemmy.comment.ParseComment.ParseCommentListener
import eu.toldi.infinityforlemmy.comment.ParseComment.ParseSentCommentListener
import eu.toldi.infinityforlemmy.commentfilter.CommentFilter
import eu.toldi.infinityforlemmy.dto.*
import eu.toldi.infinityforlemmy.post.ParsePost
import eu.toldi.infinityforlemmy.post.ParsePost.ParsePostListener
import eu.toldi.infinityforlemmy.post.Post
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher
import eu.toldi.infinityforlemmy.site.SiteInfo
import eu.toldi.infinityforlemmy.subreddit.ParseSubredditData
import eu.toldi.infinityforlemmy.subreddit.ParseSubredditData.ParseSubredditDataListener
import eu.toldi.infinityforlemmy.subreddit.SubredditData
import eu.toldi.infinityforlemmy.user.MyUserInfo
import eu.toldi.infinityforlemmy.user.ParseUserData
import eu.toldi.infinityforlemmy.user.ParseUserData.ParseUserDataListener
import eu.toldi.infinityforlemmy.user.UserData
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.Executor

class LemmyBetaApiHandler(private val retrofit: Retrofit) : ApiHandler {

    private val api: LemmyBetaAPI = retrofit.create(LemmyBetaAPI::class.java)

    // Community Block
    override fun blockCommunity(
        communityId: Int,
        auth: String,
        listener: ApiHandler.BlockCommunityListener
    ) {
        api.communityBlock(BlockCommunityDTO(communityId, true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    override fun unblockCommunity(
        communityId: Int,
        auth: String,
        listener: ApiHandler.BlockCommunityListener
    ) {
        api.communityBlock(BlockCommunityDTO(communityId, false, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Instance Block
    override fun blockInstance(
        instanceId: Int,
        block: Boolean,
        listener: ApiHandler.BlockInstanceListener
    ) {



        api.blockInstance(BlockInstanceDTO(instanceId, block))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onResponse()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    override fun unblockInstance(
        instanceId: Int,
        listener: ApiHandler.BlockInstanceListener
    ) {
        api.blockInstance(BlockInstanceDTO(instanceId, false))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onResponse()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // User Block
    override fun blockUser(
        userId: Int,
        auth: String,
        listener: ApiHandler.BlockUserListener
    ) {
        api.userBlock(UserBlockDTO(userId, true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.failed()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.failed()
                }
            })

    }

    override fun unblockUser(
        userId: Int,
        auth: String,
        listener: ApiHandler.BlockUserListener
    ) {
        api.userBlock(UserBlockDTO(userId, false, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.failed()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.failed()
                }
            })

    }

    // Report Post
    override fun reportPost(
        postId: Int,
        reason: String,
        auth: String,
        listener: ApiHandler.ReportPostListener
    ) {
        api.postReport(ReportPostDTO(postId, reason, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Save Post
    override fun savePost(
        postId: Int,
        auth: String,
        listener: ApiHandler.SavePostListener
    ) {
        api.postSave(SavePostDTO(postId, true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    override fun unsavePost(
        postId: Int,
        auth: String,
        listener: ApiHandler.SavePostListener
    ) {
        api.postSave(SavePostDTO(postId, false, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                    listener.onFailure()
                }
            })

    }

    override fun reportComment(
        commentId: Int,
        reason: String,
        auth: String,
        listener: ApiHandler.ReportCommentListener
    ) {
        TODO("Not yet implemented")
    }

    // Save Comment
    override fun saveComment(
        commentId: Int,
        auth: String,
        listener: ApiHandler.SaveCommentListener
    ) {
        api.commentSave(SaveCommentDTO(commentId, true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    override fun unsaveComment(
        commentId: Int,
        auth: String,
        listener: ApiHandler.SaveCommentListener
    ) {
        api.commentSave(SaveCommentDTO(commentId, false, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    listener.onFailure()
                }
            })

    }

    // Submit Post
    override fun submitPost(executor:Executor,
                            handler: Handler, accessToken: String,
                            communityId: Int, title: String, content: String,
                            isNSFW: Boolean,
                            receivePostReplyNotifications: Boolean, kind: String,
                            posterUrl: String?, postEnricher: PostEnricher,
                            submitPostListener: SubmitPostListener
    ){
        val submitPostCall = api.postCreate(
            SubmitPostDTO(
                title,
                communityId,
                posterUrl,
                content,
                null,
                isNSFW,
                null,
                accessToken
            )
        )

        try {
            val response = submitPostCall.execute()
            if (response.isSuccessful) {


                ParsePost.parsePost(
                    executor,
                    handler,
                    postEnricher,
                    response.body(),
                    object : ParsePostListener {
                        override fun onParsePostSuccess(post: Post) {
                            submitPostListener.success(post)
                        }

                        override fun onParsePostFail() {
                            submitPostListener.onFailure(null)
                        }
                    })
            } else {
                submitPostListener.onFailure(response.message())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            submitPostListener.onFailure(e.message)
        } catch (e: JSONException) {
            e.printStackTrace()
            submitPostListener.onFailure(e.message)
        }


    }

    // Submit Comment
    override fun submitComment(
        executor:Executor,
        handler: Handler,
        postId: Int,
        content: String,
        parentId: Int?,
        auth: String,
        listener: ApiHandler.SubmitCommentListener
    ) {
        api.postComment(
            CommentDTO(content, postId, parentId, null, null,  auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        ParseComment.parseSentComment(
                            executor,
                            handler,
                            response.body(),
                            object : ParseSentCommentListener {
                                override fun onParseSentCommentSuccess(comment: Comment) {
                                    listener.success(comment)
                                }

                                override fun onParseSentCommentFailed(errorMessage: String?) {
                                    listener.onFailure(errorMessage)
                                }
                            })
                    } else {
                        listener.onFailure(null)
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure(null)
                }
            })

    }

    override fun editComment(
        commentId: Int,
        content: String,
        auth: String,
        listener: ApiHandler.EditCommentListener
    ) {
        TODO("Not yet implemented")
    }

    // Edit Post
    override fun editPost(
        postId: Int,
        title: String,
        content: String,
        posterUrl: String,
        isNSFW: Boolean,
        auth: String,
        listener: ApiHandler.EditPostListener
    ) {
        api.postUpdate(
            EditPostDTO(postId, title, content,posterUrl, isNSFW,null, auth)
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                       listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Delete Post
    override fun deletePost(
        postId: Int,
        auth: String,
        listener: ApiHandler.DeletePostListener
    ) {
        api.postDelete(DeletePostDTO(postId,true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Delete Comment
    override fun deleteComment(
        commentId: Int,
        auth: String,
        listener: ApiHandler.DeleteCommentListener
    ) {
        api.commentDelete(DeleteCommentDTO(commentId,true, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.success()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Fetch Comments
    override fun fetchComments(
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
        listener: ApiHandler.FetchCommentListListener
    ) {
        val comments = api.getComments(
            "All",
            sortType.value,
            8,
            page,
            25,
            null,
            null,
            article,
            commentId,
            false,
            accessToken
        )


        comments.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    ParseComment.parseComments(
                        executor, handler, response.body(), commentId,
                        expandChildren, commentFilter,
                        object : ParseCommentListener {
                            override fun onParseCommentSuccess(
                                topLevelComments: ArrayList<Comment>,
                                expandedComments: ArrayList<Comment>,
                                parentId: Int, moreChildrenIds: ArrayList<Int>
                            ) {
                                listener.onSuccess( topLevelComments, expandedComments, parentId, moreChildrenIds)
                            }

                            override fun onParseCommentFailed() {
                                listener.onFailure()
                            }
                        })
                } else {
                    listener.onFailure()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                listener.onFailure()
            }
        })
    }

    // Fetch Subscribed Things
    override fun fetchSubscribedThings(
        retrofit: Retrofit,
        accessToken: String,
        accountName: String,
        listener: ApiHandler.FetchSubscribedThingsListener
    ) {
        api.listCommunities("Subscribed", null, 1, null, accessToken)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        // Parse and return the data
                        listener.onSuccess(listOf(), listOf(), listOf())
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Fetch Subreddit Data
    override fun fetchCommunityData(
        retrofit: Retrofit,
        subredditName: String,
        accessToken: String,
        listener: ApiHandler.FetchCommunityDataListener
    ) {
        api.communityInfo(subredditName, accessToken)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        ParseSubredditData.parseSubredditData(
                            response.body(),
                            object : ParseSubredditDataListener {
                                override fun onParseSubredditDataSuccess(
                                    subredditData: SubredditData,
                                    nCurrentOnlineSubscribers: Int
                                ) {
                                    listener.onSuccess(
                                        subredditData
                                    )
                                }

                                override fun onParseSubredditDataFail() {
                                    listener.onFailure()
                                }
                            })
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Fetch Post
    override fun fetchPost(
        executor: Executor,
        handler: Handler,
        postId: Int,
        accessToken: String,
        postEnricher: PostEnricher,
        listener: ApiHandler.FetchPostListener
    ) {
        api.postInfo(postId, null, accessToken)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        ParsePost.parsePost(
                            executor,
                            handler,
                            postEnricher,
                            response.body(),
                            object : ParsePostListener {
                                override fun onParsePostSuccess(post: Post) {
                                    listener.onSuccess(post)
                                }

                                override fun onParsePostFail() {
                                    listener.onFailure()
                                }
                            })
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Fetch User
    override fun fetchUser(
        redditDataRoomDatabase: RedditDataRoomDatabase,
        username: String,
        auth: String?,
        listener: ApiHandler.FetchUserListener
    ) {
        api.userInfo(username,auth)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        ParseUserData.parseUserData(
                            redditDataRoomDatabase,
                            response.body(),
                            object : ParseUserDataListener {
                                override fun onParseUserDataSuccess(
                                    userData: UserData,
                                    inboxCount: Int
                                ) {
                                    listener.onSuccess(
                                        userData
                                    )
                                }

                                override fun onParseUserDataFailed() {
                                    listener.onFailure()
                                }
                            })
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Fetch Site Info
    override fun fetchSiteInfo(
        retrofit: Retrofit,
        accessToken: String,
        listener: ApiHandler.FetchSiteInfoListener
    ) {
        api.getSiteInfo(accessToken)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val siteInfoJson = response.body()
                            val siteInfo = SiteInfo.parseSiteInfo(siteInfoJson)
                            val myUserInfo = MyUserInfo.parseFromSiteInfo(siteInfoJson)
                            listener.onSuccess(siteInfo, myUserInfo)
                        } catch (e: JSONException) {
                            listener.onFailure(true)
                        }
                    } else {
                        listener.onFailure(false)
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure(false)
                }
            })

    }

    // Fetch Comment
    override fun fetchComment(
        commentId: Int,
        accessToken: String,
        listener: ApiHandler.FetchCommentListener
    ) {
        api.getComment(commentId, accessToken)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val comment = ParseComment.parseSingleComment(
                                response.body()
                                    ?.let { JSONObject(it).getJSONObject("comment_view") }
                            )
                            listener.onSuccess(comment)
                        } catch (e: JSONException) {
                            listener.onFailure()
                        }
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Mark Post as Read
    override fun markPostAsRead(
        postId: Int,
        isRead: Boolean,
        auth: String,
        listener: ApiHandler.MarkPostAsReadListener
    ) {
        api.postRead(ReadPostDTO(postId, isRead, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    // Mark Comment as Read
    override fun markCommentAsRead(
        commentId: Int,
        isRead: Boolean,
        auth: String,
        listener: ApiHandler.MarkCommentAsReadListener
    ) {
        api.commentMarkAsRead(ReadCommentDTO(commentId, isRead, auth))
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<String>,
                    t: Throwable
                ) {
                    listener.onFailure()
                }
            })

    }

    override fun votePost(
        postId: Int,
        point: Int,
        accessToken: String,
        listener: ApiHandler.VoteListener
    ) {
        val voteThingCall = api.postLike(PostVoteDTO(postId, point, accessToken))
        voteThingCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    listener.onVoteThingSuccess()
                } else {
                    listener.onVoteThingFail()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                listener.onVoteThingFail()
            }
        })
    }



    override fun voteComment(
        commentId: Int,
        point: Int,
        accessToken: String,
        listener: ApiHandler.VoteListener
    ) {
        val voteThingCall = api.commentLike(CommentVoteDTO(commentId, point, accessToken))
        voteThingCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    listener.onVoteThingSuccess()
                } else {
                    listener.onVoteThingFail()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                listener.onVoteThingFail()
            }
        })
    }

}