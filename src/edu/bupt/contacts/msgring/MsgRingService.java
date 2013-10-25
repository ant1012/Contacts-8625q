package edu.bupt.contacts.msgring;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

/** zzz */
public class MsgRingService extends Service {
    private final String TAG = "MsgRingService";
    private final boolean OFFLINEDEBUG = true;
    private static Context receiverContext;
    private static Intent receiverIntent;

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.v(TAG, "onStart");
        super.onStart(intent, startId);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String incomingNumber;
                if (!OFFLINEDEBUG) {
                    incomingNumber = getIncomingNumber(receiverIntent);
                } else {
                    incomingNumber = "18911227942";
                }
                Log.i(TAG, "Msg from " + incomingNumber);
                Log.i(TAG, "formatted " + fomatNumber(incomingNumber));

                ArrayList<String> contactidList = getContactidFromNumber(incomingNumber);
                MsgRingDBHelper dbhelper = new MsgRingDBHelper(MsgRingService.this, 1);
                for (String s : contactidList) {
                    Log.i(TAG, "contatcid - " + s);
                }
                Log.i(TAG, "ring - " + dbhelper.queryRing(contactidList));
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void setFromReveiver(Context c, Intent i) {
        receiverIntent = i;
        receiverContext = c;
    }

    private String getIncomingNumber(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action = " + action);
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return null;
        }
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];

        int i = 0;
        for (i = 0; i < messages.length; i++) {
            byte[] pdu = (byte[]) pdus[i];
            messages[i] = SmsMessage.createFromPdu(pdu);
        }
        if (i == 0) {
            return null;
        }

        return messages[0].getOriginatingAddress();
    }

    private ArrayList<String> getContactidFromNumber(String phoneNumber) {
        Log.d(TAG, "getContactidFromNumber");
        ArrayList<String> contactidList = new ArrayList<String>();
        Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?", new String[] { phoneNumber }, null);
        while (pCur.moveToFirst()) {
            contactidList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
        }
        pCur.close();
        return contactidList;
    }

    private String fomatNumber(String input) {
        if (input.startsWith("1")) {
            if (input.length() == 1) {
                return input;
            } else if (input.length() > 1 && input.length() < 5) {
                return input.substring(0, 1) + "-" + input.substring(1, input.length());
            } else if (input.length() >= 5 && input.length() < 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4) + "-" + input.substring(4, input.length());
            } else if (input.length() >= 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4) + "-" + input.substring(4, 7) + "-"
                        + input.substring(7, input.length());
            }
        } else {
            if (input.length() <= 3) {
                return input;
            } else if (input.length() > 3 && input.length() < 7) {
                return input.substring(0, 3) + "-" + input.substring(3, input.length());
            } else if (input.length() >= 7) {
                return input.substring(0, 3) + "-" + input.substring(3, 6) + "-" + input.substring(6, input.length());
            }
        }
        return "";
    }
}
