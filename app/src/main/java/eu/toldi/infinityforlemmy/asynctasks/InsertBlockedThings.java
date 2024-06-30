package eu.toldi.infinityforlemmy.asynctasks;

import android.os.Handler;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityDao;
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityData;
import eu.toldi.infinityforlemmy.blockedinstances.BlockedInstanceData;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserDao;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserData;


public class InsertBlockedThings {

    public static void insertBlockedThings(Executor executor, Handler handler,
                                           RedditDataRoomDatabase redditDataRoomDatabase, @Nullable String accountName,
                                           List<BlockedCommunityData> blockedCommunityDataList,
                                           List<BlockedUserData> blockedUserDataDataList,
                                           List<BlockedInstanceData> blockedInstanceDataList,

                                           InsertBlockedThingListener insertSubscribedThingListener) {
        executor.execute(() -> {
            if (accountName != null && redditDataRoomDatabase.accountDao().getAccountData(accountName) == null) {
                handler.post(insertSubscribedThingListener::insertSuccess);
                return;
            }

            BlockedUserDao blockedUserDao = redditDataRoomDatabase.blockedUserDao();
            BlockedCommunityDao blockedCommunityDao = redditDataRoomDatabase.blockedCommunityDao();


            if (blockedCommunityDataList != null) {
                List<BlockedCommunityData> existingBlockedCommunityDaoList =
                        blockedCommunityDao.getAllBlockedCommunitiesList(accountName);
                Collections.sort(blockedCommunityDataList, (subscribedSubredditData, t1) -> subscribedSubredditData.getName().compareToIgnoreCase(t1.getName()));
                List<String> unsubscribedSubreddits = new ArrayList<>();
                compareTwoSubscribedSubredditList(blockedCommunityDataList, existingBlockedCommunityDaoList,
                        unsubscribedSubreddits);

                for (String unsubscribed : unsubscribedSubreddits) {
                    blockedCommunityDao.deleteBlockedCommunity(unsubscribed, accountName);
                }

                for (BlockedCommunityData s : blockedCommunityDataList) {
                    blockedCommunityDao.insert(s);
                }
            }

            if (blockedUserDataDataList != null) {
                List<BlockedUserData> existingBlockedUserDataList =
                        blockedUserDao.getAllBlockedUsersList(accountName);
                Collections.sort(blockedUserDataDataList, (subscribedUserData, t1) -> subscribedUserData.getName().compareToIgnoreCase(t1.getName()));
                List<String> unsubscribedUsers = new ArrayList<>();
                compareTwoSubscribedUserList(blockedUserDataDataList, existingBlockedUserDataList,
                        unsubscribedUsers);

                for (String unsubscribed : unsubscribedUsers) {
                    blockedUserDao.deleteBlockedUser(unsubscribed, accountName);
                }

                for (BlockedUserData s : blockedUserDataDataList) {
                    blockedUserDao.insert(s);
                }
            }

            if (blockedInstanceDataList != null) {
                List<BlockedInstanceData> existingBlockedInstanceDataList =
                        redditDataRoomDatabase.blockedInstanceDao().getAllInstanceInstancesList(accountName);
                Collections.sort(blockedInstanceDataList, (subscribedInstanceData, t1) -> subscribedInstanceData.getDomain().compareToIgnoreCase(t1.getDomain()));
                List<String> unblockedInstances = new ArrayList<>();
                compareTwoBlockedInstanceList(blockedInstanceDataList, existingBlockedInstanceDataList,
                        unblockedInstances);

                for (String unblocked : unblockedInstances) {
                    redditDataRoomDatabase.blockedInstanceDao().deleteInstanceUser(unblocked, accountName);
                }

                for (BlockedInstanceData s : blockedInstanceDataList) {
                    redditDataRoomDatabase.blockedInstanceDao().insert(s);
                }
            }

            handler.post(insertSubscribedThingListener::insertSuccess);
        });
    }

    private static void compareTwoSubscribedSubredditList(List<BlockedCommunityData> newSubscribedSubreddits,
                                                          List<BlockedCommunityData> oldSubscribedSubreddits,
                                                          List<String> unsubscribedSubredditNames) {
        int newIndex = 0;
        for (int oldIndex = 0; oldIndex < oldSubscribedSubreddits.size(); oldIndex++) {
            if (newIndex >= newSubscribedSubreddits.size()) {
                for (; oldIndex < oldSubscribedSubreddits.size(); oldIndex++) {
                    unsubscribedSubredditNames.add(oldSubscribedSubreddits.get(oldIndex).getQualified_name());
                }
                return;
            }

            BlockedCommunityData old = oldSubscribedSubreddits.get(oldIndex);
            for (; newIndex < newSubscribedSubreddits.size(); newIndex++) {
                if (newSubscribedSubreddits.get(newIndex).getQualified_name().compareToIgnoreCase(old.getQualified_name()) == 0) {
                    newIndex++;
                    break;
                }
                if (newSubscribedSubreddits.get(newIndex).getQualified_name().compareToIgnoreCase(old.getQualified_name()) > 0) {
                    unsubscribedSubredditNames.add(old.getQualified_name());
                    break;
                }
            }
        }
    }

    private static void compareTwoSubscribedUserList(List<BlockedUserData> newSubscribedUsers,
                                                     List<BlockedUserData> oldSubscribedUsers,
                                                     List<String> unsubscribedUserNames) {
        int newIndex = 0;
        for (int oldIndex = 0; oldIndex < oldSubscribedUsers.size(); oldIndex++) {
            if (newIndex >= newSubscribedUsers.size()) {
                for (; oldIndex < oldSubscribedUsers.size(); oldIndex++) {
                    unsubscribedUserNames.add(oldSubscribedUsers.get(oldIndex).getQualifiedName());
                }
                return;
            }

            BlockedUserData old = oldSubscribedUsers.get(oldIndex);
            for (; newIndex < newSubscribedUsers.size(); newIndex++) {
                if (newSubscribedUsers.get(newIndex).getName().compareToIgnoreCase(old.getName()) == 0) {
                    newIndex++;
                    break;
                }
                if (newSubscribedUsers.get(newIndex).getName().compareToIgnoreCase(old.getName()) > 0) {
                    unsubscribedUserNames.add(old.getQualifiedName());
                    break;
                }
            }
        }
    }

    private static void compareTwoBlockedInstanceList(List<BlockedInstanceData> newBlockedInstances,
                                                      List<BlockedInstanceData> oldBlockedInstances,
                                                      List<String> unblockedInstances) {
        int newIndex = 0;
        for (int oldIndex = 0; oldIndex < oldBlockedInstances.size(); oldIndex++) {
            if (newIndex >= newBlockedInstances.size()) {
                for (; oldIndex < oldBlockedInstances.size(); oldIndex++) {
                    unblockedInstances.add(oldBlockedInstances.get(oldIndex).getDomain());
                }
                return;
            }

            BlockedInstanceData old = oldBlockedInstances.get(oldIndex);
            for (; newIndex < newBlockedInstances.size(); newIndex++) {
                if (newBlockedInstances.get(newIndex).getDomain().compareToIgnoreCase(old.getDomain()) == 0) {
                    newIndex++;
                    break;
                }
                if (newBlockedInstances.get(newIndex).getDomain().compareToIgnoreCase(old.getDomain()) > 0) {
                    unblockedInstances.add(old.getDomain());
                    break;
                }
            }
        }
    }

    public interface InsertBlockedThingListener {
        void insertSuccess();
    }
}
