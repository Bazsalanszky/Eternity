package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import eu.toldi.infinityforlemmy.NetworkState
import retrofit2.Retrofit
import java.util.Locale


class PrivateMessageViewModel(
    retrofit: Retrofit,
    locale: Locale,
    accessToken: String,
    lemmyPrivateMessageAPI: LemmyPrivateMessageAPI
) :
    ViewModel() {
    private val messageDataSourceFactory: PrivateMessageDataSourceFactory
    val paginationNetworkState: LiveData<NetworkState>
    val initialLoadingState: LiveData<NetworkState>
    private val hasMessageLiveData: LiveData<Boolean>
    val messages: LiveData<PagedList<PrivateMessage>>
    private val whereLiveData: MutableLiveData<LemmyPrivateMessageAPI> = MutableLiveData()

    init {
        messageDataSourceFactory =
            PrivateMessageDataSourceFactory(retrofit, locale, accessToken, lemmyPrivateMessageAPI)
        initialLoadingState = Transformations.switchMap(
            messageDataSourceFactory.getMessageDataSourceLiveData(),
            PrivateMessageDataSource::initialLoadStateLiveData
        )
        paginationNetworkState = Transformations.switchMap(
            messageDataSourceFactory.getMessageDataSourceLiveData(),
            PrivateMessageDataSource::paginationNetworkStateLiveData
        )
        hasMessageLiveData = Transformations.switchMap(
            messageDataSourceFactory.getMessageDataSourceLiveData(),
            PrivateMessageDataSource::hasPostLiveData
        )

        whereLiveData.postValue(lemmyPrivateMessageAPI)

        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(25)
            .build()
        messages = Transformations.switchMap<LemmyPrivateMessageAPI, PagedList<PrivateMessage>>(
            whereLiveData
        ) { newWhere: LemmyPrivateMessageAPI? ->
            messageDataSourceFactory.changeLemmPrivateMessageAPI(whereLiveData.value!!)
            LivePagedListBuilder(
                messageDataSourceFactory,
                pagedListConfig
            ).build()
        }

    }

    fun hasMessage(): LiveData<Boolean> {
        return hasMessageLiveData
    }

    fun refresh() {
        messageDataSourceFactory.getMessageDataSource().invalidate()
    }

    fun retryLoadingMore() {
        messageDataSourceFactory.getMessageDataSource().retryLoadingMore()
    }

    class Factory(
        private val retrofit: Retrofit,
        private val locale: Locale,
        private val accessToken: String,
        private val lemmyPrivateMessageAPI: LemmyPrivateMessageAPI
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrivateMessageViewModel(
                retrofit,
                locale,
                accessToken,
                lemmyPrivateMessageAPI
            ) as T
        }
    }
}
