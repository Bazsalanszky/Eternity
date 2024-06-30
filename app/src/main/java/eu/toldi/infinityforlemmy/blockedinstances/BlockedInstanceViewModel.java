package eu.toldi.infinityforlemmy.blockedinstances;

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

public class BlockedInstanceViewModel extends AndroidViewModel {
    private BlockedInstanceRepository blockedInstanceRepository;
    private LiveData<List<BlockedInstanceData>> mAllSubscribedInstances;
    private MutableLiveData<String> searchQueryLiveData;

    public BlockedInstanceViewModel(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        super(application);
        blockedInstanceRepository = new BlockedInstanceRepository(redditDataRoomDatabase, accountName);
        searchQueryLiveData = new MutableLiveData<>();
        searchQueryLiveData.postValue("");

        mAllSubscribedInstances = Transformations.switchMap(searchQueryLiveData, searchQuery -> blockedInstanceRepository.getAllFavoriteSubscribedInstancesWithSearchQuery(searchQuery));
    }

    public LiveData<List<BlockedInstanceData>> getAllSubscribedInstances() {
        return mAllSubscribedInstances;
    }


    public void insert(BlockedInstanceData BlockedInstanceData) {
        blockedInstanceRepository.insert(BlockedInstanceData);
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
            return (T) new BlockedInstanceViewModel(mApplication, mRedditDataRoomDatabase, mAccountName);
        }
    }
}
