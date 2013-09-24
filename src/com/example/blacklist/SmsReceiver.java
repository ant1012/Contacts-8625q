package com.example.blacklist;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.telephony.SmsMessage;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = "franco--->SmsReceiver";
    public static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_CALL = "android.intent.action.PHONE_STATE";
    private Context context;
    private DBHelper mDBHelper;
    private MsgBlockDBHelper msgDBHelper;
    private CallBlockDBHelper callDBHelper;
    private SimpleDateFormat formatter;
    private CallLogContent callLogContent;
    private Vibrator mVibrator;
    private SharedPreferences sp;
    private boolean blockStranger;
    public MediaPlayer  mMediaPlayer        = null;  
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String incomingNumber = "";
        this.context = context;
        sp = context.getSharedPreferences("blacklist", 0);
        Log.v(TAG, "action = " + action);
        if (ACTION_SMS.equals(action)) {

            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }
            
            
            
    		mMediaPlayer = new MediaPlayer();  
    		
//  		 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//  			try {
//  				Log.i("ExternalStorage",""+Environment.getExternalStorageDirectory().getCanonicalPath());
//  				String path = Environment.getExternalStorageDirectory().getCanonicalPath();
//  				playMusic(path+"/abc/3.mp3");//path+"/M.mp3"
//  			} catch (IOException e) {
//  				// TODO Auto-generated catch block
//  				e.printStackTrace();
//  			}
  		 String mediaPath = "content://media/internal/audio/media/31";

		    Uri audioUrl = Uri.parse(mediaPath);
		    mMediaPlayer = MediaPlayer.create(this.context, audioUrl);
		    mMediaPlayer.start();
            
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            
            int i = 0;
            for (i = 0; i < messages.length; i++)
            {
                byte[] pdu = (byte[]) pdus[i];
                messages[i] = SmsMessage.createFromPdu(pdu);
                Log.i("messages"+i,""+messages[i]);
            }
            if(i == 0) {
                return;
            }
            
            incomingNumber = messages[0].getOriginatingAddress();
            mDBHelper = new DBHelper(context, "BlackList", null, 1);
            String sql = "select * from BlackList where phone = ?";
            Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql,
                    new String[] { incomingNumber });

            if(cursor.moveToFirst()) {

                int blockId = cursor.getInt(4);
                String name = cursor.getString(1);
                
                if(blockId != 3) {
                    
                    String content = "";
                    for (SmsMessage msg:messages) {
                        content += msg.getMessageBody();

                    }
                    
                    for (i = 0; i < messages.length; i++)
                    {
                        byte[] pdu = (byte[]) pdus[i];
                        messages[i] = SmsMessage.createFromPdu(pdu);
                    }
                    for (SmsMessage msg:messages) {
                        content += msg.getMessageBody();

                    }

                    int ringtonePos = sp.getInt("ringtone", 0);

                    if (ringtonePos == 1) {// 震动
                        long[] pattern = { 100, 400, 100, 400 };
                        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        mVibrator.vibrate(pattern, -1);
                        // mVibrator.cancel();
                    }

                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
                    String time = formatter.format(new Date());
                    msgDBHelper = new MsgBlockDBHelper(context,
                            "MsgBlockRecord", null, 1);
                    msgDBHelper.addRecord(name, incomingNumber, content, time);
                    msgDBHelper.close();

                    context.sendBroadcast(new Intent(MsgBlock.ACTION_SMS_UPDATE));
                    abortBroadcast();
                }
                cursor.close();
            }
            mDBHelper.close();
            return;
        }

        if (ACTION_CALL.equals(action)) {

            TelephonyManager telMgr = (TelephonyManager) context
                    .getSystemService("phone");

            switch (telMgr.getCallState()) {

            case TelephonyManager.CALL_STATE_IDLE:// 待机

                try {
                    AudioManager audioManager = (AudioManager) context
                            .getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager != null) {
                        /* 设置手机为待机时响铃为正常模式 */
                        audioManager
                                .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                }
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                
                incomingNumber = intent.getStringExtra("incoming_number");
                Log.v(TAG, incomingNumber + " is calling...");
                blockStranger = sp.getBoolean("blockStranger", false);

                mDBHelper = new DBHelper(context, "BlackList", null, 1);
                String sql = "select * from BlackList where phone = ?";
                Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql,
                        new String[] { incomingNumber });
                formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
                if (cursor.moveToFirst()
                        || (blockStranger && isStranger(incomingNumber))) {
                    Log.v(TAG, "This number is in blacklist...");
                    int blockId = 2;
                    String time = formatter.format(new Date());
                    String name = context.getResources().getString(
                            R.string.stranger);

                    if (cursor.moveToFirst()) {
                        blockId = cursor.getInt(4);
                        name = cursor.getString(1);
                    }

                    if (blockId != 1) {
                        // int modePos = sp.getInt("mode", 0);
                        int ringtonePos = sp.getInt("ringtone", 0);
                        // 静音
                        try {
                            AudioManager audioManager = (AudioManager) context
                                    .getSystemService(Context.AUDIO_SERVICE);
                            if (audioManager != null) {
                                audioManager
                                        .setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                audioManager
                                        .getStreamVolume(AudioManager.STREAM_RING);

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "error: ", e);
                        }

                        if (ringtonePos == 1) {// 震动
                            long[] pattern = { 100, 400, 100, 400 };
                            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            mVibrator.vibrate(pattern, -1);
                        }

                        try {
//                            Method method = Class.forName(
//                                    "android.os.ServiceManager").getMethod(
//                                    "getService", String.class);
//                            IBinder binder = (IBinder) method.invoke(null,
//                                    new Object[] { "phone" });
//                            ITelephony telephony = ITelephony.Stub
//                                    .asInterface(binder);
//                            telephony.endCall();
                            ITelephonyMSim telephony = ITelephonyMSim.Stub.asInterface(ServiceManager.getService(Context.MSIM_TELEPHONY_SERVICE));
//                            for(int i = 0; i < 10000; i++){
//                            	telephony.endCall(i);
//                            }
                            telephony.endCall(0);
                            telephony.endCall(1);
                        } catch (Exception e) {
                            Log.e(TAG, "error: ", e);
                        }

                        callDBHelper = new CallBlockDBHelper(context,
                                "CallBlockRecord", null, 1);
                        callDBHelper.addRecord(name, incomingNumber, time);
                        callDBHelper.close();

                        context.sendBroadcast(new Intent(
                                CallBlock.ACTION_CALL_UPDATE));

                        if (callLogContent != null) {
                            context.getContentResolver()
                                    .unregisterContentObserver(callLogContent);
                        }

                        callLogContent = new CallLogContent(new Handler(), incomingNumber);
                        context.getContentResolver().registerContentObserver (
                                        CallLog.Calls.CONTENT_URI, true, callLogContent);
                    }
                    cursor.close();
                } else if (callLogContent != null) {
                    context.getContentResolver().unregisterContentObserver(callLogContent);
                }
                mDBHelper.close();
                context.sendBroadcast(new Intent(MsgBlock.ACTION_SMS_UPDATE));
                break;
            }
        }

    }

    class CallLogContent extends ContentObserver {

        String phone = null;

        public CallLogContent(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);

            ContentResolver resolver = context.getContentResolver();
            Cursor cursorContact = resolver.query(CallLog.Calls.CONTENT_URI,
                    new String[] { "_id" }, "number=? and (type=1 or type=3)",
                    new String[] { phone }, "_id desc limit 1");
            if (cursorContact.moveToFirst()) {
                int id = cursorContact.getInt(0);
                resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
                        new String[] { id + "" });
            }
            cursorContact.close();
        }
    }

    private boolean isStranger(String phoneNumber) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, // Which columns to return.
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
                        + phoneNumber + "'", // WHERE clause.
                null, // WHERE clause value substitution
                null); // Sort order.
        return (!cursor.moveToFirst());// 不能以cursor是否为null判断
    }

}