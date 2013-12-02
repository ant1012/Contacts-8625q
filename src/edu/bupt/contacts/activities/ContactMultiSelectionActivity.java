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

    private static final int FLAG_SELECT_CONTACT = 0x0;
    private static final int FLAG_PACKAGE_VCARD = 0x1;
    private static final int UPDATE_LIST = 0x10;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // for mms
        Intent i = getIntent();
        flagPackageVcard = i.getIntExtra("package_vcard", FLAG_SELECT_CONTACT);

        list = new ArrayList<Map<String, String>>();

        // Log.v(TAG, "before initData");
        // long tb = System.currentTimeMillis();
        initData(list);
        // Log.v(TAG, "after initData");
        // long ta = System.currentTimeMillis();
        // Log.w(TAG, "time cost for initData, " + (ta - tb));

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

    private void initData(ArrayList<Map<String, String>> list) {
        // contactLookupArrayList.clear();
        list.clear();

        // Thread getcontacts = new Thread(new GetContacts());

        if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
            // use old way
            Thread getcontacts = new Thread(new GetContacts());
            getcontacts.start();
        } else {
            Thread getcontacts = new Thread(new GetCachedContacts());
            getcontacts.start();
        }

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
                pickContacts();
            } else if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
                doShareCheckedContacts();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** zzz */
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

        intent.putExtra("ret", ret);
        setResult(RESULT_OK, intent);
        finish();

    }

    /** zzz */
    private void doShareCheckedContacts() {
        final int vcardType = VCardConfig.getVCardTypeFromString(getString(R.string.config_export_vcard_type));

        VCardComposer composer = null;
        composer = new VCardComposer(this, vcardType, true);

        // for file name
        StringBuilder sbName = new StringBuilder();

        // projection
        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME };

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
                Log.i(TAG, "list.get(i).get(\"name\") - " + list.get(i).get("name"));
                argsList.add(list.get(i).get("id"));
                first = false;
            }
        }
        args = argsList.toArray(new String[argsList.size()]);
        Log.i(TAG, "sbwhere - " + sbwhere.toString());
        Log.i(TAG, "args - " + args.length);

        // do query
        Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, projection, sbwhere.toString(), args, null);

        // init
        if (!composer.init(cursor)) {
            final String errorReason = composer.getErrorReason();
            Log.e(TAG, "initialization of vCard composer failed: " + errorReason);
            return;
        }

        final int total = composer.getCount();
        if (total == 0) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
            ;
            return;
        } else if (total > 1) {
            sbName.append(getString(R.string.vcard_share_filename_more, total));
        }
        Log.i(TAG, "composer.getCount() - " + total);

        // compose
        StringBuilder sb = new StringBuilder();
        while (!composer.isAfterLast()) {
            sb.append(composer.createOneEntry());
        }
        Log.i(TAG, sb.toString());
        File tempFile = null;
        try {
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

        intent.putExtra("ret", Uri.fromFile(tempFile));
        setResult(RESULT_OK, intent);
        finish();
    }

    private class GetContacts implements Runnable {
        @Override
        public void run() {
            Log.v(TAG, "before initData");
            long tb = System.currentTimeMillis();
            soManyLines = 0;

            // // for caching data
            // ContactsCacheDBHelper contactsCacheDBHelper = new
            // ContactsCacheDBHelper(ContactMultiSelectionActivity.this,
            // 1);
            // contactsCacheDBHelper.dropTable();

            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID };
            // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
            // + " = '1'";
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            Cursor phonecur = null;

            while (cursor.moveToNext()) {

                // get name
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // get id
                String contactId = cursor.getString(cursor
                        .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                // String strPhoneNumber = "";

                // if (flagPackageVcard == FLAG_PACKAGE_VCARD) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", contactId);
                map.put("name", name);
                map.put("number", null);
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

            Message msg1 = new Message();
            msg1.what = UPDATE_LIST;
            updateListHandler.sendMessage(msg1);

            Log.v(TAG, "after initData");
            long ta = System.currentTimeMillis();
            Log.w(TAG, "time cost for GetContacts, " + (ta - tb));
            Log.w(TAG, "soManyLines - " + soManyLines);
        }
    }

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

                if (!UpdateContactsCacheRunnable.isInitilized || UpdateContactsCacheRunnable.isRunning) {
                    throw new Exception("no cached data, try to get data in foreground");
                }

                readFromCache();

            } catch (Exception e) {
                Log.w(TAG, e.toString());

                new Thread(new UpdateContactsCacheRunnable(ContactMultiSelectionActivity.this,
                        new OnCacheUpdatedListener() {

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

        Message msg1 = new Message();
        msg1.what = UPDATE_LIST;
        updateListHandler.sendMessage(msg1);
    }

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

                    ContactMultiSelectAdapter.ViewHolder holder = (ContactMultiSelectAdapter.ViewHolder) arg1.getTag();
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
                    ContactMultiSelectAdapter.getIsSelected().put(arg2, holder.checkbox.isChecked());
                }
            });
        }
    }
}
