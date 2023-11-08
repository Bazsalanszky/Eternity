package eu.toldi.infinityforlemmy.subreddit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;

public class SubredditViewModel extends AndroidViewModel {
    private SubredditRepository mSubredditRepository;
    private LiveData<SubredditData> mSubredditLiveData;

    private MutableLiveData<String> mSubredditNameLiveData;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;

    public SubredditViewModel(Application application, RedditDataRoomDatabase redditDataRoomDatabase, String id) {
        super(application);
        mSubredditRepository = new SubredditRepository(redditDataRoomDatabase, id);
        mSubredditLiveData = mSubredditRepository.getSubredditLiveData();
        mSubredditNameLiveData = new MutableLiveData<>(id);
        mRedditDataRoomDatabase = redditDataRoomDatabase;
    }

    public LiveData<SubredditData> getSubredditLiveData() {
        return mSubredditLiveData;
    }

    public LiveData<String> getSubredditNameLiveData() {
        return mSubredditNameLiveData;
    }

    public void setSubredditName(String subredditName) {
        mSubredditNameLiveData.setValue(subredditName);
        updateSubredditRepository();
    }

    private void updateSubredditRepository() {
        String subredditName = mSubredditNameLiveData.getValue();
        if (subredditName != null) {
            mSubredditRepository = new SubredditRepository(mRedditDataRoomDatabase, subredditName);

            mSubredditLiveData = mSubredditRepository.getSubredditLiveData();

        }
    }

    public void insert(SubredditData subredditData) {
        mSubredditRepository.insert(subredditData);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final RedditDataRoomDatabase mRedditDataRoomDatabase;
        private final String mSubredditName;

        public Factory(@NonNull Application application, RedditDataRoomDatabase redditDataRoomDatabase, String subredditname) {
            mApplication = application;
            mRedditDataRoomDatabase = redditDataRoomDatabase;
            mSubredditName = subredditname;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SubredditViewModel(mApplication, mRedditDataRoomDatabase, mSubredditName);
        }
    }
}
