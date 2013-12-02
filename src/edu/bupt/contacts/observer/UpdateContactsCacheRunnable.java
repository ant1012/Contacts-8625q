package edu.bupt.contacts.observer;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/** zzz */
public class UpdateContactsCacheRunnable implements Runnable, OnCacheUpdatedListener {
    private final String TAG = "UpdateContactsCacheRunnable";
    private Context context;
    private static OnCacheUpdatedListener onCacheUpdatedListener;

    public static boolean isInitilized = false;
    // public static boolean isUpdated = false;
    public static boolean isRunning = false;

    public UpdateContactsCacheRunnable(Context context) {
        this.context = context;
    }

    public UpdateContactsCacheRunnable(Context context, OnCacheUpdatedListener l) {
        this.context = context;
        onCacheUpdatedListener = l;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");

        if (isRunning) {
            return;
        }

        Log.v(TAG, "before caching Data");
        // isUpdated = false;
        isRunning = true;
        long tb = System.currentTimeMillis();
        int soManyLines = 0;

        ContactsCacheDBHelper contactsCacheDBHelper = new ContactsCacheDBHelper(context, 1);
        contactsCacheDBHelper.dropTable();

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

            // if (flagPackageVcard == 1) {
            // Map<String, String> map = new HashMap<String, String>();
            // map.put("id", contactId);
            // map.put("name", name);
            // map.put("number", null);
            // list.add(map);
            // soManyLines++;
            // } else {
            phonecur = context.getContentResolver().query(
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
                contactsCacheDBHelper.addLine(contactId, name, strPhoneNumber);

                // list.add(map);
                soManyLines++;
            }
            phonecur.close();
            // }
        }
        // if (phonecur != null)
        cursor.close();
        contactsCacheDBHelper.close();

        // Message msg1 = new Message();
        // msg1.what = UPDATE_LIST;
        // updateListHandler.sendMessage(msg1);

        Log.v(TAG, "after caching Data");
        long ta = System.currentTimeMillis();
        Log.w(TAG, "time cost for caching Data, " + (ta - tb));
        Log.w(TAG, "soManyLines - " + soManyLines);
        // isUpdated = true;
        isInitilized = true;
        isRunning = false;
        OnCacheUpdated();
    }

    @Override
    public void OnCacheUpdated() {
        if (onCacheUpdatedListener != null) {
            onCacheUpdatedListener.OnCacheUpdated();
        }
    }

}
