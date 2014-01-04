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
import android.widget.ListView;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 拦截记录页面的Fragment
 * 
 * */

public class CallBlockFragment extends Fragment {

    public static final String TAG = "franco--->CallBlockFragment";
    public static final String ACTION_CALL_UPDATE = "call";
    private Context context;
    private View view;
    private ListView listView;
    private CallBlockDBHelper callDBHelper;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private CallReceiver callReceiver;
    private int _ID;
    private String name, phone;

    public CallBlockFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.blacklist_callblock, container, false);
        findViewAndSetListener();
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        callReceiver = new CallReceiver();
        IntentFilter commandFilter = new IntentFilter(ACTION_CALL_UPDATE);
        context.registerReceiver(callReceiver, commandFilter);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        context.unregisterReceiver(callReceiver);
    }

    @Override
    public void onDestroy() {

        if (cursor != null) {
            Log.v(TAG, "cursor.close()...");
            cursor.close();
        }

        if (callDBHelper != null) {
            Log.v(TAG, "callDBHelper.close()...");
            callDBHelper.close();
        }

        super.onDestroy();
    }


    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 初始化控件
     * 
     * */
    private void findViewAndSetListener() {
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        callDBHelper = new CallBlockDBHelper(context);
        cursor = callDBHelper.getWritableDatabase().query(
                CallBlockDBHelper.TB_NAME, null, null, null, null, null,
                CallBlockDBHelper.ID + " ASC");

        String[] from = new String[] { CallBlockDBHelper.NAME,
                CallBlockDBHelper.PHONE, CallBlockDBHelper.TIME };
        int[] to = new int[] { R.id.block_item_text1, R.id.block_item_text2,
                R.id.block_item_text3 };
        adapter = new SimpleCursorAdapter(context,
                R.layout.blacklist_block_item, cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // zzz 点击响应
                Log.v(TAG, "arg3 = " + arg3);
                String sql = "select * from CallBlockRecord where _ID = "
                        + arg3;
                Cursor cursor = callDBHelper.getWritableDatabase().rawQuery(
                        sql, null);
                cursor.moveToFirst();

                _ID = cursor.getInt(0);
                name = cursor.getString(1);
                phone = cursor.getString(2);
                Log.v(TAG, "_ID = " + _ID);
                Log.v(TAG, "name = " + name);
                Log.v(TAG, "phone = " + phone);
                cursor.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(name)
                        .setItems(R.array.call_item_click,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        switch (arg1) {

                                        case 0:// 删除
                                            callDBHelper.delRecord(_ID);
                                            update();
                                            break;

                                        case 1:// 发送短信
                                            Uri uri = Uri.parse("smsto:"
                                                    + phone);
                                            Intent sms = new Intent(
                                                    Intent.ACTION_SENDTO, uri);
                                            startActivity(sms);
                                            break;

                                        case 2:// 呼叫
                                            Intent call = new Intent(
                                                    Intent.ACTION_DIAL);
                                            call.setData(Uri.parse("tel:"
                                                    + phone));
                                            startActivity(call);
                                            break;

                                        case 3:// 清空列表
                                            callDBHelper.delAllRecord();
                                            update();
                                            break;
                                        }
                                    }
                                }).create().show();
            }
        });
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 收到广播后更新显示
     * 
     * */
    private void update() {
        cursor = callDBHelper.getWritableDatabase().query(
                CallBlockDBHelper.TB_NAME, null, null, null, null, null,
                CallBlockDBHelper.ID + " ASC");
        adapter.changeCursor(cursor);
    }

    class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // zzz 收到广播后更新显示，因为有可能新来电被拦截，数据库有变化
            update();
        }

    }
}
