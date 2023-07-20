package eu.toldi.infinityforlemmy.readpost;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class InsertReadPost {
    public static void insertReadPost(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                      String username, int postId) {
        executor.execute(() -> {
            ReadPostDao readPostDao = redditDataRoomDatabase.readPostDao();
            if (readPostDao.getReadPostsCount() > 500) {
                readPostDao.deleteOldestReadPosts(username);
            }
            if (username != null && !username.equals("")) {
                readPostDao.insert(new ReadPost(username, String.valueOf(postId)));
            }
        });
    }
}
