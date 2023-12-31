package eu.toldi.infinityforlemmy.asynctasks;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.site.FetchSiteInfo;
import eu.toldi.infinityforlemmy.site.SiteInfo;
import eu.toldi.infinityforlemmy.user.MyUserInfo;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class SwitchAccount {
    public static void switchAccount(RedditDataRoomDatabase redditDataRoomDatabase,RetrofitHolder retrofitHolder,
                                     SharedPreferences currentAccountSharedPreferences, Executor executor,
                                     Handler handler, String newAccountName,
                                     SwitchAccountListener switchAccountListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.accountDao().markAllAccountsNonCurrent();
            redditDataRoomDatabase.accountDao().markAccountCurrent(newAccountName);
            Account account = redditDataRoomDatabase.accountDao().getCurrentAccount();
            currentAccountSharedPreferences.edit()
                    .putString(SharedPreferencesUtils.ACCESS_TOKEN, account.getAccessToken())
                    .putString(SharedPreferencesUtils.ACCOUNT_NAME, account.getDisplay_name())
                    .putString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, account.getAccountName())
                    .putString(SharedPreferencesUtils.ACCOUNT_INSTANCE, account.getInstance_url())
                    .putBoolean(SharedPreferencesUtils.BEARER_TOKEN_AUTH, false)
                    .putString(SharedPreferencesUtils.ACCOUNT_IMAGE_URL, account.getProfileImageUrl()).apply();
            retrofitHolder.setBaseURL(account.getInstance_url());
            retrofitHolder.setAccessToken(null);
            FetchSiteInfo.fetchSiteInfo(retrofitHolder.getRetrofit(), account.getAccessToken(), new FetchSiteInfo.FetchSiteInfoListener() {
                @Override
                public void onFetchSiteInfoSuccess(SiteInfo siteInfo, MyUserInfo myUserInfo) {
                    boolean canDownvote = siteInfo.isEnable_downvotes();
                    currentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, canDownvote).apply();
                    String[] version = siteInfo.getVersion().split("\\.");
                    if (version.length > 0) {
                        Log.d("SwitchAccount", "Lemmy Version: " + version[0] + "." + version[1]);
                        int majorVersion = Integer.parseInt(version[0]);
                        int minorVersion = Integer.parseInt(version[1]);
                        if (majorVersion > 0 || (majorVersion == 0 && minorVersion >= 19)) {
                            retrofitHolder.setAccessToken(account.getAccessToken());
                            currentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.BEARER_TOKEN_AUTH, true).apply();
                        }
                    }
                }

                @Override
                public void onFetchSiteInfoFailed(boolean parseFailed) {
                    Log.e("SwitchAccount", "Failed to fetch site info");
                    currentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, true).apply();
                }
            });
            handler.post(() -> switchAccountListener.switched(account));
        });

    }

    public interface SwitchAccountListener {
        void switched(Account account);
    }
}
