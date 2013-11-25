package edu.bupt.contacts.numberlocate;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/** zzz */
public class CountryCodeProvider extends ContentProvider {
    private static final String TAG = "CountryCodeProvider";
    public static final String AUTHORITY = "edu.bupt.contacts.numberlocate.international_phonecode";
    private CountryCodeDBHelper dbhelper;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/international_phonecode");
    public static final String CODE = "code";
    public static final String CN_NAME = "cn_name";

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        dbhelper = new CountryCodeDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, "CountryCodeProvider.query");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CountryCodeDBHelper.TB_NAME);

        return qb.query(dbhelper.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder,
                null);
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return 0;
    }

}
