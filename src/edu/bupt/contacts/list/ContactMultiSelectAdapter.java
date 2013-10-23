package edu.bupt.contacts.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactMultiSelectAdapter extends BaseAdapter {

    private ArrayList<Map<String, String>> list;
    private Context context;
    private LayoutInflater inflater = null;

    // private TextView textViewName;
    // private TextView textViewNumber;
    // private CheckBox checkbox;

    public ContactMultiSelectAdapter(ArrayList<Map<String, String>> list,
            Context context) {

        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(
                    R.layout.contact_multi_selection_adapter, null);
            holder.textViewName = (TextView) convertView
                    .findViewById(R.id.multiselect_tv_name);
            holder.textViewNumber = (TextView) convertView
                    .findViewById(R.id.multiselect_tv_number);
            holder.checkbox = (CheckBox) convertView
                    .findViewById(R.id.multiselect_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(list.get(position).get("name"));
        holder.textViewNumber.setText(list.get(position).get("number"));
        // 根据isSelected来设置checkbox的选中状况
//        holder.checkbox.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public class ViewHolder {
        TextView textViewName;
        TextView textViewNumber;
        public CheckBox checkbox;
    }
}
