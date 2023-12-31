package eu.toldi.infinityforlemmy.subreddit;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class SubredditRepository {
    private SubredditDao mSubredditDao;
    private LiveData<SubredditData> mSubredditLiveData;

    SubredditRepository(RedditDataRoomDatabase redditDataRoomDatabase, String actor_id) {
        mSubredditDao = redditDataRoomDatabase.subredditDao();
        mSubredditLiveData = mSubredditDao.getSubredditLiveDataByActorId(actor_id);
    }

    LiveData<SubredditData> getSubredditLiveData() {
        return mSubredditLiveData;
    }

    public void insert(SubredditData subredditData) {
        new InsertAsyncTask(mSubredditDao).execute(subredditData);
    }

    private static class InsertAsyncTask extends AsyncTask<SubredditData, Void, Void> {

        private SubredditDao mAsyncTaskDao;

        InsertAsyncTask(SubredditDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubredditData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
