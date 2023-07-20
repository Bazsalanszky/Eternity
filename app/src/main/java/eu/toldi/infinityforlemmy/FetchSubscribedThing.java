package eu.toldi.infinityforlemmy;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.subscribeduser.SubscribedUserData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchSubscribedThing {
    public static void fetchSubscribedThing(final Retrofit retrofit, String accessToken, String accountName,
                                            final Integer page, final ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                            final ArrayList<SubscribedUserData> subscribedUserData,
                                            final ArrayList<SubredditData> subredditData,
                                            final FetchSubscribedThingListener fetchSubscribedThingListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);

        Call<String> subredditDataCall = api.listCommunities("Subscribed",null,page,null,accessToken);
        subredditDataCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ParseSubscribedThing.parseSubscribedSubreddits(response.body(), accountName,
                            subscribedSubredditData, subscribedUserData, subredditData,
                            new ParseSubscribedThing.ParseSubscribedSubredditsListener() {

                                @Override
                                public void onParseSubscribedSubredditsSuccess(ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                                                               ArrayList<SubscribedUserData> subscribedUserData,
                                                                               ArrayList<SubredditData> subredditData,
                                                                               boolean lastItem) {
                                    if (lastItem) {
                                        fetchSubscribedThingListener.onFetchSubscribedThingSuccess(
                                                subscribedSubredditData, subscribedUserData, subredditData);
                                    } else {
                                        fetchSubscribedThing(retrofit, accessToken, accountName, (page == null) ? 2 : page+1,
                                                subscribedSubredditData, subscribedUserData, subredditData,
                                                fetchSubscribedThingListener);
                                    }
                                }

                                @Override
                                public void onParseSubscribedSubredditsFail() {
                                    fetchSubscribedThingListener.onFetchSubscribedThingFail();
                                }
                            });
                } else {
                    fetchSubscribedThingListener.onFetchSubscribedThingFail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                fetchSubscribedThingListener.onFetchSubscribedThingFail();
            }
        });
    }

    public interface FetchSubscribedThingListener {
        void onFetchSubscribedThingSuccess(ArrayList<SubscribedSubredditData> subscribedSubredditData,
                                           ArrayList<SubscribedUserData> subscribedUserData,
                                           ArrayList<SubredditData> subredditData);

        void onFetchSubscribedThingFail();
    }
}
