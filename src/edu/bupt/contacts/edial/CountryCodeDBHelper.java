package edu.bupt.contacts.edial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountryCodeDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "country_calling_codes";
    private static final String TB_NAME = "international_phonecode";
    private static final int VERSION = 1;
    private static final String TAG = "CountryCodeDBHelper";
    private Context mContext;

    public CountryCodeDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        if (tabbleIsExist(TB_NAME)) {
            executeSQLScript(database, "international_phonecode.sql");
        }
    }

    public void executeSQLScript(SQLiteDatabase database, String dbname) {
        Log.d(TAG, "executeSQLScript");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[15000];
        int len;
        AssetManager assetManager = mContext.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(dbname);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                // TODO You may want to parse out comments here
                Log.v(TAG, "sqlStatement - " + sqlStatement);
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
        executeSQLScript(database, "SQLiteDatabase");
    }

    private boolean tabbleIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tableName.trim()
                    + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            cursor.close();
        }
        return result;
    }
}
