package edu.bupt.contacts.activities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.vcard.VCardConfig;

import edu.bupt.contacts.R;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import edu.bupt.contacts.model.AccountTypeWithDataSet;
import edu.bupt.contacts.vcard.ExportRequest;
import edu.bupt.contacts.vcard.VCardComposer;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

public class MultiSelectExport extends ListActivity {
    private final String TAG = "MultiSelectExport";

    // public String dataSet;
    // private static final String[] ID_PROJECTION = new String[] {
    // BaseColumns._ID };
    // private static final Uri RAW_CONTACTS_URI_LIMIT_1 =
    // RawContacts.CONTENT_URI
    // .buildUpon().build();//
    // .appendQueryParameter(ContactsContract.LIMIT_PARAM_KEY,
    // // "1")

    // public List<PersonInfo> contactList = null;
    // public List<String> contactArrayList;
    // public List<String> contactModArrayList;
    // public List<String> TestArrayList;
    // public String[] test = { "a", "b", "c", "d" };
    // public List<String> sim1ArrayList;
    // public List<String> contactNameArrayList;
    // public List<String> contactLookupArrayList;
    public ListView listView;
    // private final String[] LOOKUP_PROJECTION = new String[] {
    // Contacts.LOOKUP_KEY, Contacts._ID };
    public int[] pos;
    private ArrayList<Map<String, String>> list;

    // public List<Integer> pos;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // ddd

        // setContentView(R.layout.multiselectexport_activity);
        // contactArrayList = new ArrayList<String>();
        // contactModArrayList = new ArrayList<String>();
        // sim1ArrayList = new ArrayList<String>();
        // contactNameArrayList = new ArrayList<String>();
        // contactLookupArrayList = new ArrayList<String>();
        // setContactList();
        // GetSimContact1("content://iccmsim/adn");

        // TestArrayList = new ArrayList<String>();
        // getTestList(0);
        // SimDelete();
        // pos = new ArrayList<Integer>();

        // getNameandNumber();
        // pos = new int[contactArrayList.size()];
        // for (int i = 0; i < contactArrayList.size(); i++) {
        // pos[i] = 0;
        // }
        // setListAdapter(new ArrayAdapter<String>(this,
        // android.R.layout.simple_list_item_multiple_choice,
        // contactModArrayList));


        list = new ArrayList<Map<String, String>>();
        initData(list);
        mAdapter = new ContactMultiSelectAdapter(list, this);
        listView = getListView();
        listView.setAdapter(mAdapter);

//        pos = new int[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            pos[i] = 0;
//        }



        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub

                ContactMultiSelectAdapter.ViewHolder holder = (ContactMultiSelectAdapter.ViewHolder) arg1
                        .getTag();
                holder.checkbox.toggle();
                // Log.i("Click",""+contactArrayList.get(arg2).toString());
                // String xx = "You had click those items: \n";
                // for (int i = 0; i < listView.getCount(); i++) {
                // // 注意这里使用的getItemAtPosition()方法
                // if (listView.isItemChecked(i)) {
                //
                // if (0 == pos[i]) {
                // pos[i] = 1;
                // }
                // // else if(1 == pos[i]){
                // // pos[i] = 0;
                // // }
                //
                // // xx += listView.getItemAtPosition(i) + "\n";
                // } else {
                // if (1 == pos[i]) {
                // pos[i] = 0;
                // }
                // }
                // }
                // Log.i("Check", "" + xx);
                // for (int i = 0; i < listView.getCount(); i++) {
                // Log.i("pos", "" + pos[i]);
                // }

                ContactMultiSelectAdapter.getIsSelected().put(arg2,
                        holder.checkbox.isChecked());
            }
        });

        Intent intent = getIntent();
        int positionSelected = intent.getIntExtra("selected", -1);
        if (positionSelected > 0) {
            HashMap<Integer, Boolean> isSelected = ContactMultiSelectAdapter.getIsSelected();
            isSelected.put(positionSelected, true);
            ContactMultiSelectAdapter.setIsSelected(isSelected);
        }
    }

    private void initData(ArrayList<Map<String, String>> list) {
        Log.d(TAG, "initData");
        list.clear();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID };
        // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
        // + " = '1'";
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        Cursor cursor = getContentResolver().query(uri, projection, selection,
                selectionArgs, sortOrder);
        // Cursor phonecur = null;

        Log.d(TAG, "corsor - " + cursor.getCount());

        while (cursor.moveToNext()) {
            // get name
            int nameFieldColumnIndex = cursor
                    .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            // get id
            String contactId = cursor
                    .getString(cursor
                            .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            // phonecur = getContentResolver().query(
            // android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            // null,
            // android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            // + " = " + contactId, null, null);
            // // get number
            // while (phonecur.moveToNext()) {
            // String strPhoneNumber = phonecur
            // .getString(phonecur
            // .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
            // if (strPhoneNumber.length() > 4)
            // contactsList.add("18610011001" + "\n测试");
            // // contactsList.add(strPhoneNumber+"\n"+name+"");
            //
            // }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", contactId);
            map.put("name", name);
            map.put("number", "");

            list.add(map);
        }
        // if (phonecur != null)
        cursor.close();

        // Message msg1 = new Message();
        // msg1.what = UPDATE_LIST;
        // updateListHandler.sendMessage(msg1);

        Log.d(TAG, "initData finished");
    }

    // private void getNameandNumber() {
    // contactModArrayList.clear();
    // contactNameArrayList.clear();
    // contactLookupArrayList.clear();
    // for (int i = 0; i < contactArrayList.size(); i++) {
    //
    // String str = contactArrayList.get(i).toString();
    // contactModArrayList.add(str.substring(str.indexOf("|") + 1,
    // str.length()));
    // String[] a = str.split("\\|");
    // Log.i("a[0]", a[0]);
    // contactLookupArrayList.add(a[0]);
    // String[] b = a[1].split("\\\n");
    // Log.i("b[0]", b[0]);
    // contactNameArrayList.add(b[0]);
    //
    // }
    // }

    /** zzz */
    private void doShareCheckedContacts() {
        // TODO move the query into a loader and do this in a background
        // thread//Contacts.CONTENT_URI

        // final Cursor cursor =
        // getContentResolver().query(Contacts.CONTENT_URI,
        // LOOKUP_PROJECTION, null, null,
        // null);
        //
        // Log.i("Contacts.CONTENT_URI", Contacts._ID + ";" +
        // Contacts.CONTENT_URI);
        // if (cursor != null) {
        // try {
        // if (!cursor.moveToFirst()) {
        // Toast.makeText(getApplicationContext(),
        // R.string.share_error, Toast.LENGTH_SHORT).show();
        // return;
        // }
        //
        // StringBuilder uriListBuilder = new StringBuilder();
        // // int index = 0;
        // do {
        //
        // // if (index != 0)
        // for (int i = 0; i < contactArrayList.size(); i++) {
        // if (0 != pos[i]) {
        // if (cursor.getString(1).equals(
        // contactLookupArrayList.get(i))) {
        // Log.i("cursor",
        // "no." + i + ";"
        // + contactNameArrayList.get(i)
        // + "\n" + cursor.getString(1)
        // + "\n" + cursor.getString(0));
        // uriListBuilder.append(':');
        // uriListBuilder.append(cursor.getString(0));
        // }
        //
        // }
        // }
        //
        // // index++;
        //
        // } while (cursor.moveToNext());
        // // Log.i("index","index = "+index);
        // Uri uri = Uri.withAppendedPath(
        // Contacts.CONTENT_MULTI_VCARD_URI,
        // Uri.encode(uriListBuilder.toString()));
        // Log.i("share", "\n" + uri);
        // final Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.setType(Contacts.CONTENT_VCARD_TYPE);
        //
        // intent.putExtra(Intent.EXTRA_STREAM, uri);
        // startActivity(intent);
        //
        // } finally {
        // cursor.close();
        // }
        // }

        final int vcardType = VCardConfig
                .getVCardTypeFromString(getString(R.string.config_export_vcard_type));

        VCardComposer composer = null;
        composer = new VCardComposer(this, vcardType, true);

        // final Uri contentUriForRawContactsEntity =
        // RawContactsEntity.CONTENT_URI
        // .buildUpon()
        // .appendQueryParameter(RawContactsEntity.FOR_EXPORT_ONLY, "1")
        // .build();
        //
        // if (!composer.init(Contacts.CONTENT_URI, new String[] { Contacts._ID
        // },
        // null, null, null, contentUriForRawContactsEntity)) {
        // final String errorReason = composer.getErrorReason();
        // Log.e(TAG, "initialization of vCard composer failed: "
        // + errorReason);
        // return;
        // }

        // for file name
        StringBuilder sbName = new StringBuilder();

        // projection
        String[] projection = new String[] { Contacts._ID,
                Contacts.DISPLAY_NAME };

        // selection
        StringBuilder sbwhere = new StringBuilder();
        sbwhere.append("_id = ? ");

        // selectionArgs
        String[] args = new String[] {};
        List<String> argsList = new ArrayList<String>();
        boolean first = true;
        for (int i = 0; i < list.size(); i++) {
            if (ContactMultiSelectAdapter.getIsSelected().get(i) == true) {
                if (!first) {
                    sbwhere.append(" or _id = ? ");
                } else {
                    sbName.append(list.get(i).get("name"));
                    Log.i(TAG, "sbName - " + sbName.toString());
                }
                Log.i(TAG,
                        "list.get(i).get(\"name\") - "
                                + list.get(i).get("name"));
                argsList.add(list.get(i).get("id"));
                first = false;
            }
        }
        args = argsList.toArray(new String[argsList.size()]);
        Log.i(TAG, "sbwhere - " + sbwhere.toString());
        Log.i(TAG, "args - " + args.length);

        // do query
        Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI,
                projection, sbwhere.toString(), args, null);

        // init
        if (!composer.init(cursor)) {
            final String errorReason = composer.getErrorReason();
            Log.e(TAG, "initialization of vCard composer failed: "
                    + errorReason);
            return;
        }

        final int total = composer.getCount();
        if (total == 0) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT)
                    .show();
            ;
            return;
        } else if (total > 1) {
            sbName.append(getString(R.string.vcard_share_filename_more, total));
        }
        Log.i(TAG, "composer.getCount() - " + total);

        // compose
        // final OutputStream outputStream;
        // outputStream = getContentResolver().openOutputStream(uri);
        // Writer writer = new BufferedWriter(new
        // OutputStreamWriter(outputStream));
        StringBuilder sb = new StringBuilder();
        while (!composer.isAfterLast()) {
            // if (isCancelled()) {
            // Log.i(LOG_TAG,
            // "Export request is cancelled during composing vCard");
            // return;
            // }
            // try {
            // writer.write(composer.createOneEntry());
            sb.append(composer.createOneEntry());
            // } catch (IOException e) {
            // final String errorReason = composer.getErrorReason();
            // Log.e(TAG, "Failed to read a contact: " + errorReason);
            // // final String translatedErrorReason =
            // // translateComposerError(errorReason);
            // // final String title =
            // //
            // mService.getString(R.string.fail_reason_error_occurred_during_export,
            // // translatedErrorReason);
            // // doFinishNotification(title, null);
            // return;
            // }

            // vCard export is quite fast (compared to import), and frequent
            // notifications
            // bother notification bar too much.
            // if (current % 100 == 1) {
            // doProgressNotification(uri, total, current);
            // }
            // current++;
        }
        Log.i(TAG, sb.toString());
        File tempFile = null;
        try {
            tempFile = File.createTempFile("VCard-" + sbName.toString(), ".vcf",
                    this.getExternalCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] bytes = sb.toString().getBytes();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // send
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/x-vcard");
        // i.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
        // uris);
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
        startActivity(Intent.createChooser(i, getText(R.string.menu_share)));
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 0, 0, R.string.menu_share_as_text);
        menu.add(0, 1, 0, R.string.menu_share_as_vcard);
        menu.findItem(0).setShowAsAction(1);
        menu.findItem(1).setShowAsAction(2);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO Auto-generated method stub
        // 获取联系人处理实例

        switch (item.getItemId()) {
        case 0:
            break;

        case 1:
            doShareCheckedContacts();
            break;

        }

        return super.onOptionsItemSelected(item);

    }

    // /**
    // * 获取通讯录列表
    // */
    // private void setContactList() {
    // contactArrayList.clear();
    // String[] projection = { Phone.DISPLAY_NAME, Phone.NUMBER,
    // Phone.PHOTO_ID, Phone.RAW_CONTACT_ID };
    // Cursor cur = getContentResolver().query(Phone.CONTENT_URI, projection,
    // null, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    // // Cursor cur = getContentResolver().query(Contacts.CONTENT_URI,
    // // LOOKUP_PROJECTION, Contacts.IN_VISIBLE_GROUP + "!=0", null, null);
    // cur.moveToFirst();
    // while (cur.getCount() > cur.getPosition()) {
    // PersonInfo person = new PersonInfo(MultiSelectExport.this);
    // List<String> phone = new ArrayList<String>();
    // String id = cur.getString(cur.getColumnIndex(Phone.RAW_CONTACT_ID));
    // String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
    // String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
    // String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
    //
    // contactArrayList.add(id + "|" + name + "\n" + number);
    //
    // Log.i("contacts>>>", "id:" + id + "name:" + name + ";number:"
    // + number);
    // cur.moveToNext();
    // }
    // cur.close();
    // }

    // //从SIM卡中取号
    // private void GetSimContact1(String add) {
    // // 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
    // try {
    //
    // Intent intent = new Intent();
    // intent.setData(Uri.parse(add));
    // Uri uri = intent.getData();
    // // String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER,
    // // Phone.PHOTO_ID,where_num};
    // Cursor mCursor = getContentResolver().query(uri, null, null, null,
    // null);
    // if (mCursor != null) {
    // while (mCursor.moveToNext()) {
    // // ContactInfo sci = new ContactInfo();
    // // 取得联系人名字
    // int nameFieldColumnIndex = mCursor.getColumnIndex("name");
    // String name = mCursor.getString(nameFieldColumnIndex);
    // // 取得电话号码
    // int numberFieldColumnIndex = mCursor
    // .getColumnIndex("number");
    // String number = mCursor.getString(numberFieldColumnIndex);
    //
    // sim1ArrayList.add(name + "\n" + number);
    //
    // }
    // mCursor.close();
    // for (int i = 0; i < sim1ArrayList.size(); i++) {
    // Log.i("sim1ArrayList", "" + sim1ArrayList.get(i));
    // }
    //
    // }
    // } catch (Exception e) {
    // Log.i("eoe", e.toString());
    // }
    // }
    //
    // // 通讯社按中文拼音排序
    // public class Mycomparator implements Comparator {
    // public int compare(Object o1, Object o2) {
    // String c1 = (String) o1;
    // String c2 = (String) o2;
    // Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
    // return cmp.compare(c1, c2);
    // }
    //
    // }
    //
    // private static final String[] GENRES = new String[] {
    //
    // "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary",
    // "Drama",
    //
    // "Foreign", "History", "Independent", "Romance", "Sci-Fi",
    // "Television", "Thriller"
    //
    // };
    //
    // private List<String> getData() {
    //
    // List<String> data = new ArrayList<String>();
    //
    // data.add("测试数据1");
    //
    // data.add("测试数据2");
    //
    // data.add("测试数据3");
    //
    // data.add("测试数据4");
    //
    // return data;
    //
    // }

}
