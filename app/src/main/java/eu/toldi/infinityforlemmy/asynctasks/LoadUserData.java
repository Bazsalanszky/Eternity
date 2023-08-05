package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.user.FetchUserData;
import eu.toldi.infinityforlemmy.user.UserDao;
import eu.toldi.infinityforlemmy.user.UserData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import retrofit2.Retrofit;

public class LoadUserData {

    public static void loadUserData(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase, String userName,
                                    Retrofit retrofit, LoadUserDataAsyncTaskListener loadUserDataAsyncTaskListener) {
        executor.execute(() -> {
            UserDao userDao = redditDataRoomDatabase.userDao();
            UserData userData= userDao.getUserDataByActorId(LemmyUtils.qualifiedUserName2ActorId(userName));
            if (userData != null) {
                String iconImageUrl = userData.getIconUrl();
                handler.post(() -> loadUserDataAsyncTaskListener.loadUserDataSuccess(iconImageUrl));
            } else {
                handler.post(() -> FetchUserData.fetchUserData(retrofit, userName, new FetchUserData.FetchUserDataListener() {
                    @Override
                    public void onFetchUserDataSuccess(UserData userData, int inboxCount) {
                        InsertUserData.insertUserData(executor, handler, redditDataRoomDatabase, userData,
                                () -> loadUserDataAsyncTaskListener.loadUserDataSuccess(userData.getIconUrl()));
                    }

                    @Override
                    public void onFetchUserDataFailed() {
                        loadUserDataAsyncTaskListener.loadUserDataSuccess(null);
                    }
                }));
            }
        });
    }

    public interface LoadUserDataAsyncTaskListener {
        void loadUserDataSuccess(String iconImageUrl);
    }
}
