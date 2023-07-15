package eu.toldi.infinityforlemmy.postfilter;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class SavePostFilterUsage {
    public static void savePostFilterUsage(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                           PostFilterUsage postFilterUsage) {
        executor.execute(() -> redditDataRoomDatabase.postFilterUsageDao().insert(postFilterUsage));
    }
}
