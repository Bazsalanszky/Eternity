package eu.toldi.infinityforlemmy.customtheme;

import static eu.toldi.infinityforlemmy.utils.CustomThemeSharedPreferencesUtils.AMOLED;
import static eu.toldi.infinityforlemmy.utils.CustomThemeSharedPreferencesUtils.DARK;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.utils.CustomThemeSharedPreferencesUtils;

public class CustomThemeWrapper {
    private SharedPreferences lightThemeSharedPreferences;
    private SharedPreferences darkThemeSharedPreferences;
    private SharedPreferences amoledThemeSharedPreferences;
    private int themeType;

    public CustomThemeWrapper(SharedPreferences lightThemeSharedPreferences,
                              SharedPreferences darkThemeSharedPreferences,
                              SharedPreferences amoledThemeSharedPreferences) {
        this.lightThemeSharedPreferences = lightThemeSharedPreferences;
        this.darkThemeSharedPreferences = darkThemeSharedPreferences;
        this.amoledThemeSharedPreferences = amoledThemeSharedPreferences;
    }

    private SharedPreferences getThemeSharedPreferences() {
        switch (themeType) {
            case DARK:
                return darkThemeSharedPreferences;
            case AMOLED:
                return amoledThemeSharedPreferences;
            default:
                return lightThemeSharedPreferences;
        }
    }

    private int getDefaultColor(String normalHex, String darkHex, String amoledDarkHex) {
        switch (themeType) {
            case DARK:
                return Color.parseColor(darkHex);
            case AMOLED:
                return Color.parseColor(amoledDarkHex);
            default:
                return Color.parseColor(normalHex);
        }
    }

    public void setThemeType(int themeType) {
        this.themeType = themeType;
    }

    public int getColorPrimary() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COLOR_PRIMARY,
                getDefaultColor("#F2E9E1", "#2B3B51", "#282828"));
    }

    public int getColorPrimaryDark() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COLOR_PRIMARY_DARK,
                getDefaultColor("#F2E9E1", "#192330", "#161616"));
    }

    public int getColorAccent() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COLOR_ACCENT,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getColorPrimaryLightTheme() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COLOR_PRIMARY_LIGHT_THEME,
                getDefaultColor("#F2E9E1", "#192330", "#161616"));
    }

    public int getPrimaryTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.PRIMARY_TEXT_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getSecondaryTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SECONDARY_TEXT_COLOR,
                getDefaultColor("#352C24", "#DFDFE0", "#E4E4E5"));
    }

    public int getPostTitleColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.POST_TITLE_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getPostContentColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.POST_CONTENT_COLOR,
                getDefaultColor("#352C24", "#DFDFE0", "#E4E4E5"));
    }

    public int getReadPostTitleColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.READ_POST_TITLE_COLOR,
                getDefaultColor("#352C24", "#738091", "#484848"));
    }

    public int getReadPostContentColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.READ_POST_CONTENT_COLOR,
                getDefaultColor("#352C24", "#738091", "#484848"));
    }

    public int getCommentColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getButtonTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.BUTTON_TEXT_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.BACKGROUND_COLOR,
                getDefaultColor("#F6F2EE", "#192330", "#161616"));
    }

    public int getCardViewBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.CARD_VIEW_BACKGROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#282828"));
    }

    public int getReadPostCardViewBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.READ_POST_CARD_VIEW_BACKGROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#282828"));
    }

    public int getCommentBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_BACKGROUND_COLOR,
                getDefaultColor("#F6F2EE", "#192330", "#282828"));
    }

    public int getBottomAppBarBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.BOTTOM_APP_BAR_BACKGROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#161616"));
    }

    public int getPrimaryIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.PRIMARY_ICON_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getBottomAppBarIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.BOTTOM_APP_BAR_ICON_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getPostIconAndInfoColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.POST_ICON_AND_INFO_COLOR,
                getDefaultColor("#352C24", "#DFDFE0", "#E4E4E5"));
    }

    public int getCommentIconAndInfoColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_ICON_AND_INFO_COLOR,
                getDefaultColor("#352C24", "#DFDFE0", "#E4E4E5"));
    }

    public int getToolbarPrimaryTextAndIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TOOLBAR_PRIMARY_TEXT_AND_ICON_COLOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getToolbarSecondaryTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TOOLBAR_SECONDARY_TEXT_COLOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getCircularProgressBarBackground() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.CIRCULAR_PROGRESS_BAR_BACKGROUND,
                getDefaultColor("#F6F2EE", "#2B3B51", "#282828"));
    }

    public int getMediaIndicatorIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.MEDIA_INDICATOR_ICON_COLOR,
                getDefaultColor("#F6F2EE", "#393B44", "#161616"));
    }

    public int getMediaIndicatorBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.MEDIA_INDICATOR_BACKGROUND_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getTabLayoutWithExpandedCollapsingToolbarTabBackground() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_EXPANDED_COLLAPSING_TOOLBAR_TAB_BACKGROUND,
                getDefaultColor("#F6F2EE", "#2B3B51", "#282828"));
    }

    public int getTabLayoutWithExpandedCollapsingToolbarTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_EXPANDED_COLLAPSING_TOOLBAR_TEXT_COLOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getTabLayoutWithExpandedCollapsingToolbarTabIndicator() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_EXPANDED_COLLAPSING_TOOLBAR_TAB_INDICATOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getTabLayoutWithCollapsedCollapsingToolbarTabBackground() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_COLLAPSED_COLLAPSING_TOOLBAR_TAB_BACKGROUND,
                getDefaultColor("#F6F2EE", "#2B3B51", "#282828"));
    }

    public int getTabLayoutWithCollapsedCollapsingToolbarTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_COLLAPSED_COLLAPSING_TOOLBAR_TEXT_COLOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getTabLayoutWithCollapsedCollapsingToolbarTabIndicator() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.TAB_LAYOUT_WITH_COLLAPSED_COLLAPSING_TOOLBAR_TAB_INDICATOR,
                getDefaultColor("#352C24", "#CDCECF", "#F2F4F8"));
    }

    public int getUpvoted() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.UPVOTED,
                getDefaultColor("#A5222F", "#DBC074", "#F16DA6"));
    }

    public int getDownvoted() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.DOWNVOTED,
                getDefaultColor("#4863B6", "#D16983", "#52BDFF"));
    }

    public int getPostTypeBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.POST_TYPE_BACKGROUND_COLOR,
                getDefaultColor("#4863B6", "#719CD6", "#33B1FF"));
    }

    public int getPostTypeTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.POST_TYPE_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#DFDFE0", "#282828"));
    }

    public int getSpoilerBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SPOILER_BACKGROUND_COLOR,
                getDefaultColor("#A440B5", "#E0C989", "#F16DA6"));
    }

    public int getSpoilerTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SPOILER_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#393B44", "#282828"));
    }

    public int getNsfwBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.NSFW_BACKGROUND_COLOR,
                getDefaultColor("#A5222F", "#D16983", "#F16DA6"));
    }

    public int getNsfwTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.NSFW_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#393B44", "#282828"));
    }

    public int getFlairBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.FLAIR_BACKGROUND_COLOR,
                getDefaultColor("#488D93", "#9D79D6", "#3DDBD9"));
    }

    public int getFlairTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.FLAIR_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#DFDFE0", "#282828"));
    }

    public int getAwardsBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.AWARDS_BACKGROUND_COLOR,
                getDefaultColor("#B86E28", "#E0C989", "#3DDBD9"));
    }

    public int getAwardsTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.AWARDS_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#575860", "#282828"));
    }

    public int getArchivedIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.ARCHIVED_ICON_TINT,
                getDefaultColor("#A440B5", "#D67AD2", "#EE5396"));
    }

    public int getLockedIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.LOCKED_ICON_TINT,
                getDefaultColor("#AC5402", "#F4A261", "#EE5396"));
    }

    public int getCrosspostIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.CROSSPOST_ICON_TINT,
                getDefaultColor("#A5222F", "#DBC074", "#F16DA6"));
    }

    public int getUpvoteRatioIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.UPVOTE_RATIO_ICON_TINT,
                getDefaultColor("#2848A9", "#86ABDC", "#33B1FF"));
    }

    public int getStickiedPostIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.STICKIED_POST_ICON_TINT,
                getDefaultColor("#4863B6", "#719CD6", "#33B1FF"));
    }

    public int getNoPreviewPostTypeIconTint() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.NO_PREVIEW_POST_TYPE_ICON_TINT,
                getDefaultColor("#F6F2EE", "#CDCECF", "#F2F4F8"));
    }

    public int getSubscribed() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SUBSCRIBED,
                getDefaultColor("#A5222F", "#D16983", "#F16DA6"));
    }

    public int getUnsubscribed() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.UNSUBSCRIBED,
                getDefaultColor("#4863B6", "#719CD6", "#33B1FF"));
    }

    public int getUsername() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.USERNAME,
                getDefaultColor("#4863B6", "#86ABDC", "#52BDFF"));
    }

    public int getSubreddit() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SUBREDDIT,
                getDefaultColor("#A5222F", "#DBC074", "#F16DA6"));
    }

    public int getAuthorFlairTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.AUTHOR_FLAIR_TEXT_COLOR,
                getDefaultColor("#A440B5", "#D67AD2", "#F16DA6"));
    }

    public int getSubmitter() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SUBMITTER,
                getDefaultColor("#AC5402", "#E0C989", "#3DDBD9"));
    }

    public int getModerator() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.MODERATOR,
                getDefaultColor("#577F63", "#D67AD2", "#25BE6A"));
    }

    public int getCurrentUser() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.CURRENT_USER,
                getDefaultColor("#488D93", "#7AD5D6", "#2DC7C4"));
    }

    public int getSingleCommentThreadBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SINGLE_COMMENT_THREAD_BACKGROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#484848"));
    }

    public int getUnreadMessageBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.UNREAD_MESSAGE_BACKGROUND_COLOR,
                getDefaultColor("#F6F2EE", "#393B44", "#484848"));
    }

    public int getDividerColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.DIVIDER_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#484848"));
    }

    public int getNoPreviewPostTypeBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.NO_PREVIEW_POST_TYPE_BACKGROUND_COLOR,
                getDefaultColor("#3D2B5A", "#D16983", "#161616"));
    }

    public int getVoteAndReplyUnavailableButtonColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.VOTE_AND_REPLY_UNAVAILABLE_BUTTON_COLOR,
                getDefaultColor("#F6F2EE", "#192330", "#484848"));
    }

    public int getCommentVerticalBarColor1() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_1,
                getDefaultColor("#2848A9", "#719CD6", "#33B1FF"));
    }

    public int getCommentVerticalBarColor2() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_2,
                getDefaultColor("#A440B5", "#D16983", "#C8A5FF"));
    }

    public int getCommentVerticalBarColor3() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_3,
                getDefaultColor("#4863B6", "#86ABDC", "#2DC7C4"));
    }

    public int getCommentVerticalBarColor4() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_4,
                getDefaultColor("#B86E28", "#DBC074", "#78A9FF"));
    }

    public int getCommentVerticalBarColor5() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_5,
                getDefaultColor("#B3434E", "#C94F6D", "#EE5396"));
    }

    public int getCommentVerticalBarColor6() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_6,
                getDefaultColor("#577F63", "#81B27A", "#25BE6A"));
    }

    public int getCommentVerticalBarColor7() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.COMMENT_VERTICAL_BAR_COLOR_7,
                getDefaultColor("#955F61", "#D16983", "#FF7EB6"));
    }

    public int getFABIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.FAB_ICON_COLOR,
                getDefaultColor("#F6F2EE", "#192330", "#161616"));
    }

    public int getChipTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.CHIP_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#CDCECF", "#282828"));
    }

    public int getLinkColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.LINK_COLOR,
                getDefaultColor("#A5222F", "#DBC074", "#F16DA6"));
    }

    public int getReceivedMessageTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.RECEIVED_MESSAGE_TEXT_COLOR,
                getDefaultColor("#3D2B5A", "#CDCECF", "#F2F4F8"));
    }

    public int getSentMessageTextColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SENT_MESSAGE_TEXT_COLOR,
                getDefaultColor("#F6F2EE", "#CDCECF", "#F2F4F8"));
    }

    public int getReceivedMessageBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.RECEIVED_MESSAGE_BACKROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#484848"));
    }

    public int getSentMessageBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SENT_MESSAGE_BACKGROUND_COLOR,
                getDefaultColor("#577F63", "#393B44", "#46C880"));
    }

    public int getSendMessageIconColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.SEND_MESSAGE_ICON_COLOR,
                getDefaultColor("#4863B6", "#C94F6D", "#52BDFF"));
    }

    public int getFullyCollapsedCommentBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.FULLY_COLLAPSED_COMMENT_BACKGROUND_COLOR,
                getDefaultColor("#F2E9E1", "#2B3B51", "#484848"));
    }

    public int getAwardedCommentBackgroundColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.AWARDED_COMMENT_BACKGROUND_COLOR,
                getDefaultColor("#F6F2EE", "#E0C989", "#282828"));
    }

    public int getNavBarColor() {
        return getThemeSharedPreferences().getInt(CustomThemeSharedPreferencesUtils.NAV_BAR_COLOR,
                getDefaultColor("#F6F2EE", "#192330", "#161616"));
    }

    public boolean isLightStatusBar() {
        return getThemeSharedPreferences().getBoolean(CustomThemeSharedPreferencesUtils.LIGHT_STATUS_BAR, false);
    }

    public boolean isLightNavBar() {
        return getThemeSharedPreferences().getBoolean(CustomThemeSharedPreferencesUtils.LIGHT_NAV_BAR,
                themeType == CustomThemeSharedPreferencesUtils.LIGHT);
    }

    public boolean isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface() {
        return getThemeSharedPreferences().getBoolean(
                CustomThemeSharedPreferencesUtils.CHANGE_STATUS_BAR_ICON_COLOR_AFTER_TOOLBAR_COLLAPSED_IN_IMMERSIVE_INTERFACE,
                themeType == CustomThemeSharedPreferencesUtils.LIGHT);
    }

    public static CustomTheme getPredefinedCustomTheme(Context context, String name) {
        if (name.equals(context.getString(R.string.theme_name_indigo_dark))) {
            return getIndigoDark(context);
        } else if (name.equals(context.getString(R.string.theme_name_indigo_amoled))) {
            return getIndigoAmoled(context);
        } else if (name.equals(context.getString(R.string.theme_name_white))) {
            return getWhite(context);
        } else if (name.equals(context.getString(R.string.theme_name_white_dark))) {
            return getWhiteDark(context);
        } else if (name.equals(context.getString(R.string.theme_name_white_amoled))) {
            return getWhiteAmoled(context);
        } else if (name.equals(context.getString(R.string.theme_name_red))) {
            return getRed(context);
        } else if (name.equals(context.getString(R.string.theme_name_red_dark))) {
            return getRedDark(context);
        } else if (name.equals(context.getString(R.string.theme_name_red_amoled))) {
            return getRedAmoled(context);
        } else if (name.equals(context.getString(R.string.theme_name_dracula))) {
            return getDracula(context);
        } else if (name.equals(context.getString(R.string.theme_name_calm_pastel))) {
            return getCalmPastel(context);
        } else {
            return getIndigo(context);
        }
    }

    public static ArrayList<CustomTheme> getPredefinedThemes(Context context) {
        ArrayList<CustomTheme> customThemes = new ArrayList<>();
        customThemes.add(getIndigo(context));
        customThemes.add(getIndigoDark(context));
        customThemes.add(getIndigoAmoled(context));
        customThemes.add(getWhite(context));
        customThemes.add(getWhiteDark(context));
        customThemes.add(getWhiteAmoled(context));
        customThemes.add(getRed(context));
        customThemes.add(getRedDark(context));
        customThemes.add(getRedAmoled(context));
        customThemes.add(getDracula(context));
        customThemes.add(getCalmPastel(context));
        return customThemes;
    }

    public static CustomTheme getIndigo(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_indigo));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#0336FF");
        customTheme.colorPrimaryDark = Color.parseColor("#002BF0");
        customTheme.colorAccent = Color.parseColor("#FF1868");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#0336FF");
        customTheme.primaryTextColor = Color.parseColor("#000000");
        customTheme.secondaryTextColor = Color.parseColor("#8A000000");
        customTheme.postTitleColor = Color.parseColor("#000000");
        customTheme.postContentColor = Color.parseColor("#8A000000");
        customTheme.readPostTitleColor = Color.parseColor("#9D9D9D");
        customTheme.readPostContentColor = Color.parseColor("#9D9D9D");
        customTheme.commentColor = Color.parseColor("#000000");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#FFFFFF");
        customTheme.cardViewBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#F5F5F5");
        customTheme.commentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.primaryIconColor = Color.parseColor("#000000");
        customTheme.bottomAppBarIconColor = Color.parseColor("#000000");
        customTheme.postIconAndInfoColor = Color.parseColor("#8A000000");
        customTheme.commentIconAndInfoColor = Color.parseColor("#8A000000");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#0336FF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#0336FF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#0336FF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#002BF0");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#002BF0");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#002BF0");
        customTheme.username = Color.parseColor("#002BF0");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.dividerColor = Color.parseColor("#E0E0E0");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#E0E0E0");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#F0F0F0");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#EE02BE");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#02DFEE");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EED502");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0220");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#02EE6E");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#8EDFBA");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.navBarColor = Color.parseColor("#FFFFFF");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = true;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = true;

        return customTheme;
    }

    public static CustomTheme getIndigoDark(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_indigo_dark));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = true;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#242424");
        customTheme.colorPrimaryDark = Color.parseColor("#121212");
        customTheme.colorAccent = Color.parseColor("#FF1868");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#0336FF");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#121212");
        customTheme.cardViewBackgroundColor = Color.parseColor("#242424");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#101010");
        customTheme.commentBackgroundColor = Color.parseColor("#242424");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#121212");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#242424");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#424242");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#242424");
        customTheme.navBarColor = Color.parseColor("#121212");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    public static CustomTheme getIndigoAmoled(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_indigo_amoled));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = true;
        customTheme.colorPrimary = Color.parseColor("#000000");
        customTheme.colorPrimaryDark = Color.parseColor("#000000");
        customTheme.colorAccent = Color.parseColor("#FF1868");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#0336FF");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#000000");
        customTheme.cardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.commentBackgroundColor = Color.parseColor("#000000");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#000000");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#000000");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#424242");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#000000");
        customTheme.navBarColor = Color.parseColor("#000000");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getWhite(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_white));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#FFFFFF");
        customTheme.colorPrimaryDark = Color.parseColor("#FFFFFF");
        customTheme.colorAccent = Color.parseColor("#000000");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#FFFFFF");
        customTheme.primaryTextColor = Color.parseColor("#000000");
        customTheme.secondaryTextColor = Color.parseColor("#8A000000");
        customTheme.postTitleColor = Color.parseColor("#000000");
        customTheme.postContentColor = Color.parseColor("#8A000000");
        customTheme.readPostTitleColor = Color.parseColor("#9D9D9D");
        customTheme.readPostContentColor = Color.parseColor("#9D9D9D");
        customTheme.commentColor = Color.parseColor("#000000");
        customTheme.buttonTextColor = Color.parseColor("#000000");
        customTheme.backgroundColor = Color.parseColor("#FFFFFF");
        customTheme.cardViewBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#F5F5F5");
        customTheme.commentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.primaryIconColor = Color.parseColor("#000000");
        customTheme.bottomAppBarIconColor = Color.parseColor("#000000");
        customTheme.postIconAndInfoColor = Color.parseColor("#3C4043");
        customTheme.commentIconAndInfoColor = Color.parseColor("#3C4043");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#3C4043");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#3C4043");
        customTheme.circularProgressBarBackground = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#3C4043");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#3C4043");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#3C4043");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#3C4043");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#002BF0");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#002BF0");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#FFFFFF");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#002BF0");
        customTheme.username = Color.parseColor("#002BF0");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.dividerColor = Color.parseColor("#E0E0E0");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#000000");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#F0F0F0");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#EE02BE");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#02DFEE");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EED502");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0220");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#02EE6E");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#8EDFBA");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.navBarColor = Color.parseColor("#FFFFFF");
        customTheme.isLightStatusBar = true;
        customTheme.isLightNavBar = true;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getWhiteDark(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_white_dark));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = true;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#242424");
        customTheme.colorPrimaryDark = Color.parseColor("#121212");
        customTheme.colorAccent = Color.parseColor("#FFFFFF");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#121212");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#121212");
        customTheme.cardViewBackgroundColor = Color.parseColor("#242424");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#101010");
        customTheme.commentBackgroundColor = Color.parseColor("#242424");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#121212");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#242424");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#FFFFFF");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#000000");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#000000");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#242424");
        customTheme.navBarColor = Color.parseColor("#121212");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getWhiteAmoled(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_white_amoled));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = true;
        customTheme.colorPrimary = Color.parseColor("#000000");
        customTheme.colorPrimaryDark = Color.parseColor("#000000");
        customTheme.colorAccent = Color.parseColor("#FFFFFF");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#000000");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#000000");
        customTheme.cardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.commentBackgroundColor = Color.parseColor("#000000");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#000000");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#000000");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#FFFFFF");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#000000");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#000000");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#000000");
        customTheme.navBarColor = Color.parseColor("#000000");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getRed(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_red));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#EE0270");
        customTheme.colorPrimaryDark = Color.parseColor("#C60466");
        customTheme.colorAccent = Color.parseColor("#02EE80");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#EE0270");
        customTheme.primaryTextColor = Color.parseColor("#000000");
        customTheme.secondaryTextColor = Color.parseColor("#8A000000");
        customTheme.postTitleColor = Color.parseColor("#000000");
        customTheme.postContentColor = Color.parseColor("#8A000000");
        customTheme.readPostTitleColor = Color.parseColor("#9D9D9D");
        customTheme.readPostContentColor = Color.parseColor("#9D9D9D");
        customTheme.commentColor = Color.parseColor("#000000");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#FFFFFF");
        customTheme.cardViewBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#F5F5F5");
        customTheme.commentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.primaryIconColor = Color.parseColor("#000000");
        customTheme.bottomAppBarIconColor = Color.parseColor("#000000");
        customTheme.postIconAndInfoColor = Color.parseColor("#8A000000");
        customTheme.commentIconAndInfoColor = Color.parseColor("#8A000000");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#EE0270");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#EE0270");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#EE0270");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#002BF0");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#002BF0");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#002BF0");
        customTheme.username = Color.parseColor("#002BF0");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#B3E5F9");
        customTheme.dividerColor = Color.parseColor("#E0E0E0");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#E0E0E0");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#F0F0F0");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#EE02BE");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#02DFEE");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EED502");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0220");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#02EE6E");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#8EDFBA");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.navBarColor = Color.parseColor("#FFFFFF");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = true;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = true;

        return customTheme;
    }

    private static CustomTheme getRedDark(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_red_dark));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = true;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#242424");
        customTheme.colorPrimaryDark = Color.parseColor("#121212");
        customTheme.colorAccent = Color.parseColor("#02EE80");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#EE0270");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#121212");
        customTheme.cardViewBackgroundColor = Color.parseColor("#242424");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#101010");
        customTheme.commentBackgroundColor = Color.parseColor("#242424");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#121212");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#242424");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#242424");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#424242");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#242424");
        customTheme.navBarColor = Color.parseColor("#121212");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getRedAmoled(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_red_amoled));
        customTheme.isLightTheme = false;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = true;
        customTheme.colorPrimary = Color.parseColor("#000000");
        customTheme.colorPrimaryDark = Color.parseColor("#000000");
        customTheme.colorAccent = Color.parseColor("#02EE80");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#EE0270");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#000000");
        customTheme.cardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#000000");
        customTheme.commentBackgroundColor = Color.parseColor("#000000");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#000000");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#B3FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#000000");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#000000");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#000000");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#0336FF");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#0336FF");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#123E77");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#123E77");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#424242");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#3C3C3C");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#C300B3");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#00B8DA");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EDCA00");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0219");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#00B925");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#FFFFFF");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#000000");
        customTheme.navBarColor = Color.parseColor("#000000");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getDracula(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_dracula));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = true;
        customTheme.isAmoledTheme = true;
        customTheme.colorPrimary = Color.parseColor("#393A59");
        customTheme.colorPrimaryDark = Color.parseColor("#393A59");
        customTheme.colorAccent = Color.parseColor("#F8F8F2");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#393A59");
        customTheme.primaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.secondaryTextColor = Color.parseColor("#B3FFFFFF");
        customTheme.postTitleColor = Color.parseColor("#FFFFFF");
        customTheme.postContentColor = Color.parseColor("#B3FFFFFF");
        customTheme.readPostTitleColor = Color.parseColor("#9D9D9D");
        customTheme.readPostContentColor = Color.parseColor("#9D9D9D");
        customTheme.commentColor = Color.parseColor("#FFFFFF");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#282A36");
        customTheme.cardViewBackgroundColor = Color.parseColor("#393A59");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#1C1F3D");
        customTheme.commentBackgroundColor = Color.parseColor("#393A59");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#393A59");
        customTheme.primaryIconColor = Color.parseColor("#FFFFFF");
        customTheme.bottomAppBarIconColor = Color.parseColor("#FFFFFF");
        customTheme.postIconAndInfoColor = Color.parseColor("#FFFFFF");
        customTheme.commentIconAndInfoColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#FFFFFF");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#FFFFFF");
        customTheme.circularProgressBarBackground = Color.parseColor("#393A59");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#393A59");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#393A59");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#FFFFFF");
        customTheme.upvoted = Color.parseColor("#FF008C");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#0336FF");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#02ABEE");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#FFFFFF");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#002BF0");
        customTheme.username = Color.parseColor("#1E88E5");
        customTheme.subreddit = Color.parseColor("#FF4B9C");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#5F5B85");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#5F5B85");
        customTheme.dividerColor = Color.parseColor("#69666C");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#6272A4");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#777C82");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#8BE9FD");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#50FA7B");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#FFB86C");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#FF79C6");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#BD93F9");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#FF5555");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#F1FA8C");
        customTheme.fabIconColor = Color.parseColor("#000000");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#21C561");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#393A59");
        customTheme.navBarColor = Color.parseColor("#393A59");
        customTheme.isLightStatusBar = false;
        customTheme.isLightNavBar = false;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    private static CustomTheme getCalmPastel(Context context) {
        CustomTheme customTheme = new CustomTheme(context.getString(R.string.theme_name_calm_pastel));
        customTheme.isLightTheme = true;
        customTheme.isDarkTheme = false;
        customTheme.isAmoledTheme = false;
        customTheme.colorPrimary = Color.parseColor("#D48AE0");
        customTheme.colorPrimaryDark = Color.parseColor("#D476E0");
        customTheme.colorAccent = Color.parseColor("#775EFF");
        customTheme.colorPrimaryLightTheme = Color.parseColor("#D48AE0");
        customTheme.primaryTextColor = Color.parseColor("#000000");
        customTheme.secondaryTextColor = Color.parseColor("#8A000000");
        customTheme.postTitleColor = Color.parseColor("#000000");
        customTheme.postContentColor = Color.parseColor("#8A000000");
        customTheme.readPostTitleColor = Color.parseColor("#979797");
        customTheme.readPostContentColor = Color.parseColor("#979797");
        customTheme.commentColor = Color.parseColor("#000000");
        customTheme.buttonTextColor = Color.parseColor("#FFFFFF");
        customTheme.backgroundColor = Color.parseColor("#DAD0DE");
        customTheme.cardViewBackgroundColor = Color.parseColor("#C0F0F4");
        customTheme.readPostCardViewBackgroundColor = Color.parseColor("#D2E7EA");
        customTheme.commentBackgroundColor = Color.parseColor("#C0F0F4");
        customTheme.bottomAppBarBackgroundColor = Color.parseColor("#D48AE0");
        customTheme.primaryIconColor = Color.parseColor("#000000");
        customTheme.bottomAppBarIconColor = Color.parseColor("#000000");
        customTheme.postIconAndInfoColor = Color.parseColor("#000000");
        customTheme.commentIconAndInfoColor = Color.parseColor("#000000");
        customTheme.toolbarPrimaryTextAndIconColor = Color.parseColor("#3C4043");
        customTheme.toolbarSecondaryTextColor = Color.parseColor("#3C4043");
        customTheme.circularProgressBarBackground = Color.parseColor("#D48AE0");
        customTheme.mediaIndicatorIconColor = Color.parseColor("#FFFFFF");
        customTheme.mediaIndicatorBackgroundColor = Color.parseColor("#000000");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabBackground = Color.parseColor("#FFFFFF");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTextColor = Color.parseColor("#D48AE0");
        customTheme.tabLayoutWithExpandedCollapsingToolbarTabIndicator = Color.parseColor("#D48AE0");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabBackground = Color.parseColor("#D48AE0");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTextColor = Color.parseColor("#3C4043");
        customTheme.tabLayoutWithCollapsedCollapsingToolbarTabIndicator = Color.parseColor("#3C4043");
        customTheme.upvoted = Color.parseColor("#FF1868");
        customTheme.downvoted = Color.parseColor("#007DDE");
        customTheme.postTypeBackgroundColor = Color.parseColor("#002BF0");
        customTheme.postTypeTextColor = Color.parseColor("#FFFFFF");
        customTheme.spoilerBackgroundColor = Color.parseColor("#EE02EB");
        customTheme.spoilerTextColor = Color.parseColor("#FFFFFF");
        customTheme.nsfwBackgroundColor = Color.parseColor("#FF1868");
        customTheme.nsfwTextColor = Color.parseColor("#FFFFFF");
        customTheme.flairBackgroundColor = Color.parseColor("#00AA8C");
        customTheme.flairTextColor = Color.parseColor("#FFFFFF");
        customTheme.awardsBackgroundColor = Color.parseColor("#EEAB02");
        customTheme.awardsTextColor = Color.parseColor("#FFFFFF");
        customTheme.archivedTint = Color.parseColor("#B4009F");
        customTheme.lockedIconTint = Color.parseColor("#EE7302");
        customTheme.crosspostIconTint = Color.parseColor("#FF1868");
        customTheme.upvoteRatioIconTint = Color.parseColor("#0256EE");
        customTheme.stickiedPostIconTint = Color.parseColor("#002BF0");
        customTheme.noPreviewPostTypeIconTint = Color.parseColor("#808080");
        customTheme.subscribed = Color.parseColor("#FF1868");
        customTheme.unsubscribed = Color.parseColor("#002BF0");
        customTheme.username = Color.parseColor("#002BF0");
        customTheme.subreddit = Color.parseColor("#FF1868");
        customTheme.authorFlairTextColor = Color.parseColor("#EE02C4");
        customTheme.submitter = Color.parseColor("#EE8A02");
        customTheme.moderator = Color.parseColor("#00BA81");
        customTheme.currentUser = Color.parseColor("#00D5EA");
        customTheme.singleCommentThreadBackgroundColor = Color.parseColor("#25D5E5");
        customTheme.unreadMessageBackgroundColor = Color.parseColor("#25D5E5");
        customTheme.dividerColor = Color.parseColor("#E0E0E0");
        customTheme.noPreviewPostTypeBackgroundColor = Color.parseColor("#E0E0E0");
        customTheme.voteAndReplyUnavailableButtonColor = Color.parseColor("#F0F0F0");
        customTheme.commentVerticalBarColor1 = Color.parseColor("#0336FF");
        customTheme.commentVerticalBarColor2 = Color.parseColor("#EE02BE");
        customTheme.commentVerticalBarColor3 = Color.parseColor("#02DFEE");
        customTheme.commentVerticalBarColor4 = Color.parseColor("#EED502");
        customTheme.commentVerticalBarColor5 = Color.parseColor("#EE0220");
        customTheme.commentVerticalBarColor6 = Color.parseColor("#02EE6E");
        customTheme.commentVerticalBarColor7 = Color.parseColor("#EE4602");
        customTheme.fabIconColor = Color.parseColor("#000000");
        customTheme.chipTextColor = Color.parseColor("#FFFFFF");
        customTheme.linkColor = Color.parseColor("#FF1868");
        customTheme.receivedMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.sentMessageTextColor = Color.parseColor("#FFFFFF");
        customTheme.receivedMessageBackgroundColor = Color.parseColor("#4185F4");
        customTheme.sentMessageBackgroundColor = Color.parseColor("#31BF7D");
        customTheme.sendMessageIconColor = Color.parseColor("#4185F4");
        customTheme.fullyCollapsedCommentBackgroundColor = Color.parseColor("#8EDFBA");
        customTheme.awardedCommentBackgroundColor = Color.parseColor("#C0F0F4");
        customTheme.navBarColor = Color.parseColor("#D48AE0");
        customTheme.isLightStatusBar = true;
        customTheme.isLightNavBar = true;
        customTheme.isChangeStatusBarIconColorAfterToolbarCollapsedInImmersiveInterface = false;

        return customTheme;
    }

    public static int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);

        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

}
