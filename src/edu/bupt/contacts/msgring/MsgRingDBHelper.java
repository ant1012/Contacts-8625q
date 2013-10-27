package edu.bupt.contacts.msgring;

import java.util.ArrayList;

import edu.bupt.contacts.blacklist.BlacklistDBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/** zzz */
public class MsgRingDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "msgring.db";
    private static final String TAG = "MsgRingDBHelper";
    private static final String TB_NAME = "msgring";
    public static final String ID = "_id";
    public static final String MSG_RING = "msgring";
    private Context context;

    public MsgRingDBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        Log.d(TAG, "onCreate");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + ID + " INTEGER PRIMARY KEY," + MSG_RING
                + " VARCHAR" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        arg0.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(arg0);
    }

    private void addRing(String contactid, String ring) {
        Log.d(TAG, "new record");

        Uri RingUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        Log.i(TAG, "ringtone - " + RingUri);
        Log.i(TAG, "ring - " + ring);
        if (ring == null || ring.equals(RingUri.toString())) {
            delRing(contactid);
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ID, contactid);
        values.put(MSG_RING, ring);
        this.getWritableDatabase().insert(TB_NAME, null, values);
    }

    public void delRing(String contactid) {
        Log.d(TAG, "del record");
        this.getWritableDatabase().delete(TB_NAME, ID + " = ?", new String[] { contactid });
    }

    public void setRing(String contactid, String ring) {
        String sql = "select * from " + TB_NAME + " where " + ID + " = ?";
        Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] { contactid });
        if (!cursor.moveToFirst()) { // new record
            addRing(contactid, ring);
        } else { // exist
            Log.d(TAG, "exist");
            delRing(contactid);
            addRing(contactid, ring);
        }
        cursor.close();
    }

    public Uri queryRing(ArrayList<String> contactidList) {
        for (String id : contactidList) {
            Cursor cursor = this.getWritableDatabase().query(TB_NAME, null, ID + " = ?", new String[] { id }, null,
                    null, null);
            if (cursor.moveToFirst()) {
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MSG_RING)));
                cursor.close();
                return uri;
            }
            cursor.close();
        }
        return null;
    }
}
