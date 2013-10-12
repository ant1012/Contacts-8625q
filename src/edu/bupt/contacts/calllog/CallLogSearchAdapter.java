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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class CallLogSearchAdapter extends SimpleCursorAdapter{

	public CallLogSearchAdapter(Context context, int layout, Cursor cursor,String[] from, int[] to) {
		super(context, layout, cursor, from, to);
	} 
	
	@Override  
    public void bindView(View view, Context context, Cursor cursor) {   
        super.bindView(view, context, cursor);
        TextView textView_name = (TextView)view.findViewById(R.id.item_name);
        TextView textView_number = (TextView)view.findViewById(R.id.item_number);
        TextView textView_date = (TextView)view.findViewById(R.id.item_time); 
        CharSequence dateValue = DateUtils.formatDateRange(mContext, cursor.getLong(2), cursor.getLong(2),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR);
        textView_date.setText(dateValue);
        
        if(cursor.getString(4)!=null){
        	textView_name.setText(cursor.getString(4));
        }else{
        	textView_name.setText(cursor.getString(1));
        }
        textView_number.setText(cursor.getString(1));
        ImageView imageView_type = (ImageView)view.findViewById(R.id.imageView_type);
        switch(cursor.getInt(3)){
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
        
        
        
//        final int id = cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.VOLUMN_ID));   
//        Button delete = (Button) view.findViewById(R.id.item_DeleteButton);   
//        delete.setOnClickListener(new View.OnClickListener() {   
//               
//            @Override  
//            public void onClick(View v) {   
//                SQLiteDatabase writableDB = sqlite.getWritableDatabase();   
//                writableDB.delete(MySQLiteOpenHelper.TABLE_NAME   
//                        , MySQLiteOpenHelper.VOLUMN_ID+"=?" //Ìí¼Ó"=£¿"   
//                        , new String[] {String.valueOf(id)});   
//                writableDB.close();   
//                initListView();   
//            }   
//        });   
    }   

	
}