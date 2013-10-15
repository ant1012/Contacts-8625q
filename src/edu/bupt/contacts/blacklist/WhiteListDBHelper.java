package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WhiteListDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TAG = "WhiteWhiteDBHelper";
    public static final String TB_NAME = "WhiteListFragment";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String Phone = "phone";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    public WhiteListDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
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

//    public void addPeople(String name, String phone, String blockContent,
//            Integer blockId) {
//        ContentValues values = new ContentValues();
//        values.put(WhiteListDBHelper.NAME, name);
//        values.put(WhiteListDBHelper.Phone, phone);
//        values.put(WhiteListDBHelper.BlockContent, blockContent);
//        values.put(WhiteListDBHelper.BlockId, blockId);
//        this.getWritableDatabase().insert(WhiteListDBHelper.TB_NAME,
//                WhiteListDBHelper.ID, values);
//    }

    public void addPeople(String name, String phone) {
        String sql = "select * from "+ TB_NAME + " where phone = ?";
        Cursor cursor = this.getWritableDatabase().rawQuery(
                sql, new String[] { phone });
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(WhiteListDBHelper.NAME, name);
            values.put(WhiteListDBHelper.Phone, phone);
            // values.put(WhiteListDBHelper.BlockContent, blockContent);
            // values.put(WhiteListDBHelper.BlockId, blockId);
            this.getWritableDatabase().insert(WhiteListDBHelper.TB_NAME,
                    WhiteListDBHelper.ID, values);
        }
    }

    public void delPeople(int id) {
        this.getWritableDatabase().delete(WhiteListDBHelper.TB_NAME,
                WhiteListDBHelper.ID + " = " + id, null);
    }

    public void delAllPeople() {
        this.getWritableDatabase()
                .delete(WhiteListDBHelper.TB_NAME, null, null);
    }
    
    public int getCount() {
        int count = 0;
        Cursor cursor = this.getWritableDatabase().rawQuery(
                "select count(*) from " + TB_NAME, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}