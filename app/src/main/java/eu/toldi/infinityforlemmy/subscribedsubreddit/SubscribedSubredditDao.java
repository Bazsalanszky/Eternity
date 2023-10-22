package eu.toldi.infinityforlemmy.subscribedsubreddit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubscribedSubredditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SubscribedSubredditData subscribedSubredditData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SubscribedSubredditData> subscribedSubredditDataList);

    @Query("DELETE FROM subscribed_subreddits")
    void deleteAllSubscribedSubreddits();

    @Query("SELECT * from subscribed_subreddits WHERE username = :accountName AND name LIKE '%' || :searchQuery || '%' ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<SubscribedSubredditData>> getAllSubscribedSubredditsWithSearchQuery(String accountName, String searchQuery);

    @Query("SELECT * from subscribed_subreddits WHERE username = :accountName COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    List<SubscribedSubredditData> getAllSubscribedSubredditsList(String accountName);

    @Query("SELECT * from subscribed_subreddits WHERE name = :subredditName COLLATE NOCASE AND username = :accountName COLLATE NOCASE LIMIT 1")
    SubscribedSubredditData getSubscribedSubreddit(String subredditName, String accountName);

    @Query("SELECT * from subscribed_subreddits WHERE qualified_name = :qualified_name COLLATE NOCASE AND username = :accountName COLLATE NOCASE LIMIT 1")
    SubscribedSubredditData getSubscribedSubredditByQualifiedName(String qualified_name, String accountName);

    @Query("DELETE FROM subscribed_subreddits WHERE qualified_name = :subredditName COLLATE NOCASE AND username = :accountName COLLATE NOCASE")
    void deleteSubscribedSubreddit(String subredditName, String accountName);

    @Query("SELECT * from subscribed_subreddits WHERE username = :qualified_name AND name LIKE '%' || :searchQuery || '%' COLLATE NOCASE AND is_favorite = 1 ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<SubscribedSubredditData>> getAllFavoriteSubscribedSubredditsWithSearchQuery(String qualified_name, String searchQuery);

    @Query("UPDATE subscribed_subreddits SET name = :displayName, icon = :icon WHERE qualified_name = :qualified_name")
    void updateSubscribedSubreddit(String qualified_name, String displayName, String icon);
}
