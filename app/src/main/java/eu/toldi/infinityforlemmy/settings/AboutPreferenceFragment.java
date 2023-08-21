package eu.toldi.infinityforlemmy.settings;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import eu.toldi.infinityforlemmy.BuildConfig;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.customviews.CustomFontPreferenceFragmentCompat;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;

/**
 * A simple {@link PreferenceFragmentCompat} subclass.
 */
public class AboutPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_preferences, rootKey);

        Preference openSourcePreference = findPreference(SharedPreferencesUtils.OPEN_SOURCE_KEY);
        Preference ratePreference = findPreference(SharedPreferencesUtils.RATE_KEY);
        Preference emailPreference = findPreference(SharedPreferencesUtils.EMAIL_KEY);
        Preference redditAccountPreference = findPreference(SharedPreferencesUtils.REDDIT_ACCOUNT_KEY);
        Preference subredditPreference = findPreference(SharedPreferencesUtils.SUBREDDIT_KEY);
        Preference sharePreference = findPreference(SharedPreferencesUtils.SHARE_KEY);
        Preference versionPreference = findPreference(SharedPreferencesUtils.VERSION_KEY);

        if (openSourcePreference != null) {
            openSourcePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, LinkResolverActivity.class);
                intent.setData(Uri.parse("https://codeberg.org/Bazsalanszky/Infinity-For-Lemmy"));
                activity.startActivity(intent);
                return true;
            });
        }


        if (emailPreference != null) {
            emailPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, LinkResolverActivity.class);
                intent.setData(Uri.parse("https://fosstodon.org/@bazsalanszky"));
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(activity, R.string.no_email_client, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        if (redditAccountPreference != null) {
            redditAccountPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, LinkResolverActivity.class);
                intent.setData(Uri.parse("https://lemmy.toldi.eu/u/bazsalanszky"));
                activity.startActivity(intent);
                return true;
            });
        }

        if (subredditPreference != null) {
            subredditPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, LinkResolverActivity.class);
                intent.setData(Uri.parse("https://lemdro.id/c/eternityapp"));
                activity.startActivity(intent);
                return true;
            });
        }

        if (sharePreference != null) {
            sharePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_this_app));
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, R.string.no_app, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        if (versionPreference != null) {
            versionPreference.setSummary(getString(R.string.settings_version_summary, BuildConfig.VERSION_NAME));

            versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                int clickedTimes = 0;

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    clickedTimes++;
                    if (clickedTimes > 6) {
                        Toast.makeText(activity, R.string.no_developer_easter_egg, Toast.LENGTH_SHORT).show();
                        clickedTimes = 0;
                    }
                    return true;
                }
            });
        }
    }
}
