package edu.bupt.contacts.msgring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MsgRingBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "MsgRingBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action = " + action);
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];

        int i = 0;
        for (i = 0; i < messages.length; i++) {
            byte[] pdu = (byte[]) pdus[i];
            messages[i] = SmsMessage.createFromPdu(pdu);
        }
        if (i == 0) {
            return;
        }

        String incomingNumber = messages[0].getOriginatingAddress();
        Log.i(TAG, "Msg from " + incomingNumber);
    }

}
