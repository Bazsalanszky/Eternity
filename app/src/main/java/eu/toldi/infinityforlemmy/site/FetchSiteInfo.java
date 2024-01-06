package eu.toldi.infinityforlemmy.site;

import org.json.JSONException;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.user.MyUserInfo;
import retrofit2.Retrofit;

public class FetchSiteInfo {

    public static void fetchSiteInfo(Retrofit retrofit, String accesToken, FetchSiteInfoListener fetchSiteInfoListener) {
        retrofit.create(LemmyAPI.class).getSiteInfo(accesToken).enqueue(
                new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                String siteInfoJson = response.body();
                                SiteInfo siteInfo = SiteInfo.parseSiteInfo(siteInfoJson);
                                MyUserInfo myUserInfo = MyUserInfo.parseFromSiteInfo(siteInfoJson);
                                fetchSiteInfoListener.onFetchSiteInfoSuccess(siteInfo, myUserInfo);
                            } catch (JSONException e) {
                                fetchSiteInfoListener.onFetchSiteInfoFailed(true);
                            }
                        } else {
                            fetchSiteInfoListener.onFetchSiteInfoFailed(false);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<String> call, Throwable t) {
                        fetchSiteInfoListener.onFetchSiteInfoFailed(false);
                    }
                }
        );
    }

    public interface FetchSiteInfoListener {
        void onFetchSiteInfoSuccess(SiteInfo siteInfo, MyUserInfo myUserInfo);
        void onFetchSiteInfoFailed(boolean parseFailed);
    }
}
