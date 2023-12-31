package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.subreddit.FetchSubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditDao;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import retrofit2.Retrofit;

public class LoadSubredditIcon {

    public static void loadSubredditIcon(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                         String subredditName, String accessToken, Retrofit retrofit,
                                         LoadSubredditIconAsyncTaskListener loadSubredditIconAsyncTaskListener) {
        executor.execute(() -> {
            SubredditDao subredditDao = redditDataRoomDatabase.subredditDao();
            SubredditData subredditData = subredditDao.getSubredditDataByActorId(LemmyUtils.qualifiedCommunityName2ActorId(subredditName));
            if (subredditData != null) {
                String iconImageUrl = subredditDao.getSubredditDataByActorId(LemmyUtils.qualifiedCommunityName2ActorId(subredditName)).getIconUrl();
                handler.post(() -> loadSubredditIconAsyncTaskListener.loadIconSuccess(iconImageUrl));
            } else {
                handler.post(() -> FetchSubredditData.fetchSubredditData(retrofit, subredditName, accessToken, new FetchSubredditData.FetchSubredditDataListener() {
                    @Override
                    public void onFetchSubredditDataSuccess(SubredditData subredditData1, int nCurrentOnlineSubscribers) {
                        ArrayList<SubredditData> singleSubredditDataList = new ArrayList<>();
                        singleSubredditDataList.add(subredditData1);
                        InsertSubscribedThings.insertSubscribedThings(executor, handler, redditDataRoomDatabase, null,
                                null, null, singleSubredditDataList,
                                () -> loadSubredditIconAsyncTaskListener.loadIconSuccess(subredditData1.getIconUrl()));
                    }

                    @Override
                    public void onFetchSubredditDataFail(boolean isQuarantined) {
                        loadSubredditIconAsyncTaskListener.loadIconSuccess(null);
                    }
                }));
            }
        });
    }

    public interface LoadSubredditIconAsyncTaskListener {
        void loadIconSuccess(String iconImageUrl);
    }
}
