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
import android.telephony.TelephonyManager;
import android.util.Log;

public class EdialService extends Service {

    private static final String TAG = "EdialService";

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

        String digit = intent.getStringExtra("digit");

        if (!ShouldShowEdial(digit)) {
            call(digit);
            return super.onStartCommand(intent, flags, startId);
        }

        EdialDialog edialDialog = new EdialDialog(this, digit);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getString("EDialPreference", "0").equals("0")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"0\")");
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.isNetworkRoaming()) {
                Log.v(TAG, "tm.isNetworkRoaming()");
                // show dialog here
                edialDialog.show();
            } else {
                call(digit);
            }
        } else if (sp.getString("EDialPreference", "0").equals("1")) {
            Log.v(TAG, "sp.getString(\"EDialPreference\", \"0\").equals(\"1\")");
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

    private String replacePattern(String origin, String pattern, String replace) {
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
    private boolean ShouldShowEdial(String digit) {
        Log.i(TAG, "ShouldShowEdial?");
        Pattern p1 = Pattern.compile("^+");
        Matcher m1 = p1.matcher(digit);
        if (m1.find()) {
            Log.v(TAG, "start with \'+\'");
            if (isC2CRoaming()) {
                return true;
            } else {
                return false;
            }
        }
        
        

        Pattern p2 = Pattern.compile("^\\*\\*133.*\\#");
        Matcher m2 = p2.matcher(digit);
        if (m2.find()) {
            Log.v(TAG, "start with \'**133\', end with \'#\'");
            if (isC2CRoaming()) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean isC2CRoaming() {
        return false;
    }
}
