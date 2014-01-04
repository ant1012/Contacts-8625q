package edu.bupt.contacts.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 操作拦截记录数据库的Helper类
 * 
 * */

public class CallBlockDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TB_NAME = "CallBlockRecord";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String TIME = "time";

    private static final int VERSION = 2;

    public CallBlockDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID + " INTEGER PRIMARY KEY," + NAME + " VARCHAR,"
                + PHONE + " VARCHAR," + TIME + " VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 新添加记录
     * 
     * */
    public void addRecord(String name, String phone, String time) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PHONE, phone);
        values.put(TIME, time);
        this.getWritableDatabase().insert(TB_NAME, ID, values);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 删除记录
     * 
     * */
    public void delRecord(int id) {
        this.getWritableDatabase().delete(TB_NAME, ID + " = " + id, null);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 清空记录
     * 
     * */
    public void delAllRecord() {
        this.getWritableDatabase().delete(TB_NAME, null, null);
    }
}
