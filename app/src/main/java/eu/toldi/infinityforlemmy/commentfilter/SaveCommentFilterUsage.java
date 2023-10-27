package eu.toldi.infinityforlemmy.commentfilter;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;


public class SaveCommentFilterUsage {
    public static void saveCommentFilterUsage(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                              CommentFilterUsage commentFilterUsage) {
        executor.execute(() -> redditDataRoomDatabase.commentFilterUsageDao().insert(commentFilterUsage));
    }
}
