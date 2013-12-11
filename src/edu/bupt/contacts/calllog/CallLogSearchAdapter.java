package edu.bupt.contacts.calllog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.bupt.contacts.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CallLogSearchAdapter extends SimpleCursorAdapter {

    // private Context myContext;
    // ddd
    public static Context myContext;
    private String TAG = "CallLogSearchAdapter";

    public CallLogSearchAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {

        super(context, layout, cursor, from, to);
        // ddd 修改context空值的问题
        myContext = context;
    }

    // ddd 在历史记录列表中，点击某一项，弹出联系人历史记录详情页面
    private final View.OnClickListener mPrimaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.i(TAG, "item click 2");
            IntentProvider intentProvider = (IntentProvider) view.getTag();

            if (view.getTag() == null) {
                Log.i(TAG, "item click 3");
            }
            Log.i(TAG, "item click 4");
            if (intentProvider != null) {

                Log.i(TAG, "myContext: " + myContext);

                myContext.startActivity(intentProvider.getIntent(myContext));
            }

        }
    };

    private void findAndCacheViews(View view) {
        // Get the views to bind to.
        CallLogListItemViews views = CallLogListItemViews.fromView(view);
        views.primaryActionView.setOnClickListener(mPrimaryActionListener);

        /** zzz */
        // views.primaryActionView.setOnLongClickListener(mPrimaryLongActionListener);
        //
        // views.secondaryActionView.setOnClickListener(mSecondaryActionListener);
        // views.thirdaryActionView.setOnClickListener(mThirdaryActionListener);
        view.setTag(views);
    }

    protected View newStandAloneView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_log_list_item, parent, false);
        findAndCacheViews(view);
        return view;
    }

    // @Override
    // ddd 添加setTag
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView textView_name = (TextView) view.findViewById(R.id.item_name);
        TextView textView_number = (TextView) view.findViewById(R.id.item_number);
        TextView textView_date = (TextView) view.findViewById(R.id.item_time);
        CharSequence dateValue = DateUtils.formatDateRange(mContext, cursor.getLong(2), cursor.getLong(2),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_SHOW_YEAR);
        textView_date.setText(dateValue);

        if (cursor.getString(4) != null) {
            textView_name.setText(cursor.getString(4));
        } else {
            textView_name.setText(cursor.getString(1));
        }
        textView_number.setText(cursor.getString(1));
        ImageView imageView_type = (ImageView) view.findViewById(R.id.imageView_type);
        switch (cursor.getInt(3)) {
        case 1:
            imageView_type.setImageResource(R.drawable.ic_call_incoming_holo_dark);
            break;
        case 2:
            imageView_type.setImageResource(R.drawable.ic_call_outgoing_holo_dark);
            break;
        case 3:
            imageView_type.setImageResource(R.drawable.ic_call_missed_holo_dark);
            break;
        }
        Log.i(TAG, "item click 1" + cursor.toString());
        view.setTag(IntentProvider.getCallSearchDetailIntentProvider(cursor.getLong(CallLogQuery.ID)));
        Log.i(TAG, "position--" + cursor.getPosition());
        Log.i(TAG, "ID--" + cursor.getLong(CallLogQuery.ID));
        Log.i(TAG, "count--" + 1);
        view.setOnClickListener(mPrimaryActionListener);

        // final int id =
        // cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.VOLUMN_ID));
        // Button delete = (Button) view.findViewById(R.id.item_DeleteButton);
        // delete.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // SQLiteDatabase writableDB = sqlite.getWritableDatabase();
        // writableDB.delete(MySQLiteOpenHelper.TABLE_NAME
        // , MySQLiteOpenHelper.VOLUMN_ID+"=?" //���"=��"
        // , new String[] {String.valueOf(id)});
        // writableDB.close();
        // initListView();
        // }
        // });
    }

}