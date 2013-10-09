package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class MsgBlockDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TAG = "MsgBlockDBHelper";
    public static final String TB_NAME = "MsgBlockRecord";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    public MsgBlockDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID
                + " INTEGER PRIMARY KEY," + NAME + " VARCHAR," + PHONE
                + " VARCHAR," + MESSAGE + " VARCHAR," + TIME + " VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void addRecord(String name, String phone, String message, String time) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PHONE, phone);
        values.put(MESSAGE, message);
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
