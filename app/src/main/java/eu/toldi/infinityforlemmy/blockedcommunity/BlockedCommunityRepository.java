package eu.toldi.infinityforlemmy.blockedcommunity;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class BlockedCommunityRepository {
    private BlockedCommunityDao blockedCommunityDao;
    private String mAccountName;

    BlockedCommunityRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        mAccountName = accountName;
        blockedCommunityDao = redditDataRoomDatabase.blockedCommunityDao();
    }

    LiveData<List<BlockedCommunityData>> getAllSubscribedSubredditsWithSearchQuery(String searchQuery) {
        return blockedCommunityDao.getAllBlockedCommunitiesWithSearchQuery(mAccountName, searchQuery);
    }

    public void insert(BlockedCommunityData subscribedSubredditData) {
        new insertAsyncTask(blockedCommunityDao).execute(subscribedSubredditData);
    }

    private static class insertAsyncTask extends AsyncTask<BlockedCommunityData, Void, Void> {

        private BlockedCommunityDao mAsyncTaskDao;

        insertAsyncTask(BlockedCommunityDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BlockedCommunityData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
