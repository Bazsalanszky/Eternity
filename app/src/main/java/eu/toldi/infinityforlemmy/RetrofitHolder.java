package eu.toldi.infinityforlemmy;

import eu.toldi.infinityforlemmy.network.SortTypeConverterFactory;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHolder {

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setBaseURL(String baseURL){
        retrofit = createRetrofit(okHttpClient,baseURL);
    }

    public RetrofitHolder(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
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
}
