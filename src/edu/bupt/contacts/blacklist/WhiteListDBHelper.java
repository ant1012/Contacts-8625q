package edu.bupt.contacts.blacklist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 操作白名单数据库的Helper类
 * 
 * */

public class WhiteListDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TAG = "WhiteWhiteDBHelper";
    public static final String TB_NAME = "WhiteList";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String Phone = "phone";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    private static final int VERSION = 2;

    public WhiteListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID + " INTEGER PRIMARY KEY," + NAME + " VARCHAR,"
                + Phone + " VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    // public void addPeople(String name, String phone, String blockContent,
    // Integer blockId) {
    // ContentValues values = new ContentValues();
    // values.put(WhiteListDBHelper.NAME, name);
    // values.put(WhiteListDBHelper.Phone, phone);
    // values.put(WhiteListDBHelper.BlockContent, blockContent);
    // values.put(WhiteListDBHelper.BlockId, blockId);
    // this.getWritableDatabase().insert(WhiteListDBHelper.TB_NAME,
    // WhiteListDBHelper.ID, values);
    // }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 添加号码
     * 
     * */
    public void addPeople(String name, String phone) {
        String sql = "select * from " + TB_NAME + " where phone = ?";
        onCreate(this.getWritableDatabase());

        String strip1 = replacePattern(phone, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                           // +86
        String strip2 = replacePattern(strip1, "(\\-)", ""); // strip -
        String strip3 = replacePattern(strip2, "(\\ )", ""); // strip space
        phone = strip3;

        // zzz 先查询数据库，如果已经存在则不重复添加 
        Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] { phone });

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();

            values.put(WhiteListDBHelper.NAME, name);
            values.put(WhiteListDBHelper.Phone, phone);

            // values.put(WhiteListDBHelper.BlockContent, blockContent);
            // values.put(WhiteListDBHelper.BlockId, blockId);
            this.getWritableDatabase().insert(WhiteListDBHelper.TB_NAME, WhiteListDBHelper.ID, values);
        }
        cursor.close();
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 删除号码
     * 
     * */
    public void delPeople(int id) {
        this.getWritableDatabase().delete(WhiteListDBHelper.TB_NAME, WhiteListDBHelper.ID + " = " + id, null);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 删除所有
     * 
     * */
    public void delAllPeople() {
        this.getWritableDatabase().delete(WhiteListDBHelper.TB_NAME, null, null);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 计数
     * 
     * */
    public int getCount() {
        int count = 0;
        Cursor cursor = this.getWritableDatabase().rawQuery("select count(*) from " + TB_NAME, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 用正则修改字符串
     * 
     * */
    private String replacePattern(String origin, String pattern, String replace) {
        Log.i(TAG, "origin - " + origin);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }

        m.appendTail(sb);
        Log.i(TAG, "sb.toString() - " + sb.toString());
        return sb.toString();
    }
}