package edu.bupt.contacts.ipcall;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class IPCall {

    protected SharedPreferences sp;

    public IPCall(Context context) {
        // sp = context.getSharedPreferences("edu.bupt.contacts.ip",
        // context.MODE_PRIVATE);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isCDMAIPEnabled() {
        if (sp.getString("CDMAIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
            return true;
        }
        return false;
    }

    public boolean isGSMIPEnabled() {
        if (sp.getString("GSMIPPreference", null) != null && !sp.getString("CDMAIPPreference", null).equals("")) {
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
