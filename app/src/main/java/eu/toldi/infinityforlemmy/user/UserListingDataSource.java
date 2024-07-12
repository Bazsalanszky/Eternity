package eu.toldi.infinityforlemmy.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.NetworkState;
import eu.toldi.infinityforlemmy.SortType;
import retrofit2.Retrofit;

public class UserListingDataSource extends PageKeyedDataSource<Integer, UserData> {

    private Retrofit retrofit;
    private String query;
    private SortType sortType;
    private boolean nsfw;

    private MutableLiveData<NetworkState> paginationNetworkStateLiveData;
    private MutableLiveData<NetworkState> initialLoadStateLiveData;
    private MutableLiveData<Boolean> hasUserLiveData;

    private PageKeyedDataSource.LoadParams<Integer> params;
    private PageKeyedDataSource.LoadCallback<Integer, UserData> callback;

    UserListingDataSource(Retrofit retrofit, String query, SortType sortType, boolean nsfw) {
        this.retrofit = retrofit;
        this.query = query;
        this.sortType = sortType;
        this.nsfw = nsfw;
        paginationNetworkStateLiveData = new MutableLiveData<>();
        initialLoadStateLiveData = new MutableLiveData<>();
        hasUserLiveData = new MutableLiveData<>();
    }

    MutableLiveData<NetworkState> getPaginationNetworkStateLiveData() {
        return paginationNetworkStateLiveData;
    }

    MutableLiveData<NetworkState> getInitialLoadStateLiveData() {
        return initialLoadStateLiveData;
    }

    MutableLiveData<Boolean> hasUserLiveData() {
        return hasUserLiveData;
    }

    @Override
    public void loadInitial(@NonNull PageKeyedDataSource.LoadInitialParams<Integer> params, @NonNull PageKeyedDataSource.LoadInitialCallback<Integer, UserData> callback) {
        initialLoadStateLiveData.postValue(NetworkState.LOADING);

        FetchUserData.fetchUserListingData(retrofit, query, null, sortType.getType(), nsfw,
                new FetchUserData.FetchUserListingDataListener() {
                    @Override
                    public void onFetchUserListingDataSuccess(ArrayList<UserData> UserData, String after) {
                        hasUserLiveData.postValue(!UserData.isEmpty());

                        callback.onResult(UserData, null, 2);
                        initialLoadStateLiveData.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onFetchUserListingDataFailed() {
                        initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error retrieving eu.toldi.infinityforlemmy.User list"));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull PageKeyedDataSource.LoadParams<Integer> params, @NonNull PageKeyedDataSource.LoadCallback<Integer, UserData> callback) {

    }

    @Override
    public void loadAfter(@NonNull PageKeyedDataSource.LoadParams<Integer> params, @NonNull PageKeyedDataSource.LoadCallback<Integer, UserData> callback) {
        this.params = params;
        this.callback = callback;

        if (params.key.equals("null") || params.key.equals("")) {
            return;
        }

        FetchUserData.fetchUserListingData(retrofit, query, params.key, sortType.getType(), nsfw,
                new FetchUserData.FetchUserListingDataListener() {
                    @Override
                    public void onFetchUserListingDataSuccess(ArrayList<UserData> UserData, String after) {
                        callback.onResult(UserData, params.key + 1);
                        paginationNetworkStateLiveData.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onFetchUserListingDataFailed() {
                        paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error retrieving eu.toldi.infinityforlemmy.User list"));
                    }
                });
    }

    void retryLoadingMore() {
        loadAfter(params, callback);
    }
}
