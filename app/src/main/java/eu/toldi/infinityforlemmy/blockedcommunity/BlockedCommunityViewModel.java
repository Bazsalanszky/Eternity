package eu.toldi.infinityforlemmy.blockedcommunity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class BlockedCommunityViewModel extends AndroidViewModel {
    private BlockedCommunityRepository blockedCommunityRepository;
    private LiveData<List<BlockedCommunityData>> mAllBlockedCommunities;
    private MutableLiveData<String> searchQueryLiveData;

    public BlockedCommunityViewModel(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        super(application);
        blockedCommunityRepository = new BlockedCommunityRepository(redditDataRoomDatabase, accountName);
        searchQueryLiveData = new MutableLiveData<>();
        searchQueryLiveData.postValue("");

        mAllBlockedCommunities = Transformations.switchMap(searchQueryLiveData, searchQuery -> blockedCommunityRepository.getAllSubscribedSubredditsWithSearchQuery(searchQuery));
    }

    public LiveData<List<BlockedCommunityData>> getAllBlockedCommunities() {
        return mAllBlockedCommunities;
    }

    public void insert(BlockedCommunityData subscribedSubredditData) {
        blockedCommunityRepository.insert(subscribedSubredditData);
    }

    public void setSearchQuery(String searchQuery) {
        searchQueryLiveData.postValue(searchQuery);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private RedditDataRoomDatabase mRedditDataRoomDatabase;
        private String mAccountName;

        public Factory(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
            this.mApplication = application;
            this.mRedditDataRoomDatabase = redditDataRoomDatabase;
            this.mAccountName = accountName;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new BlockedCommunityViewModel(mApplication, mRedditDataRoomDatabase, mAccountName);
        }
    }
}
