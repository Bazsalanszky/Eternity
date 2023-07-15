package eu.toldi.infinityforlemmy.settings;

import android.os.Bundle;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;

public class NumberOfColumnsInPostFeedPreferenceFragment extends CustomFontPreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.number_of_columns_in_post_feed_preferences, rootKey);
    }
}
