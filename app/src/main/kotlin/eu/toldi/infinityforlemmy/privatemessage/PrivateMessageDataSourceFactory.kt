package eu.toldi.infinityforlemmy.privatemessage

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import retrofit2.Retrofit
import java.util.Locale


internal class PrivateMessageDataSourceFactory(
    retrofit: Retrofit,
    locale: Locale,
    accessToken: String,
    private var mLemmyPrivateMessageAPI: LemmyPrivateMessageAPI
) : DataSource.Factory<Int, PrivateMessage>() {
    private val mPrivateMessageDataSource: PrivateMessageDataSource =
        PrivateMessageDataSource(retrofit, locale, accessToken, mLemmyPrivateMessageAPI)
    private val messageDataSourceLiveData: MutableLiveData<PrivateMessageDataSource> =
        MutableLiveData<PrivateMessageDataSource>()

    override fun create(): DataSource<Int, PrivateMessage> {
        messageDataSourceLiveData.postValue(mPrivateMessageDataSource)
        return mPrivateMessageDataSource
    }

    fun getMessageDataSourceLiveData(): MutableLiveData<PrivateMessageDataSource> {
        return messageDataSourceLiveData
    }

    fun getMessageDataSource(): PrivateMessageDataSource {
        return mPrivateMessageDataSource
    }

    fun changeLemmPrivateMessageAPI(lemmyPrivateMessageAPI: LemmyPrivateMessageAPI) {
        this.mLemmyPrivateMessageAPI = lemmyPrivateMessageAPI
    }

}
