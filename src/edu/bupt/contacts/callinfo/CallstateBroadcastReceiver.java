package edu.bupt.contacts.callinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallstateBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "CallstateBroadcastReceiver";
    public static final String ACTION_CALL = "android.intent.action.PHONE_STATE";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();

        if (ACTION_CALL.equals(action)) {
            Log.v(TAG, "action = " + action);
            MSimTelephonyManager telMgr = (MSimTelephonyManager) context
                    .getSystemService("phone");
            Log.v(TAG, "telMgr.getCallState(0) - " + telMgr.getCallState(0));
            Log.v(TAG, "telMgr.getCallState(1) - " + telMgr.getCallState(1));
            if (telMgr.getCallState(0) == TelephonyManager.CALL_STATE_IDLE
                    && telMgr.getCallState(1) == TelephonyManager.CALL_STATE_IDLE) {
                Log.d(TAG, "------------------------------------call hang up");
                if (!CallinfoActivity.exist) {
                    Intent i = new Intent(context, CallinfoActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }

    }
}
