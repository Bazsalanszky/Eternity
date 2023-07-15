package eu.toldi.infinityforlemmy.postfilter;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class DeletePostFilterUsage {
    public static void deletePostFilterUsage(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                             PostFilterUsage postFilterUsage) {
        executor.execute(() -> redditDataRoomDatabase.postFilterUsageDao().deletePostFilterUsage(postFilterUsage));
    }
}
