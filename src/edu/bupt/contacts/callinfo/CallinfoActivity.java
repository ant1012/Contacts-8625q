package edu.bupt.contacts.callinfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
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
    String number = "";
    String name = "";
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

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, "date desc limit 1");
        cursor.moveToFirst();

        long id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID));
        Log.v(TAG, "id - " + id);
        number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        Log.v(TAG, "number - " + number);
        name = cursor.getString(cursor
                .getColumnIndex(CallLog.Calls.CACHED_NAME));
        Log.v(TAG, "name - " + name);
        cursor.close();

        ArrayList<String> contactids = getContactidFromNumber(number);

        Bitmap bm = contactids.size() == 0 ? BitmapFactory.decodeResource(
                getResources(), R.drawable.ic_contact_picture_holo_dark)
                : loadContactPhoto(getContentResolver(),
                        Long.valueOf(contactids.get(0)));

        iv_photo.setImageBitmap(bm);

        textviewName.setText(name);
        textviewPhoneNumber.setText(number);
        buttonMsg.setOnClickListener(clickListener);
        buttonDial.setOnClickListener(clickListener);
        buttonAdd.setOnClickListener(clickListener);

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
                try {
                    ITelephonyMSim telephony = ITelephonyMSim.Stub
                            .asInterface(ServiceManager
                                    .getService(Context.MSIM_TELEPHONY_SERVICE));
                    telephony.call(number, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (v.equals(buttonAdd)) {
                Log.v(TAG, "buttonAdd clicked");
            }
        }
    };

    public Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_contact_picture_holo_dark);
        }
        return BitmapFactory.decodeStream(input);
    }

    private ArrayList<String> getContactidFromNumber(String phoneNumber) {
        Log.d(TAG, "getContactidFromNumber");
        ArrayList<String> contactidList = new ArrayList<String>();
        Cursor pCur = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] { phoneNumber }, null);
        while (pCur.moveToNext()) {
            contactidList
                    .add(pCur.getString(pCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCur.close();

        // -
        Cursor pCurFormat = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] { fomatNumber(phoneNumber) }, null);
        while (pCurFormat.moveToNext()) {
            contactidList
                    .add(pCurFormat.getString(pCurFormat
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCurFormat.close();

        // +86
        phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                               // +86
        Cursor pCur86 = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] { phoneNumber }, null);
        while (pCur86.moveToNext()) {
            contactidList
                    .add(pCur86.getString(pCur86
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }
        pCur86.close();

        // -
        Cursor pCur86Format = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] { fomatNumber(phoneNumber) }, null);
        while (pCur86Format.moveToNext()) {
            contactidList
                    .add(pCur86Format.getString(pCur86Format
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
                return input.substring(0, 1) + "-"
                        + input.substring(1, input.length());
            } else if (input.length() >= 5 && input.length() < 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4)
                        + "-" + input.substring(4, input.length());
            } else if (input.length() >= 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4)
                        + "-" + input.substring(4, 7) + "-"
                        + input.substring(7, input.length());
            }
        } else {
            if (input.length() <= 3) {
                return input;
            } else if (input.length() > 3 && input.length() < 7) {
                return input.substring(0, 3) + "-"
                        + input.substring(3, input.length());
            } else if (input.length() >= 7) {
                return input.substring(0, 3) + "-" + input.substring(3, 6)
                        + "-" + input.substring(6, input.length());
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        exist = true;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        exist = false;
        super.onDestroy();
    }
}
