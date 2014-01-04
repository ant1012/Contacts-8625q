package edu.bupt.contacts.blacklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 操作数据库的Helper类,主要用于对数据库三个表的初始化和更新，防止表不存在的情况下对表进行操作
 * 
 * */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blacklist.db";
    public static final String TAG = "DBHelper";
    public static final String TB_NAME_BLACK_LIST = "BlackList";
    public static final String TB_NAME_MSG_BLOCK = "MsgBlockRecord";
    public static final String TB_NAME_CALL_BLOCK = "CallBlockRecord";
    public static final String TB_NAME_WHITE_LIST = "WhiteList";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";
    public static final String BlockContent = "blockContent";
    public static final String BlockId = "blockId";

    private static final int VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        Log.d(TAG, "onCreate");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME_BLACK_LIST + " (" + ID + " INTEGER PRIMARY KEY," + NAME
                + " VARCHAR," + PHONE + " VARCHAR)");
        // arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME_MSG_BLOCK + " ("
        // + ID + " INTEGER PRIMARY KEY," + NAME
        // + " VARCHAR," + PHONE + " VARCHAR," + MESSAGE + " VARCHAR," + TIME +
        // " VARCHAR)");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME_CALL_BLOCK + " (" + ID + " INTEGER PRIMARY KEY," + NAME
                + " VARCHAR," + PHONE + " VARCHAR," + TIME + " VARCHAR)");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME_WHITE_LIST + " (" + ID + " INTEGER PRIMARY KEY," + NAME
                + " VARCHAR," + PHONE + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME_BLACK_LIST);
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME_MSG_BLOCK);
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME_CALL_BLOCK);
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME_WHITE_LIST);
        onCreate(arg0);
    }

}
