package eu.toldi.infinityforlemmy.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;
import eu.toldi.infinityforlemmy.events.ChangeCompactLayoutToolbarHiddenByDefaultEvent;
import eu.toldi.infinityforlemmy.events.ChangeDefaultLinkPostLayoutEvent;
import eu.toldi.infinityforlemmy.events.ChangeDefaultPostLayoutEvent;
import eu.toldi.infinityforlemmy.events.ChangeFixedHeightPreviewInCardEvent;
import eu.toldi.infinityforlemmy.events.ChangeHidePostFlairEvent;
import eu.toldi.infinityforlemmy.events.ChangeHidePostTypeEvent;
import eu.toldi.infinityforlemmy.events.ChangeHideSubredditAndUserPrefixEvent;
import eu.toldi.infinityforlemmy.events.ChangeHideTextPostContent;
import eu.toldi.infinityforlemmy.events.ChangeHideTheNumberOfAwardsEvent;
import eu.toldi.infinityforlemmy.events.ChangeHideTheNumberOfCommentsEvent;
import eu.toldi.infinityforlemmy.events.ChangeHideTheNumberOfVotesEvent;
import eu.toldi.infinityforlemmy.events.ChangeLongPressToHideToolbarInCompactLayoutEvent;
import eu.toldi.infinityforlemmy.events.ChangeShowAbsoluteNumberOfVotesEvent;
import eu.toldi.infinityforlemmy.events.ShowDividerInCompactLayoutPreferenceEvent;
import eu.toldi.infinityforlemmy.events.ShowThumbnailOnTheRightInCompactLayoutEvent;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class PostPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.post_preferences, rootKey);

        ListPreference defaultPostLayoutList = findPreference(SharedPreferencesUtils.DEFAULT_POST_LAYOUT_KEY);
        ListPreference defaultLinkPostLayoutList = findPreference(SharedPreferencesUtils.DEFAULT_LINK_POST_LAYOUT_KEY);
        SwitchPreference showDividerInCompactLayoutSwitch = findPreference(SharedPreferencesUtils.SHOW_DIVIDER_IN_COMPACT_LAYOUT);
        SwitchPreference showThumbnailOnTheRightInCompactLayoutSwitch = findPreference(SharedPreferencesUtils.SHOW_THUMBNAIL_ON_THE_LEFT_IN_COMPACT_LAYOUT);
        SwitchPreference showAbsoluteNumberOfVotesSwitch = findPreference(SharedPreferencesUtils.SHOW_ABSOLUTE_NUMBER_OF_VOTES);
        SwitchPreference longPressToHideToolbarInCompactLayoutSwitch = findPreference(SharedPreferencesUtils.LONG_PRESS_TO_HIDE_TOOLBAR_IN_COMPACT_LAYOUT);
        SwitchPreference postCompactLayoutToolbarHiddenByDefaultSwitch = findPreference(SharedPreferencesUtils.POST_COMPACT_LAYOUT_TOOLBAR_HIDDEN_BY_DEFAULT);
        SwitchPreference hidePostTypeSwitch = findPreference(SharedPreferencesUtils.HIDE_POST_TYPE);
        SwitchPreference hidePostFlairSwitch = findPreference(SharedPreferencesUtils.HIDE_POST_FLAIR);
        SwitchPreference hideTheNumberOfAwardsSwitch = findPreference(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_AWARDS);
        SwitchPreference hideSubredditAndUserPrefixSwitch = findPreference(SharedPreferencesUtils.HIDE_SUBREDDIT_AND_USER_PREFIX);
        SwitchPreference hideTheNumberOfVotesSwitch = findPreference(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_VOTES);
        SwitchPreference hideTheNumberOfCommentsSwitch = findPreference(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_COMMENTS);
        SwitchPreference hideTextPostContentSwitch = findPreference(SharedPreferencesUtils.HIDE_TEXT_POST_CONTENT);
        SwitchPreference fixedHeightPreviewInCardSwitch = findPreference(SharedPreferencesUtils.FIXED_HEIGHT_PREVIEW_IN_CARD);

        if (defaultPostLayoutList != null) {
            defaultPostLayoutList.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeDefaultPostLayoutEvent(Integer.parseInt((String) newValue)));
                return true;
            });
        }

        if (defaultLinkPostLayoutList != null) {
            defaultLinkPostLayoutList.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeDefaultLinkPostLayoutEvent(Integer.parseInt((String) newValue)));
                return true;
            });
        }

        if (showDividerInCompactLayoutSwitch != null) {
            showDividerInCompactLayoutSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ShowDividerInCompactLayoutPreferenceEvent((Boolean) newValue));
                return true;
            });
        }

        if (showThumbnailOnTheRightInCompactLayoutSwitch != null) {
            showThumbnailOnTheRightInCompactLayoutSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ShowThumbnailOnTheRightInCompactLayoutEvent((Boolean) newValue));
                return true;
            });
        }

        if (showAbsoluteNumberOfVotesSwitch != null) {
            showAbsoluteNumberOfVotesSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeShowAbsoluteNumberOfVotesEvent((Boolean) newValue));
                return true;
            });
        }

        if (longPressToHideToolbarInCompactLayoutSwitch != null) {
            longPressToHideToolbarInCompactLayoutSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeLongPressToHideToolbarInCompactLayoutEvent((Boolean) newValue));
                return true;
            });
        }

        if (postCompactLayoutToolbarHiddenByDefaultSwitch != null) {
            postCompactLayoutToolbarHiddenByDefaultSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeCompactLayoutToolbarHiddenByDefaultEvent((Boolean) newValue));
                return true;
            });
        }

        if (hidePostTypeSwitch != null) {
            hidePostTypeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHidePostTypeEvent((Boolean) newValue));
                return true;
            });
        }

        if (hidePostFlairSwitch != null) {
            hidePostFlairSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHidePostFlairEvent((Boolean) newValue));
                return true;
            });
        }

        if (hideTheNumberOfAwardsSwitch != null) {
            hideTheNumberOfAwardsSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHideTheNumberOfAwardsEvent((Boolean) newValue));
                return true;
            });
        }

        if (hideSubredditAndUserPrefixSwitch != null) {
            hideSubredditAndUserPrefixSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHideSubredditAndUserPrefixEvent((Boolean) newValue));
                return true;
            });
        }

        if (hideTheNumberOfVotesSwitch != null) {
            hideTheNumberOfVotesSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHideTheNumberOfVotesEvent((Boolean) newValue));
                return true;
            });
        }

        if (hideTheNumberOfCommentsSwitch != null) {
            hideTheNumberOfCommentsSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHideTheNumberOfCommentsEvent((Boolean) newValue));
                return true;
            });
        }

        if (hideTextPostContentSwitch != null) {
            hideTextPostContentSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeHideTextPostContent((Boolean) newValue));
                return true;
            });
        }

        if (fixedHeightPreviewInCardSwitch != null) {
            fixedHeightPreviewInCardSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeFixedHeightPreviewInCardEvent((Boolean) newValue));
                return true;
            });
        }
    }
}