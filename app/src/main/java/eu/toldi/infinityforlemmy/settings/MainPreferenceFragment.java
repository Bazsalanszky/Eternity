package eu.toldi.infinityforlemmy.settings;


import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.preference.Preference;

import javax.inject.Inject;
import javax.inject.Named;

import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.PostFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.activities.CommentFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.PostFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;

public class MainPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        Preference securityPreference = findPreference(SharedPreferencesUtils.SECURITY);
        Preference postFilterPreference = findPreference(SharedPreferencesUtils.POST_FILTER);
        Preference commentFilterPreference = findPreference(SharedPreferencesUtils.COMMENT_FILTER);
        Preference privacyPolicyPreference = findPreference(SharedPreferencesUtils.PRIVACY_POLICY_KEY);
        Preference redditUserAgreementPreference = findPreference(SharedPreferencesUtils.REDDIT_USER_AGREEMENT_KEY);

        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS) {
            if (securityPreference != null) {
                securityPreference.setVisible(false);
            }
        }

        if (postFilterPreference != null) {
            postFilterPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, PostFilterPreferenceActivity.class);
                activity.startActivity(intent);
                return true;
            });
        }

        if (commentFilterPreference != null) {
            commentFilterPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    Intent intent = new Intent(activity, CommentFilterPreferenceActivity.class);
                    activity.startActivity(intent);
                    return true;
                }
            });
        }
    }
}
