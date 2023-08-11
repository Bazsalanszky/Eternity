package eu.toldi.infinityforlemmy.blockeduser;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockedUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlockedUserData userData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockedUserData> blockedUserDataDataList);

    @Query("SELECT * FROM blocked_users WHERE account_name = :accountName AND name LIKE '%' || :searchQuery || '%' COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<BlockedUserData>> getAllBlockedUsersWithSearchQuery(String accountName, String searchQuery);

    @Query("SELECT * FROM blocked_users WHERE account_name = :accountName COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    List<BlockedUserData> getAllBlockedUsersList(String accountName);

    @Query("SELECT * FROM blocked_users WHERE name = :name COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE LIMIT 1")
    BlockedUserData getBlockedUser(String name, String accountName);

    @Query("DELETE FROM blocked_users WHERE qualified_name = :name COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE")
    void deleteBlockedUser(String name, String accountName);
}
