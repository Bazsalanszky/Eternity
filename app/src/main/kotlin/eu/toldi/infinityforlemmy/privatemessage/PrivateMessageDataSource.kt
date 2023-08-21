package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import eu.toldi.infinityforlemmy.NetworkState


class PrivateMessageDataSource(
    private val mLemmyPrivateMessageAPI: LemmyPrivateMessageAPI,
    private val accessToken: String
) : PageKeyedDataSource<Int, PrivateMessage>() {

    val initialLoadStateLiveData = MutableLiveData<NetworkState>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PrivateMessage>
    ) {
        mLemmyPrivateMessageAPI.fetchPrivateMessages(accessToken, 1, object :
            LemmyPrivateMessageAPI.PrivateMessageFetchedListener {
            override fun onPrivateMessageFetchedSuccess(privateMessages: List<PrivateMessage>) {
                initialLoadStateLiveData.postValue(NetworkState.LOADED)
                if (privateMessages.isEmpty()) {
                    callback.onResult(ArrayList(), null, null)
                } else {
                    callback.onResult(privateMessages, null, 2)
                }
            }

            override fun onPrivateMessageFetchedError() {
                initialLoadStateLiveData.postValue(
                    NetworkState(
                        NetworkState.Status.FAILED,
                        "Error fetching messages"
                    )
                )
            }
        })
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PrivateMessage>
    ) {
        mLemmyPrivateMessageAPI.fetchPrivateMessages(accessToken, params.key, object :
            LemmyPrivateMessageAPI.PrivateMessageFetchedListener {
            override fun onPrivateMessageFetchedSuccess(privateMessages: List<PrivateMessage>) {
                initialLoadStateLiveData.postValue(NetworkState.LOADED)
                if (privateMessages.isEmpty()) {
                    callback.onResult(ArrayList(), null)
                } else {
                    callback.onResult(privateMessages, params.key + 1)
                }

            }

            override fun onPrivateMessageFetchedError() {
                initialLoadStateLiveData.postValue(
                    NetworkState(
                        NetworkState.Status.FAILED,
                        "Error fetching messages"
                    )
                )
            }
        })
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PrivateMessage>
    ) {

    }

    fun refresh() {
        invalidate()
    }
}
