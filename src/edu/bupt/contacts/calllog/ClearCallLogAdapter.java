package edu.bupt.contacts.calllog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.bupt.contacts.R;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class ClearCallLogAdapter extends SimpleCursorAdapter{

	 static List<Boolean> mChecked;
     HashMap<Integer,View> map = new HashMap<Integer,View>();
     private Cursor cursor;

	
	public ClearCallLogAdapter(Context context, int layout, Cursor cursor,String[] from, int[] to) {
		super(context, layout, cursor, from, to);
		// TODO Auto-generated constructor stub
		this.cursor = cursor;
        mChecked = new ArrayList<Boolean>();
        for(int i=0;i<cursor.getCount();i++){
            mChecked.add(false);
        }

	} 
	
	@Override
	public long getItemId(int position) {
	    return position;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder = null;
		if (map.get(position) == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.activity_clear_call_log_item, null);
			holder = new ViewHolder();
			holder.selected = (CheckBox)view.findViewById(R.id.item_cb);
			holder.time = (TextView)view.findViewById(R.id.item_time);
			holder.number = (TextView)view.findViewById(R.id.item_number);
			holder.type = (TextView)view.findViewById(R.id.item_type);
			final int p = position;
			map.put(position, view);
			holder.selected.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					mChecked.set(p, cb.isChecked());
					//notifyDataSetChanged();
					}
				});
			view.setTag(holder);
		}else{
			view = map.get(position);
			holder = (ViewHolder)view.getTag();
		}
		holder.selected.setChecked(mChecked.get(position));
		cursor.moveToPosition(position);
        CharSequence dateValue = DateUtils.formatDateRange(mContext, cursor.getLong(2), cursor.getLong(2),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR);
		holder.time.setText(dateValue);
		holder.number.setText(cursor.getString(1));
		int typeInt  = cursor.getInt(3);
		switch(typeInt){
		case 1:
		    holder.type.setText(R.string.incoming);
		    break;
		case 2:
			holder.type.setText(R.string.outcoming);
			break;
		case 3:
			holder.type.setText(R.string.missed);
			break;
		default:
			holder.type.setText("unknown");
			break;
		}
        cursor.moveToFirst();
        return view;
	}
	
	static class ViewHolder{
		CheckBox selected;
        TextView time;
        TextView number;
        TextView type;
    }
	
	public static List<Boolean> getIsSelected(){
		return mChecked;
	}
	
	


	
}