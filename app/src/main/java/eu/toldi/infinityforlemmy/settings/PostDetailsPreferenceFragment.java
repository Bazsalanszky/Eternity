package eu.toldi.infinityforlemmy.settings;

import android.os.Bundle;

import androidx.preference.PreferenceManager;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

public class PostDetailsPreferenceFragment extends CustomFontPreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(SharedPreferencesUtils.POST_DETAILS_SHARED_PREFERENCES_FILE);
        setPreferencesFromResource(R.xml.post_details_preferences, rootKey);
    }
}
