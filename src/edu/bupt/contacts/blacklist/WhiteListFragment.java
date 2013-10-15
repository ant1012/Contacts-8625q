package edu.bupt.contacts.blacklist;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.bupt.contacts.R;
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

public class WhiteListFragment extends Fragment {

    public static final String TAG = "franco--->WhiteListFragment";
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
    private static WhiteListDBHelper mDBHelper;
    private int _ID, blockId;
    private String name, phone;
    private HashMap<Integer, Boolean> checkedMap;

    public WhiteListFragment(Context context) {
        this.context = context;
        spinnerLatestClicked = blockId = -1;
        blockContent = context.getResources().getStringArray(
                R.array.block_content);
        checkedMap = new HashMap<Integer, Boolean>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.blacklist_whitelist, container, false);
        findViewAndSetListener();
        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
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
        mDBHelper = new WhiteListDBHelper(context, 1);
        cursor = mDBHelper.getWritableDatabase().query(
                WhiteListDBHelper.TB_NAME, null, null, null, null, null,
                WhiteListDBHelper.NAME + " ASC");
        String[] from = new String[] { WhiteListDBHelper.NAME,
                WhiteListDBHelper.Phone, WhiteListDBHelper.BlockContent };
        int[] to = new int[] { R.id.whitelist_item_text1,
                R.id.whitelist_item_text2, R.id.whitelist_item_text3 };
        adapter = new SimpleCursorAdapter(context,
                R.layout.blacklist_whitelist_item, cursor, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                Log.v(TAG, "arg3 = " + arg3);
                String sql = "select * from WhiteListFragment where _ID = "
                        + arg3;
                Cursor cursor = mDBHelper.getWritableDatabase().rawQuery(sql,
                        null);
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
                builder.setTitle(name)
                        .setItems(R.array.blacklist_item_click,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
//                                        switch (arg1) {
//
//                                        case 0:// 编辑
//                                            showNewRecordDialog(name, phone,
//                                                    blockId, true);
//                                            break;

//                                        case 1:// 删除
                                            mDBHelper.delPeople(_ID);
                                            update();
//                                            break;

//                                        case 2:// 发送短信
//                                            Uri uri = Uri.parse("smsto:"
//                                                    + phone);
//                                            Intent sms = new Intent(
//                                                    Intent.ACTION_SENDTO, uri);
//                                            startActivity(sms);
//                                            break;
//
//                                        case 3:// 呼叫
//                                            Intent call = new Intent(
//                                                    Intent.ACTION_DIAL);
//                                            call.setData(Uri.parse("tel:"
//                                                    + phone));
//                                            startActivity(call);
//                                            break;
//
//                                        case 4:// 清空列表
//                                            mDBHelper.delAllPeople();
//                                            update();
//                                            break;
//                                        }
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

        String[] projection = { ContactsContract.Contacts._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        contact = context.getContentResolver().query(Phone.CONTENT_URI,
                projection, // Which columns to return.
                null, // WHERE clause.
                null, // WHERE clause value substitution
                ContactsContract.Contacts._ID); // Sort order.
        String[] from = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
        int[] to = new int[] { R.id.contact_checked_text_view };
        SimpleCursorAdapter adapter = new MyAdapter(context,
                R.layout.blacklist_contact_item, contact, from, to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Log.d(TAG, "onItemClick");

                if (checkedMap.get(arg2) == null
                        || checkedMap.get(arg2) == false) {
                    Log.d(TAG, "true");
                    checkedMap.put(arg2, true);
                    CheckedTextView checkedTextView = (CheckedTextView) arg1
                            .findViewById(R.id.contact_checked_text_view);
                    checkedTextView.setChecked(true);
                } else {
                    Log.d(TAG, "false");
                    checkedMap.put(arg2, false);
                    CheckedTextView checkedTextView = (CheckedTextView) arg1
                            .findViewById(R.id.contact_checked_text_view);
                    checkedTextView.setChecked(false);
                }
            }
        });

        save.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                importContactDialog.dismiss();
                for (int position : checkedMap.keySet()) {
                    if (checkedMap.get(position)) {
                        contact.moveToPosition(position);
                        String name = contact.getString(PHONES_DISPLAY_NAME);
                        String phone = contact.getString(PHONES_NUMBER);

                        String strip1 = replacePattern(phone,
                                "^((\\+{0,1}86){0,1})", ""); // strip +86
                        String strip2 = replacePattern(strip1, "(\\-)", ""); // strip
                                                                             // -
                        String strip3 = replacePattern(strip2, "(\\ )", ""); // strip
                                                                             // space

                        phone = strip3;

                        save(name, phone, 0);
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

        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context)
                .setView(view);
        importContactDialog = builder.create();
        importContactDialog.setCanceledOnTouchOutside(true);
        importContactDialog.show();
    }

    private String replacePattern(String origin, String pattern, String replace) {
        Log.i(TAG, "origin - " + origin);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }

        m.appendTail(sb);
        Log.i(TAG, "sb.toString() - " + sb.toString());
        return sb.toString();
    }

    private void showNewRecordDialog(String name, String phone, int blockId,
            final boolean isExisted) {

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.block_content,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (blockId != -1) {
            spinner.setSelection(blockId);
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                spinnerLatestClicked = arg2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        cancle.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                newRecordDialog.dismiss();
            }

        });
        save.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String phone = phoneNumber.getText().toString();
                String name = contactName.getText().toString();
                Log.i("phone&name", name + ";" + phone);
                if ("".equals(phone)) {
                    Toast.makeText(context, R.string.no_phone_input,
                            Toast.LENGTH_SHORT).show();
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

        MyAlertDialog.Builder builder = new MyAlertDialog.Builder(context)
                .setView(view);
        newRecordDialog = builder.create();
        newRecordDialog.setCanceledOnTouchOutside(true);
        newRecordDialog.show();
    }

    private void save(String name, String phone, Integer latestClicked) {
//        mDBHelper.addPeople(name, phone, blockContent[latestClicked],
//                latestClicked);
        mDBHelper.addPeople(name, phone);
        update();
    }

    private void update() {
        cursor = mDBHelper.getWritableDatabase().query(
                WhiteListDBHelper.TB_NAME, null, null, null, null, null,
                WhiteListDBHelper.NAME + " ASC");
        adapter.changeCursor(cursor);
    }

    public class MyAdapter extends SimpleCursorAdapter {

        Context context;
        int layout;
        LayoutInflater mInflater;

        class ViewHolder {
            CheckedTextView checkedTextView = null;
        }

        public MyAdapter(Context context, int layout, Cursor c, String[] from,
                int[] to, int flags) {
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
                holder.checkedTextView = (CheckedTextView) arg1
                        .findViewById(R.id.contact_checked_text_view);
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