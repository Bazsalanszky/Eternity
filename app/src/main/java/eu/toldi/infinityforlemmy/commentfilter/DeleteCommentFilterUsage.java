package eu.toldi.infinityforlemmy.commentfilter;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;


public class DeleteCommentFilterUsage {
    public static void deleteCommentFilterUsage(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                                CommentFilterUsage commentFilterUsage) {
        executor.execute(() -> redditDataRoomDatabase.commentFilterUsageDao().deleteCommentFilterUsage(commentFilterUsage));
    }
}
