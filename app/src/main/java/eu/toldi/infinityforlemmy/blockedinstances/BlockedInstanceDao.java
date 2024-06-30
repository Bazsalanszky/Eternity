package eu.toldi.infinityforlemmy.blockedinstances;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockedInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlockedInstanceData instanceData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockedInstanceData> blockedUserDataDataList);

    @Query("SELECT * FROM blocked_instances WHERE account_name = :accountName AND domain LIKE '%' || :searchQuery || '%' COLLATE NOCASE ORDER BY domain COLLATE NOCASE ASC")
    LiveData<List<BlockedInstanceData>> getAllBlockedInstancesWithSearchQuery(String accountName, String searchQuery);

    @Query("SELECT * FROM blocked_instances WHERE account_name = :accountName COLLATE NOCASE ORDER BY domain COLLATE NOCASE ASC")
    List<BlockedInstanceData> getAllInstanceInstancesList(String accountName);

    @Query("SELECT * FROM blocked_instances WHERE domain = :domain COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE LIMIT 1")
    BlockedInstanceData getInstanceUser(String domain, String accountName);

    @Query("DELETE FROM blocked_instances WHERE domain = :domain COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE")
    void deleteInstanceUser(String domain, String accountName);
}
