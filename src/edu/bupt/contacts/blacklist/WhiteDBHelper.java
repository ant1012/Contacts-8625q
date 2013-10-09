package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WhiteDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "franco--->WhiteWhiteDBHelper";
    public static final String TB_NAME = "WhiteListFragment";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String Phone = "phone";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    public WhiteDBHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID
                + " INTEGER PRIMARY KEY," + NAME + " VARCHAR," + Phone
                + " VARCHAR," + BlockContent + " VARCHAR," + BlockId
                + " INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void addPeople(String name, String phone, String blockContent,
            Integer blockId) {
        ContentValues values = new ContentValues();
        values.put(WhiteDBHelper.NAME, name);
        values.put(WhiteDBHelper.Phone, phone);
        values.put(WhiteDBHelper.BlockContent, blockContent);
        values.put(WhiteDBHelper.BlockId, blockId);
        this.getWritableDatabase().insert(WhiteDBHelper.TB_NAME,
                WhiteDBHelper.ID, values);
    }

    public void delPeople(int id) {
        this.getWritableDatabase().delete(WhiteDBHelper.TB_NAME,
                WhiteDBHelper.ID + " = " + id, null);
    }

    public void delAllPeople() {
        this.getWritableDatabase().delete(WhiteDBHelper.TB_NAME, null, null);
    }
}