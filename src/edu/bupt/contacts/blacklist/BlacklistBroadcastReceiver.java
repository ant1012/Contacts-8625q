package edu.bupt.contacts.blacklist;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.telephony.MSimTelephonyManager;
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

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 实现黑白名单功能，接收呼入电话的广播，对比数据库并判断是否需要拦截
 * 
 * */

public class BlacklistBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "BlacklistBroadcastReceiver";
    public static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_CALL = "android.intent.action.PHONE_STATE";
    private Context context;
    private BlacklistDBHelper mDBHelper;
    private WhiteListDBHelper mWhiteDBHelper;
    // private MsgBlockDBHelper msgDBHelper;
    private CallBlockDBHelper callDBHelper;
    private SimpleDateFormat formatter;
    private CallLogContent callLogContent;
    private Vibrator mVibrator;
    private SharedPreferences sp;
    private SharedPreferences whiteMode;
    private boolean blockStranger;
    private boolean white_block_mode;
    public MediaPlayer mMediaPlayer = null;

    // zzz 拦截记录时不弹出挂断后的通话信息界面，通过时间差来判断是否是自动拦截
    public static long justBlockOne = System.currentTimeMillis(); // for
                                                                  // disabling
                                                                  // call info
                                                                  // activity

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String incomingNumber = "";
        this.context = context;

        /** zzz */
        // sp = context.getSharedPreferences("blacklist", 0);
        // whiteMode = context.getSharedPreferences("whitelist", 0);
        sp = context.getSharedPreferences("blacklist_pref", 0);

        Log.v(TAG, "action = " + action);
        if (ACTION_SMS.equals(action)) {
            // zzz 收到短信
            // 暂时取消了拦截短信的功能
            Log.v(TAG, "incoming msg !");

            // Bundle bundle = intent.getExtras();
            // if (bundle == null) {
            // return;
            // }
            //
            // mMediaPlayer = new MediaPlayer();

            // if
            // (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            // try {
            // Log.i("ExternalStorage",""+Environment.getExternalStorageDirectory().getCanonicalPath());
            // String path =
            // Environment.getExternalStorageDirectory().getCanonicalPath();
            // playMusic(path+"/abc/3.mp3");//path+"/M.mp3"
            // } catch (IOException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            // String mediaPath = "content://media/internal/audio/media/31";
            //
            // Uri audioUrl = Uri.parse(mediaPath);
            // mMediaPlayer = MediaPlayer.create(this.context, audioUrl);
            // mMediaPlayer.start();
            //
            // Object[] pdus = (Object[]) bundle.get("pdus");
            // SmsMessage[] messages = new SmsMessage[pdus.length];
            //
            // int i = 0;
            // for (i = 0; i < messages.length; i++) {
            // byte[] pdu = (byte[]) pdus[i];
            // messages[i] = SmsMessage.createFromPdu(pdu);
            // Log.i("messages" + i, "" + messages[i]);
            // }
            // if (i == 0) {
            // return;
            // }

            // incomingNumber = messages[0].getOriginatingAddress();
            // mDBHelper = new BlacklistDBHelper(context, 1);
            // String sql = "select * from BlackListFragment where phone = ?";
            // Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql,
            // new String[] { incomingNumber });
            //
            // if (cursor.moveToFirst()) {
            //
            // int blockId = cursor.getInt(4);
            // String name = cursor.getString(1);
            //
            // if (blockId != 3) {
            //
            // String content = "";
            // for (SmsMessage msg : messages) {
            // content += msg.getMessageBody();
            //
            // }
            //
            // for (i = 0; i < messages.length; i++) {
            // byte[] pdu = (byte[]) pdus[i];
            // messages[i] = SmsMessage.createFromPdu(pdu);
            // }
            // for (SmsMessage msg : messages) {
            // content += msg.getMessageBody();
            //
            // }
            //
            // /** zzz */
            // // int ringtonePos = sp.getInt("ringtone", 0);
            // int ringtonePos = 0;
            //
            // if (ringtonePos == 1) {// 震动
            // long[] pattern = { 100, 400, 100, 400 };
            // mVibrator = (Vibrator) context
            // .getSystemService(Context.VIBRATOR_SERVICE);
            // mVibrator.vibrate(pattern, -1);
            // // mVibrator.cancel();
            // }
            //
            // formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
            // String time = formatter.format(new Date());
            // msgDBHelper = new MsgBlockDBHelper(context, 1);
            // msgDBHelper.addRecord(name, incomingNumber, content, time);
            // msgDBHelper.close();
            //
            // context.sendBroadcast(new Intent(
            // MsgBlockFragment.ACTION_SMS_UPDATE));
            // abortBroadcast();
            // }
            // cursor.close();
            // }
            // mDBHelper.close();
            // return;
        }

        if (ACTION_CALL.equals(action)) {
            // zzz 电话，有可能来电、接通、去电或者挂断，需要进一步判断

            MSimTelephonyManager telMgr = (MSimTelephonyManager) context.getSystemService("phone");

            Log.i(TAG, "telMgr.getCallState(0) - " + telMgr.getCallState(0));
            Log.i(TAG, "telMgr.getCallState(1) - " + telMgr.getCallState(1));
            //
            // switch (telMgr.getCallState()) {
            //
            // case TelephonyManager.CALL_STATE_IDLE:// 待机
            //
            // try {
            // AudioManager audioManager = (AudioManager) context
            // .getSystemService(Context.AUDIO_SERVICE);
            // if (audioManager != null) {
            // /* 设置手机为待机时响铃为正常模式 */
            // audioManager
            // .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            // audioManager.getStreamVolume(AudioManager.STREAM_RING);
            // }
            // } catch (Exception e) {
            // Log.e(TAG, "error: ", e);
            // }
            // break;
            //
            // case TelephonyManager.CALL_STATE_RINGING:

            // zzz 当两个SIM卡有一个处于CALL_STATE_RINGING状态，说明有新的来电
            if (telMgr.getCallState(0) == TelephonyManager.CALL_STATE_RINGING
                    || telMgr.getCallState(1) == TelephonyManager.CALL_STATE_RINGING) { // incoming
                                                                                        // call
                                                                                        // !
                /** zzz */
                // 先判断当前是黑名单模式还是白名单模式
                white_block_mode = sp.getBoolean("white_mode", false);

                Log.v(TAG, "incoming call !");
                Log.i("white_block_mode", "white_block_mode is " + white_block_mode);

                incomingNumber = intent.getStringExtra("incoming_number");

                if (white_block_mode) {
                    // zzz 白名单模式，拦截所有不在名单中的号码
                    Log.i(TAG, incomingNumber + " is calling...");

                    /** zzz */
                    // blockStranger = sp.getBoolean("blockStranger", false);
                    blockStranger = false; // zzz 屏蔽陌生人的选项，暂时不起作用

                    // zzz 查询数据库中白名单的表
                    mWhiteDBHelper = new WhiteListDBHelper(context);
                    String sql = "select * from " + WhiteListDBHelper.TB_NAME + " where phone = ?";
                    Cursor cursor = mWhiteDBHelper.getWritableDatabase().rawQuery(sql, new String[] { incomingNumber });

                    Log.v(TAG, "cursor.getCount() - " + cursor.getCount());
                    Log.v(TAG, "cursor.moveToFirst() - " + cursor.moveToFirst());

                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
                    if (!cursor.moveToFirst() || (isStranger(incomingNumber))) {
                        // zzz 没有查询到即表明来电号码不在白名单中
                        Log.v(TAG, "This number is not in whitelist...");
                        int blockId = 2;
                        String time = formatter.format(new Date());
                        String name = context.getResources().getString(R.string.stranger);

                        if (cursor.moveToFirst()) {
                            // blockId = cursor.getInt(4);
                            name = cursor.getString(1);
                        }

                        // if (blockId != 1) {
                        // int modePos = sp.getInt("mode", 0);
                        // int ringtonePos = sp.getInt("ringtone", 0);
                        // // 静音
                        // try {
                        // AudioManager audioManager = (AudioManager) context
                        // .getSystemService(Context.AUDIO_SERVICE);
                        // if (audioManager != null) {
                        // audioManager
                        // .setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        // audioManager
                        // .getStreamVolume(AudioManager.STREAM_RING);
                        //
                        // }
                        // } catch (Exception e) {
                        // Log.e(TAG, "error: ", e);
                        // }
                        //
                        // if (ringtonePos == 1) {// 震动
                        // long[] pattern = { 100, 400, 100, 400 };
                        // mVibrator = (Vibrator) context
                        // .getSystemService(Context.VIBRATOR_SERVICE);
                        // mVibrator.vibrate(pattern, -1);
                        // }

                        // zzz 拦截记录时不弹出挂断后的通话信息界面，通过时间差来判断是否是自动拦截
                        justBlockOne = System.currentTimeMillis(); // for
                                                                   // disabling
                                                                   // call info
                                                                   // activity

                        try {
                            // zzz 挂断电话
                            ITelephonyMSim telephony = ITelephonyMSim.Stub.asInterface(ServiceManager
                                    .getService(Context.MSIM_TELEPHONY_SERVICE));
                            telephony.endCall(0);
                            telephony.endCall(1);
                        } catch (Exception e) {
                            Log.e(TAG, "error: ", e);
                        }

                        // zzz 拦截后保存记录
                        callDBHelper = new CallBlockDBHelper(context);
                        callDBHelper.addRecord(name, incomingNumber, time);
                        callDBHelper.close();

                        // zzz 更新广播，对于电话的无序广播有用么？
                        context.sendBroadcast(new Intent(CallBlockFragment.ACTION_CALL_UPDATE));

                        // zzz 为了删除通话记录中的信息注册ContentObserver
                        if (callLogContent != null) {
                            context.getContentResolver().unregisterContentObserver(callLogContent);
                        }

                        callLogContent = new CallLogContent(new Handler(), incomingNumber);
                        context.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true,
                                callLogContent);
                        // }
                        cursor.close();

                    } else if (callLogContent != null) {
                        context.getContentResolver().unregisterContentObserver(callLogContent);
                    }
                    mWhiteDBHelper.close();

                    Log.v(TAG, "mWhiteDBHelper.close()");

                    // context.sendBroadcast(new
                    // Intent(MsgBlockFragment.ACTION_SMS_UPDATE));
                    // break;
                } else {
                    Log.i(TAG, incomingNumber + " is calling...");

                    /** zzz */
                    // blockStranger = sp.getBoolean("blockStranger", false);
                    blockStranger = false; // zzz 屏蔽陌生人的选项，暂时不起作用

                    // zzz 查询数据库中黑名单的表
                    mDBHelper = new BlacklistDBHelper(context);
                    String sql = "select * from  " + BlacklistDBHelper.TB_NAME + "  where phone = ?";
                    Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql, new String[] { incomingNumber });
                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
                    if (cursor.moveToFirst() || (blockStranger && isStranger(incomingNumber))) {
                        // zzz 查询到即表明来电号码在黑名单中
                        Log.v(TAG, "This number is in blacklist...");
                        // zzz blockId用于判断拦截模式，需求中没有用到，取消了这个功能
                        int blockId = 2;
                        String time = formatter.format(new Date());
                        String name = context.getResources().getString(R.string.stranger);

                        if (cursor.moveToFirst()) {
                            // blockId = cursor.getInt(4);
                            name = cursor.getString(1);
                        }

                        if (blockId != 1) {
                            // int modePos = sp.getInt("mode", 0);
                            // int ringtonePos = sp.getInt("ringtone", 0);
                            // // 静音
                            // try {
                            // AudioManager audioManager = (AudioManager)
                            // context
                            // .getSystemService(Context.AUDIO_SERVICE);
                            // if (audioManager != null) {
                            // audioManager
                            // .setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            // audioManager
                            // .getStreamVolume(AudioManager.STREAM_RING);
                            //
                            // }
                            // } catch (Exception e) {
                            // Log.e(TAG, "error: ", e);
                            // }
                            //
                            // if (ringtonePos == 1) {// 震动
                            // long[] pattern = { 100, 400, 100, 400 };
                            // mVibrator = (Vibrator) context
                            // .getSystemService(Context.VIBRATOR_SERVICE);
                            // mVibrator.vibrate(pattern, -1);
                            // }

                            // zzz 拦截记录时不弹出挂断后的通话信息界面，通过时间差来判断是否是自动拦截
                            justBlockOne = System.currentTimeMillis(); // for
                                                                       // disabling
                                                                       // call
                                                                       // info

                            try {
                                // zzz 挂断电话
                                ITelephonyMSim telephony = ITelephonyMSim.Stub.asInterface(ServiceManager
                                        .getService(Context.MSIM_TELEPHONY_SERVICE));
                                telephony.endCall(0);
                                telephony.endCall(1);
                            } catch (Exception e) {
                                Log.e(TAG, "error: ", e);
                            }

                            callDBHelper = new CallBlockDBHelper(context);
                            callDBHelper.addRecord(name, incomingNumber, time);
                            callDBHelper.close();

                            // zzz 更新广播，对于电话的无序广播有用么？
                            context.sendBroadcast(new Intent(CallBlockFragment.ACTION_CALL_UPDATE));

                            // zzz 为了删除通话记录中的信息注册ContentObserver
                            if (callLogContent != null) {
                                context.getContentResolver().unregisterContentObserver(callLogContent);
                            }

                            callLogContent = new CallLogContent(new Handler(), incomingNumber);
                            context.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true,
                                    callLogContent);
                        }
                        cursor.close();

                    } else if (callLogContent != null) {
                        context.getContentResolver().unregisterContentObserver(callLogContent);
                    }
                    mDBHelper.close();
                    // context.sendBroadcast(new
                    // Intent(MsgBlockFragment.ACTION_SMS_UPDATE));
                    // break;
                }

                // }
            }
        }

    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * ContentObserver用于监听通话记录数据库，在拦截后删除通话记录，只保留在拦截记录中
     * 
     * */
    class CallLogContent extends ContentObserver {

        String phone = null;

        public CallLogContent(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "onChange, del the last call log");
            // zzz 判断是否是由onChange函数带来的改变
            super.onChange(selfChange);

            ContentResolver resolver = context.getContentResolver();
            // zzz 查询通话记录表
            Cursor cursorContact = resolver.query(CallLog.Calls.CONTENT_URI, new String[] { "_id" },
                    "number=? and (type=1 or type=3)", new String[] { phone }, "_id desc limit 1");
            if (cursorContact.moveToFirst()) {
                // zzz 删除最后一项
                int id = cursorContact.getInt(0);
                resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[] { id + "" });
            }
            cursorContact.close();
            // zzz 只需要一次，解除注册
            context.getContentResolver().unregisterContentObserver(callLogContent);
        }
    }

    private boolean isStranger(String phoneNumber) {
        // String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
        // ContactsContract.CommonDataKinds.Phone.NUMBER };
        // Cursor cursor = context.getContentResolver().query(
        // ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        // projection, // Which columns to return.
        // ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
        // + phoneNumber + "'", // WHERE clause.
        // null, // WHERE clause value substitution
        // null); // Sort order.
        // return (!cursor.moveToFirst());// 不能以cursor是否为null判断
        return false;
    }

}