package eu.toldi.infinityforlemmy.recentsearchquery;

import androidx.lifecycle.LiveData;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class RecentSearchQueryRepository {
    private LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    RecentSearchQueryRepository(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mAllRecentSearchQueries = redditDataRoomDatabase.recentSearchQueryDao().getAllRecentSearchQueriesLiveData(username);
    }

    LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }
}
