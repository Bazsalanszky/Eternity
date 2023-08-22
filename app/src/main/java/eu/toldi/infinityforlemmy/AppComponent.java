package eu.toldi.infinityforlemmy;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import eu.toldi.infinityforlemmy.activities.AccountPostsActivity;
import eu.toldi.infinityforlemmy.activities.AccountSavedThingActivity;
import eu.toldi.infinityforlemmy.activities.BlockedThingListingActivity;
import eu.toldi.infinityforlemmy.activities.CommentActivity;
import eu.toldi.infinityforlemmy.activities.CreateMultiRedditActivity;
import eu.toldi.infinityforlemmy.activities.CustomThemeListingActivity;
import eu.toldi.infinityforlemmy.activities.CustomThemePreviewActivity;
import eu.toldi.infinityforlemmy.activities.CustomizePostFilterActivity;
import eu.toldi.infinityforlemmy.activities.CustomizeThemeActivity;
import eu.toldi.infinityforlemmy.activities.EditCommentActivity;
import eu.toldi.infinityforlemmy.activities.EditMultiRedditActivity;
import eu.toldi.infinityforlemmy.activities.EditPostActivity;
import eu.toldi.infinityforlemmy.activities.EditProfileActivity;
import eu.toldi.infinityforlemmy.activities.FetchRandomSubredditOrPostActivity;
import eu.toldi.infinityforlemmy.activities.FilteredPostsActivity;
import eu.toldi.infinityforlemmy.activities.FullMarkdownActivity;
import eu.toldi.infinityforlemmy.activities.GiveAwardActivity;
import eu.toldi.infinityforlemmy.activities.HistoryActivity;
import eu.toldi.infinityforlemmy.activities.InboxActivity;
import eu.toldi.infinityforlemmy.activities.InstanceInfoActivity;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.LockScreenActivity;
import eu.toldi.infinityforlemmy.activities.LoginActivity;
import eu.toldi.infinityforlemmy.activities.MainActivity;
import eu.toldi.infinityforlemmy.activities.MultiredditSelectionActivity;
import eu.toldi.infinityforlemmy.activities.PostFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.activities.PostFilterUsageListingActivity;
import eu.toldi.infinityforlemmy.activities.PostGalleryActivity;
import eu.toldi.infinityforlemmy.activities.PostImageActivity;
import eu.toldi.infinityforlemmy.activities.PostLinkActivity;
import eu.toldi.infinityforlemmy.activities.PostPollActivity;
import eu.toldi.infinityforlemmy.activities.PostTextActivity;
import eu.toldi.infinityforlemmy.activities.PostVideoActivity;
import eu.toldi.infinityforlemmy.activities.ReportActivity;
import eu.toldi.infinityforlemmy.activities.RulesActivity;
import eu.toldi.infinityforlemmy.activities.SearchActivity;
import eu.toldi.infinityforlemmy.activities.SearchResultActivity;
import eu.toldi.infinityforlemmy.activities.SearchSubredditsResultActivity;
import eu.toldi.infinityforlemmy.activities.SearchUsersResultActivity;
import eu.toldi.infinityforlemmy.activities.SelectUserFlairActivity;
import eu.toldi.infinityforlemmy.activities.SelectedSubredditsAndUsersActivity;
import eu.toldi.infinityforlemmy.activities.SendPrivateMessageActivity;
import eu.toldi.infinityforlemmy.activities.SettingsActivity;
import eu.toldi.infinityforlemmy.activities.SubmitCrosspostActivity;
import eu.toldi.infinityforlemmy.activities.SubredditMultiselectionActivity;
import eu.toldi.infinityforlemmy.activities.SubredditSelectionActivity;
import eu.toldi.infinityforlemmy.activities.SubscribedThingListingActivity;
import eu.toldi.infinityforlemmy.activities.SuicidePreventionActivity;
import eu.toldi.infinityforlemmy.activities.TrendingActivity;
import eu.toldi.infinityforlemmy.activities.ViewImageOrGifActivity;
import eu.toldi.infinityforlemmy.activities.ViewImgurMediaActivity;
import eu.toldi.infinityforlemmy.activities.ViewMultiRedditDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewPostDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewPrivateMessagesActivity;
import eu.toldi.infinityforlemmy.activities.ViewRedditGalleryActivity;
import eu.toldi.infinityforlemmy.activities.ViewSubredditDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewVideoActivity;
import eu.toldi.infinityforlemmy.activities.WebViewActivity;
import eu.toldi.infinityforlemmy.activities.WikiActivity;
import eu.toldi.infinityforlemmy.bottomsheetfragments.AccountChooserBottomSheetFragment;
import eu.toldi.infinityforlemmy.bottomsheetfragments.CommentMoreBottomSheetFragment;
import eu.toldi.infinityforlemmy.bottomsheetfragments.FlairBottomSheetFragment;
import eu.toldi.infinityforlemmy.fragments.BlockedCommunitiesListingFragment;
import eu.toldi.infinityforlemmy.fragments.BlockedUsersListingFragment;
import eu.toldi.infinityforlemmy.fragments.CommentsListingFragment;
import eu.toldi.infinityforlemmy.fragments.FollowedUsersListingFragment;
import eu.toldi.infinityforlemmy.fragments.HistoryPostFragment;
import eu.toldi.infinityforlemmy.fragments.InboxFragment;
import eu.toldi.infinityforlemmy.fragments.MorePostsInfoFragment;
import eu.toldi.infinityforlemmy.fragments.MultiRedditListingFragment;
import eu.toldi.infinityforlemmy.fragments.PostFragment;
import eu.toldi.infinityforlemmy.fragments.PrivateMessageFragment;
import eu.toldi.infinityforlemmy.fragments.SidebarFragment;
import eu.toldi.infinityforlemmy.fragments.SubredditListingFragment;
import eu.toldi.infinityforlemmy.fragments.SubscribedSubredditsListingFragment;
import eu.toldi.infinityforlemmy.fragments.UserListingFragment;
import eu.toldi.infinityforlemmy.fragments.ViewImgurImageFragment;
import eu.toldi.infinityforlemmy.fragments.ViewImgurVideoFragment;
import eu.toldi.infinityforlemmy.fragments.ViewPostDetailFragment;
import eu.toldi.infinityforlemmy.fragments.ViewRedditGalleryImageOrGifFragment;
import eu.toldi.infinityforlemmy.fragments.ViewRedditGalleryVideoFragment;
import eu.toldi.infinityforlemmy.services.DownloadMediaService;
import eu.toldi.infinityforlemmy.services.DownloadRedditVideoService;
import eu.toldi.infinityforlemmy.services.EditProfileService;
import eu.toldi.infinityforlemmy.services.SubmitPostService;
import eu.toldi.infinityforlemmy.settings.AdvancedPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.CommentPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.CrashReportsFragment;
import eu.toldi.infinityforlemmy.settings.CustomizeBottomAppBarFragment;
import eu.toldi.infinityforlemmy.settings.CustomizeMainPageTabsFragment;
import eu.toldi.infinityforlemmy.settings.DownloadLocationPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.FontPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.GesturesAndButtonsPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.MainPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.MiscellaneousPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.NotificationPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.NsfwAndSpoilerFragment;
import eu.toldi.infinityforlemmy.settings.PostHistoryFragment;
import eu.toldi.infinityforlemmy.settings.SecurityPreferenceFragment;
import eu.toldi.infinityforlemmy.settings.ThemePreferenceFragment;
import eu.toldi.infinityforlemmy.settings.TranslationFragment;
import eu.toldi.infinityforlemmy.settings.VideoPreferenceFragment;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(LoginActivity loginActivity);

    void inject(PostFragment postFragment);

    void inject(SubredditListingFragment subredditListingFragment);

    void inject(UserListingFragment userListingFragment);

    void inject(ViewPostDetailActivity viewPostDetailActivity);

    void inject(ViewSubredditDetailActivity viewSubredditDetailActivity);

    void inject(ViewUserDetailActivity viewUserDetailActivity);

    void inject(CommentActivity commentActivity);

    void inject(SubscribedThingListingActivity subscribedThingListingActivity);

    void inject(PostTextActivity postTextActivity);

    void inject(SubscribedSubredditsListingFragment subscribedSubredditsListingFragment);

    void inject(PostLinkActivity postLinkActivity);

    void inject(PostImageActivity postImageActivity);

    void inject(PostVideoActivity postVideoActivity);

    void inject(FlairBottomSheetFragment flairBottomSheetFragment);

    void inject(RulesActivity rulesActivity);

    void inject(CommentsListingFragment commentsListingFragment);

    void inject(SubmitPostService submitPostService);

    void inject(FilteredPostsActivity filteredPostsActivity);

    void inject(SearchResultActivity searchResultActivity);

    void inject(SearchSubredditsResultActivity searchSubredditsResultActivity);

    void inject(FollowedUsersListingFragment followedUsersListingFragment);

    void inject(SubredditSelectionActivity subredditSelectionActivity);

    void inject(EditPostActivity editPostActivity);

    void inject(EditCommentActivity editCommentActivity);

    void inject(AccountPostsActivity accountPostsActivity);

    void inject(PullNotificationWorker pullNotificationWorker);

    void inject(InboxActivity inboxActivity);

    void inject(NotificationPreferenceFragment notificationPreferenceFragment);

    void inject(LinkResolverActivity linkResolverActivity);

    void inject(SearchActivity searchActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(MainPreferenceFragment mainPreferenceFragment);

    void inject(AccountSavedThingActivity accountSavedThingActivity);

    void inject(ViewImageOrGifActivity viewGIFActivity);

    void inject(ViewMultiRedditDetailActivity viewMultiRedditDetailActivity);

    void inject(ViewVideoActivity viewVideoActivity);

    void inject(GesturesAndButtonsPreferenceFragment gesturesAndButtonsPreferenceFragment);

    void inject(CreateMultiRedditActivity createMultiRedditActivity);

    void inject(SubredditMultiselectionActivity subredditMultiselectionActivity);

    void inject(ThemePreferenceFragment themePreferenceFragment);

    void inject(CustomizeThemeActivity customizeThemeActivity);

    void inject(CustomThemeListingActivity customThemeListingActivity);

    void inject(SidebarFragment sidebarFragment);

    void inject(AdvancedPreferenceFragment advancedPreferenceFragment);

    void inject(CustomThemePreviewActivity customThemePreviewActivity);

    void inject(EditMultiRedditActivity editMultiRedditActivity);

    void inject(SelectedSubredditsAndUsersActivity selectedSubredditsAndUsersActivity);

    void inject(ReportActivity reportActivity);

    void inject(ViewImgurMediaActivity viewImgurMediaActivity);

    void inject(ViewImgurVideoFragment viewImgurVideoFragment);

    void inject(DownloadRedditVideoService downloadRedditVideoService);

    void inject(MultiRedditListingFragment multiRedditListingFragment);

    void inject(InboxFragment inboxFragment);

    void inject(ViewPrivateMessagesActivity viewPrivateMessagesActivity);

    void inject(SendPrivateMessageActivity sendPrivateMessageActivity);

    void inject(VideoPreferenceFragment videoPreferenceFragment);

    void inject(ViewRedditGalleryActivity viewRedditGalleryActivity);

    void inject(ViewRedditGalleryVideoFragment viewRedditGalleryVideoFragment);

    void inject(CustomizeMainPageTabsFragment customizeMainPageTabsFragment);

    void inject(DownloadMediaService downloadMediaService);

    void inject(DownloadLocationPreferenceFragment downloadLocationPreferenceFragment);

    void inject(SubmitCrosspostActivity submitCrosspostActivity);

    void inject(FullMarkdownActivity fullMarkdownActivity);

    void inject(SelectUserFlairActivity selectUserFlairActivity);

    void inject(SecurityPreferenceFragment securityPreferenceFragment);

    void inject(NsfwAndSpoilerFragment nsfwAndSpoilerFragment);

    void inject(CustomizeBottomAppBarFragment customizeBottomAppBarFragment);

    void inject(GiveAwardActivity giveAwardActivity);

    void inject(TranslationFragment translationFragment);

    void inject(FetchRandomSubredditOrPostActivity fetchRandomSubredditOrPostActivity);

    void inject(MiscellaneousPreferenceFragment miscellaneousPreferenceFragment);

    void inject(CustomizePostFilterActivity customizePostFilterActivity);

    void inject(PostHistoryFragment postHistoryFragment);

    void inject(PostFilterPreferenceActivity postFilterPreferenceActivity);

    void inject(PostFilterUsageListingActivity postFilterUsageListingActivity);

    void inject(SearchUsersResultActivity searchUsersResultActivity);

    void inject(MultiredditSelectionActivity multiredditSelectionActivity);

    void inject(ViewImgurImageFragment viewImgurImageFragment);

    void inject(ViewRedditGalleryImageOrGifFragment viewRedditGalleryImageOrGifFragment);

    void inject(ViewPostDetailFragment viewPostDetailFragment);

    void inject(SuicidePreventionActivity suicidePreventionActivity);

    void inject(WebViewActivity webViewActivity);

    void inject(CrashReportsFragment crashReportsFragment);

    void inject(LockScreenActivity lockScreenActivity);

    void inject(PostGalleryActivity postGalleryActivity);

    void inject(TrendingActivity trendingActivity);

    void inject(WikiActivity wikiActivity);

    void inject(Infinity infinity);

    void inject(EditProfileService editProfileService);

    void inject(EditProfileActivity editProfileActivity);

    void inject(FontPreferenceFragment fontPreferenceFragment);

    void inject(CommentPreferenceFragment commentPreferenceFragment);

    void inject(PostPollActivity postPollActivity);

    void inject(AccountChooserBottomSheetFragment accountChooserBottomSheetFragment);

    void inject(MaterialYouWorker materialYouWorker);

    void inject(HistoryPostFragment historyPostFragment);

    void inject(HistoryActivity historyActivity);

    void inject(MorePostsInfoFragment morePostsInfoFragment);

    void inject(BlockedThingListingActivity blockedThingListingActivity);

    void inject(BlockedCommunitiesListingFragment blockedCommunitiesListingFragment);

    void inject(BlockedUsersListingFragment blockedUsersListingFragment);

    void inject(CommentMoreBottomSheetFragment commentMoreBottomSheetFragment);

    void inject(PrivateMessageFragment privateMessageFragment);

    void inject(@NotNull InstanceInfoActivity instanceInfoActivity);

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }
}
