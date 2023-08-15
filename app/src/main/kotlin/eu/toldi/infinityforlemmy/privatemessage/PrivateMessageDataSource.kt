package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import eu.toldi.infinityforlemmy.NetworkState
import retrofit2.Retrofit
import java.util.Locale


class PrivateMessageDataSource(
    private val retrofit: Retrofit,
    private val locale: Locale,
    private val accessToken: String,
    private val mLemmyPrivateMessageAPI: LemmyPrivateMessageAPI
) :
    PageKeyedDataSource<Int, PrivateMessage>() {


    val paginationNetworkStateLiveData: MutableLiveData<NetworkState>
    val initialLoadStateLiveData: MutableLiveData<NetworkState>
    private val hasPostLiveData: MutableLiveData<Boolean>
    private var params: LoadParams<Int>? = null
    private var callback: LoadCallback<Int, PrivateMessage>? = null
    private val page = 1

    init {
        paginationNetworkStateLiveData = MutableLiveData()
        initialLoadStateLiveData = MutableLiveData()
        hasPostLiveData = MutableLiveData()
    }

    fun hasPostLiveData(): MutableLiveData<Boolean> {
        return hasPostLiveData
    }

    fun retryLoadingMore() {
        loadAfter(params!!, callback!!)
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PrivateMessage>
    ) {
        initialLoadStateLiveData.postValue(NetworkState.LOADING)
        mLemmyPrivateMessageAPI.fetchPrivateMessages(accessToken, page, object :
            LemmyPrivateMessageAPI.PrivateMessageFetchedListener {
            override fun onPrivateMessageFetchedSuccess(privateMessages: List<PrivateMessage>) {
                hasPostLiveData.postValue(true)
                if (privateMessages.isEmpty()) {
                    callback.onResult(ArrayList(), null, null)
                } else {
                    callback.onResult(privateMessages, null, page + 1)
                }
                initialLoadStateLiveData.postValue(NetworkState.LOADED)
            }

            override fun onPrivateMessageFetchedError() {
                initialLoadStateLiveData.postValue(
                    NetworkState(
                        NetworkState.Status.FAILED,
                        "Error fetch messages"
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

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, PrivateMessage>
    ) {
        this.params = params
        this.callback = callback
        paginationNetworkStateLiveData.postValue(NetworkState.LOADING)
        mLemmyPrivateMessageAPI.fetchPrivateMessages(accessToken, params.key, object :
            LemmyPrivateMessageAPI.PrivateMessageFetchedListener {
            override fun onPrivateMessageFetchedSuccess(privateMessages: List<PrivateMessage>) {
                hasPostLiveData.postValue(true)
                if (privateMessages.isEmpty()) {
                    callback.onResult(ArrayList(), null)
                } else {
                    callback.onResult(privateMessages, params.key + 1)
                }
                paginationNetworkStateLiveData.postValue(NetworkState.LOADED)
            }

            override fun onPrivateMessageFetchedError() {
                paginationNetworkStateLiveData.postValue(
                    NetworkState(
                        NetworkState.Status.FAILED,
                        "Error fetch messages"
                    )
                )
            }
        })
    }
}
