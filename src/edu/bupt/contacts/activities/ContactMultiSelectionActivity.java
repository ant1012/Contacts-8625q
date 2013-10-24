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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.vcard.VCardConfig;

import edu.bupt.contacts.R;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import edu.bupt.contacts.vcard.VCardComposer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Displays a list of contacts (or phone numbers or postal addresses) for the
 * purposes of selecting one.
 */
public class ContactMultiSelectionActivity extends ListActivity {
    private final String TAG = "ContactMultiSelectionActivity";

    public ListView listView;
    // public int[] pos;
    private ArrayList<Map<String, String>> list;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        list = new ArrayList<Map<String, String>>();
        initData(list);
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {

                ContactMultiSelectAdapter.ViewHolder holder = (ContactMultiSelectAdapter.ViewHolder) arg1
                        .getTag();
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
                ContactMultiSelectAdapter.getIsSelected().put(arg2,
                        holder.checkbox.isChecked());
            }
        });
    }

    private void initData(ArrayList<Map<String, String>> list) {
        // contactLookupArrayList.clear();
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
        Cursor phonecur = null;

        while (cursor.moveToNext()) {

            // get name
            int nameFieldColumnIndex = cursor
                    .getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            // get id
            String contactId = cursor
                    .getString(cursor
                            .getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            String strPhoneNumber = "";

            phonecur = getContentResolver()
                    .query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { contactId }, null);
            // get number
            while (phonecur.moveToNext()) {
                strPhoneNumber = phonecur
                        .getString(phonecur
                                .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                // if (strPhoneNumber.length() > 4)
                // contactsList.add("18610011001" + "\n测试");
                // contactsList.add(strPhoneNumber+"\n"+name+"");
                Log.i(TAG, "strPhoneNumber - " + strPhoneNumber);

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
            break;

        case 1:
            pickContacts();
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
                Log.i(TAG,
                        "list.get(i).get(\"name\") - "
                                + list.get(i).get("name"));
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

}
