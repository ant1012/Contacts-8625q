/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.contacts.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.android.vcard.VCardConfig;

import edu.bupt.contacts.R;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import edu.bupt.contacts.vcard.VCardComposer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 分组选择联系人的选择界面，即先选择群组，再选择群组中的联系人的Activity，由短信应用调用(短信功能38)
 * 
 * */

/**
 * Displays a list of contacts (or phone numbers or postal addresses) for the
 * purposes of selecting one.
 */
public class ContactMultiSelectionGroupActivity extends ListActivity {
    private final String TAG = "ContactMultiSelectionActivity";

    public ListView listView;
    // public int[] pos;
    private ArrayList<Map<String, String>> list;

    // zzz 与ContactMultiSelectionActivity考虑类似的场景
    // 调起此Activity的两种情况，可能需要返回号码，也可能需要返回打包后的vcard文件
    // 当前暂时没有用到按分组打包vcard的功能
    private int flagPackageVcard = 0; // when 0 returns arraylist
                                      // when 1 returns vcard file uri

    private ArrayList<String> groupName = new ArrayList<String>();
    private ArrayList<String> groupId = new ArrayList<String>();
    private String SelectedGrouId = null;

    private ProgressDialog proDialog;
    private int soManyLines = 0;
    private final int UPDATE_LIST = 0x0;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // for mms
        // zzz 判断是需要返回号码，或者需要返回打包后的vcard文件
        Intent i = getIntent();
        flagPackageVcard = i.getIntExtra("package_vcard", 0);

        // AlertDialog alert = builder.create();

        // list = new ArrayList<Map<String, String>>();
        // initData(list);
        // mAdapter = new ContactMultiSelectAdapter(list, this);
        // listView = getListView();
        // listView.setAdapter(mAdapter);
        //
        // // pos = new int[list.size()];
        // // for (int i = 0; i < list.size(); i++) {
        // // pos[i] = 0;
        // // }
        //
        // listView.setItemsCanFocus(false);
        // listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // listView.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        // long arg3) {
        //
        // ContactMultiSelectAdapter.ViewHolder holder =
        // (ContactMultiSelectAdapter.ViewHolder) arg1.getTag();
        // holder.checkbox.toggle();
        // // for (int i = 0; i < listView.getCount(); i++) {
        // // if (listView.isItemChecked(i)) {
        // // if (0 == pos[i]) {
        // // pos[i] = 1;
        // // }
        // // } else {
        // // if (1 == pos[i]) {
        // // pos[i] = 0;
        // // }
        // // }
        // // }
        // ContactMultiSelectAdapter.getIsSelected().put(arg2,
        // holder.checkbox.isChecked());
        // }
        // });

        // setListView();

        /** zzz */
        // zzz 读取群组信息
        // filter deleted groups
        // zzz 在Groups中的群组项可能具有DELETED的属性，即可能已经被删除
        String sel = ContactsContract.Groups.DELETED + "=?";
        String[] selArgs = new String[] { String.valueOf(0) };

        Cursor groupCursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                new String[] { ContactsContract.Groups.TITLE, ContactsContract.Groups._ID }, sel, selArgs, null);
        while (groupCursor.moveToNext()) {
            groupName.add(groupCursor.getString(0));
            groupId.add(groupCursor.getString(1));
        }

        final CharSequence[] items = groupName.toArray(new CharSequence[groupName.size()]);
        // zzz 显示群组列表
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnCancelListener(new OnCancelListener(){
            @Override
            public void onCancel(DialogInterface arg0) {
                // zzz 群组列表的Dialog取消后需要结束Activity，避免bug
                ContactMultiSelectionGroupActivity.this.finish();
            }
        });
        builder.setTitle(R.string.groupsLabel);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.v(TAG, "clicked group " + groupId.get(item));
                // zzz 只支持单选，选择后更新Activity上的列表
                SelectedGrouId = groupId.get(item);
                setListView();
            }
        }).create().show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // choose group
        // ArrayList<String> groupName = new ArrayList<String>();
        // ArrayList<String> groupId = new ArrayList<String>();

    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 初始化联系人列表
     * 
     * */
    private void setListView() {
        list = new ArrayList<Map<String, String>>();

        // zzz 计时
        Log.v(TAG, "before initData");
        long tb = System.currentTimeMillis();
        // zzz 初始化数据
        initData(list);
        // zzz 计时
        Log.v(TAG, "after initData");
        long ta = System.currentTimeMillis();
        Log.w(TAG, "time cost for initData, " + (ta - tb));

        // mAdapter = new ContactMultiSelectAdapter(list, this);
        // listView = getListView();
        // listView.setAdapter(mAdapter);
        //
        // // pos = new int[list.size()];
        // // for (int i = 0; i < list.size(); i++) {
        // // pos[i] = 0;
        // // }
        //
        // listView.setItemsCanFocus(false);
        // listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // listView.setOnItemClickListener(new OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        // long arg3) {
        //
        // ContactMultiSelectAdapter.ViewHolder holder =
        // (ContactMultiSelectAdapter.ViewHolder) arg1.getTag();
        // holder.checkbox.toggle();
        // // for (int i = 0; i < listView.getCount(); i++) {
        // // if (listView.isItemChecked(i)) {
        // // if (0 == pos[i]) {
        // // pos[i] = 1;
        // // }
        // // } else {
        // // if (1 == pos[i]) {
        // // pos[i] = 0;
        // // }
        // // }
        // // }
        // ContactMultiSelectAdapter.getIsSelected().put(arg2,
        // holder.checkbox.isChecked());
        // }
        // });
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 初始化联系人列表
     * 
     * */
    private void initData(ArrayList<Map<String, String>> list) {
        // contactLookupArrayList.clear();
        list.clear();
        Thread getcontacts = new Thread(new GetContacts());
        getcontacts.start();
        // zzz 用ProgressDialog表示正在读取列表的过程
        proDialog = ProgressDialog.show(ContactMultiSelectionGroupActivity.this,
                getString(R.string.multi_select_loading), getString(R.string.multi_select_loading), true, true);
        // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
        // + " = '1'";
        // String selection = null;
        // String[] selectionArgs = null;
        //
        // /** zzz */
        // // for group selection
        // // query raw contact id using group id
        // Cursor rawcontactCursor =
        // getContentResolver().query(ContactsContract.Data.CONTENT_URI,
        // new String[] { ContactsContract.Data.RAW_CONTACT_ID },
        // ContactsContract.Data.MIMETYPE + " = ? AND " +
        // ContactsContract.Data.DATA1 + " = ?",
        // new String[] { GroupMembership.CONTENT_ITEM_TYPE, SelectedGrouId },
        // null);
        // long[] rawcontacts = new long[rawcontactCursor.getCount()];
        // for (int i = 0; i < rawcontactCursor.getCount(); i++) {
        // rawcontactCursor.moveToNext();
        // rawcontacts[i] = rawcontactCursor.getLong(0);
        // Log.v(TAG, "rawcontacts[i] - " + rawcontacts[i]);
        // }
        // rawcontactCursor.close();
        //
        // // query contact id using raw contact id
        // StringBuilder rawcontactIdSelection = new
        // StringBuilder(RawContacts._ID).append(" IN ( 0");
        // for (long id : rawcontacts) {
        // rawcontactIdSelection.append(',').append(id);
        // }
        // rawcontactIdSelection.append(')');
        // Cursor contactIdCursor =
        // getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
        // new String[] { ContactsContract.RawContacts.CONTACT_ID },
        // rawcontactIdSelection.toString(), null, null);
        // long[] contactsid = new long[contactIdCursor.getCount()];
        // for (int i = 0; i < contactIdCursor.getCount(); i++) {
        // contactIdCursor.moveToNext();
        // contactsid[i] = contactIdCursor.getLong(0);
        // Log.v(TAG, "contactsid[i] - " + contactsid[i]);
        // }
        // contactIdCursor.close();
        //
        // // Cursor contactIdCursor =
        // //
        // getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
        // // new String[] { ContactsContract.RawContacts.CONTACT_ID },
        // // inSelectionBff.toString(), null, null);
        // // Map m = new HashMap();
        // // while (contactIdCursor.moveToNext()) {
        // // m.put(contactIdCursor.getLong(0), 1);
        // // }
        // // contactIdCursor.close();
        // // long[] contacts = new long[m.size()];
        // // Iterator it = m.entrySet().iterator();
        // // int i = 0;
        // // while (it.hasNext()) {
        // // Map.Entry entry = (Map.Entry) it.next();
        // // long key = (Long) entry.getKey();
        // // contacts[i] = key;
        // // i++;
        // // }rawcontactIdSelection
        //
        // // query contact using contact id
        // StringBuilder contactIdSelection = new
        // StringBuilder(ContactsContract.Contacts._ID).append(" IN ( 0");
        // for (long id : contactsid) {
        // contactIdSelection.append(',').append(id);
        // }
        // contactIdSelection.append(')');
        //
        // Uri uri = ContactsContract.Contacts.CONTENT_URI;
        // String[] projection = new String[] { ContactsContract.Contacts._ID,
        // ContactsContract.Contacts.DISPLAY_NAME,
        // ContactsContract.Contacts.PHOTO_ID };
        // String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
        // " COLLATE LOCALIZED ASC";
        // // Cursor cursor = getContentResolver().query(uri, projection,
        // // selection, selectionArgs, sortOrder);
        // Cursor cursor = getContentResolver().query(uri, projection,
        // contactIdSelection.toString(), null, sortOrder);
        // Cursor phonecur = null;
        //
        // while (cursor.moveToNext()) {
        //
        // // get name
        // int nameFieldColumnIndex = cursor
        // .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
        // String name = cursor.getString(nameFieldColumnIndex);
        // // get id
        // String contactId =
        // cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
        // String strPhoneNumber = "";
        //
        // phonecur =
        // getContentResolver().query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        // null,
        // android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
        // " = ?",
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
        //
        // }
        // // if (phonecur != null)
        // cursor.close();
        //
        // // Message msg1 = new Message();
        // // msg1.what = UPDATE_LIST;
        // // updateListHandler.sendMessage(msg1);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // zzz 不设置取消按钮，只有全选和确定
        menu.add(0, 0, 0, R.string.menu_select_all);
        // menu.add(0, 1, 1, R.string.cancel);
        menu.add(0, 2, 2, R.string.ok);
        menu.findItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        // menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case 0:
            // zzz 全选
            for (int i = 0; i < list.size(); i++) {
                ContactMultiSelectAdapter.getIsSelected().put(i, true);
            }
            ((BaseAdapter) mAdapter).notifyDataSetChanged();
            break;

        case 1:
            finish();
            break;
        case 2:
            if (flagPackageVcard == 0) {
                // zzz 选取联系人号码
                pickContacts();
            } else if (flagPackageVcard == 1) {
                doShareCheckedContacts();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 选取联系人号码
     * 
     * */
    private void pickContacts() {
        Log.i(TAG, "pickContacts");

        ArrayList<String> ret = new ArrayList<String>();
        // // projection
        // String[] projection = new String[] { Contacts._ID,
        // Contacts.DISPLAY_NAME };
        //
        // // selection
        // StringBuilder sbwhere = new StringBuilder();
        // sbwhere.append("_id = ? ");
        //
        // // selectionArgs
        // String[] args = new String[] {};
        // List<String> argsList = new ArrayList<String>();
        // boolean first = true;
        for (int i = 0; i < list.size(); i++) {
            // zzz 选中的项添加到返回的ArrayList中，ContactMultiSelectAdapter.getIsSelected()方法提供了被选中的项的列表
            if (ContactMultiSelectAdapter.getIsSelected().get(i) == true) {
                // if (!first) {
                // sbwhere.append(" or _id = ? ");
                // }
                Log.i(TAG, "list.get(i).get(\"name\") - " + list.get(i).get("name"));
                // argsList.add(list.get(i).get("id"));
                // first = false;
                ret.add(list.get(i).get("number"));
            }
        }
        // args = argsList.toArray(new String[argsList.size()]);
        // Log.i(TAG, "sbwhere - " + sbwhere.toString());
        // Log.i(TAG, "args - " + args.length);

        // do query
        // Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI,
        // projection, sbwhere.toString(), args, null);
        // cursor.close();

        Intent intent = new Intent();

        // zzz 返回
        intent.putExtra("ret", ret);
        setResult(RESULT_OK, intent);
        finish();

    }

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 选打包成vcard，因为暂时没有俺群组打包的场景，故此方法不会被调用
     * 
     * */
    private void doShareCheckedContacts() {
        final int vcardType = VCardConfig.getVCardTypeFromString(getString(R.string.config_export_vcard_type));

        VCardComposer composer = null;
        // zzz 利用vcad包提供的打包方法生成vcard文件
        composer = new VCardComposer(this, vcardType, true);

        // for file name
        // zzz 用选择的联系人姓名生成vcard文件的文件名
        StringBuilder sbName = new StringBuilder();

        // projection
        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME };

        // selection
        // zzz 打包文件用到的VCardComposer类需要用cursor来初始化，因此首先要根据id查询出要打包的联系人的cursor
        // 用sbwhere生成用于查询的selection参数
        StringBuilder sbwhere = new StringBuilder();
        sbwhere.append("_id = ? ");

        // selectionArgs
        String[] args = new String[] {};
        List<String> argsList = new ArrayList<String>();
        boolean first = true;
        for (int i = 0; i < list.size(); i++) {
            if (ContactMultiSelectAdapter.getIsSelected().get(i) == true) {
                if (!first) {
                    // zzz 附加sql中的‘or’语句
                    sbwhere.append(" or _id = ? ");
                } else {
                    // zzz 如果是第一个，则取出联系人的名字，以生成文件名
                    sbName.append(list.get(i).get("name"));
                    Log.i(TAG, "sbName - " + sbName.toString());
                }
                Log.i(TAG, "list.get(i).get(\"name\") - " + list.get(i).get("name"));
                argsList.add(list.get(i).get("id"));
                first = false;
            }
        }
        // zzz 生成selectionArgs用于查询
        args = argsList.toArray(new String[argsList.size()]);
        Log.i(TAG, "sbwhere - " + sbwhere.toString());
        Log.i(TAG, "args - " + args.length);

        // do query
        Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, projection, sbwhere.toString(), args, null);

        // init
        // zzz 用查询得到的cursor初始化composer
        if (!composer.init(cursor)) {
            final String errorReason = composer.getErrorReason();
            Log.e(TAG, "initialization of vCard composer failed: " + errorReason);
            return;
        }

        final int total = composer.getCount();
        if (total == 0) { // zzz 没有选择
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
            ;
            return;
        } else if (total > 1) { // zzz 不止一个联系人信息被打包，文件名附加‘等xxx人’
            sbName.append(getString(R.string.vcard_share_filename_more, total));
        }
        Log.i(TAG, "composer.getCount() - " + total);

        // compose
        // zzz 打包，将vcard文件输出成String
        StringBuilder sb = new StringBuilder();
        while (!composer.isAfterLast()) {
            sb.append(composer.createOneEntry());
        }
        Log.i(TAG, sb.toString());
        File tempFile = null;
        try {
            // zzz 将生成的String保存到临时文件
            tempFile = File.createTempFile("VCard-" + sbName.toString(), ".vcf", this.getExternalCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] bytes = sb.toString().getBytes();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // send
        // Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/x-vcard");
        // // i.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
        // // uris);
        // i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
        // startActivity(Intent.createChooser(i, getText(R.string.menu_share)));

        // return
        Intent intent = new Intent();

        // zzz 返回临时文件的URI
        intent.putExtra("ret", Uri.fromFile(tempFile));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 异步读取联系人信息
     * 
     * */
    private class GetContacts implements Runnable {
        @Override
        public void run() {
            Log.v(TAG, "before initData");
            long tb = System.currentTimeMillis();
            soManyLines = 0;

            /** zzz */
            // for group selection
            // query raw contact id using group id
            // zzz 按群组查询RAW_CONTACT_ID
            Cursor rawcontactCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    new String[] { ContactsContract.Data.RAW_CONTACT_ID },
                    ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.DATA1 + " = ?",
                    new String[] { GroupMembership.CONTENT_ITEM_TYPE, SelectedGrouId }, null);
            long[] rawcontacts = new long[rawcontactCursor.getCount()];
            for (int i = 0; i < rawcontactCursor.getCount(); i++) {
                rawcontactCursor.moveToNext();
                rawcontacts[i] = rawcontactCursor.getLong(0);
                Log.v(TAG, "rawcontacts[i] - " + rawcontacts[i]);
            }
            rawcontactCursor.close();

            // query contact id using raw contact id
            // zzz 按RAW_CONTACT_ID查询CONTACT_ID
            StringBuilder rawcontactIdSelection = new StringBuilder(RawContacts._ID).append(" IN ( 0");
            for (long id : rawcontacts) {
                rawcontactIdSelection.append(',').append(id);
            }
            rawcontactIdSelection.append(')');

            /** zzz */
            // filter deleted contacts
            // zzz 需要过滤掉易经被删除的联系人
            rawcontactIdSelection.append(" AND " + RawContacts.DELETED + " = 0");

            Cursor contactIdCursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    new String[] { ContactsContract.RawContacts.CONTACT_ID }, rawcontactIdSelection.toString(), null,
                    null);
            long[] contactsid = new long[contactIdCursor.getCount()];
            for (int i = 0; i < contactIdCursor.getCount(); i++) {
                contactIdCursor.moveToNext();
                contactsid[i] = contactIdCursor.getLong(0);
                Log.v(TAG, "contactsid[i] - " + contactsid[i]);
            }
            contactIdCursor.close();

            // Cursor contactIdCursor =
            // getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
            // new String[] { ContactsContract.RawContacts.CONTACT_ID },
            // inSelectionBff.toString(), null, null);
            // Map m = new HashMap();
            // while (contactIdCursor.moveToNext()) {
            // m.put(contactIdCursor.getLong(0), 1);
            // }
            // contactIdCursor.close();
            // long[] contacts = new long[m.size()];
            // Iterator it = m.entrySet().iterator();
            // int i = 0;
            // while (it.hasNext()) {
            // Map.Entry entry = (Map.Entry) it.next();
            // long key = (Long) entry.getKey();
            // contacts[i] = key;
            // i++;
            // }rawcontactIdSelection

            // query contact using contact id
            // zzz 用CONTACT_ID查询联系人的其他信息
            StringBuilder contactIdSelection = new StringBuilder(ContactsContract.Contacts._ID).append(" IN ( 0");
            for (long id : contactsid) {
                contactIdSelection.append(',').append(id);
            }
            contactIdSelection.append(')');

            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID };
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            // Cursor cursor = getContentResolver().query(uri, projection,
            // selection, selectionArgs, sortOrder);
            Cursor cursor = getContentResolver().query(uri, projection, contactIdSelection.toString(), null, sortOrder);
            Cursor phonecur = null;

            while (cursor.moveToNext()) {

                // get name
                // zzz 获取联系人姓名
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // get id
                // zzz 获取联系人id，用于确定选择项
                String contactId = cursor.getString(cursor
                        .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                String strPhoneNumber = "";

                phonecur = getContentResolver().query(
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[] { contactId }, null);
                // get number
                // zzz 获取联系人号码，每个联系人可能有多个号码，需要把每个号码单独保存到ArrayList中的一项
                while (phonecur.moveToNext()) {
                    strPhoneNumber = phonecur.getString(phonecur
                            .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // if (strPhoneNumber.length() > 4)
                    // contactsList.add("18610011001" + "\n测试");
                    // contactsList.add(strPhoneNumber+"\n"+name+"");

                    // Log.i(TAG, "strPhoneNumber - " + strPhoneNumber);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", contactId);
                    map.put("name", name);
                    map.put("number", strPhoneNumber);
                    list.add(map);
                }
                phonecur.close();

            }
            // if (phonecur != null)
            cursor.close();

            // Message msg1 = new Message();
            // msg1.what = UPDATE_LIST;
            // updateListHandler.sendMessage(msg1);

            // zzz 查询完成后取消掉’正在读取‘的对话框
            Message msg1 = new Message();
            msg1.what = UPDATE_LIST;
            updateListHandler.sendMessage(msg1);

            // zzz 计时
            Log.v(TAG, "after initData");
            long ta = System.currentTimeMillis();
            Log.w(TAG, "time cost for GetContacts, " + (ta - tb));
            Log.w(TAG, "soManyLines - " + soManyLines);
        }
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 取消’正在读取‘的对话框
     * 
     * */
    private Handler updateListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case UPDATE_LIST:
                if (proDialog != null) {
                    proDialog.dismiss();
                }
                updateList();
            }
        }
    };

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 读取完成后更新列表显示
     * 
     * */
    private void updateList() {
        if (list != null) {
            mAdapter = new ContactMultiSelectAdapter(list, this);
            listView = getListView();
            listView.setAdapter(mAdapter);

            // pos = new int[list.size()];
            // for (int i = 0; i < list.size(); i++) {
            // pos[i] = 0;
            // }

            listView.setItemsCanFocus(false);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    // zzz 用ViewHolder控制列表项
                    ContactMultiSelectAdapter.ViewHolder holder = (ContactMultiSelectAdapter.ViewHolder) arg1.getTag();
                    // zzz 点击后设为选中
                    holder.checkbox.toggle();
                    // for (int i = 0; i < listView.getCount(); i++) {
                    // if (listView.isItemChecked(i)) {
                    // if (0 == pos[i]) {
                    // pos[i] = 1;
                    // }
                    // } else {
                    // if (1 == pos[i]) {
                    // pos[i] = 0;
                    // }
                    // }
                    // }
                    // zzz 记录列表项的选中状态
                    ContactMultiSelectAdapter.getIsSelected().put(arg2, holder.checkbox.isChecked());
                }
            });
        }
    }
}
