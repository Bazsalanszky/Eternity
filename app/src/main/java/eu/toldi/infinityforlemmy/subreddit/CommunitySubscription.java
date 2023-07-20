package eu.toldi.infinityforlemmy.subreddit;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.FollowCommunityDTO;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class CommunitySubscription {
    public static void subscribeToCommunity(Executor executor, Handler handler,
                                            Retrofit retrofit, String accessToken, int communityId, String communityQualifiedName,
                                            String accountName, RedditDataRoomDatabase redditDataRoomDatabase,
                                            SubredditSubscriptionListener subredditSubscriptionListener) {
        communitySubscription(executor, handler, retrofit, accessToken, communityId, communityQualifiedName,
                accountName, "sub", redditDataRoomDatabase, subredditSubscriptionListener);
    }

    public static void anonymousSubscribeToSubreddit(Executor executor, Handler handler, Retrofit retrofit,
                                                     RedditDataRoomDatabase redditDataRoomDatabase,
                                                     String subredditName,
                                                     SubredditSubscriptionListener subredditSubscriptionListener) {
        FetchSubredditData.fetchSubredditData(retrofit, subredditName, null, new FetchSubredditData.FetchSubredditDataListener() {
            @Override
            public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                insertSubscription(executor, handler, redditDataRoomDatabase,
                        subredditData, "-", subredditSubscriptionListener);
            }

            @Override
            public void onFetchSubredditDataFail(boolean isQuarantined) {
                subredditSubscriptionListener.onSubredditSubscriptionFail();
            }
        });
    }

    public static void unsubscribeToCommunity(Executor executor, Handler handler,
                                              Retrofit retrofit, String accessToken, int communityId, String communityQualifiedName, String accountName,
                                              RedditDataRoomDatabase redditDataRoomDatabase,
                                              SubredditSubscriptionListener subredditSubscriptionListener) {
        communitySubscription(executor, handler, retrofit, accessToken, communityId, communityQualifiedName,
                accountName, "unsub", redditDataRoomDatabase, subredditSubscriptionListener);
    }

    public static void anonymousUnsubscribeToSubreddit(Executor executor, Handler handler,
                                                       RedditDataRoomDatabase redditDataRoomDatabase,
                                                       String subredditName,
                                                       SubredditSubscriptionListener subredditSubscriptionListener) {
        removeSubscription(executor, handler, redditDataRoomDatabase, subredditName, "-", subredditSubscriptionListener);
    }

    private static void communitySubscription(Executor executor, Handler handler,
                                              Retrofit retrofit, String accessToken, int communityId, String communityQualifiedName,
                                              String accountName, String action,
                                              RedditDataRoomDatabase redditDataRoomDatabase,
                                              SubredditSubscriptionListener subredditSubscriptionListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);


        Call<String> subredditSubscriptionCall = api.communityFollow(new FollowCommunityDTO(communityId, action.equals("sub"), accessToken));
        subredditSubscriptionCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    if (action.equals("sub")) {
                        FetchSubredditData.fetchSubredditData(retrofit, communityQualifiedName, accessToken, new FetchSubredditData.FetchSubredditDataListener() {
                            @Override
                            public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                                insertSubscription(executor, handler, redditDataRoomDatabase,
                                        subredditData, accountName, subredditSubscriptionListener);
                            }

                            @Override
                            public void onFetchSubredditDataFail(boolean isQuarantined) {

                            }
                        });
                    } else {
                        removeSubscription(executor, handler, redditDataRoomDatabase, communityQualifiedName,
                                accountName, subredditSubscriptionListener);
                    }
                } else {
                    subredditSubscriptionListener.onSubredditSubscriptionFail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                subredditSubscriptionListener.onSubredditSubscriptionFail();
            }
        });
    }

    public interface SubredditSubscriptionListener {
        void onSubredditSubscriptionSuccess();

        void onSubredditSubscriptionFail();
    }

    private static void insertSubscription(Executor executor, Handler handler,
                                           RedditDataRoomDatabase redditDataRoomDatabase,
                                           SubredditData subredditData, String accountName,
                                           SubredditSubscriptionListener subredditSubscriptionListener) {
        executor.execute(() -> {
            SubscribedSubredditData subscribedSubredditData = new SubscribedSubredditData(subredditData.getId(), subredditData.getName(), LemmyUtils.actorID2FullName(subredditData.getActorId()),
                    subredditData.getIconUrl(), accountName, false);
            if (accountName.equals("-")) {
                if (!redditDataRoomDatabase.accountDao().isAnonymousAccountInserted()) {
                    redditDataRoomDatabase.accountDao().insert(Account.getAnonymousAccount());
                }
            }
            redditDataRoomDatabase.subscribedSubredditDao().insert(subscribedSubredditData);
            handler.post(subredditSubscriptionListener::onSubredditSubscriptionSuccess);
        });
    }

    private static void removeSubscription(Executor executor, Handler handler,
                                           RedditDataRoomDatabase redditDataRoomDatabase,
                                           String subredditName, String accountName,
                                           SubredditSubscriptionListener subredditSubscriptionListener) {
        executor.execute(() -> {
            if (accountName.equals("-")) {
                if (!redditDataRoomDatabase.accountDao().isAnonymousAccountInserted()) {
                    redditDataRoomDatabase.accountDao().insert(Account.getAnonymousAccount());
                }
            }
            redditDataRoomDatabase.subscribedSubredditDao().deleteSubscribedSubreddit(subredditName, accountName);
            handler.post(subredditSubscriptionListener::onSubredditSubscriptionSuccess);
        });
    }
}
