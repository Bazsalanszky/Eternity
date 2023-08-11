package eu.toldi.infinityforlemmy.blockedcommunity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockedCommunityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlockedCommunityData communityData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockedCommunityData> blockedCommunityDataList);

    @Query("SELECT * FROM blocked_communities WHERE account_name = :accountName AND name LIKE '%' || :searchQuery || '%' COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<BlockedCommunityData>> getAllBlockedCommunitiesWithSearchQuery(String accountName, String searchQuery);

    @Query("SELECT * FROM blocked_communities WHERE account_name = :accountName COLLATE NOCASE ORDER BY name COLLATE NOCASE ASC")
    List<BlockedCommunityData> getAllBlockedCommunitiesList(String accountName);

    @Query("SELECT * FROM blocked_communities WHERE name = :name COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE LIMIT 1")
    BlockedCommunityData getBlockedCommunity(String name, String accountName);

    @Query("DELETE FROM blocked_communities WHERE qualified_name = :name COLLATE NOCASE AND account_name = :accountName COLLATE NOCASE")
    void deleteBlockedCommunity(String name, String accountName);
}
