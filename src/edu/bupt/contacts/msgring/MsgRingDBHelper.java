package edu.bupt.contacts.msgring;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MsgRingDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "msgring.db";
    private static final String TAG = "MsgRingDBHelper";
    private static final String TB_NAME = "msgring";
    public static final String ID = "_id";
    public static final String MSG_RING = "msgring";

    public MsgRingDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        Log.d(TAG, "onCreate");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID
                + " INTEGER PRIMARY KEY," + MSG_RING + " VARCHAR" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(arg0);
    }

    private void addRing(String contactid, String ring) {
        Log.d(TAG, "new record");
    }

    private void updateRing(String contactid, String ring) {
        Log.d(TAG, "exist");
    }

    public void setRing(String contactid, String ring) {
        String sql = "select * from " + TB_NAME + " where " + ID + " = ?";
        Cursor cursor = this.getWritableDatabase().rawQuery(sql,
                new String[] { contactid });
        if (!cursor.moveToFirst()) { // new record
            addRing(contactid, ring);
        } else { // exist
            updateRing(contactid, ring);
        }
        cursor.close();
    }
}
