package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.subreddit.SubredditDao;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditDao;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.subscribeduser.SubscribedUserDao;
import eu.toldi.infinityforlemmy.subscribeduser.SubscribedUserData;

public class InsertSubscribedThings {

    public static void insertSubscribedThings(Executor executor, Handler handler,
                                              RedditDataRoomDatabase redditDataRoomDatabase, @Nullable String accountName,
                                              List<SubscribedSubredditData> subscribedSubredditDataList,
                                              List<SubscribedUserData> subscribedUserDataList,
                                              List<SubredditData> subredditDataList,
                                              InsertSubscribedThingListener insertSubscribedThingListener) {
        executor.execute(() -> {
            if (accountName != null && redditDataRoomDatabase.accountDao().getAccountData(accountName) == null) {
                handler.post(insertSubscribedThingListener::insertSuccess);
                return;
            }

            SubscribedSubredditDao subscribedSubredditDao = redditDataRoomDatabase.subscribedSubredditDao();
            SubscribedUserDao subscribedUserDao = redditDataRoomDatabase.subscribedUserDao();
            SubredditDao subredditDao = redditDataRoomDatabase.subredditDao();

            if (subscribedSubredditDataList != null) {
                List<SubscribedSubredditData> existingSubscribedSubredditDataList =
                        subscribedSubredditDao.getAllSubscribedSubredditsList(accountName);
                Collections.sort(subscribedSubredditDataList, (subscribedSubredditData, t1) -> subscribedSubredditData.getQualified_name().compareToIgnoreCase(t1.getQualified_name()));
                List<String> unsubscribedSubreddits = new ArrayList<>();
                compareTwoSubscribedSubredditList(subscribedSubredditDataList, existingSubscribedSubredditDataList,
                        unsubscribedSubreddits);

                for (String unsubscribed : unsubscribedSubreddits) {
                    subscribedSubredditDao.deleteSubscribedSubreddit(unsubscribed, accountName);
                }

                for (SubscribedSubredditData s : subscribedSubredditDataList) {
                    if (existingSubscribedSubredditDataList.contains(s)) {
                        subscribedSubredditDao.updateSubscribedSubreddit(s.getQualified_name(), s.getName(), s.getIconUrl(), accountName);
                        continue;
                    }
                    subscribedSubredditDao.insert(s);
                }
            }

            if (subscribedUserDataList != null) {
                List<SubscribedUserData> existingSubscribedUserDataList =
                        subscribedUserDao.getAllSubscribedUsersList(accountName);
                Collections.sort(subscribedUserDataList, (subscribedUserData, t1) -> subscribedUserData.getName().compareToIgnoreCase(t1.getName()));
                List<String> unsubscribedUsers = new ArrayList<>();
                compareTwoSubscribedUserList(subscribedUserDataList, existingSubscribedUserDataList,
                        unsubscribedUsers);

                for (String unsubscribed : unsubscribedUsers) {
                    subscribedUserDao.deleteSubscribedUser(unsubscribed, accountName);
                }

                for (SubscribedUserData s : subscribedUserDataList) {
                    subscribedUserDao.insert(s);
                }
            }

            if (subredditDataList != null) {
                for (SubredditData s : subredditDataList) {
                    subredditDao.insert(s);
                }
            }

            handler.post(insertSubscribedThingListener::insertSuccess);
        });
    }

    public static void insertSubscribedThings(Executor executor, Handler
            handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                              SubscribedSubredditData singleSubscribedSubredditData,
                                              InsertSubscribedThingListener insertSubscribedThingListener) {
        executor.execute(() -> {
            String accountName = singleSubscribedSubredditData.getUsername();
            if (accountName != null && redditDataRoomDatabase.accountDao().getAccountData(accountName) == null) {
                handler.post(insertSubscribedThingListener::insertSuccess);
                return;
            }

            redditDataRoomDatabase.subscribedSubredditDao().insert(singleSubscribedSubredditData);
            handler.post(insertSubscribedThingListener::insertSuccess);
        });
    }

    public static void insertSubscribedThings(Executor executor, Handler
            handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                              SubscribedUserData mSingleSubscribedUserData,
                                              InsertSubscribedThingListener insertSubscribedThingListener) {
        executor.execute(() -> {
            String accountName = mSingleSubscribedUserData.getUsername();
            if (accountName != null && redditDataRoomDatabase.accountDao().getAccountData(accountName) == null) {
                handler.post(insertSubscribedThingListener::insertSuccess);
                return;
            }

            redditDataRoomDatabase.subscribedUserDao().insert(mSingleSubscribedUserData);
            handler.post(insertSubscribedThingListener::insertSuccess);
        });
    }

    private static void compareTwoSubscribedSubredditList
            (List<SubscribedSubredditData> newSubscribedSubreddits,
             List<SubscribedSubredditData> oldSubscribedSubreddits,
             List<String> unsubscribedSubredditNames) {
        List<String> oldSubredditNames = new ArrayList<>();
        for (SubscribedSubredditData subredditData : oldSubscribedSubreddits) {
            oldSubredditNames.add(subredditData.getQualified_name());
        }

        List<String> newSubredditNames = new ArrayList<>();
        for (SubscribedSubredditData subredditData : newSubscribedSubreddits) {
            newSubredditNames.add(subredditData.getQualified_name());
        }


        for (String subredditName : oldSubredditNames) {
            if (!newSubredditNames.contains(subredditName)) {
                unsubscribedSubredditNames.add(subredditName);
            }
        }
    }

    private static void compareTwoSubscribedUserList
            (List<SubscribedUserData> newSubscribedUsers,
             List<SubscribedUserData> oldSubscribedUsers,
             List<String> unsubscribedUserNames) {
        int newIndex = 0;
        for (int oldIndex = 0; oldIndex < oldSubscribedUsers.size(); oldIndex++) {
            if (newIndex >= newSubscribedUsers.size()) {
                for (; oldIndex < oldSubscribedUsers.size(); oldIndex++) {
                    unsubscribedUserNames.add(oldSubscribedUsers.get(oldIndex).getName());
                }
                return;
            }

            SubscribedUserData old = oldSubscribedUsers.get(oldIndex);
            for (; newIndex < newSubscribedUsers.size(); newIndex++) {
                if (newSubscribedUsers.get(newIndex).getName().compareToIgnoreCase(old.getName()) == 0) {
                    newIndex++;
                    break;
                }
                if (newSubscribedUsers.get(newIndex).getName().compareToIgnoreCase(old.getName()) > 0) {
                    unsubscribedUserNames.add(old.getName());
                    break;
                }
            }
        }
    }

    public interface InsertSubscribedThingListener {
        void insertSuccess();
    }
}
