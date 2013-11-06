package edu.bupt.contacts.ipcall;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class IPCall {

    protected SharedPreferences sp;
    private Context context;

    public IPCall(Context context) {
        // sp = context.getSharedPreferences("edu.bupt.contacts.ip",
        // context.MODE_PRIVATE);

        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public boolean isCDMAIPEnabled() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (sp.getString("CDMAIPPreference", null) != null //
                && !sp.getString("CDMAIPPreference", null).equals("") //
                && !tm.isNetworkRoaming()) {
            return true;
        }
        return false;
    }

    public boolean isGSMIPEnabled() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (sp.getString("GSMIPPreference", null) != null //
                && !sp.getString("CDMAIPPreference", null).equals("") //
                && !tm.isNetworkRoaming()) {
            return true;
        }
        return false;
    }

    public String getCDMAIPCode() {

        return sp.getString("CDMAIPPreference", null);
    }

    public String getGSMIPCode() {
        return sp.getString("GSMIPPreference", null);
    }
}
