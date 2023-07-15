package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;

public class InsertSubredditData {

    public static void insertSubredditData(Executor executor, Handler handler, RedditDataRoomDatabase db,
                                           SubredditData subredditData,
                                           InsertSubredditDataAsyncTaskListener insertSubredditDataAsyncTaskListener) {
        executor.execute(() -> {
            db.subredditDao().insert(subredditData);
            handler.post(insertSubredditDataAsyncTaskListener::insertSuccess);
        });
    }

    public interface InsertSubredditDataAsyncTaskListener {
        void insertSuccess();
    }
}
