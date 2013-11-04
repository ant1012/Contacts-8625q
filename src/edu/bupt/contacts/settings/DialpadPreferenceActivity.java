package edu.bupt.contacts.settings;

import edu.bupt.contacts.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class DialpadPreferenceActivity extends PreferenceActivity {
    private final String TAG = "DialpadPreferenceActivity";
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.dialpad_preference);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        findPreference("EDialPreference").setSummary(
                getResources().getStringArray(R.array.edial_list_preference)[Integer.parseInt(sp.getString(
                        "EDialPreference", "0"))]);
        if (sp.getString("CDMAIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
            findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
        }
        if (sp.getString("GSMIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
            findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference", ""));
        }

        sp.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("EDialPreference")) {
                // String EDialPreference =
                // sharedPreferences.getString("EDialPreference", "0");
                // Log.i("i", "EDialPreference - " + EDialPreference);
                // Editor edit = sp.edit();
                // edit.putString("EDialPreference", EDialPreference);
                // edit.commit();
                findPreference("EDialPreference").setSummary(
                        getResources().getStringArray(R.array.edial_list_preference)[Integer.parseInt(sharedPreferences
                                .getString("EDialPreference", "0"))]);

            } else if (key.equals("CDMAIPPreference")) {
                // String CDMAIPPreference =
                // sharedPreferences.getString("CDMAIPPreference", "");
                // Log.i("i", "CDMAIPPreference - " + CDMAIPPreference);
                // Editor edit = sp.edit();
                // edit.putString("CDMAIPPreference", CDMAIPPreference);
                // edit.commit();

                if (sp.getString("CDMAIPPreference", null) != null
                        && !sp.getString("CDMAIPPreference", null).equals("")) {
                    Log.v(TAG, "refresh CDMAIPPreference");
                    findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
                } else {
                    Log.v(TAG, "no CDMAIPPreference !");
                    findPreference("CDMAIPPreference").setSummary(R.string.default_ip_setting);
                }

            } else if (key.equals("GSMIPPreference")) {
                // String GSMIPPreference =
                // sharedPreferences.getString("GSMIPPreference", "");
                // Log.i("i", "GSMIPPreference - " + GSMIPPreference);
                // Editor edit = sp.edit();
                // edit.putString("GSMIPPreference", GSMIPPreference);
                // edit.commit();

                if (sp.getString("GSMIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
                    Log.v(TAG, "refresh GSMIPPreference");
                    findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference", ""));
                } else {
                    Log.v(TAG, "no GSMIPPreference !");
                    findPreference("GSMIPPreference").setSummary(R.string.default_ip_setting);
                }

            } else if (key.equals("DispLocatePreference")) {
                Log.v(TAG, "refresh DispLocatePreference");
            }
        }

    };

}