package edu.bupt.contacts.callinfo;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CallinfoActivity extends Activity {
    private final String TAG = "CallinfoActivity";
    private TextView textviewName;
    private TextView textviewPhoneNumber;
    private TextView textviewLabel;
    
    private Button buttonMsg;
    private Button buttonDial;
    String number = "";
    String name = "";
    static public boolean exist = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call_info);

        textviewName = (TextView) findViewById(R.id.name);
        textviewPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        textviewLabel = (TextView) findViewById(R.id.label);
        buttonMsg = (Button) findViewById(R.id.bt_msg);
        buttonDial = (Button) findViewById(R.id.bt_dial);

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
        
        textviewName.setText(name);
        textviewPhoneNumber.setText(number);
        
        buttonMsg.setOnClickListener(clickListener);
        buttonDial.setOnClickListener(clickListener);
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
            }
        }
    };

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
