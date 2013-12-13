package edu.bupt.contacts.callinfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import edu.bupt.contacts.observer.ContactsCacheDBHelper;
import a_vcard.android.content.ContentValues;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CallinfoActivity extends Activity {
    private final String TAG = "CallinfoActivity";

    private ImageView iv_photo;

    private TextView textviewName;
    private TextView textviewPhoneNumber;
    private TextView textviewLabel;

    private Button buttonMsg;
    private Button buttonDial;
    private Button buttonAdd;
    private long id = -1;
    private String number = "";
    private String name = "";
    private String numberCut10= "";
    private String numberCut11 = "";  //原始后十一位电话号码
    private String numberCutFormat11 =""; //格式化的后十一位电话号码
    private String numberCutFormat10 = "";//格式化的后十位位电话号码，因为添加了横杠，实际有十三位
    private String strPhoneNumber;
    private String numberBlank10;
    private Context context;
    static public boolean exist = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call_info);

        iv_photo = (ImageView) findViewById(R.id.photo);
        textviewName = (TextView) findViewById(R.id.name);
        textviewPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        textviewLabel = (TextView) findViewById(R.id.label);
        buttonMsg = (Button) findViewById(R.id.bt_msg);
        buttonDial = (Button) findViewById(R.id.bt_dial);
        buttonAdd = (Button) findViewById(R.id.bt_add);

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date desc limit 1");
        cursor.moveToFirst();

        id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID));
        Log.v(TAG, "id - " + id);
        number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        Log.v(TAG, "number - " + number);
        Log.v(TAG,"raw_number_format - "+fomatNumber(number));
        name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        Log.v(TAG, "name - " + name);
        cursor.close();
        
        /**
         * ddd 取电话号码后十位，具体操作，首先取后11位，格式化（加横杠，加空格，不加）之后，再对格式化之后的号码取
         * */
    	
        // 获取TelephonyManager用于判断是否漫游
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 获取SharedPreferences用于判断是否开启漫游测试
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        if(tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)){
        	Log.v(TAG, "tm.isNetworkRoaming()");
        	if(number.length()<11){
        		numberCut10 = number;
        		numberBlank10 = fomatBlankNumber(number);
        		numberCutFormat10 = fomatNumber(number);
        		Log.v(TAG,"numberCutFormat10_less_than_11"+numberCutFormat10);
            }
            else{
            	
            numberCut10=number.substring(number.length()-10,number.length());
            
            numberCut11 = number.substring(number.length()-11,number.length());
            numberCutFormat11 = fomatNumber(numberCut11);
            Log.v(TAG, "numberCutFormat11 - " + numberCutFormat11);
            Log.v(TAG,"fomatBlankNumber(numberCut11)"+fomatBlankNumber(numberCut11));
            
            numberBlank10 = fomatBlankNumber(numberCut11).substring(fomatBlankNumber(numberCut11).length()-12,fomatBlankNumber(numberCut11).length());
            
            numberCutFormat10 = numberCutFormat11.substring(numberCutFormat11.length()-13,numberCutFormat11.length());
            Log.v(TAG, "numberCutFormat10 - " + numberCutFormat10);
            }
        	
            Log.v(TAG,"numberCut10--"+numberCut10);
            Log.v(TAG,"numberBlank10--"+numberBlank10);

            Map<String, String> map =  getContactidFromCutNumber(numberCut10,numberCutFormat10,numberBlank10);
            
           Log.v(TAG,"map--"+map);
           
           if(map.size() != 0){
        	   name = map.get("displayName");
        	   Log.v(TAG,"displayName--"+name);
        	   number = map.get("contactNumber");
        	   Log.v(TAG,"contactNumber--"+number);
        	   modifyCallLog(id,map.get("displayName"),map.get("contactNumber"));
           } 

        	
        	
        }

        

        
        

        ArrayList<String> contactids = getContactidFromNumber(number);

        Bitmap bm = contactids.size() == 0 ? BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_contact_picture_holo_dark) : loadContactPhoto(getContentResolver(),
                Long.valueOf(contactids.get(0)));

        iv_photo.setImageBitmap(bm);

        textviewName.setText(name);
        textviewPhoneNumber.setText(number);
        buttonMsg.setOnClickListener(clickListener);
        buttonDial.setOnClickListener(clickListener);
        buttonAdd.setOnClickListener(clickListener);

        buttonMsg.setText(getSpan(R.drawable.dialpad_sms_button_tiny_white, ""));
        buttonDial.setText(getSpan(R.drawable.ic_ab_dialer_holo_dark, ""));
        buttonAdd.setText(getSpan(R.drawable.ic_add_contact_holo_dark_white, ""));

        if (name != null) {
            Log.v(TAG, "name != null");
            buttonAdd.setClickable(false);
        }
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(buttonMsg)) {
                Log.v(TAG, "buttonMsg clicked");
                Uri uri = Uri.parse("smsto:" + number);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(i);

            } else if (v.equals(buttonDial)) {
                Log.v(TAG, "buttonDial clicked");
                // Uri uri = Uri.parse("tel:" + number);
                // Intent i = new Intent(Intent.ACTION_DIAL, uri);
                // startActivity(i);

                // try {
                // ITelephonyMSim telephony =
                // ITelephonyMSim.Stub.asInterface(ServiceManager
                // .getService(Context.MSIM_TELEPHONY_SERVICE));
                // telephony.call(number, 0);
                // } catch (RemoteException e) {
                // e.printStackTrace();
                // }

                /** zzz */
                Intent intent = new Intent();
                intent.setAction("edu.bupt.action.EDIAL");
                intent.putExtra("digit", number);
                startService(intent);

            } else if (v.equals(buttonAdd)) {
                Log.v(TAG, "buttonAdd clicked");
                Intent intent = new Intent(Intent.ACTION_INSERT, People.CONTENT_URI);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

                startActivity(intent);
                finish();
            }
        }
    };

    public Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.ic_contact_picture_holo_dark);
        }
        return BitmapFactory.decodeStream(input);
    }

    private ArrayList<String> getContactidFromNumber(String phoneNumber) {
        Log.d(TAG, "getContactidFromNumber");
        ArrayList<String> contactidList = new ArrayList<String>();
        Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",        		
        		new String[] { phoneNumber }, null);
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

    //ddd 通过截短的十位电话找到contact_id
//    private ArrayList<String> getContactidFromCutNumber(String phoneNumber) {
//        Log.d(TAG, "getContactidFromCutNumber");
//        ArrayList<String> contactidList = new ArrayList<String>();
//        Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//        		android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+phoneNumber+"'",
//        		null, null);
//        while (pCur.moveToNext()) {
//            contactidList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
//         //   contactidList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//        }
//        pCur.close();
//
//        // -
//        Cursor pCurFormat = getContentResolver()
//                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                        //ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
//                		android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+phoneNumber+"'",
//                		null, null);
//        while (pCurFormat.moveToNext()) {
//            contactidList.add(pCurFormat.getString(pCurFormat
//                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
//    //        contactidList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//        }
//        pCurFormat.close();
//        return contactidList;
//    }
    
    private Map<String, String> getContactidFromCutNumber(String phoneNumber,String formatNmuber,String formatBlankNmuber) {
        Log.i(TAG, "phone-number--"+phoneNumber);
        Map<String, String> map = new HashMap<String, String>();
        
        Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
        		android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+phoneNumber+"'",
        		null, null);
        while (pCur.moveToNext()) {
            map.put("contactId",pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            map.put("contactNumber",pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            map.put("displayName",pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        Log.v(TAG,"ddd-pur-number--"+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))); 
        }
        
        pCur.close();

        // -
        Cursor pCurFormat = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        //ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                		android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+formatNmuber+"'",
                		null, null);
        while (pCurFormat.moveToNext()) {
            map.put("contactId",pCurFormat.getString(pCurFormat
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            map.put("contactNumber",pCurFormat.getString(pCurFormat.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            map.put("displayName",pCurFormat.getString(pCurFormat.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            Log.i(TAG,"ddd-purformat-number--"+pCurFormat.getString(pCurFormat.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))); 
        }
        pCurFormat.close();
        
        //ddd  空格的情况
        Cursor pCurBlank = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
        		android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+formatBlankNmuber+"'",
        		null, null);
        while (pCurBlank.moveToNext()) {
            map.put("contactId",pCurBlank.getString(pCurBlank.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            map.put("contactNumber",pCurBlank.getString(pCurBlank.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            map.put("displayName",pCurBlank.getString(pCurBlank.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        Log.v(TAG,"ddd-pCurBlank-number--"+pCurBlank.getString(pCurBlank.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))); 
        }

        pCurBlank.close();
        
        return map;
    }
    
    
    //add '-' to input number, match the format in DB
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

    /**
     * ddd 将数字中加入空格
     * */
    private String fomatBlankNumber(String input) {
            if (input.length() <= 3) {
                return input;
            } else if (input.length() > 3 && input.length() < 7) {
                return input.substring(0, 3) + " " + input.substring(3, input.length());
            } else if (input.length() >= 7 && input.length() <= 11 ) {
                return input.substring(0, 3) + " " + input.substring(3, 7) + " " + input.substring(7, input.length());
            }
            else if(input.length()>11){
            	return input;
            	
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

    public Spanned getSpan(int id, String s) {
        ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                // TODO Auto-generated method stub
                Drawable drawable = null;
                drawable = CallinfoActivity.this.getResources().getDrawable(Integer.parseInt(source));
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
        };
        StringBuffer sb = new StringBuffer();
        sb.append("<img src=\"").append(id).append("\"/>").append("              ").append("<font>" + s + "</font>");
        ;
        Spanned span = Html.fromHtml(sb.toString(), imgGetter, null);
        return span;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        exist = true;
        super.onResume();
        // dismiss in 5 secs
        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                finish();
            }
        };

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        exist = false;
        super.onDestroy();
    }
    
    
    
    /**
     * ddd 修改数据库中的通话记录
     * */
    private void modifyCallLog(long id2,String name,String number){
    	android.content.ContentValues content = new android.content.ContentValues();
    	content.put(CallLog.Calls.NUMBER, number);
    	content.put(CallLog.Calls.CACHED_NAME,name);
    	getContentResolver().update(CallLog.Calls.CONTENT_URI, content, CallLog.Calls._ID+"="+id2, null);
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
