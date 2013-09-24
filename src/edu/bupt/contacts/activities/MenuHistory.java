package edu.bupt.contacts.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.R;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MenuHistory extends Activity{
	public static Uri ALL_INBOX = Uri.parse("content://sms/");
	String phoneNumber = null;
	public ArrayList<Map<String, Object>> list;
	public ListView listView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list = new ArrayList<Map<String,Object>>(); 
		
		Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        String phone_id = bundle.getString("check_history");
        getPhoneid(phone_id);
        getCallrecord();
        
        listView = new ListView(this);   
        
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.menu_history, new String[]{"img","typeIndex","date","duration","sub_id"}, new int[]{R.id.img,R.id.typeIndex,R.id.date,R.id.duration,R.id.sub_id});   
        listView.setAdapter(adapter);   
           
        setContentView(listView);  
        
        
	}
	
	
	public String DialType(int index){
		String str = null;
		switch(index){
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
	
	public String ImgType(int index){
		String str = null;
		switch(index){
		case 1:
			str = ""+R.drawable.ic_call_incoming_holo_dark;
			break;
		case 2:
			str = ""+R.drawable.ic_call_outgoing_holo_dark;
			break;
		case 3:
			str = ""+R.drawable.ic_call_missed_holo_dark;
			break;
		}
		return str;
	}
	
	
	public String SimType(int sub_id){
		String str = null;
		switch(sub_id){
		case 0:
			str = "			CDMA";
			break;
		case 1:
			str = "			GSM";
			break;

		}
		return str;
	}
	
	public String checkDur(int duration){
		String str = null;
		int hour =  duration /(60*60);
		int min = (duration %(60*60)) /(60);
		int second =  (duration %(60));
		if(0 == hour){
			str =  min + "分" + second + "秒";
		}else{
			str =  hour + "时" + min + "分" + second + "秒";
		}
//		System.out.println("hour:" + hour +",min:" + min + ",sec:" + second);
		return str;
	}
	
	
	public static String dt(long time){
		Date now=new Date(time);
		SimpleDateFormat temp=new SimpleDateFormat("yyyy-MM-dd kk:mm E");
		String str=temp.format(now);
		return str;
		}
	
	
	private void getPhoneid(String phone_id) {			
		String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID,Phone.CONTACT_ID};
		Cursor cur = getContentResolver().query(Phone.CONTENT_URI, projection, null, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		cur.moveToFirst();
		while(cur.getCount() > cur.getPosition()) {
			String id = cur.getString(cur.getColumnIndex(Phone.CONTACT_ID));			
			String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));			
			String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
//			Log.i("number",id+";"+name+";"+number);
			if(id.equals(phone_id)){
				phoneNumber = number;
				phoneNumber = phoneNumber.replace(" ", "");
				phoneNumber = phoneNumber.replace("-", "");
				
				Log.i("phoneNumber",""+phoneNumber);
				break;
			}else{
				phoneNumber = "UNKNOWN";
			}
			
			 
			cur.moveToNext();
		}
		cur.close();
	}
	
	
	
	public void getCallrecord(){
		try{
			list.clear();
			Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,

					   null, "number=?", new String[]{phoneNumber}, CallLog.Calls.DEFAULT_SORT_ORDER);
					if(!cursor.moveToFirst())

					{

					Log.i("通话记录","目前没有通话记录");

					return;

					}
					
					do

					{
						
					int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));	//s
					int sub_id = cursor.getInt(cursor.getColumnIndex("sub_id"));//0/1
					int typeIndex = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));//1/2/3
					long date =  cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));//
					Log.i("date",checkDur(duration)+";"+dt(date));
					

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("img", ImgType(typeIndex)); 
			        map.put("typeIndex", DialType(typeIndex));   
			        map.put("date", dt(date));   
			        map.put("duration",checkDur(duration)); 
			        map.put("sub_id", SimType(sub_id));   
			        
			        list.add(map);   

					


					}while(cursor.moveToNext());
//					Cursor cur = getContentResolver().query(ALL_INBOX,
//
//							   null, "number=?", new String[]{phoneNumber}, null);
//							if(!cur.moveToFirst())
//
//							{
//
////							Log.i("通话记录","目前没有通话记录");
//
//							return;
//
//							}
//							
//							do
//
//							{
//								
//							int duration = cur.getInt(cur.getColumnIndex(CallLog.Calls.DURATION));	//s
//							int sub_id = cur.getInt(cur.getColumnIndex("sub_id"));//0/1
//							int typeIndex = cur.getInt(cur.getColumnIndex(CallLog.Calls.TYPE));//1/2/3
//							long date =  cur.getLong(cur.getColumnIndex(CallLog.Calls.DATE));//
//							Log.i("date",checkDur(duration)+";"+dt(date));
//							
//
//							Map<String, Object> map = new HashMap<String, Object>();
//							map.put("img", ImgType(typeIndex)); 
//					        map.put("typeIndex", DialType(typeIndex));   
//					        map.put("date", dt(date));   
//					        map.put("duration",checkDur(duration)); 
//					        map.put("sub_id", SimType(sub_id));   
//					        
//					        list.add(map);   
//
//							
//
//
//							}while(cur.moveToNext());			
					
					
		}catch(Exception e){
			e.toString();
		}
		
		
		

	}
	
}
