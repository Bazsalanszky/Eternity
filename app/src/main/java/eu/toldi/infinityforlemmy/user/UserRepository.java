package eu.toldi.infinityforlemmy.user;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class UserRepository {
    private UserDao mUserDao;
    private LiveData<UserData> mUserLiveData;

    UserRepository(RedditDataRoomDatabase redditDataRoomDatabase, String actor_id) {
        mUserDao = redditDataRoomDatabase.userDao();
        mUserLiveData = mUserDao.getUserLiveDataByActorId(actor_id);
    }

    LiveData<UserData> getUserLiveData() {
        return mUserLiveData;
    }

    public void insert(UserData userData) {
        new InsertAsyncTask(mUserDao).execute(userData);
    }

    private static class InsertAsyncTask extends AsyncTask<UserData, Void, Void> {

        private UserDao mAsyncTaskDao;

        InsertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
