package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.account.AccountDao;

public class ParseAndInsertNewAccount {

    public static void parseAndInsertNewAccount(Executor executor, Handler handler, String username,
                                                String display_name,String accessToken, String profileImageUrl,
                                                String bannerImageUrl, String code,String instance, AccountDao accountDao,
                                                ParseAndInsertAccountListener parseAndInsertAccountListener) {
        executor.execute(() -> {
            Account account = new Account(username,display_name, accessToken, code, profileImageUrl,
                    bannerImageUrl, true,instance);
            accountDao.markAllAccountsNonCurrent();
            accountDao.insert(account);

            handler.post(parseAndInsertAccountListener::success);
        });
    }

    public interface ParseAndInsertAccountListener {
        void success();
    }
}
