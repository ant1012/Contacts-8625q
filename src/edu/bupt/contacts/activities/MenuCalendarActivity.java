package edu.bupt.contacts.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.contacts.R;
import edu.bupt.contacts.util.PhoneQueryUtils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 查看与该联系人相关的日程安排(通讯录功能24)
 * 
 * */

public class MenuCalendarActivity extends Activity {
    public static final Uri CONTENT_URI = Uri.parse("content://edu.bupt.calendar.attendee/AttendeePhone");//
    private static final String TAG = "MenuCalendarActivity";
    public List<String> calendarArrayList;
    public List<String> calendarEventArrayList;
    public ArrayList<Map<String, Object>> list;
    public ListView listView;
    String phoneNumber = null;

    // 修改日记界面 ddd
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        calendarArrayList = new ArrayList<String>();
        calendarEventArrayList = new ArrayList<String>();
        list = new ArrayList<Map<String, Object>>();
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        String phone_id = bundle.getString("check_calendar");
        Log.i("phone_id", "" + phone_id);
        // zzz 用CONTACT_ID查询得到通讯录中存储的号码phoneNumber
        getPhoneid(phone_id);
        if (phoneNumber != null && !phoneNumber.equals("UNKNOWN")) {
            // zzz 先读取日程id列表calendarArrayList
            getCalendar();
            // zzz 根据日程id列表显示日程信息
            getCalendarEvent();
        }
        // else{
        // Map<String, Object> map = new HashMap<String, Object>();
        // map.put("title", "事件：暂无");
        // map.put("eventLocation", "地点：暂无");
        // map.put("dtstart","开始时间：暂无");
        // map.put("dtend", "结束时间：暂无");
        // map.put("description", "描述：暂无");
        // list.add(map);
        // }

        // listView = new ListView(this);
        //
        // listView.setAdapter(new ArrayAdapter<String>(this,
        // android.R.layout.simple_expandable_list_item_1,calendarEventArrayList));
        //
        // setContentView(listView);

        // listView = new ListView(this);
        //
        // SimpleAdapter adapter = new SimpleAdapter(this, list,
        // R.layout.calendar_item, new
        // String[]{"title","eventLocation","dtstart","dtend","description"},
        // new
        // int[]{R.id.title,R.id.eventLocation,R.id.dtstart,R.id.dtend,R.id.description});
        // listView.setAdapter(adapter);
        //
        // setContentView(listView);
        // SimpleAdapter adapter = new SimpleAdapter(this, list,
        // R.layout.calendar_item, new
        // String[]{"img","typeIndex","date","duration","sub_id"}, new
        // int[]{R.id.img,R.id.typeIndex,R.id.date,R.id.duration,R.id.sub_id});

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.calendar_item, new String[] { "title",
                "eventLocation", "dtstart", "dtend", "description" }, new int[] { R.id.title, R.id.eventLocation,
                R.id.dtstart, R.id.dtend, R.id.description });
        ListView list = (ListView) findViewById(R.id.calendar_list);

        list.setAdapter(adapter);

    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 用CONTACT_ID查询得到通讯录中存储的号码phoneNumber
     * 
     * */
    private void getPhoneid(String phone_id) {
        String[] projection = { Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID, Phone.CONTACT_ID };
        Cursor cur = getContentResolver().query(Phone.CONTENT_URI, projection, null, null,
                Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            String id = cur.getString(cur.getColumnIndex(Phone.CONTACT_ID));
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            Log.i("number", id + ";" + name + ";" + number);
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

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 根据电话号码查询出相关的日程的列表，保存到calendarArrayList
     * 
     * */
    private void getCalendar() {
        // TODO Auto-generated method stub
        calendarArrayList.clear();
        Log.i(TAG, "phoneNumber - " + phoneNumber);
        Log.i(TAG, "fomatNumberWithDash - " + PhoneQueryUtils.fomatNumberWithDash(phoneNumber));
        Log.i(TAG, "fomatNumberWithSpace - " + PhoneQueryUtils.fomatNumberWithSpace(phoneNumber));

        // zzz 需要考虑多种情况，因为在数据库中存储的号码可能是正常号码，也可能用‘-’分隔，也可能用‘ ’分隔
        StringBuilder selectionSB = new StringBuilder();
        selectionSB.append("phoneNumber" + " like ? or "); // zzz
                                                           // 原始
        selectionSB.append("phoneNumber" + " like ? or "); // zzz
                                                           // 减号
        selectionSB.append("phoneNumber" + " like ?"); // zzz
                                                       // 空格

        String[] selectionArgsSB = new String[] { "%" + phoneNumber,// zzz
                                                                    // 原始
                "%" + PhoneQueryUtils.fomatNumberWithDash(phoneNumber), // zzz
                                                                        // 减号
                "%" + PhoneQueryUtils.fomatNumberWithSpace(phoneNumber) }; // zzz
                                                                           // 空格

        Cursor cur = getContentResolver().query(CONTENT_URI, null, selectionSB.toString(), selectionArgsSB, null);

        Log.i(TAG, "cur.getCount() - " + cur.getCount());

        while (cur.moveToNext()) {
            String event_id = cur.getString(cur.getColumnIndex("event_id"));
            calendarArrayList.add(event_id);
        }
        cur.close();
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 根据电话号码查询出相关的日程的列表，保存到calendarArrayList
     * 
     * */
    private void getCalendarEvent() {
        // TODO Auto-generated method stub
        // calendarEventArrayList.clear();
        list.clear();
        Cursor cur = getContentResolver().query(Events.CONTENT_URI, null, null, null, null, null);
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            String id = cur.getString(cur.getColumnIndex("_id"));
            String title = cur.getString(cur.getColumnIndex("title"));
            String eventLocation = cur.getString(cur.getColumnIndex("eventLocation"));
            Long dtstart = cur.getLong(cur.getColumnIndex("dtstart"));
            Long dtend = cur.getLong(cur.getColumnIndex("dtend"));
            String description = cur.getString(cur.getColumnIndex("description"));

            /** zzz */
            // zzz 时间现实方案(国际漫游相关需求)
            // String eventStart = dt(dtstart);
            // String eventEnd = dt(dtend);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean showBJTime = !sp.getString("TimeSettingPreference", "0").equals("0");

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            StringBuilder dateValueStartSB = new StringBuilder();
            StringBuilder dateValueEndSB = new StringBuilder();

            // zzz 只在漫游时判断
            if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
                // zzz 显示‘北京时间’或‘当地时间’
                String timeLocate = showBJTime ? getResources().getStringArray(R.array.time_setting)[1]
                        : getResources().getStringArray(R.array.time_setting)[0];
                dateValueStartSB.append(timeLocate);
                dateValueStartSB.append(' ');
                dateValueEndSB.append(timeLocate);
                dateValueEndSB.append(' ');
            }

            if (!showBJTime) { // local time
                dateValueStartSB.append(DateUtils.formatDateRange(this, dtstart, dtstart, DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

                dateValueEndSB.append(DateUtils.formatDateRange(this, dtend, dtend, DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            } else { // bj time
                // zzz 时间格式化时指定时区
                dateValueStartSB.append(DateUtils.formatDateRange(
                        this,
                        new Formatter(),
                        dtstart,
                        dtstart,
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                                | DateUtils.FORMAT_SHOW_YEAR, getResources().getString(R.string.home_tz)).toString()); // TODO
                dateValueEndSB.append(DateUtils.formatDateRange(
                        this,
                        new Formatter(),
                        dtend,
                        dtend,
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                                | DateUtils.FORMAT_SHOW_YEAR, getResources().getString(R.string.home_tz)).toString()); // TODO
            }
            String eventStart = dateValueStartSB.toString();
            String eventEnd = dateValueEndSB.toString();

            /** zzz */
            if (title == null //
                    && eventLocation == null //
                    && description == null //
                    && eventStart == null //
                    && eventEnd == null) {

                cur.moveToNext();
            }

            if (null == title) {
                title = "暂无";
            }
            if (null == eventLocation) {
                eventLocation = "暂无";
            }
            if (null == description) {
                description = "暂无";
            }
            if (null == eventStart) {
                eventStart = "暂无";
            }
            if (null == eventEnd) {
                eventEnd = "暂无";
            }

            for (int i = 0; i < calendarArrayList.size(); i++) {
                // Log.i("name compare",id+";"+calendarArrayList.get(i));
                if (id.equals(calendarArrayList.get(i))) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", "事件：" + title);
                    map.put("eventLocation", "地点：" + eventLocation);
                    map.put("dtstart", "开始时间：" + eventStart);
                    map.put("dtend", "结束时间：" + eventEnd);
                    map.put("description", "描述：" + description);
                    list.add(map);
                    break;
                    // calendarEventArrayList.add("事件："+title+"\n地点："+eventLocation+"\n开始时间："+eventStart+"\n结束时间："+eventEnd+"\n描述："+description);
                }
            }
            if (0 == list.size()) {

                /** zzz */
                // title = "暂无";
                // eventLocation = "暂无";
                // description = "暂无";
                // eventStart = "暂无";
                // eventEnd = "暂无";
                //
                // Map<String, Object> map = new HashMap<String, Object>();
                // map.put("title", "事件：" + title);
                // map.put("eventLocation", "地点：" + eventLocation);
                // map.put("dtstart", "开始时间：" + eventStart);
                // map.put("dtend", "结束时间：" + eventEnd);
                // map.put("description", "描述：" + description);
                // list.add(map);
            }

            cur.moveToNext();
        }
        cur.close();
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 格式化时间，似乎已经废弃
     * 
     * */
    public static String dt(long time) {
        Date now = new Date(time);
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String str = temp.format(now);
        return str;
    }

}
