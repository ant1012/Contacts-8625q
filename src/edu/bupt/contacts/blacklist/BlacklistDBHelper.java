package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlacklistDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TAG = "BlacklistDBHelper";
    public static final String TB_NAME = "BlackListFragment";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String Phone = "phone";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    public BlacklistDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
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
        values.put(BlacklistDBHelper.NAME, name);
        values.put(BlacklistDBHelper.Phone, phone);
        values.put(BlacklistDBHelper.BlockContent, blockContent);
        values.put(BlacklistDBHelper.BlockId, blockId);
        this.getWritableDatabase()
                .insert(BlacklistDBHelper.TB_NAME, BlacklistDBHelper.ID, values);
    }

    public void delPeople(int id) {
        this.getWritableDatabase().delete(BlacklistDBHelper.TB_NAME,
                BlacklistDBHelper.ID + " = " + id, null);
    }

    public void delAllPeople() {
        this.getWritableDatabase().delete(BlacklistDBHelper.TB_NAME, null, null);
    }
}