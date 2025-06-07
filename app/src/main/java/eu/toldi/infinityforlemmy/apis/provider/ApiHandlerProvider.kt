package eu.toldi.infinityforlemmy.apis.provider

import android.util.Log
import eu.toldi.infinityforlemmy.apis.apihandler.ApiHandler
import eu.toldi.infinityforlemmy.apis.apihandler.LemmyBetaApiHandler
import eu.toldi.infinityforlemmy.network.SortTypeConverterFactory
import eu.toldi.infinityforlemmy.utils.APIUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiHandlerProvider(private val okHttpClientBase: OkHttpClient) {

    private var _baseURL = APIUtils.API_BASE_URI
    private var retrofit = createRetrofit(okHttpClientBase,_baseURL)
    private var _accessToken: String? = null;
    private var oAuthInterceptor: Interceptor? = null
    private var okHttpClient = okHttpClientBase
    private var _apiHandler : ApiHandler = LemmyBetaApiHandler(retrofit)
    var baseUrl: String
        get() {
            var result: String = _baseURL
            if (_baseURL.endsWith("/")) {
                result = _baseURL.substring(0, _baseURL.length - 1)
            }
            return result
        }
        set(value) {
            _baseURL = value
            retrofit = createRetrofit(okHttpClientBase,_baseURL)
            _apiHandler = LemmyBetaApiHandler(retrofit)
        }

    var accessToken: String?
        get() = _accessToken
        set(value) {
            _accessToken = value
            val builder = okHttpClientBase.newBuilder()
            Log.d("ApiHandlerProvider", "Access token changed")
            if (accessToken != null && accessToken != "") {
                Log.i("ApiHandlerProvider", "Setting access token interceptor")
                oAuthInterceptor = APIUtils.getOAuthInterceptor(accessToken)
                builder.addInterceptor(oAuthInterceptor!!)
            }
            okHttpClient = builder.build()
            retrofit = createRetrofit(okHttpClient, baseUrl)
            _apiHandler = LemmyBetaApiHandler(retrofit)
        }

    val apiHandler: ApiHandler
        get() = _apiHandler

    private fun createRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SortTypeConverterFactory.create())
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}