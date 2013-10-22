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
import java.util.List;

import com.android.vcard.VCardConfig;

import edu.bupt.contacts.R;
import edu.bupt.contacts.vcard.VCardComposer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
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

    public String dataSet;
    private static final String[] ID_PROJECTION = new String[] { BaseColumns._ID };
    private static final Uri RAW_CONTACTS_URI_LIMIT_1 = RawContacts.CONTENT_URI
            .buildUpon().build();// .appendQueryParameter(ContactsContract.LIMIT_PARAM_KEY,
                                 // "1")

    public List<PersonInfo> contactList = null;
    public List<String> contactArrayList;
    public List<String> contactModArrayList;
    public List<String> TestArrayList;
    public String[] test = { "a", "b", "c", "d" };
    public List<String> sim1ArrayList;
    public List<String> contactNameArrayList;
    public List<String> contactLookupArrayList;
    public ListView listView;
    private final String[] LOOKUP_PROJECTION = new String[] {
            Contacts.LOOKUP_KEY, Contacts._ID };
    public int[] pos;

    // public List<Integer> pos;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setContentView(R.layout.multiselectexp);
        contactArrayList = new ArrayList<String>();
        contactModArrayList = new ArrayList<String>();
        sim1ArrayList = new ArrayList<String>();
        contactNameArrayList = new ArrayList<String>();
        contactLookupArrayList = new ArrayList<String>();
        setContactList();
        GetSimContact1("content://iccmsim/adn");

        TestArrayList = new ArrayList<String>();
        // getTestList(0);
        // SimDelete();
        // pos = new ArrayList<Integer>();

        getNameandNumber();
        pos = new int[contactArrayList.size()];
        for (int i = 0; i < contactArrayList.size(); i++) {
            pos[i] = 0;
        }
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                contactModArrayList));
        listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub

                // Log.i("Click",""+contactArrayList.get(arg2).toString());
                String xx = "You had click those items: \n";
                for (int i = 0; i < listView.getCount(); i++) {
                    // 注意这里使用的getItemAtPosition()方法
                    if (listView.isItemChecked(i)) {

                        if (0 == pos[i]) {
                            pos[i] = 1;
                        }
                        // else if(1 == pos[i]){
                        // pos[i] = 0;
                        // }

                        xx += listView.getItemAtPosition(i) + "\n";
                    } else {
                        if (1 == pos[i]) {
                            pos[i] = 0;
                        }
                    }
                }
                Log.i("Check", "" + xx);
                for (int i = 0; i < listView.getCount(); i++) {
                    Log.i("pos", "" + pos[i]);
                }

            }
        });

    }

    private void getNameandNumber() {
        contactModArrayList.clear();
        contactNameArrayList.clear();
        contactLookupArrayList.clear();
        for (int i = 0; i < contactArrayList.size(); i++) {

            String str = contactArrayList.get(i).toString();
            contactModArrayList.add(str.substring(str.indexOf("|") + 1,
                    str.length()));
            String[] a = str.split("\\|");
            Log.i("a[0]", a[0]);
            contactLookupArrayList.add(a[0]);
            String[] b = a[1].split("\\\n");
            Log.i("b[0]", b[0]);
            contactNameArrayList.add(b[0]);

        }
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

        // TODO Auto-generated method stub
        // 获取联系人处理实例

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
    }

    /**
     * 获取通讯录列表
     */
    private void setContactList() {
        contactArrayList.clear();
        String[] projection = { Phone.DISPLAY_NAME, Phone.NUMBER,
                Phone.PHOTO_ID, Phone.RAW_CONTACT_ID };
        Cursor cur = getContentResolver().query(Phone.CONTENT_URI, projection,
                null, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        // Cursor cur = getContentResolver().query(Contacts.CONTENT_URI,
        // LOOKUP_PROJECTION, Contacts.IN_VISIBLE_GROUP + "!=0", null, null);
        cur.moveToFirst();
        while (cur.getCount() > cur.getPosition()) {
            PersonInfo person = new PersonInfo(
                    ContactMultiSelectionActivity.this);
            List<String> phone = new ArrayList<String>();
            String id = cur.getString(cur.getColumnIndex(Phone.RAW_CONTACT_ID));
            String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
            String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
            String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));

            contactArrayList.add(id + "|" + name + "\n" + number);

            Log.i("contacts>>>", "id:" + id + "name:" + name + ";number:"
                    + number);
            cur.moveToNext();
        }
        cur.close();
    }

    // //从SIM卡中取号
    private void GetSimContact1(String add) {
        // 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
        try {

            Intent intent = new Intent();
            intent.setData(Uri.parse(add));
            Uri uri = intent.getData();
            // String[] projection= {Phone.DISPLAY_NAME, Phone.NUMBER,
            // Phone.PHOTO_ID,where_num};
            Cursor mCursor = getContentResolver().query(uri, null, null, null,
                    null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // ContactInfo sci = new ContactInfo();
                    // 取得联系人名字
                    int nameFieldColumnIndex = mCursor.getColumnIndex("name");
                    String name = mCursor.getString(nameFieldColumnIndex);
                    // 取得电话号码
                    int numberFieldColumnIndex = mCursor
                            .getColumnIndex("number");
                    String number = mCursor.getString(numberFieldColumnIndex);

                    sim1ArrayList.add(name + "\n" + number);

                }
                mCursor.close();
                for (int i = 0; i < sim1ArrayList.size(); i++) {
                    Log.i("sim1ArrayList", "" + sim1ArrayList.get(i));
                }

            }
        } catch (Exception e) {
            Log.i("eoe", e.toString());
        }
    }

    // 通讯社按中文拼音排序
    public class Mycomparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String c1 = (String) o1;
            String c2 = (String) o2;
            Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
            return cmp.compare(c1, c2);
        }

    }

}
