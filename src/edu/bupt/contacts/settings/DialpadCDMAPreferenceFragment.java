package edu.bupt.contacts.settings;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class DialpadCDMAPreferenceFragment extends PreferenceFragment {
    private final String TAG = "DialpadCDMAPreferenceFragment";
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dialpad_cdma_preference);
        sp = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sp.getString("CDMAIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
            findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
        }

        // if (sp.getString("GSMIPPreference", null) != null &&
        // !sp.getString("GSMIPPreference", null).equals("")) {
        // findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference",
        // ""));
        // }

        sp.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("CDMAIPPreference")) {
                if (sp.getString("CDMAIPPreference", null) != null
                        && !sp.getString("CDMAIPPreference", null).equals("")) {
                    Log.v(TAG, "CDMAIPPreference");
                    findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
                } else {
                    Log.v(TAG, "no CDMAIPPreference !");
                    findPreference("CDMAIPPreference").setSummary(R.string.default_ip_setting);
                }

                // } else if (key.equals("GSMIPPreference")) {
                //
                // if (sp.getString("GSMIPPreference", null) != null &&
                // !sp.getString("CDMAIPPreference", null).equals("")) {
                // Log.v(TAG, "GSMIPPreference");
                // findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference",
                // ""));
                // } else {
                // Log.v(TAG, "no GSMIPPreference !");
                // findPreference("GSMIPPreference").setSummary(R.string.default_ip_setting);
                // }

            }
        }
    };

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final Activity activity = getActivity();
        if (preference == findPreference("CDMAIPPreference")) {
            new IPSelectDialog(activity, IPSelectDialog.CDMA).show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}