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

public class BlacklistDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "blacklist.db";

    public static final String TAG = "BlacklistDBHelper";
    public static final String TB_NAME = "BlackList";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String Phone = "phone";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    private static final int VERSION = 2;

    public BlacklistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.getWritableDatabase();
    }

    public void close() {
        this.getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
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
    // values.put(BlacklistDBHelper.NAME, name);
    // values.put(BlacklistDBHelper.Phone, phone);
    // values.put(BlacklistDBHelper.BlockContent, blockContent);
    // values.put(BlacklistDBHelper.BlockId, blockId);
    // this.getWritableDatabase()
    // .insert(BlacklistDBHelper.TB_NAME, BlacklistDBHelper.ID, values);
    // }

    public void addPeople(String name, String phone) {
        String sql = "select * from " + TB_NAME + " where phone = ?";

        onCreate(this.getWritableDatabase());
        String strip1 = replacePattern(phone, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                           // +86
        String strip2 = replacePattern(strip1, "(\\-)", ""); // strip -
        String strip3 = replacePattern(strip2, "(\\ )", ""); // strip space
        phone = strip3;

        Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] { phone });

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(BlacklistDBHelper.NAME, name);
            values.put(BlacklistDBHelper.Phone, phone);
            // values.put(BlacklistDBHelper.BlockContent, "");
            // values.put(BlacklistDBHelper.BlockId, 0);
            this.getWritableDatabase().insert(BlacklistDBHelper.TB_NAME, BlacklistDBHelper.ID, values);
        }
        cursor.close();
    }

    public void delPeople(int id) {
        this.getWritableDatabase().delete(BlacklistDBHelper.TB_NAME, BlacklistDBHelper.ID + " = " + id, null);
    }

    public void delAllPeople() {
        this.getWritableDatabase().delete(BlacklistDBHelper.TB_NAME, null, null);
    }

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