package edu.bupt.contacts.msgring;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/** zzz */
public class MsgRingBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "MsgRingBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "action = " + intent.getAction());

        MsgRingService.setFromReveiver(context, intent);

        Intent serviceIntent = new Intent(context, MsgRingService.class);
        context.startService(serviceIntent);
    }
}
