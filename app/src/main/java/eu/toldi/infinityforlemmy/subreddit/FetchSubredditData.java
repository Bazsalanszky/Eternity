package eu.toldi.infinityforlemmy.subreddit;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchSubredditData {
    public static void fetchSubredditData(Retrofit oauthRetrofit, Retrofit retrofit, String subredditName, String accessToken, final FetchSubredditDataListener fetchSubredditDataListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);

        Call<String> subredditData;
        subredditData = api.communityInfo(subredditName,accessToken);

        subredditData.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseSubredditData.parseSubredditData(response.body(), new ParseSubredditData.ParseSubredditDataListener() {
                        @Override
                        public void onParseSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                            fetchSubredditDataListener.onFetchSubredditDataSuccess(subredditData, nCurrentOnlineSubscribers);
                        }

                        @Override
                        public void onParseSubredditDataFail() {
                            fetchSubredditDataListener.onFetchSubredditDataFail(false);
                        }
                    });
                } else {
                    fetchSubredditDataListener.onFetchSubredditDataFail(response.code() == 403);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchSubredditDataListener.onFetchSubredditDataFail(false);
            }
        });
    }

    static void fetchSubredditListingData(Retrofit retrofit, String query, Integer page, SortType.Type sortType, String accessToken,
                                          boolean nsfw, final FetchSubredditListingDataListener fetchSubredditListingDataListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> subredditDataCall = api.search(query, null, null, null, "Communities", sortType.value, "All", page, 25, accessToken);


        subredditDataCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseSubredditData.parseSubredditListingData(response.body(), nsfw,
                            new ParseSubredditData.ParseSubredditListingDataListener() {
                                @Override
                                public void onParseSubredditListingDataSuccess(ArrayList<SubredditData> subredditData, String after) {
                                    fetchSubredditListingDataListener.onFetchSubredditListingDataSuccess(subredditData, after);
                                }

                                @Override
                                public void onParseSubredditListingDataFail() {
                                    fetchSubredditListingDataListener.onFetchSubredditListingDataFail();
                                }
                            });
                } else {
                    fetchSubredditListingDataListener.onFetchSubredditListingDataFail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchSubredditListingDataListener.onFetchSubredditListingDataFail();
            }
        });
    }

    public interface FetchSubredditDataListener {
        void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers);

        void onFetchSubredditDataFail(boolean isQuarantined);
    }

    interface FetchSubredditListingDataListener {
        void onFetchSubredditListingDataSuccess(ArrayList<SubredditData> subredditData, String after);

        void onFetchSubredditListingDataFail();
    }
}
