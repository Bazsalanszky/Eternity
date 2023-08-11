package eu.toldi.infinityforlemmy.blockeduser;

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

public class BlockedUserViewModel extends AndroidViewModel {
    private BlockedUserRepository blockedUserRepository;
    private LiveData<List<BlockedUserData>> mAllSubscribedUsers;
    private MutableLiveData<String> searchQueryLiveData;

    public BlockedUserViewModel(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        super(application);
        blockedUserRepository = new BlockedUserRepository(redditDataRoomDatabase, accountName);
        searchQueryLiveData = new MutableLiveData<>();
        searchQueryLiveData.postValue("");

        mAllSubscribedUsers = Transformations.switchMap(searchQueryLiveData, searchQuery -> blockedUserRepository.getAllFavoriteSubscribedUsersWithSearchQuery(searchQuery));
    }

    public LiveData<List<BlockedUserData>> getAllSubscribedUsers() {
        return mAllSubscribedUsers;
    }


    public void insert(BlockedUserData BlockedUserData) {
        blockedUserRepository.insert(BlockedUserData);
    }

    public void setSearchQuery(String searchQuery) {
        searchQueryLiveData.postValue(searchQuery);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;
        private RedditDataRoomDatabase mRedditDataRoomDatabase;
        private String mAccountName;

        public Factory(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
            mApplication = application;
            mRedditDataRoomDatabase = redditDataRoomDatabase;
            mAccountName = accountName;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new BlockedUserViewModel(mApplication, mRedditDataRoomDatabase, mAccountName);
        }
    }
}
