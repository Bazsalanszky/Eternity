package eu.toldi.infinityforlemmy.commentfilter;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;


public class DeleteCommentFilter {
    public static void deleteCommentFilter(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor, CommentFilter commentFilter) {
        executor.execute(() -> redditDataRoomDatabase.commentFilterDao().deleteCommentFilter(commentFilter));
    }
}
