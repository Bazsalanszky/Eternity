package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


class PrivateMessageDataSourceFactory(
    private val mLemmyPrivateMessageAPI: LemmyPrivateMessageAPI,
    private val accessToken: String
) : DataSource.Factory<Int, PrivateMessage>() {

    val dataSourceLiveData = MutableLiveData<PrivateMessageDataSource>()

    override fun create(): DataSource<Int, PrivateMessage> {
        val dataSource = PrivateMessageDataSource(mLemmyPrivateMessageAPI, accessToken)
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }
}

