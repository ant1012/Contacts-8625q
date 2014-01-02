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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.vcard.VCardConfig;

import edu.bupt.contacts.R;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import edu.bupt.contacts.observer.ContactsCacheDBHelper;
import edu.bupt.contacts.observer.OnCacheUpdatedListener;
import edu.bupt.contacts.observer.UpdateContactsCacheRunnable;
import edu.bupt.contacts.vcard.VCardComposer;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 多选联系人的Activity，由短信应用调用(短信功能34)
 * 
 * */

/**
 * Displays a list of contacts (or phone numbers or postal addresses) for the
 * purposes of selecting one.
 */
/** zzz */
public class ContactMultiSelectionActivity extends ListActivity {
    private final String TAG = "ContactMultiSelectionActivity";

    public ListView listView;
    // public int[] pos;
    private ArrayList<Map<String, String>> list;
    private int flagPackageVcard = FLAG_SELECT_CONTACT; // when 0 returns
                                                        // arraylist
    // when 1 returns vcard file uri

    private ProgressDialog proDialog;
    private int soManyLines = 0;

    // zzz 调起此Activity的两种情况，可能需要返回号码，也可能需要返回打包后的vcard文件
    private static final int FLAG_SELECT_CONTACT = 0x0;
    private static final int FLAG_PACKAGE_VCARD = 0x1;
    private static final int UPDATE_LIST = 0x10;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // for mms
        // zzz 判断是需要返回号码，或者需要返回打包后的vcard文件
        Intent i = getIntent();
        flagPackageVcard = i.getIntExtra("package_vcard", FLAG_SELECT_CONTACT);

        list = new ArrayList<Map<String, String>>();

        // zzz 初始化联系人列表
        initData(list);

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

        // zzz 确保清空数据
        list.clear();

        // Thread getcontacts = new Thread(new GetContacts());

        if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
            // use old way
            // zzz 如果是打包联系人成vcard文件，则只需要给出联系人的列表，不需要列出每人的所有号码
            // 只需要读取Contacts表，复杂度为O(n)，这样不必考虑效率问题
            Thread getcontacts = new Thread(new GetContacts());
            getcontacts.start();
        } else {
            // zzz
            // 如果是选择联系人作为短信接收者，则需要给出所有联系人的每个电话号码的信息
            // 为了提高效率必须进行预读取的缓存，实际数据从缓存中读取
            Thread getcontacts = new Thread(new GetCachedContacts());
            getcontacts.start();
        }

        // zzz 如果缓存没有提前完成，会有很长的响应时间，因此用ProgressDialog表示正在读取列表的过程
        proDialog = ProgressDialog.show(ContactMultiSelectionActivity.this, getString(R.string.multi_select_loading),
                getString(R.string.multi_select_loading), true, true);

        // Uri uri = ContactsContract.Contacts.CONTENT_URI;
        // String[] projection = new String[] { ContactsContract.Contacts._ID,
        // ContactsContract.Contacts.DISPLAY_NAME,
        // ContactsContract.Contacts.PHOTO_ID };
        // // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
        // // + " = '1'";
        // String selection = null;
        // String[] selectionArgs = null;
        // String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
        // " COLLATE LOCALIZED ASC";
        // Cursor cursor = getContentResolver().query(uri, projection,
        // selection, selectionArgs, sortOrder);
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

        menu.add(0, 0, 0, R.string.cancel);
        menu.add(0, 1, 0, R.string.ok);
        menu.findItem(0).setShowAsAction(1);
        menu.findItem(1).setShowAsAction(2);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case 0:
            finish();
            break;

        case 1:
            if (flagPackageVcard == FLAG_SELECT_CONTACT) {
                // zzz 选取联系人号码
                pickContacts();
            } else if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
                // zzz 打包成vcard
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
    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 选打包成vcard
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
            tempFile = File.createTempFile("VCard-" + sbName.toString() + "-" + "" + "", ".vcf",
                    this.getExternalCacheDir());
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

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 在系统联系人数据库中异步读取联系人信息
     * 
     * */
    private class GetContacts implements Runnable {
        @Override
        public void run() {
            // zzz 计时
            Log.v(TAG, "before initData");
            long tb = System.currentTimeMillis();
            soManyLines = 0;

            // // for caching data
            // ContactsCacheDBHelper contactsCacheDBHelper = new
            // ContactsCacheDBHelper(ContactMultiSelectionActivity.this,
            // 1);
            // contactsCacheDBHelper.dropTable();

            // zzz 查询联系人信息
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID };
            // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
            // + " = '1'";

            String selection = null;
            String[] selectionArgs = null;

            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            // Cursor phonecur = null;

            while (cursor.moveToNext()) {

                // get name
                // 获取联系人姓名用于显示
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // get id
                // zzz 获取联系人的id用于选择之后的进一步查询
                String contactId = cursor.getString(cursor
                        .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                // String strPhoneNumber = "";

                // if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", contactId);
                map.put("name", name);
                map.put("number", null); // zzz 只需要打包vcard使用，不再需要列出联系人的所有号码
                list.add(map);
                soManyLines++;
                // } else {
                // phonecur = getContentResolver().query(
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
                //
                // // Log.i(TAG, "strPhoneNumber - " + strPhoneNumber);
                //
                // Map<String, String> map = new HashMap<String, String>();
                // map.put("id", contactId);
                // map.put("name", name);
                // map.put("number", strPhoneNumber);
                // list.add(map);
                //
                // // // cache data
                // // contactsCacheDBHelper.addLine(contactId, name,
                // strPhoneNumber);
                //
                // soManyLines++;
                // }
                // phonecur.close();
                // }
            }
            // if (phonecur != null)
            cursor.close();
            // contactsCacheDBHelper.close();

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

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 异步读取联系人信息
     * 
     * */
    private class GetCachedContacts implements Runnable {
        @Override
        public void run() {
            Log.v(TAG, "before GetCachedContacts");
            long tb = System.currentTimeMillis();
            soManyLines = 0;

            try {

                // if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
                // throw new Exception("need query name only");
                // }

                // zzz 首先判断缓存是否完成
                if (!UpdateContactsCacheRunnable.isInitilized || UpdateContactsCacheRunnable.isRunning) {
                    throw new Exception("no cached data, try to get data in foreground");
                }

                // zzz 如果缓存完成则直接在缓存的表中读取数据，提高了效率
                readFromCache();

            } catch (Exception e) {
                Log.w(TAG, e.toString());

                // zzz 如果没有缓存，则重新请求一次缓存，主要是为了将OnCacheUpdatedListener初始化
                new Thread(new UpdateContactsCacheRunnable(ContactMultiSelectionActivity.this,
                        new OnCacheUpdatedListener() {
                            // zzz 缓存完成时直接读取数据
                            @Override
                            public void OnCacheUpdated() {
                                readFromCache();
                            }
                        })).start();

                // use old way
                // Thread getcontacts = new Thread(new GetContacts());
                // getcontacts.start();

            }

            Log.v(TAG, "after GetCachedContacts");
            long ta = System.currentTimeMillis();
            Log.w(TAG, "time cost for GetCachedContacts, " + (ta - tb));
            Log.w(TAG, "soManyLines - " + soManyLines);
        }

    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 从缓存的表中读取联系人号码信息
     * 
     * */
    private void readFromCache() {
        ContactsCacheDBHelper contactsCacheDBHelper = new ContactsCacheDBHelper(ContactMultiSelectionActivity.this, 1);
        Cursor c = contactsCacheDBHelper.query();
        // if (c.getCount() == 0) {
        // throw new
        // Exception("no cached data, try to get data in foreground");
        // }
        while (c.moveToNext()) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", c.getString(0));
            map.put("name", c.getString(1));
            map.put("number", c.getString(2));
            list.add(map);
            soManyLines++;
        }

        c.close();
        contactsCacheDBHelper.close();

        // zzz 查询完成后取消掉’正在读取‘的对话框
        Message msg1 = new Message();
        msg1.what = UPDATE_LIST;
        updateListHandler.sendMessage(msg1);
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
