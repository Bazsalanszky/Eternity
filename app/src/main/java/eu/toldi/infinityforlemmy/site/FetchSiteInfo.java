package eu.toldi.infinityforlemmy.site;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import retrofit2.Retrofit;

public class FetchSiteInfo {

    public static void fetchSiteInfo(Retrofit retrofit, String accesToken, FetchSiteInfoListener fetchSiteInfoListener) {
        retrofit.create(LemmyAPI.class).getSiteInfo(accesToken).enqueue(
                new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                        if (response.isSuccessful()) {
                            String siteInfoJson = response.body();
                            SiteInfo siteInfo = SiteInfo.parseSiteInfo(siteInfoJson);
                            fetchSiteInfoListener.onFetchSiteInfoSuccess(siteInfo);
                        } else {
                            fetchSiteInfoListener.onFetchSiteInfoFailed();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<String> call, Throwable t) {
                        fetchSiteInfoListener.onFetchSiteInfoFailed();
                    }
                }
        );
    }

    public interface FetchSiteInfoListener {
        void onFetchSiteInfoSuccess(SiteInfo siteInfo);
        void onFetchSiteInfoFailed();
    }
}
