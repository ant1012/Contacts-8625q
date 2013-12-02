package edu.bupt.contacts.observer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateContactsCacheService extends Service {
    private static final String TAG = "UpdateContactsCacheService";

    private int flag;
    private static final String FLAG = "flag";
    private static final int FLAG_DEFAULT = -0x1;
    private static final int FLAG_UPDATE_CONTACTS = 0x1;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Service.onStartCommand()");
        if (intent == null || !intent.hasExtra(FLAG)) {
            Log.e(TAG, "intent == null || !intent.hasExtra(\"flag\")");
            return super.onStartCommand(intent, flags, startId);
        }
        flag = intent.getIntExtra(FLAG, FLAG_DEFAULT);
        
        Log.v(TAG, "flag - " + flag);

        switch (flag) {

        case FLAG_UPDATE_CONTACTS:
            Log.v(TAG, "FLAG_UPDATE_CONTACTS");
            if (!UpdateContactsCacheRunnable.isInitilized && !UpdateContactsCacheRunnable.isRunning) {
                new Thread(new UpdateContactsCacheRunnable(this)).start();
            }
            break;
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
