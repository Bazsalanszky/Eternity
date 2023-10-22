package eu.toldi.infinityforlemmy.settings;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;
import eu.toldi.infinityforlemmy.events.ChangePostFeedMaxResolutionEvent;
import eu.toldi.infinityforlemmy.events.ChangeSavePostFeedScrolledPositionEvent;
import eu.toldi.infinityforlemmy.events.RecreateActivityEvent;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class MiscellaneousPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Inject
    @Named("post_feed_scrolled_position_cache")
    SharedPreferences cache;

    public MiscellaneousPreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.miscellaneous_preferences, rootKey);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        ListPreference mainPageBackButtonActionListPreference = findPreference(SharedPreferencesUtils.MAIN_PAGE_BACK_BUTTON_ACTION);
        SwitchPreference savePostFeedScrolledPositionSwitch = findPreference(SharedPreferencesUtils.SAVE_FRONT_PAGE_SCROLLED_POSITION);
        ListPreference languageListPreference = findPreference(SharedPreferencesUtils.LANGUAGE);
        EditTextPreference anonymousAccountInstance = findPreference(SharedPreferencesUtils.ANONYMOUS_ACCOUNT_INSTANCE);
        EditTextPreference postFeedMaxResolution = findPreference(SharedPreferencesUtils.POST_FEED_MAX_RESOLUTION);
        ListPreference iconPreference = findPreference(SharedPreferencesUtils.ICON_PREFERENCE);

        if (mainPageBackButtonActionListPreference != null) {
            mainPageBackButtonActionListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (savePostFeedScrolledPositionSwitch != null) {
            savePostFeedScrolledPositionSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(Boolean) newValue) {
                    cache.edit().clear().apply();
                }
                EventBus.getDefault().post(new ChangeSavePostFeedScrolledPositionEvent((Boolean) newValue));
                return true;
            });
        }

        if (languageListPreference != null) {
            languageListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (anonymousAccountInstance != null) {
            anonymousAccountInstance.setOnPreferenceChangeListener((preference, newValue) -> {
                String url = (String) newValue;
                if (url == null || url.equals("")) {
                    Toast.makeText(activity, R.string.url_cannot_be_null_or_empty, Toast.LENGTH_SHORT).show();
                    return false;

                }

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Toast.makeText(activity, "The url has to start with https:// or http://", Toast.LENGTH_SHORT).show();
                    return false;
                }

                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (postFeedMaxResolution != null) {
            postFeedMaxResolution.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    int resolution = Integer.parseInt((String) newValue);
                    if (resolution <= 0) {
                        Toast.makeText(activity, R.string.not_a_valid_number, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EventBus.getDefault().post(new ChangePostFeedMaxResolutionEvent(resolution));
                } catch (NumberFormatException e) {
                    Toast.makeText(activity, R.string.not_a_valid_number, Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            });
        }

        if (iconPreference != null) {
            iconPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                updateIcon((String) newValue);
                return true;
            });
        }
    }

    private void updateIcon(String iconValue) {
        PackageManager pm = getActivity().getPackageManager();

        // Disable all the alternative icons
        pm.setComponentEnabledSetting(
                new ComponentName(getActivity(), "eu.toldi.infinityforlemmy.OriginalIcon"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(getActivity(), "eu.toldi.infinityforlemmy.DefaultIcon"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // Enable the chosen icon
        if ("original_icon".equals(iconValue)) {
            pm.setComponentEnabledSetting(
                    new ComponentName(getActivity(), "eu.toldi.infinityforlemmy.OriginalIcon"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(
                    new ComponentName(getActivity(), "eu.toldi.infinityforlemmy.DefaultIcon"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

}