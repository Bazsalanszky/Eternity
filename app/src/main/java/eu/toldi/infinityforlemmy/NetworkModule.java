package eu.toldi.infinityforlemmy;

import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.toldi.infinityforlemmy.apis.StreamableAPI;
import eu.toldi.infinityforlemmy.network.SortTypeConverterFactory;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module(includes = AppModule.class)
abstract class NetworkModule {


    @Provides
    @Named("base")
    @Singleton
    static OkHttpClient provideBaseOkhttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    @Provides
    @Named("base")
    @Singleton
    static RetrofitHolder provideBaseRetrofitHolder(@Named("base") OkHttpClient okHttpClient) {
        return new RetrofitHolder(okHttpClient);
    }

    /*@Provides
    @Named("base")
    @Singleton
    static Retrofit provideBaseRetrofit(@Named("base") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(APIUtils.API_BASE_URI)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(SortTypeConverterFactory.create())
                .addCallAdapterFactory(GuavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }*/

    @Provides
    static ConnectionPool provideConnectionPool() {
        return new ConnectionPool(0, 1, TimeUnit.NANOSECONDS);
    }

    @Provides
    @Named("no_oauth")
    static RetrofitHolder provideRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit;
    }

    @Provides
    @Named("oauth")
    static Retrofit provideOAuthRetrofit(@Named("base") RetrofitHolder retrofit,
                                         @Named("default") OkHttpClient okHttpClient) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.OAUTH_API_BASE_URI)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Named("default")
    @Singleton
    static OkHttpClient provideOkHttpClient(@Named("base") OkHttpClient httpClient,
                                            @Named("base") RetrofitHolder retrofit,
                                            RedditDataRoomDatabase accountRoomDatabase,
                                            @Named("current_account") SharedPreferences currentAccountSharedPreferences,
                                            ConnectionPool connectionPool) {
        return httpClient.newBuilder()
                .authenticator(new AccessTokenAuthenticator(retrofit.getRetrofit(), accountRoomDatabase, currentAccountSharedPreferences))
                .connectionPool(connectionPool)
                .build();
    }

    @Provides
    @Named("oauth_without_authenticator")
    @Singleton
    static Retrofit provideOauthWithoutAuthenticatorRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.OAUTH_API_BASE_URI)
                .build();
    }

    @Provides
    @Named("upload_media")
    @Singleton
    static Retrofit provideUploadMediaRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.API_UPLOAD_MEDIA_URI)
                .build();
    }

    @Provides
    @Named("upload_video")
    @Singleton
    static Retrofit provideUploadVideoRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.API_UPLOAD_VIDEO_URI)
                .build();
    }

    @Provides
    @Named("download_media")
    @Singleton
    static Retrofit provideDownloadRedditVideoRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl("http://localhost/")
                .build();
    }

    @Provides
    @Named("gfycat")
    @Singleton
    static Retrofit provideGfycatRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.GFYCAT_API_BASE_URI)
                .build();
    }

    @Provides
    @Named("RedgifsAccessTokenAuthenticator")
    static Interceptor redgifsAccessTokenAuthenticator(@Named("current_account") SharedPreferences currentAccountSharedPreferences) {
        return new RedgifsAccessTokenAuthenticator(currentAccountSharedPreferences);
    }

    @Provides
    @Named("redgifs")
    @Singleton
    static Retrofit provideRedgifsRetrofit(@Named("RedgifsAccessTokenAuthenticator") Interceptor accessTokenAuthenticator,
                                           @Named("base") OkHttpClient httpClient,
                                           @Named("base") RetrofitHolder retrofit,
                                           ConnectionPool connectionPool) {
        OkHttpClient.Builder okHttpClientBuilder = httpClient.newBuilder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request()
                                .newBuilder()
                                .header("User-Agent", APIUtils.USER_AGENT)
                                .build()
                ))
                .addInterceptor(accessTokenAuthenticator)
                .connectionPool(connectionPool);

        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.REDGIFS_API_BASE_URI)
                .client(okHttpClientBuilder.build())
                .build();
    }

    @Provides
    @Named("imgur")
    @Singleton
    static Retrofit provideImgurRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.IMGUR_API_BASE_URI)
                .build();
    }

    @Provides
    @Named("pushshift")
    @Singleton
    static Retrofit providePushshiftRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.PUSHSHIFT_API_BASE_URI)
                .build();
    }

    @Provides
    @Named("reveddit")
    @Singleton
    static Retrofit provideRevedditRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.REVEDDIT_API_BASE_URI)
                .build();
    }

    @Provides
    @Named("vReddIt")
    @Singleton
    static Retrofit provideVReddItRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl("http://localhost/")
                .build();
    }

    @Provides
    @Named("streamable")
    @Singleton
    static Retrofit provideStreamableRetrofit(@Named("base") RetrofitHolder retrofit) {
        return retrofit.getRetrofit().newBuilder()
                .baseUrl(APIUtils.STREAMABLE_API_BASE_URI)
                .build();
    }

    @Provides
    @Singleton
    static StreamableAPI provideStreamableApi(@Named("streamable") Retrofit streamableRetrofit) {
        return streamableRetrofit.create(StreamableAPI.class);
    }
}
