package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import retrofit2.Retrofit
import java.util.Locale


class PrivateMessageViewModel(
    private val mLemmyPrivateMessageAPI: LemmyPrivateMessageAPI,
    private val accessToken: String
) : ViewModel() {

    private val dataSourceFactory =
        PrivateMessageDataSourceFactory(mLemmyPrivateMessageAPI, accessToken)

    val privateMessages = LivePagedListBuilder(
        dataSourceFactory, PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()
    )
        .build()

    val initialLoadState = Transformations.switchMap(dataSourceFactory.dataSourceLiveData) {
        it.initialLoadStateLiveData
    }

    fun refresh() {
        dataSourceFactory.dataSourceLiveData.value?.refresh()
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
                lemmyPrivateMessageAPI,
                accessToken,
            ) as T
        }
    }
}
