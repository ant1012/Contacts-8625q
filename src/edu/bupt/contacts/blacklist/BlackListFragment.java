package edu.bupt.contacts.blacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.contacts.R;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class BlackListFragment extends Fragment {

    public static final String TAG = "franco--->BlackListFragment";
    public static final int PHONES_DISPLAY_NAME = 1;
    public static final int PHONES_NUMBER = 2;

    private Context context;
    private View view;
    private Button newRecord;
    private Button importContact;
    private ListView listView;
    private Cursor cursor;
    private Cursor contact;
    private SimpleCursorAdapter adapter;
    private EditText phoneNumber;
    private EditText contactName;
    private MyAlertDialog newRecordDialog;
    private MyAlertDialog importContactDialog;
    private int spinnerLatestClicked;
    private String[] blockContent;
    private static BlacklistDBHelper mDBHelper;
    private int _ID, blockId;
    private String name, phone;
    private HashMap<Integer, Boolean> checkedMap;
    private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

    public BlackListFragment(Context context) {
        this.context = context;
        spinnerLatestClicked = blockId = -1;
        blockContent = context.getResources().getStringArray(R.array.block_content);
        checkedMap = new HashMap<Integer, Boolean>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.blacklist_blacklist, container, false);
        findViewAndSetListener();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy...");

        if (cursor != null) {
            cursor.close();
        }

        if (contact != null) {
            contact.close();
        }

        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    private void findViewAndSetListener() {

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        mDBHelper = new BlacklistDBHelper(context, 1);
        cursor = mDBHelper.getWritableDatabase().query(BlacklistDBHelper.TB_NAME, null, null, null, null, null,
                BlacklistDBHelper.NAME + " ASC");
        String[] from = new String[] { BlacklistDBHelper.NAME, BlacklistDBHelper.Phone, BlacklistDBHelper.BlockContent };
        int[] to = new int[] { R.id.blacklist_item_text1, R.id.blacklist_item_text2, R.id.blacklist_item_text3 };
        adapter = new SimpleCursorAdapter(context, R.layout.blacklist_item, cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.v(TAG, "arg3 = " + arg3);
                String sql = "select * from BlackListFragment where _ID = " + arg3;
                Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql, null);
                cursor.moveToFirst();

                _ID = cursor.getInt(0);
                name = cursor.getString(1);
                phone = cursor.getString(2);
                blockId = cursor.getInt(4);
                Log.v(TAG, "_ID = " + _ID);
                Log.v(TAG, "name = " + name);
                Log.v(TAG, "phone = " + phone);
                Log.v(TAG, "blockId = " + blockId);
                cursor.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(name).setItems(R.array.blacklist_item_click, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        switch (arg1) {

                        case 0:// 删除
                            mDBHelper.delPeople(_ID);
                            update();
                            break;

                        case 1:// 清空列表
                            mDBHelper.delAllPeople();
                            update();
                            break;
                        }
                    }
                }).create().show();
            }

        });
        newRecord = (Button) view.findViewById(R.id.new_record);
        newRecord.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewRecordDialog("", "", -1, false);
            }

        });
        importContact = (Button) view.findViewById(R.id.import_contact);
        importContact.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportContactDialog();
            }

        });
    }

    private void showImportContactDialog() {

        Button save = null;
        Button cancle = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.blacklist_contact, null);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        save = (Button) view.findViewById(R.id.contact_save);
        cancle = (Button) view.findViewById(R.id.contact_cancle);

        // String[] projection = { ContactsContract.Contacts._ID,
        // ContactsContract.PhoneLookup.DISPLAY_NAME,
        // ContactsContract.CommonDataKinds.Phone.NUMBER };
        // contact = context.getContentResolver().query(Phone.CONTENT_URI,
        // projection, // Which columns to return.
        // null, // WHERE clause.
        // null, // WHERE clause value substitution
        // ContactsContract.Contacts._ID); // Sort order.
        // String[] from = new String[] {
        // ContactsContract.PhoneLookup.DISPLAY_NAME };
        // int[] to = new int[] { R.id.contact_checked_text_view };
        //
        // SimpleCursorAdapter adapter = new MyAdapter(context,
        // R.layout.blacklist_contact_item, contact, from, to,
        // CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        initData(list);
        ContactMultiSelectAdapter adapter = new ContactMultiSelectAdapter(list, context);

        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ContactMultiSelectAdapter.ViewHolder holder = (ContactMultiSelectAdapter.ViewHolder) arg1.getTag();
                holder.checkbox.toggle();

                if (checkedMap.get(arg2) == null || checkedMap.get(arg2) == false) {
                    Log.d(TAG, "true");
                    checkedMap.put(arg2, true);
                    // CheckedTextView checkedTextView = (CheckedTextView) arg1
                    // .findViewById(R.id.contact_checked_text_view);
                    // checkedTextView.setChecked(true);
                } else {
                    Log.d(TAG, "false");
                    checkedMap.put(arg2, false);
                    // CheckedTextView checkedTextView = (CheckedTextView) arg1
                    // .findViewById(R.id.contact_checked_text_view);
                    // checkedTextView.setChecked(false);
                }
            }
        });

        save.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                importContactDialog.dismiss();
                for (int position : checkedMap.keySet()) {
                    if (checkedMap.get(position)) {
                        // contact.moveToPosition(position);
                        // String name = contact.getString(PHONES_DISPLAY_NAME);
                        // String phone = contact.getString(PHONES_NUMBER);]

                        String contactId = list.get(position).get("id");

                        Cursor phonecur = context.getContentResolver().query(
                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[] { contactId }, null);
                        // get number
                        while (phonecur.moveToNext()) {
                            String strPhoneNumber = phonecur.getString(phonecur
                                    .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // if (strPhoneNumber.length() > 4)
                            // contactsList.add("18610011001" + "\n测试");
                            // contactsList.add(strPhoneNumber+"\n"+name+"");

                            // Log.i(TAG, "strPhoneNumber - " + strPhoneNumber);

                            String name = list.get(position).get("name");
                            String phone = strPhoneNumber;
                            save(name, phone, 0);

                        }
                        phonecur.close();

                        // String name = list.get(position).get("name");
                        // String phone = list.get(position).get("number");
                        // save(name, phone, 0);
                    }
                }
                checkedMap.clear();
            }

        });

        cancle.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                importContactDialog.dismiss();
                if (checkedMap != null) {
                    checkedMap.clear();
                }
            }

        });

        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context).setView(view);
        importContactDialog = builder.create();
        importContactDialog.setCanceledOnTouchOutside(true);
        importContactDialog.show();
    }

    private void initData(ArrayList<Map<String, String>> list) {
        // contactLookupArrayList.clear();
        list.clear();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID };
        // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
        // + " = '1'";
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        Cursor phonecur = null;

        while (cursor.moveToNext()) {

            // get name
            int nameFieldColumnIndex = cursor
                    .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            // get id
            String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            String strPhoneNumber = "";

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", contactId);
            map.put("name", name);
            map.put("number", strPhoneNumber);
            list.add(map);

            // phonecur = context.getContentResolver().query(
            // android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            // null,
            // android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            // + " = ?",
            // new String[] { contactId }, null);
            // // get number
            // while (phonecur.moveToNext()) {
            // strPhoneNumber = phonecur.getString(phonecur
            // .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
            // // if (strPhoneNumber.length() > 4)
            // // contactsList.add("18610011001" + "\n测试");
            // // contactsList.add(strPhoneNumber+"\n"+name+"");
            // Log.i(TAG, "strPhoneNumber - " + strPhoneNumber);
            //
            // Map<String, String> map = new HashMap<String, String>();
            // map.put("id", contactId);
            // map.put("name", name);
            // map.put("number", strPhoneNumber);
            // list.add(map);
            // }
            // phonecur.close();

        }
        // if (phonecur != null)
        cursor.close();

        // Message msg1 = new Message();
        // msg1.what = UPDATE_LIST;
        // updateListHandler.sendMessage(msg1);

    }

    private void showNewRecordDialog(String name, String phone, int blockId, final boolean isExisted) {

        Button save = null;
        Button cancle = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.blacklist_new_record_dialog, null);
        save = (Button) view.findViewById(R.id.save);
        cancle = (Button) view.findViewById(R.id.cancle);
        contactName = (EditText) view.findViewById(R.id.contact_name_et);
        contactName.setText(name);
        phoneNumber = (EditText) view.findViewById(R.id.phone_number_et);
        phoneNumber.setText(phone);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.block_content,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (blockId != -1) {
            spinner.setSelection(blockId);
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spinnerLatestClicked = arg2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        cancle.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                newRecordDialog.dismiss();
            }

        });
        save.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = phoneNumber.getText().toString();
                String name = contactName.getText().toString();
                if ("".equals(phone)) {
                    Toast.makeText(context, R.string.no_phone_input, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(name)) {
                    name = context.getResources().getString(R.string.no_name);
                }
                newRecordDialog.dismiss();

                if (isExisted) {
                    mDBHelper.delPeople(_ID);
                }
                save(name, phone, spinnerLatestClicked);
            }

        });

        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context).setView(view);
        newRecordDialog = builder.create();
        newRecordDialog.setCanceledOnTouchOutside(true);
        newRecordDialog.show();
    }

    private void save(String name, String phone, Integer latestClicked) {
        // mDBHelper.addPeople(name, phone, blockContent[latestClicked],
        // latestClicked);
        mDBHelper.addPeople(name, phone);
        update();
    }

    private void update() {
        cursor = mDBHelper.getWritableDatabase().query(BlacklistDBHelper.TB_NAME, null, null, null, null, null,
                BlacklistDBHelper.NAME + " ASC");
        adapter.changeCursor(cursor);
    }

    public class MyAdapter extends SimpleCursorAdapter {

        Context context;
        int layout;
        LayoutInflater mInflater;

        class ViewHolder {
            CheckedTextView checkedTextView = null;
        }

        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.context = context;
            this.layout = layout;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            ViewHolder holder = null;

            if (arg1 == null) {

                arg1 = mInflater.inflate(layout, null);
                holder = new ViewHolder();
                holder.checkedTextView = (CheckedTextView) arg1.findViewById(R.id.contact_checked_text_view);
                arg1.setTag(holder);

            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            if (checkedMap.get(arg0) != null && checkedMap.get(arg0) == true) {
                holder.checkedTextView.setChecked(true);
            } else {
                holder.checkedTextView.setChecked(false);
            }

            return super.getView(arg0, arg1, arg2);
        }
    }

}