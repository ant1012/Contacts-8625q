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
 * Displays a list of contacts (or phone numbers or postal addresses) for the
 * purposes of selecting one.
 */
public class ContactMultiSelectionGroupActivity extends ListActivity {
    private final String TAG = "ContactMultiSelectionActivity";

    public ListView listView;
    // public int[] pos;
    private ArrayList<Map<String, String>> list;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // choose group
        // ArrayList<String> groupName = new ArrayList<String>();
        // ArrayList<String> groupId = new ArrayList<String>();

        /** zzz */
        // filter deleted groups
        String sel = ContactsContract.Groups.DELETED + "=?";
        String[] selArgs = new String[] { String.valueOf(0) };

        Cursor groupCursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,
                new String[] { ContactsContract.Groups.TITLE, ContactsContract.Groups._ID }, sel, selArgs, null);
        while (groupCursor.moveToNext()) {
            groupName.add(groupCursor.getString(0));
            groupId.add(groupCursor.getString(1));
        }

        final CharSequence[] items = groupName.toArray(new CharSequence[groupName.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.groupsLabel);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.v(TAG, "clicked group " + groupId.get(item));
                SelectedGrouId = groupId.get(item);
                setListView();
            }
        }).create().show();

    }

    private void setListView() {
        list = new ArrayList<Map<String, String>>();

        Log.v(TAG, "before initData");
        long tb = System.currentTimeMillis();
        initData(list);
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

    private void initData(ArrayList<Map<String, String>> list) {
        // contactLookupArrayList.clear();
        list.clear();
        Thread getcontacts = new Thread(new GetContacts());
        getcontacts.start();
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
                pickContacts();
            } else if (flagPackageVcard == 1) {
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

            /** zzz */
            // for group selection
            // query raw contact id using group id
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
            StringBuilder rawcontactIdSelection = new StringBuilder(RawContacts._ID).append(" IN ( 0");
            for (long id : rawcontacts) {
                rawcontactIdSelection.append(',').append(id);
            }
            rawcontactIdSelection.append(')');

            /** zzz */
            // filter deleted contacts
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
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // get id
                String contactId = cursor.getString(cursor
                        .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                String strPhoneNumber = "";

                phonecur = getContentResolver().query(
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[] { contactId }, null);
                // get number
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

            Message msg1 = new Message();
            msg1.what = UPDATE_LIST;
            updateListHandler.sendMessage(msg1);

            Log.v(TAG, "after initData");
            long ta = System.currentTimeMillis();
            Log.w(TAG, "time cost for GetContacts, " + (ta - tb));
            Log.w(TAG, "soManyLines - " + soManyLines);
        }
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
