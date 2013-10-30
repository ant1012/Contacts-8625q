package edu.bupt.contacts.vcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.ContactLoader;
import edu.bupt.contacts.list.ContactMultiSelectAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PreviewVCardActivity extends ListActivity {

    private final String TAG = "PreviewVCardActivity";

    public ListView listView;
    // private ArrayList<Map<String, String>> list;
    private ArrayList listArray;
    String importedVCard = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        if (!i.hasExtra("importedVCard")) {
            Log.w(TAG, "importedVCard == null");
            return;
        }

        importedVCard = i.getStringExtra("importedVCard");
        Log.i(TAG, "importedVCard - " + importedVCard);

        // list = new ArrayList<Map<String, String>>();
        // initData(list);
        listArray = new ArrayList<String>();
        initData();
        // mAdapter = new ContactMultiSelectAdapter(list, this);
        // listView = getListView();
        // listView.setAdapter(mAdapter);

        Log.i(TAG, listArray.toString());
        int size = listArray.size();
        String[] arr = (String[]) listArray.toArray(new String[size]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);

        this.setListAdapter(arrayAdapter); // ListActivity本身含有ListView

    }

    private void initData() {
        listArray.clear();
        // Uri uri = ContactsContract.Contacts.CONTENT_URI;
        // String[] projection = new String[] { ContactsContract.Contacts._ID,
        // ContactsContract.Contacts.DISPLAY_NAME };
        // // String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
        // // + " = '1'";
        // String selection = "_id >= ?";
        // String[] selectionArgs = new String[] {
        // String.valueOf(importedVCardId) };
        // String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
        // " COLLATE LOCALIZED ASC";
        // Cursor cursor = getContentResolver().query(uri, projection,
        // selection, selectionArgs, sortOrder);
        // Log.i(TAG, "cursor.getCount - " + cursor.getCount());

        Cursor cursor = this.getContentResolver().query(Uri.parse(importedVCard), null, null, null, null);
        Log.i(TAG, "cursor.getCount - " + cursor.getCount());

        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<String, String>();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            // name
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            if (name != null) {
                // map.put("name", name);
                listArray.add(name);
                Log.d(TAG, "name - " + name);
            }

            // email
            Cursor emailCur = this.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                String emailType = emailCur.getString(emailCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                // map.put("email", email);

                listArray.add(email);
            }
            emailCur.close();

            // addr
            String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] addrWhereParams = new String[] { id,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
            Cursor addrCur = this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, addrWhere,
                    addrWhereParams, null);
            while (addrCur.moveToNext()) {
                String poBox = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                String street = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String state = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String postalCode = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String type = addrCur.getString(addrCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                StringBuilder sb_location = new StringBuilder();

                if (country != null) {
                    sb_location.append(country);
                }

                if (state != null) {
                    sb_location.append(state);
                }

                if (city != null) {
                    sb_location.append(city);
                }

                if (street != null) {
                    sb_location.append(street);
                }

                // if(!((String)poBox).equals("")){
                // sb_location.append(poBox);
                // }
                //
                if (!sb_location.toString().equals("")) {

                    // map.put("addr", sb_location.toString());

                    listArray.add(sb_location.toString());
                }
            }
            addrCur.close();

            // im
            String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] imWhereParams = new String[] { id, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE };
            Cursor imCur = this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, imWhere,
                    imWhereParams, null);
            if (imCur.moveToFirst()) {
                String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                String imType;
                imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                // map.put("im", imName);
                listArray.add(imName);
            }
            imCur.close();

            // org
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] orgWhereParams = new String[] { id,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE };
            Cursor orgCur = this.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, orgWhere,
                    orgWhereParams, null);
            if (orgCur.moveToFirst()) {
                String orgName = orgCur.getString(orgCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                String title = orgCur.getString(orgCur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

                // map.put("org", orgName);
                // map.put("title", title);

                listArray.add(orgName);
                listArray.add(title);
            }
            orgCur.close();

            // phone
            // String hasPhone =
            // cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            //
            // Log.i(TAG, "hasPhone - " + hasPhone);

            // if (hasPhone.equalsIgnoreCase("1")) {
            Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
            if (phones.moveToFirst()) {
                // ArrayList<String> number_selected = new
                // ArrayList<String>();

                StringBuilder phoneSb = new StringBuilder();
                phoneSb.append(phones.getString(phones.getColumnIndex("data1")));
                while (phones.moveToNext()) {
                    phoneSb.append('/');
                    phoneSb.append(phones.getString(phones.getColumnIndex("data1")));
                }
                // map.put("phone", phoneSb.toString());
                phones.close();

                listArray.add(phoneSb.toString());
            }
            // }
        }
        cursor.close();
    }
}
