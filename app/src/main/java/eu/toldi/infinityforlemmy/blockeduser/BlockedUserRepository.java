package eu.toldi.infinityforlemmy.blockeduser;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class BlockedUserRepository {

    private BlockedUserDao mBlockedUserDao;
    private String mAccountName;

    BlockedUserRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        mBlockedUserDao = redditDataRoomDatabase.blockedUserDao();
        mAccountName = accountName;
    }

    LiveData<List<BlockedUserData>> getAllBlockedUsersWithSearchQuery(String searchQuery) {
        return mBlockedUserDao.getAllBlockedUsersWithSearchQuery(mAccountName, searchQuery);
    }

    LiveData<List<BlockedUserData>> getAllFavoriteSubscribedUsersWithSearchQuery(String searchQuery) {
        return mBlockedUserDao.getAllBlockedUsersWithSearchQuery(mAccountName, searchQuery);
    }

    public void insert(BlockedUserData BlockedUserData) {
        new BlockedUserRepository.insertAsyncTask(mBlockedUserDao).execute(BlockedUserData);
    }

    private static class insertAsyncTask extends AsyncTask<BlockedUserData, Void, Void> {

        private BlockedUserDao mAsyncTaskDao;

        insertAsyncTask(BlockedUserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BlockedUserData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
