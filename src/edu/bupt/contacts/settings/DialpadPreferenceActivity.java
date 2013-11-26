//package edu.bupt.contacts.settings;
//
//import java.util.TimeZone;
//
//import edu.bupt.contacts.R;
//import edu.bupt.contacts.edial.HelpActivity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceManager;
//import android.preference.PreferenceScreen;
//import android.util.Log;
//
//public class DialpadPreferenceActivity extends PreferenceActivity {
//    private final String TAG = "DialpadPreferenceActivity";
//    private SharedPreferences sp;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.dialpad_preference);
//        sp = PreferenceManager.getDefaultSharedPreferences(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // findPreference("EDialPreference").setSummary(
//        // getResources().getStringArray(R.array.edial_list_preference)[Integer.parseInt(sp.getString(
//        // "EDialPreference", "0"))]);
//
//        if (sp.getString("CDMAIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
//            findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
//        }
//
//        if (sp.getString("GSMIPPreference", null) != null && !sp.getString("GSMIPPreference", null).equals("")) {
//            findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference", ""));
//        }
//
//        findPreference("TimeSettingPreference").setSummary(
//                getResources().getStringArray(R.array.time_setting)[Integer.parseInt(sp.getString(
//                        "TimeSettingPreference", "0"))]);
//
//        // unusable when same time zone
//        // ddd
//        TimeZone timeZone = TimeZone.getDefault();
//        // timeZone = TimeZone.getTimeZone("GMT+09:00");
//        // 获取“时间偏移”。相对于“本初子午线”的偏移，单位是ms。
//        int offset = timeZone.getRawOffset();
//        // 获取“时间偏移” 对应的小时
//        int gmt = offset / (3600 * 1000);
//        Log.i(TAG, "timeZone--" + String.valueOf(gmt));
//        if (gmt == 8) {
//            findPreference("TimeSettingPreference").setEnabled(false);
//        } else {
//            findPreference("TimeSettingPreference").setEnabled(true);
//        }
//
//        sp.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
//    }
//
//    OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//            if (key.equals("EDialPreference")) {
//                // findPreference("EDialPreference").setSummary(
//                // getResources().getStringArray(R.array.edial_list_preference)[Integer.parseInt(sharedPreferences
//                // .getString("EDialPreference", "0"))]);
//
//            } else if (key.equals("CDMAIPPreference")) {
//                if (sp.getString("CDMAIPPreference", null) != null
//                        && !sp.getString("CDMAIPPreference", null).equals("")) {
//                    Log.v(TAG, "CDMAIPPreference");
//                    findPreference("CDMAIPPreference").setSummary(sp.getString("CDMAIPPreference", ""));
//                } else {
//                    Log.v(TAG, "no CDMAIPPreference !");
//                    findPreference("CDMAIPPreference").setSummary(R.string.default_ip_setting);
//                }
//
//            } else if (key.equals("GSMIPPreference")) {
//
//                if (sp.getString("GSMIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
//                    Log.v(TAG, "GSMIPPreference");
//                    findPreference("GSMIPPreference").setSummary(sp.getString("GSMIPPreference", ""));
//                } else {
//                    Log.v(TAG, "no GSMIPPreference !");
//                    findPreference("GSMIPPreference").setSummary(R.string.default_ip_setting);
//                }
//
//            } else if (key.equals("DispLocatePreference")) {
//                Log.v(TAG, "DispLocatePreference");
//            } else if (key.equals("TimeSettingPreference")) {
//                Log.v(TAG, "TimeSettingPreference");
//                findPreference("TimeSettingPreference").setSummary(
//                        getResources().getStringArray(R.array.time_setting)[Integer.parseInt(sp.getString(
//                                "TimeSettingPreference", "0"))]);
//            } else if (key.equals("RoamingTestPreference")) {
//                Log.v(TAG, "RoamingTestPreference");
//                setShouldShowHelp(true);
//            }
//        }
//
//    };
//
//    @Override
//    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//
//        if (preference == findPreference("HelpPreference")) {
//            Log.v(TAG, "HelpPreference");
//            startActivity(new Intent(DialpadPreferenceActivity.this, HelpActivity.class));
//        } else if (preference == findPreference("GSMIPPreference")) {
//            new IPSelectDialog(this, IPSelectDialog.GSM).show();
//        } else if (preference == findPreference("CDMAIPPreference")) {
//            new IPSelectDialog(this, IPSelectDialog.CDMA).show();
//        }
//        return super.onPreferenceTreeClick(preferenceScreen, preference);
//    }
//
//    private void setShouldShowHelp(boolean b) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        Editor editor = sp.edit();
//        editor.putBoolean("ShouldShowHelpPreference", b);
//        editor.commit();
//    }
//}
