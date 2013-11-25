package edu.bupt.contacts.activities;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//修改 历史记录的页面 ddd
public class MenuHistory extends Activity {
    public static Uri ALL_INBOX = Uri.parse("content://sms/");
    String phoneNumber = null;
    public ArrayList<Map<String, Object>> list;
    public ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.history_activity);

        list = new ArrayList<Map<String, Object>>();

        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        String phone_id = bundle.getString("check_history");
        getPhoneid(phone_id);
        getCallrecord();
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.menu_history, new String[] { "img", "typeIndex",
                "date", "duration", "sub_id" }, new int[] { R.id.img, R.id.typeIndex, R.id.date, R.id.duration,
                R.id.sub_id });

        ListView list = (ListView) findViewById(R.id.mylist);

        list.setAdapter(adapter);

        // Bundle bundle = new Bundle();
        // bundle = this.getIntent().getExtras();
        // String phone_id = bundle.getString("check_history");
        // getPhoneid(phone_id);
        // getCallrecord();
        //
        // // listView = new ListView(this);
        //
        // SimpleAdapter adapter = new SimpleAdapter(this, list,
        // R.layout.history_activity, new
        // String[]{"img","typeIndex","date","duration","sub_id"}, new
        // int[]{R.id.img,R.id.typeIndex,R.id.date,R.id.duration,R.id.sub_id});
        // listView.setAdapter(adapter);
        //
        // // setContentView(listView);
        //

    }

    public String DialType(int index) {
        String str = null;
        switch (index) {
        case 1:
            str = "拨入电话";
            break;
        case 2:
            str = "拨出电话";
            break;
        case 3:
            str = "未接电话";
            break;
        }
        return str;
    }

    public int SmsIndex(int index, int read) {
        int indexS = 0;
        if (index == 2) {
            indexS = 1;
        } else if (index == 1) {
            if (read == 0) {
                indexS = 2;
            } else {
                indexS = 3;
            }
        }
        return indexS;
    }

    public String SmsType(int index, int read) {
        String str = null;
        int indexS = SmsIndex(index, read);
        switch (indexS) {
        case 1:
            str = "已发信息";
            break;
        case 2:
            str = "已收未读";
            break;
        case 3:
            str = "已收已读";
            break;
        }
        return str;
    }

    public String SmsImgType(int index, int read) {
        String str = null;
        int indexS = SmsIndex(index, read);
        switch (indexS) {
        case 1:
            str = "" + R.drawable.ic_send;
            break;
        case 2:
            str = "" + R.drawable.ic_rec_unread;
            break;
        case 3:
            str = "" + R.drawable.ic_rec_read;
            break;
        }
        return str;
    }

    public String ImgType(int index) {
        String str = null;
        switch (index) {
        case 1:
            str = "" + R.drawable.ic_call_incoming_holo_dark;
            break;
        case 2:
            str = "" + R.drawable.ic_call_outgoing_holo_dark;
            break;
        case 3:
            str = "" + R.drawable.ic_call_missed_holo_dark;
            break;
        }
        return str;
    }

    public String SimType(int sub_id) {
        String str = null;
        switch (sub_id) {
        case 0:
            str = "				卡一";
            break;
        case 1:
            str = "				卡二";
            break;

        }
        return str;
    }

    public String checkDur(int duration) {
        String str = null;
        int hour = duration / (60 * 60);
        int min = (duration % (60 * 60)) / (60);
        int second = (duration % (60));
        if (0 == hour) {
            str = min + "分" + second + "秒";
        } else {
            str = hour + "时" + min + "分" + second + "秒";
        }
        // System.out.println("hour:" + hour +",min:" + min + ",sec:" + second);
        return str;
    }

    public static String dt(long time) {
        Date now = new Date(time);
        SimpleDateFormat temp = new SimpleDateFormat("yyyy年 MM月dd日	kk:mm	E");
        String str = temp.format(now);
        return str;
    }

    private void getPhoneid(String phone_id) {
        String[] projection = { Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, Phone.CONTACT_ID };
        Cursor cur = getContentResolver().query(Phone.CONTENT_URI, projection, null, null,
                Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            String id = cur.getString(cur.getColumnIndex(Phone.CONTACT_ID));
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            // Log.i("number",id+";"+name+";"+number);
            if (id.equals(phone_id)) {
                phoneNumber = number;
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("-", "");

                Log.i("phoneNumber", "" + phoneNumber);
                break;
            } else {
                phoneNumber = "UNKNOWN";
            }

            cur.moveToNext();
        }
        cur.close();
    }

    public void getCallrecord() {
        try {
            list.clear();
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,

            null, "number=?", new String[] { phoneNumber }, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (!cursor.moveToFirst())

            {

                Log.i("通话记录", "目前没有通话记录");

                return;

            }

            do

            {

                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)); // s
                int sub_id = cursor.getInt(cursor.getColumnIndex("sub_id"));// 0/1
                int typeIndex = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));// 1/2/3
                long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));//
                // Log.i("date", checkDur(duration) + ";" + dt(date));

                /** zzz */
                // if need to show bj time or local time
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                boolean showBJTime = !sp.getString("TimeSettingPreference", "0").equals("0");
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                StringBuilder dateValueSB = new StringBuilder();
                if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
                    String timeLocate = showBJTime ? getResources().getStringArray(R.array.time_setting)[1]
                            : getResources().getStringArray(R.array.time_setting)[0];
                    dateValueSB.append(timeLocate);
                    dateValueSB.append(' ');
                }

                if (!showBJTime) { // local time
                    dateValueSB.append(DateUtils.formatDateRange(this, date, date, DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR));
                } else { // bj time
                    dateValueSB.append(DateUtils.formatDateRange(
                            this,
                            new Formatter(),
                            date,
                            date,
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                                    | DateUtils.FORMAT_SHOW_YEAR, getResources().getString(R.string.home_tz)).toString()); // TODO
                }
                Log.i("date", checkDur(duration) + ";" + dateValueSB.toString());

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("img", ImgType(typeIndex));
                map.put("typeIndex", DialType(typeIndex));

                /** zzz */
                map.put("dateOrig", dt(date));
                map.put("date", dateValueSB.toString());

                map.put("duration", "呼叫时长： " + checkDur(duration) + "\n");
                map.put("sub_id", SimType(sub_id));

                list.add(map);

            } while (cursor.moveToNext());
            Cursor cur = getContentResolver().query(ALL_INBOX, null, null, null, null);
            if (!cur.moveToFirst())

            {

                // Log.i("通话记录","目前没有通话记录");

                return;

            }

            do

            {
                String address = cur.getString(cur.getColumnIndex("address"));
                String duration = cur.getString(cur.getColumnIndex("body")); // s
                int sub_id = cur.getInt(cur.getColumnIndex("sub_id"));// 0/1
                int typeIndex = cur.getInt(cur.getColumnIndex("type"));//
                int read = cur.getInt(cur.getColumnIndex("read"));// 0 no / 1
                                                                  // yes
                long date = cur.getLong(cur.getColumnIndex("date"));//
                address = address.replace(" ", "");
                Log.i("address", address);
                if (address.contains(phoneNumber) && (typeIndex == 1 || typeIndex == 2)) {
                    if (duration.length() > 8) {
                        duration = duration.substring(0, 8) + "……";
                    }

                    /** zzz */
                    // if need to show bj time or local time
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    boolean showBJTime = !sp.getString("TimeSettingPreference", "0").equals("0");

                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                    StringBuilder dateValueSB = new StringBuilder();
                    if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
                        String timeLocate = showBJTime ? getResources().getStringArray(R.array.time_setting)[1]
                                : getResources().getStringArray(R.array.time_setting)[0];
                        dateValueSB.append(timeLocate);
                        dateValueSB.append(' ');
                    }

                    if (!showBJTime) { // local time
                        dateValueSB.append(DateUtils.formatDateRange(this, date, date, DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                                | DateUtils.FORMAT_SHOW_YEAR));
                    } else { // bj time
                        dateValueSB.append(DateUtils.formatDateRange(
                                this,
                                new Formatter(),
                                date,
                                date,
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                                        | DateUtils.FORMAT_SHOW_YEAR, getResources().getString(R.string.home_tz)).toString()); // TODO
                    }

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("img", SmsImgType(typeIndex, read));
                    map.put("typeIndex", SmsType(typeIndex, read));

                    /** zzz */
                    map.put("dateOrig", dt(date));
                    map.put("date", dateValueSB.toString());

                    map.put("duration", duration + "\n");
                    map.put("sub_id", SimType(sub_id));

                    list.add(map);
                }

            } while (cur.moveToNext());

            // 排序
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    // return (o2.getValue() - o1.getValue());
                    return (o2.get("dateOrig")).toString().compareTo((String) o1.get("dateOrig"));
                }

            });

        } catch (Exception e) {
            e.toString();
        }

    }

}
