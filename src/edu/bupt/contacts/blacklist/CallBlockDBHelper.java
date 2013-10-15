package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class CallBlockDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TB_NAME = "CallBlockRecord";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String TIME = "time";

    public CallBlockDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID
                + " INTEGER PRIMARY KEY," + NAME + " VARCHAR," + PHONE
                + " VARCHAR," + TIME + " VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void addRecord(String name, String phone, String time) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PHONE, phone);
        values.put(TIME, time);
        this.getWritableDatabase().insert(TB_NAME, ID, values);
    }

    public void delRecord(int id) {
        this.getWritableDatabase().delete(TB_NAME, ID + " = " + id, null);
    }

    public void delAllRecord() {
        this.getWritableDatabase().delete(TB_NAME, null, null);
    }
}