package edu.bupt.contacts.edial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.numberlocate.CountryCodeDBHelper;
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
/**
 * 类描述： 为翼拨号提供后台服务
 * */
public class EdialService extends Service {

    private static final String TAG = "EdialService";
    //电话号码
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

    /**
     * 方法描述：获取拨叫号码，判断是否启用翼拨号 ddd
     * */
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
        Log.w(TAG, "digit - " + digit);
        digit = formatNumber(digit);

        Log.w(TAG, "digit - " + digit);

        // prepare the country code database
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this);
        mdbHelper.onCreate(mdbHelper.getWritableDatabase());
        mdbHelper.close();

        if (!shouldShowEdial()) { // may modify the number here
            // call directly
            Log.i(TAG, "digit - " + digit);
            digit = replacePattern(digit, "#", "%23"); // replace #
            call(digit);
            return super.onStartCommand(intent, flags, startId);
        }

        if (sp.getString("EDialPreference", "0").equals("0")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"0\")");
            if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
                Log.v(TAG, "tm.isNetworkRoaming()");
                // strip beginning '0'
                digit = stripZeroPrefix(digit);

                // if (showHelpActivity(intent, flags, startId)) {
                // return super.onStartCommand(intent, flags, startId);
                // }

                // show dialog here
                Log.v(TAG, "digit - " + digit);
                // show dialog
                EdialDialog edialDialog = new EdialDialog(this, digit);
                edialDialog.show();
            } else {
                digit = replacePattern(digit, "#", "%23"); // replace #
                call(digit);
            }
        } else if (sp.getString("EDialPreference", "0").equals("1")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"1\")");
            // strip beginning '0'
            digit = stripZeroPrefix(digit);

            // if (showHelpActivity(intent, flags, startId)) {
            // return super.onStartCommand(intent, flags, startId);
            // }

            // show dialog here
            Log.v(TAG, "digit - " + digit);
            // show dialog
            EdialDialog edialDialog = new EdialDialog(this, digit);
            edialDialog.show();
        } else if (sp.getString("EDialPreference", "0").equals("2")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"2\")");
            digit = replacePattern(digit, "#", "%23"); // replace #
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

    /**
     * 方法描述： 拨打电话 ddd
     * */
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
    /**
     * 判断是否启用翼拨号，返回值为false时直接拨打 ddd
     * 
     * */
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
                Log.v(TAG, "isC2CRoaming()");
                digit = strip133Prefix(digit);
                return true;
            } else {
                Log.v(TAG, "!isC2CRoaming()");
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

        return true;
    }

    /**
     * 方法描述： 电话号码格式处理 ddd
     * */
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

    /**
     * 方法描述： 去掉**133*86前缀 ddd
     * */
    private static String strip133Prefix(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.delete(0, 8); // **133*86
        sb.deleteCharAt(sb.length() - 1); // #
        return sb.toString();
    }

    /**
     * 方法描述： 去掉0前缀 ddd
     * */
    private static String stripZeroPrefix(String s) {
        String strip1 = replacePattern(s, "^(0{0,1})", ""); // strip 0
        return strip1;
    }

    /**
     * 方法描述： 获取当地拨叫号码前缀 ddd
     * */
    private String getLocalCallPrefix() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getNetworkCountryIso();

        // for debug
        // countryIso = "jp";

        Log.i(TAG, countryIso);
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this);
        String ret = mdbHelper.queryCallPrefix(countryIso);
        mdbHelper.close();

        return ret;
    }

    /**
     * 方法描述： 判断是否是C2C模式
     * */
    private boolean isC2CRoaming() {
        MSimTelephonyManager m = (MSimTelephonyManager) getSystemService(MSIM_TELEPHONY_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(m.isNetworkRoaming(0) || sp.getBoolean("RoamingTestPreference", false))) {
            Log.v(TAG, "!m.isNetworkRoaming(0)");
            return false;
        }
        switch (MSimTelephonyManager.getNetworkType(0)) {
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            Log.v(TAG, "MSimTelephonyManager.getNetworkType(0) - " + MSimTelephonyManager.getNetworkType(0));
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

    /**
     * 方法描述： 是否显示翼拨号帮助界面 ddd
     * */
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
