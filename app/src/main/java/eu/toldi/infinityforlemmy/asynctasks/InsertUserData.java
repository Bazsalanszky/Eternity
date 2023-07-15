package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.user.UserData;

public class InsertUserData {

    public static void insertUserData(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                      UserData userData, InsertUserDataListener insertUserDataListener) {
        executor.execute(() -> {
            if (redditDataRoomDatabase.userDao().getNUsers() > 10000) {
                redditDataRoomDatabase.userDao().deleteAllUsers();
            }
            redditDataRoomDatabase.userDao().insert(userData);
            if (insertUserDataListener != null) {
                handler.post(insertUserDataListener::insertSuccess);
            }
        });
    }

    public interface InsertUserDataListener {
        void insertSuccess();
    }
}
