package eu.toldi.infinityforlemmy.blockedinstances;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class BlockedInstanceRepository {

    private BlockedInstanceDao blockedInstanceDao;
    private String mAccountName;

    BlockedInstanceRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        blockedInstanceDao = redditDataRoomDatabase.blockedInstanceDao();
        mAccountName = accountName;
    }

    LiveData<List<BlockedInstanceData>> getAllBlockedInstancesWithSearchQuery(String searchQuery) {
        return blockedInstanceDao.getAllBlockedInstancesWithSearchQuery(mAccountName, searchQuery);
    }

    LiveData<List<BlockedInstanceData>> getAllFavoriteSubscribedInstancesWithSearchQuery(String searchQuery) {
        return blockedInstanceDao.getAllBlockedInstancesWithSearchQuery(mAccountName, searchQuery);
    }

    public void insert(BlockedInstanceData BlockedInstanceData) {
        new BlockedInstanceRepository.insertAsyncTask(blockedInstanceDao).execute(BlockedInstanceData);
    }

    private static class insertAsyncTask extends AsyncTask<BlockedInstanceData, Void, Void> {

        private BlockedInstanceDao mAsyncTaskDao;

        insertAsyncTask(BlockedInstanceDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BlockedInstanceData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
