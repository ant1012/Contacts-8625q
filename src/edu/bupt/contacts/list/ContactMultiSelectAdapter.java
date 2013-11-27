package edu.bupt.contacts.list;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.R;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactMultiSelectAdapter extends BaseAdapter {

    private static final String TAG = "ContactMultiSelectAdapter";
    private ArrayList<Map<String, String>> list;
    private static Context context;
    private LayoutInflater inflater = null;
    private static HashMap<Integer, Boolean> isSelected;
    public ViewHolder holder = null;

    // private TextView textViewName;
    // private TextView textViewNumber;
    // private CheckBox checkbox;

    public ContactMultiSelectAdapter(ArrayList<Map<String, String>> list, Context context) {

        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
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
        // Log.v(TAG, "getView");
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_multi_selection_adapter, null);
            holder.textViewName = (TextView) convertView.findViewById(R.id.multiselect_tv_name);
            holder.textViewNumber = (TextView) convertView.findViewById(R.id.multiselect_tv_number);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.multiselect_checkbox);
            holder.imageView = (ImageView) convertView.findViewById(R.id.multiselect_imageview);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(list.get(position).get("name"));
        holder.textViewNumber.setText(list.get(position).get("number"));
        holder.checkbox.setChecked(isSelected.get(position));

        String id = list.get(position).get("id");
        holder.imageView.setImageBitmap(loadContactPhoto(context.getContentResolver(), Long.valueOf(id)));

        return convertView;
    }

    public class ViewHolder {
        TextView textViewName;
        TextView textViewNumber;
        public CheckBox checkbox;
        ImageView imageView;
    }

    public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_picture_holo_light);
        }
        return BitmapFactory.decodeStream(input);
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ContactMultiSelectAdapter.isSelected = isSelected;
    }
}
