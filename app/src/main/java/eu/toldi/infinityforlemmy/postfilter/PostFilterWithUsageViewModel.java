package eu.toldi.infinityforlemmy.postfilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;


public class PostFilterWithUsageViewModel extends ViewModel {
    private LiveData<List<PostFilterWithUsage>> mPostFilterWithUsageListLiveData;

    public PostFilterWithUsageViewModel(RedditDataRoomDatabase redditDataRoomDatabase) {
        mPostFilterWithUsageListLiveData = redditDataRoomDatabase.postFilterDao().getAllPostFilterWithUsageLiveData();
    }

    public LiveData<List<PostFilterWithUsage>> getPostFilterWithUsageListLiveData() {
        return mPostFilterWithUsageListLiveData;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final RedditDataRoomDatabase mRedditDataRoomDatabase;

        public Factory(RedditDataRoomDatabase redditDataRoomDatabase) {
            mRedditDataRoomDatabase = redditDataRoomDatabase;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PostFilterWithUsageViewModel(mRedditDataRoomDatabase);
        }
    }
}