package edu.bupt.contacts.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class ContactsContentObserver extends ContentObserver {
    private static String TAG = "SMSContentObserver";
    private Context context;
    private Handler handler;

    public ContactsContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d(TAG, "onChange");

        new Thread(new UpdateContactsCacheRunnable(context)).start();
    }
}
