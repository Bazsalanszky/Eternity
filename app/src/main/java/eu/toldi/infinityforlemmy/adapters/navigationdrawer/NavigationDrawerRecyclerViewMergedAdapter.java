package eu.toldi.infinityforlemmy.adapters.navigationdrawer;

import android.content.SharedPreferences;

import androidx.recyclerview.widget.ConcatAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;

public class NavigationDrawerRecyclerViewMergedAdapter {
    private HeaderSectionRecyclerViewAdapter headerSectionRecyclerViewAdapter;
    private AccountSectionRecyclerViewAdapter accountSectionRecyclerViewAdapter;
    private PreferenceSectionRecyclerViewAdapter preferenceSectionRecyclerViewAdapter;
    private FavoriteSubscribedSubredditsSectionRecyclerViewAdapter favoriteSubscribedSubredditsSectionRecyclerViewAdapter;
    private SubscribedSubredditsRecyclerViewAdapter subscribedSubredditsRecyclerViewAdapter;
    private AccountManagementSectionRecyclerViewAdapter accountManagementSectionRecyclerViewAdapter;
    private LemmySectionRecyclerViewAdapter lemmySectionRecyclerViewAdapter;
    private ConcatAdapter mainPageConcatAdapter;

    public NavigationDrawerRecyclerViewMergedAdapter(BaseActivity baseActivity, SharedPreferences sharedPreferences,
                                                     SharedPreferences nsfwAndSpoilerSharedPreferences,
                                                     SharedPreferences navigationDrawerSharedPreferences,
                                                     SharedPreferences securitySharedPreferences,
                                                     CustomThemeWrapper customThemeWrapper,
                                                     String accountName, String accountQualifiedName,
                                                     ItemClickListener itemClickListener) {
        RequestManager glide = Glide.with(baseActivity);

        headerSectionRecyclerViewAdapter = new HeaderSectionRecyclerViewAdapter(baseActivity, customThemeWrapper,
                glide, accountName, accountQualifiedName, sharedPreferences, navigationDrawerSharedPreferences, securitySharedPreferences,
                new HeaderSectionRecyclerViewAdapter.PageToggle() {
                    @Override
                    public void openAccountSection() {
                        NavigationDrawerRecyclerViewMergedAdapter.this.openAccountSection();
                    }

                    @Override
                    public void closeAccountSectionWithoutChangeIconResource() {
                        NavigationDrawerRecyclerViewMergedAdapter.this.closeAccountSectionWithoutChangeIconResource();
                    }
                });
        accountSectionRecyclerViewAdapter = new AccountSectionRecyclerViewAdapter(baseActivity, customThemeWrapper,
                navigationDrawerSharedPreferences, accountName != null, itemClickListener);
        preferenceSectionRecyclerViewAdapter = new PreferenceSectionRecyclerViewAdapter(baseActivity, customThemeWrapper,
                accountName, nsfwAndSpoilerSharedPreferences, navigationDrawerSharedPreferences, itemClickListener);
        favoriteSubscribedSubredditsSectionRecyclerViewAdapter = new FavoriteSubscribedSubredditsSectionRecyclerViewAdapter(
                baseActivity, glide, customThemeWrapper, navigationDrawerSharedPreferences, itemClickListener);
        subscribedSubredditsRecyclerViewAdapter = new SubscribedSubredditsRecyclerViewAdapter(baseActivity, glide,
                customThemeWrapper, navigationDrawerSharedPreferences, itemClickListener);
        accountManagementSectionRecyclerViewAdapter = new AccountManagementSectionRecyclerViewAdapter(baseActivity,
                customThemeWrapper, glide, accountName != null, itemClickListener);
        lemmySectionRecyclerViewAdapter = new LemmySectionRecyclerViewAdapter(baseActivity, customThemeWrapper,
                navigationDrawerSharedPreferences, itemClickListener, accountName != null);

        mainPageConcatAdapter = new ConcatAdapter(
                headerSectionRecyclerViewAdapter,
                accountSectionRecyclerViewAdapter,
                lemmySectionRecyclerViewAdapter,
                preferenceSectionRecyclerViewAdapter,
                favoriteSubscribedSubredditsSectionRecyclerViewAdapter,
                subscribedSubredditsRecyclerViewAdapter);
    }

    public ConcatAdapter getConcatAdapter() {
        return mainPageConcatAdapter;
    }

    private void openAccountSection() {
        mainPageConcatAdapter.removeAdapter(accountSectionRecyclerViewAdapter);
        mainPageConcatAdapter.removeAdapter(lemmySectionRecyclerViewAdapter);
        mainPageConcatAdapter.removeAdapter(preferenceSectionRecyclerViewAdapter);
        mainPageConcatAdapter.removeAdapter(favoriteSubscribedSubredditsSectionRecyclerViewAdapter);
        mainPageConcatAdapter.removeAdapter(subscribedSubredditsRecyclerViewAdapter);

        mainPageConcatAdapter.addAdapter(accountManagementSectionRecyclerViewAdapter);
    }

    public void closeAccountSectionWithoutChangeIconResource() {
        mainPageConcatAdapter.removeAdapter(accountManagementSectionRecyclerViewAdapter);

        mainPageConcatAdapter.addAdapter(accountSectionRecyclerViewAdapter);
        mainPageConcatAdapter.addAdapter(lemmySectionRecyclerViewAdapter);
        mainPageConcatAdapter.addAdapter(preferenceSectionRecyclerViewAdapter);
        mainPageConcatAdapter.addAdapter(favoriteSubscribedSubredditsSectionRecyclerViewAdapter);
        mainPageConcatAdapter.addAdapter(subscribedSubredditsRecyclerViewAdapter);
    }

    public void closeAccountSectionWithoutChangeIconResource(boolean checkIsInMainPage) {
        closeAccountSectionWithoutChangeIconResource();
        headerSectionRecyclerViewAdapter.closeAccountSectionWithoutChangeIconResource(checkIsInMainPage);
    }

    public void updateAccountInfo(String profileImageUrl, String bannerImageUrl) {
        headerSectionRecyclerViewAdapter.updateAccountInfo(profileImageUrl, bannerImageUrl);
    }

    public void setRequireAuthToAccountSection(boolean requireAuthToAccountSection) {
        headerSectionRecyclerViewAdapter.setRequireAuthToAccountSection(requireAuthToAccountSection);
    }

    public void setShowAvatarOnTheRightInTheNavigationDrawer(boolean showAvatarOnTheRightInTheNavigationDrawer) {
        headerSectionRecyclerViewAdapter.setShowAvatarOnTheRightInTheNavigationDrawer(showAvatarOnTheRightInTheNavigationDrawer);
    }

    public void changeAccountsDataset(List<Account> accounts) {
        accountManagementSectionRecyclerViewAdapter.changeAccountsDataset(accounts);
    }

    public void setInboxCount(int inboxCount) {
        accountSectionRecyclerViewAdapter.setInboxCount(inboxCount);
    }

    public void setNSFWEnabled(boolean isNSFWEnabled) {
        preferenceSectionRecyclerViewAdapter.setNSFWEnabled(isNSFWEnabled);
    }

    public void setFavoriteSubscribedSubreddits(List<SubscribedSubredditData> favoriteSubscribedSubreddits) {
        favoriteSubscribedSubredditsSectionRecyclerViewAdapter.setFavoriteSubscribedSubreddits(favoriteSubscribedSubreddits);
    }

    public void setSubscribedSubreddits(List<SubscribedSubredditData> subscribedSubreddits) {
        subscribedSubredditsRecyclerViewAdapter.setSubscribedSubreddits(subscribedSubreddits);
    }

    public void setHideKarma(boolean hideKarma) {
        headerSectionRecyclerViewAdapter.setHideKarma(hideKarma);
    }

    public interface ItemClickListener {
        void onMenuClick(int stringId);

        void onSubscribedSubredditClick(String subredditName, String communityQualifiedName);

        void onAccountClick(String accountName);
    }
}
