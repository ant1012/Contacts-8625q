package edu.bupt.contacts.blacklist;

import edu.bupt.contacts.R;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MsgBlockFragment extends Fragment {

    public static final String TAG = "franco--->MsgBlockFragment";
    public static final String ACTION_SMS_UPDATE = "sms";
    private Context context;
    private View view;
    private ListView listView;
    private MsgBlockDBHelper msgDBHelper;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private int _ID;
    private String name, phone, message;
    private SMSReceiver smsReceiver;

    public MsgBlockFragment(Context context) {
        this.context = context;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        smsReceiver = new SMSReceiver();
        IntentFilter commandFilter = new IntentFilter(ACTION_SMS_UPDATE);
        commandFilter.setPriority(1000);
        context.registerReceiver(smsReceiver, commandFilter);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        context.unregisterReceiver(smsReceiver);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        if (cursor != null) {
            cursor.close();
        }
        
        if (msgDBHelper != null) {
            msgDBHelper.close();
        }
        
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.msg_block, container, false);
        findViewAndSetListener();
        return view;
    }

    private void findViewAndSetListener() {
        
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        msgDBHelper = new MsgBlockDBHelper(context, "MsgBlockRecord", null, 1);
        cursor = msgDBHelper.getWritableDatabase().query(
                MsgBlockDBHelper.TB_NAME, null, null, null, null, null,     
                MsgBlockDBHelper.ID + " ASC");
        
        String[] from = new String[] { MsgBlockDBHelper.NAME, MsgBlockDBHelper.PHONE,
                    MsgBlockDBHelper.TIME, MsgBlockDBHelper.MESSAGE };
        int[] to = new int[] { R.id.block_item_text1, R.id.block_item_text2, R.id.block_item_text4, R.id.block_item_text3 };
        adapter = new SimpleCursorAdapter(context, R.layout.block_item,
                cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                Log.v(TAG, "arg3 = " + arg3);
                String sql = "select * from MsgBlockRecord where _ID = " + arg3;
                Cursor cursor = msgDBHelper.getWritableDatabase().rawQuery(sql, null);
                cursor.moveToFirst();

                _ID = cursor.getInt(0);
                name = cursor.getString(1);
                phone = cursor.getString(2);
                message = cursor.getString(3);
                Log.v(TAG, "_ID = " + _ID);
                Log.v(TAG, "name = " + name);
                Log.v(TAG, "phone = " + phone);
                Log.v(TAG, "message = " + message);
                cursor.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(name)
                        .setItems(R.array.sms_item_click, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                switch (arg1) {
                                
                                case 0://查看
                                    LayoutInflater inflater = LayoutInflater.from(context);
                                    view = inflater.inflate(R.layout.sms_view, null);
                                    TextView nameView = (TextView) view.findViewById(R.id.sms_name);
                                    TextView phoneView = (TextView) view.findViewById(R.id.sms_phone);
                                    TextView contentView = (TextView) view.findViewById(R.id.sms_text);
                                    nameView.setText(name);
                                    phoneView.setText(phone);
                                    contentView.setText(message);
                                    Button replay = (Button) view.findViewById(R.id.reply);
                                    replay.setOnClickListener(new Button.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            // TODO Auto-generated method stub
                                            Uri uri = Uri.parse("smsto:" + phone);
                                            Intent sms = new Intent(Intent.ACTION_SENDTO, uri);
                                            startActivity(sms);
                                        }
                                        
                                    });
                                    new MyAlertDialog.Builder(context)
                                    .setView(view)
                                    .create()
                                    .show();
                                    break;

                                case 1:// 删除
                                    msgDBHelper.delRecord(_ID);
                                    update();
                                    break;

                                case 2:// 发送短信
                                    Uri uri = Uri.parse("smsto:" + phone);
                                    Intent sms = new Intent(Intent.ACTION_SENDTO, uri);
                                    startActivity(sms);
                                    break;

                                case 3:// 呼叫
                                    Intent call = new Intent(Intent.ACTION_DIAL);
                                    call.setData(Uri.parse("tel:" + phone));
                                    startActivity(call);
                                    break;

                                case 4:// 清空列表
                                    msgDBHelper.delAllRecord();
                                    update();
                                    break;
                                }
                            }
                        }).create().show();
            }
        });

    }

    public void update() {
        cursor = msgDBHelper.getWritableDatabase().query(
                MsgBlockDBHelper.TB_NAME, null, null, null, null, null,
                MsgBlockDBHelper.ID + " ASC");
        adapter.changeCursor(cursor);
    }
    
    class SMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            update();
        }
        
    }

}
