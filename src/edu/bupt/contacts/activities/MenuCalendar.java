package edu.bupt.contacts.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class MenuCalendar extends Activity {
    public static final Uri CONTENT_URI = Uri.parse("content://edu.bupt.calendar.attendee/AttendeePhone");//
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
        getPhoneid(phone_id);
        if (phoneNumber != null && !phoneNumber.equals("UNKNOWN")) {
            getCalendar();
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

    private void getCalendar() {
        // TODO Auto-generated method stub
        calendarArrayList.clear();
        Log.i("getCalendar_phoneNumber", "" + phoneNumber);
        Cursor cur = getContentResolver().query(CONTENT_URI, null, null, null, "phoneNumber = " + phoneNumber, null);
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            String number = cur.getString(cur.getColumnIndex("phoneNumber"));
            String event_id = cur.getString(cur.getColumnIndex("event_id"));
            String name = cur.getString(cur.getColumnIndex("name"));
            Log.i("Calendar_number", "" + number);
            if (number.equals(phoneNumber)) {
                calendarArrayList.add(event_id);
                Log.i("name", "" + name);
                Log.i("event_id", "" + event_id);
            }

            cur.moveToNext();
        }
        cur.close();
    }

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
            // String eventStart = dt(dtstart);
            // String eventEnd = dt(dtend);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean showBJTime = !sp.getString("TimeSettingPreference", "0").equals("0");

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            StringBuilder dateValueStartSB = new StringBuilder();
            StringBuilder dateValueEndSB = new StringBuilder();

            if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {
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

    public static String dt(long time) {
        Date now = new Date(time);
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String str = temp.format(now);
        return str;
    }

}
