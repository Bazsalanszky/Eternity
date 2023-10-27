package eu.toldi.infinityforlemmy.commentfilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;


public class CommentFilterWithUsageViewModel extends ViewModel {
    private LiveData<List<CommentFilterWithUsage>> mCommentFilterWithUsageListLiveData;

    public CommentFilterWithUsageViewModel(RedditDataRoomDatabase redditDataRoomDatabase) {
        mCommentFilterWithUsageListLiveData = redditDataRoomDatabase.commentFilterDao().getAllCommentFilterWithUsageLiveData();
    }

    public LiveData<List<CommentFilterWithUsage>> getCommentFilterWithUsageListLiveData() {
        return mCommentFilterWithUsageListLiveData;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final RedditDataRoomDatabase mRedditDataRoomDatabase;

        public Factory(RedditDataRoomDatabase redditDataRoomDatabase) {
            mRedditDataRoomDatabase = redditDataRoomDatabase;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new CommentFilterWithUsageViewModel(mRedditDataRoomDatabase);
        }
    }
}
