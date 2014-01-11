package edu.bupt.contacts.msgring;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 接收收到短信的广播，以实现联系人独特的信息铃声(联系人功能29)
 * 
 * */

/** zzz */
public class MsgRingBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "MsgRingBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "action = " + intent.getAction());

        // zzz 调起serveice处理，避免可能由于Receiver周期过短无法播放铃声
        MsgRingService.setFromReveiver(context, intent);

        Intent serviceIntent = new Intent(context, MsgRingService.class);
        context.startService(serviceIntent);
    }
}
