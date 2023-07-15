package eu.toldi.infinityforlemmy.settings;

import android.os.Bundle;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;

public class SortTypePreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sort_type_preferences, rootKey);
    }
}