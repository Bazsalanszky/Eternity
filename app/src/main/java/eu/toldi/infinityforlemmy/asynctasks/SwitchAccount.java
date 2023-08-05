package eu.toldi.infinityforlemmy.asynctasks;

import android.content.SharedPreferences;
import android.os.Handler;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.site.FetchSiteInfo;
import eu.toldi.infinityforlemmy.site.SiteInfo;
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
                    .putString(SharedPreferencesUtils.ACCOUNT_INSTANCE,account.getInstance_url())
                    .putString(SharedPreferencesUtils.ACCOUNT_IMAGE_URL, account.getProfileImageUrl()).apply();
            retrofitHolder.setBaseURL(account.getInstance_url());
            FetchSiteInfo.fetchSiteInfo(retrofitHolder.getRetrofit(), account.getAccessToken(), new FetchSiteInfo.FetchSiteInfoListener() {
                @Override
                public void onFetchSiteInfoSuccess(SiteInfo siteInfo) {
                    boolean canDownvote = siteInfo.isEnable_downvotes();
                    currentAccountSharedPreferences.edit().putBoolean(SharedPreferencesUtils.CAN_DOWNVOTE, canDownvote).apply();
                }

                @Override
                public void onFetchSiteInfoFailed() {
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
