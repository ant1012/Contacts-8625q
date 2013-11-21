package edu.bupt.contacts.edial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.internal.telephony.msim.ITelephonyMSim;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/** zzz */
public class EdialService extends Service {

    private static final String TAG = "EdialService";
    private String digit = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        Log.v(TAG, "Service.onCreate()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v(TAG, "Service.onStart()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Service.onStartCommand()");

        if (intent == null || !intent.hasExtra("digit")) {
            Log.e(TAG, "intent == null || !intent.hasExtra(\"digit\")");
            return super.onStartCommand(intent, flags, startId);
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // for help activity
        // if ((tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference",
        // false))
        // && sp.getBoolean("ShouldShowHelpPreference", true)) {
        // Intent i = new Intent(EdialService.this, HelpActivity.class);
        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(i);
        // return super.onStartCommand(intent, flags, startId);
        // }
        if (showHelpActivity(intent, flags, startId)) {
            return super.onStartCommand(intent, flags, startId);
        }

        digit = intent.getStringExtra("digit");
        digit = formatNumber(digit);

        // prepare the country code database
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this);
        mdbHelper.onCreate(mdbHelper.getWritableDatabase());
        mdbHelper.close();

        if (!shouldShowEdial()) { // may modify the number here
            // call directly
            call(digit);
            return super.onStartCommand(intent, flags, startId);
        }

        // show dialog
        EdialDialog edialDialog = new EdialDialog(this, digit);

        if (sp.getString("EDialPreference", "0").equals("0")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"0\")");
            if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
                Log.v(TAG, "tm.isNetworkRoaming()");

                // if (showHelpActivity(intent, flags, startId)) {
                // return super.onStartCommand(intent, flags, startId);
                // }
                // show dialog here
                edialDialog.show();
            } else {
                call(digit);
            }
        } else if (sp.getString("EDialPreference", "0").equals("1")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"1\")");

            // if (showHelpActivity(intent, flags, startId)) {
            // return super.onStartCommand(intent, flags, startId);
            // }
            // show dialog here
            edialDialog.show();
        } else if (sp.getString("EDialPreference", "0").equals("2")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"2\")");
            call(digit);
        } else {
            Log.e(TAG, "sharedPreferences error");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Service.onDestroy()");
        super.onDestroy();
    }

    private void call(String number) {
        try {
            ITelephonyMSim telephony = ITelephonyMSim.Stub.asInterface(ServiceManager
                    .getService(Context.MSIM_TELEPHONY_SERVICE));
            telephony.call(number, 0);

            // MSimTelephonyManager m =
            // (MSimTelephonyManager)getSystemService(MSIM_TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // call directly when return false
    private boolean shouldShowEdial() {
        Log.d(TAG, "ShouldShowEdial?");

        // start with '+' ?
        Pattern p1 = Pattern.compile("^\\+");
        Matcher m1 = p1.matcher(digit);
        if (m1.find()) {
            Log.w(TAG, "start with \'+\'");
            // if (isC2CRoaming()) {
            // digit = stripCountryCodePrefix(digit);
            // return true;
            // } else {
            // return false;
            // }
            return false;
        }

        // start with '**133', end with '#' ?
        Pattern p2 = Pattern.compile("^\\*\\*133.*\\#");
        Matcher m2 = p2.matcher(digit);
        if (m2.find()) {
            Log.w(TAG, "start with \'**133\', end with \'#\'");
            if (isC2CRoaming()) {
                digit = strip133Prefix(digit);
                return true;
            } else {
                return false;
            }
        }

        // start with local country call prefix ?
        String localcode = getLocalCallPrefix();
        Pattern p3 = Pattern.compile("^" + localcode);
        Matcher m3 = p3.matcher(digit);
        if (m3.find() && digit.length() > 11) {
            Log.w(TAG, "start with local country code " + localcode);
            return false;
        }

        // strip beginning '0'
        digit = stripZeroPrefix(digit);
        return true;
    }

    private static String formatNumber(String s) {
        String strip1 = replacePattern(s, "(\\:)", ""); // strip :
        String strip2 = replacePattern(strip1, "(\\-)", ""); // strip -
        String strip3 = replacePattern(strip2, "(\\ )", ""); // strip space
        return strip3;
    }

    // private String stripCountryCodePrefix(String s) {
    // // TODO other countries?
    // String strip1 = replacePattern(s, "^((\\+{0,1}86){0,1})", ""); // strip
    // // +86
    // return strip1;
    // }

    private static String strip133Prefix(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.delete(0, 8); // **133*86
        sb.deleteCharAt(sb.length() - 1); // #
        return sb.toString();
    }

    private static String stripZeroPrefix(String s) {
        String strip1 = replacePattern(s, "^(0{0,1})", ""); // strip 0
        return strip1;
    }

    private String getLocalCallPrefix() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getNetworkCountryIso();
        Log.i(TAG, countryIso);
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this);
        String ret = mdbHelper.queryCallPrefix(tm.getNetworkCountryIso());
        mdbHelper.close();

        return ret;
    }

    private boolean isC2CRoaming() {
        MSimTelephonyManager m = (MSimTelephonyManager) getSystemService(MSIM_TELEPHONY_SERVICE);
        if (!m.isNetworkRoaming(0)) {
            return false;
        }
        switch (MSimTelephonyManager.getNetworkType(0)) {
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return true;
        default:
            return false;
        }

    }

    private static String replacePattern(String origin, String pattern, String replace) {
        Log.i(TAG, "origin - " + origin);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }

        m.appendTail(sb);
        Log.i(TAG, "sb.toString() - " + sb.toString());
        return sb.toString();
    }

    private boolean showHelpActivity(Intent intent, int flags, int startId) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // for help activity
        if ((tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false))
                && sp.getBoolean("ShouldShowHelpPreference", true)) {
            Intent i = new Intent(EdialService.this, HelpActivity.class);
            i.putExtra("digit", digit);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        } else {
            return false;
        }
    }
}
