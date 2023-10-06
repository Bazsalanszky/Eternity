package eu.toldi.infinityforlemmy;

import android.util.Log;

import eu.toldi.infinityforlemmy.network.SortTypeConverterFactory;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHolder {

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    private OkHttpClient okHttpClientBase;
    private String baseURL = APIUtils.API_BASE_URI;

    private String accessToken = null;

    private Interceptor oAuthInterceptor;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setBaseURL(String baseURL) {
        retrofit = createRetrofit(okHttpClient, baseURL);
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        String result = baseURL;
        if (baseURL.endsWith("/")) {
            result = baseURL.substring(0, baseURL.length() - 1);
        }
        return result;
    }

    public RetrofitHolder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.okHttpClientBase = okHttpClient;
        this.retrofit = createRetrofit(okHttpClient, APIUtils.API_BASE_URI);
    }

    private static Retrofit createRetrofit(OkHttpClient okHttpClient, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(SortTypeConverterFactory.create())
                .addCallAdapterFactory(GuavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        OkHttpClient.Builder builder = okHttpClientBase.newBuilder();
        Log.d("RetrofitHolder", "Access token changed");
        if (accessToken != null && !accessToken.equals("")) {
            Log.i("RetrofitHolder", "Setting access token interceptor");
            oAuthInterceptor = APIUtils.getOAuthInterceptor(accessToken);
            builder.addInterceptor(oAuthInterceptor);
        }
        okHttpClient = builder.build();
        retrofit = createRetrofit(okHttpClient, baseURL);
    }

}
