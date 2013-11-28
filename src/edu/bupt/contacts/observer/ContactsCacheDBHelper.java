package edu.bupt.contacts.observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** zzz */
public class ContactsCacheDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "ContactsCacheDBHelper";

    private static final String DATABASE_NAME = "blacklist.db";
    public static final String TB_NAME = "BlackListFragment";
    public static final String CONTACTID = "contactid";
    public static final String NAME = "name";
    public static final String NUMBER = "number";

    public ContactsCacheDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + CONTACTID + " VARCHAR ," + NAME + " VARCHAR,"
                + NUMBER + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void dropTable() {
        this.getWritableDatabase().delete(TB_NAME, null, null);
    }

    public void addLine(String contactid, String name, String number) {
        ContentValues values = new ContentValues();
        values.put(CONTACTID, contactid);
        values.put(NAME, name);
        values.put(NUMBER, number);
        this.getWritableDatabase().insert(TB_NAME, null, values);

    }

    public Cursor query() {
        return this.getWritableDatabase().query(TB_NAME, null, null, null, null, null, null);

    }
}
