package edu.bupt.contacts.msgring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

/** zzz */
public class MsgRingService extends Service {
    private final String TAG = "MsgRingService";
    private final boolean OFFLINEDEBUG = false;
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
                    // incomingNumber = "10086";
                }
                Log.i(TAG, "Msg from " + incomingNumber);
                Log.i(TAG, "formatted " + fomatNumber(incomingNumber));

                ArrayList<String> contactidList = getContactidFromNumber(incomingNumber);
                for (String s : contactidList) {
                    Log.i(TAG, "contatcid - " + s);
                }

                MsgRingDBHelper dbhelper = new MsgRingDBHelper(MsgRingService.this, 1);
                Uri ringUri = dbhelper.queryRing(contactidList);
                Log.i(TAG, "ring - " + ringUri);

                if (ringUri != null) {
                    MediaPlayer mMediaPlayer = new MediaPlayer();
                    mMediaPlayer = MediaPlayer.create(MsgRingService.this, ringUri);
                    mMediaPlayer.start();
                }
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
        while (pCur.moveToNext()) {
            contactidList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCur.close();

        // -
        Cursor pCurFormat = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                        new String[] { fomatNumber(phoneNumber) }, null);
        while (pCurFormat.moveToNext()) {
            contactidList.add(pCurFormat.getString(pCurFormat
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCurFormat.close();

        // +86
        phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                               // +86
        Cursor pCur86 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?", new String[] { phoneNumber }, null);
        while (pCur86.moveToNext()) {
            contactidList
                    .add(pCur86.getString(pCur86.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCur86.close();

        // -
        Cursor pCur86Format = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                        new String[] { fomatNumber(phoneNumber) }, null);
        while (pCur86Format.moveToNext()) {
            contactidList.add(pCur86Format.getString(pCur86Format
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCur86Format.close();
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
}
