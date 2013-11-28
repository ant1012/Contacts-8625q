package edu.bupt.contacts.calllog;

import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

public class ClearCallLog extends Activity {

    private ListView clearCalllogListview;
    private Button buttonCancel, buttonOk;
    private Cursor cursor;
    private CheckBox checkboxSelectAll;
    private ClearCallLogAdapter listAdapter;
    private ContentResolver resolver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_call_log);

        clearCalllogListview = (ListView) findViewById(R.id.listView_clear_call_log);
        buttonOk = (Button) findViewById(R.id.button_ok);
        checkboxSelectAll = (CheckBox) findViewById(R.id.checkBox_selectAll);

        resolver = getContentResolver();
        //ddd added name 
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, new String[] { "_id", "number", "date", "type","name" }, null,
                null, null);
        Log.v("yuan", cursor.getCount() + "");
        // if(cursor.moveToFirst()) {
        // for(int i=0;i<cursor.getCount();i++){
        // int id = cursor.getInt(0);
        // String number = cursor.getString(1);
        // Log.v("yuan1",id+":"+number);
        // cursor.moveToNext();
        // }
        // //resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]
        // {id + ""});
        // }
       //ddd added name
        listAdapter = new ClearCallLogAdapter(this, R.layout.activity_clear_call_log_item, cursor, new String[] {
                CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME}, new int[] { R.id.item_time,
                R.id.item_number, R.id.item_type ,R.id.item_name});

        clearCalllogListview.setAdapter(listAdapter);

        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (ClearCallLogAdapter.getIsSelected().get(i) == true) {
                        cursor.moveToPosition(i);
                        resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[] { cursor.getInt(0) + "" });
                    }
                }
                onHomeSelected();
            }
        });
        buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onHomeSelected();
            }

        });
        checkboxSelectAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.v("aaaaaa", checkboxSelectAll.isChecked() + "");
                if (checkboxSelectAll.isChecked()) {
                    checkboxSelectAll.setChecked(true);
                    for (int i = 0; i < cursor.getCount(); i++) {
                        ClearCallLogAdapter.getIsSelected().set(i, true);
                        listAdapter.notifyDataSetChanged();
                    }

                } else {
                    checkboxSelectAll.setChecked(false);
                    for (int i = 0; i < cursor.getCount(); i++) {
                        ClearCallLogAdapter.getIsSelected().set(i, false);
                        listAdapter.notifyDataSetChanged();
                    }
                }

            }

        });

        configureActionBar();

    }

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }
    }

    /** Invoked when the user presses the home button in the action bar. */
    private void onHomeSelected() {
        Intent intent = new Intent(this, DialtactsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: {
            onHomeSelected();
            return true;
        }

        // All the options menu items are handled by onMenu... methods.
        default:
            throw new IllegalArgumentException();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.clear_call_log, menu);
        return true;
    }

}
