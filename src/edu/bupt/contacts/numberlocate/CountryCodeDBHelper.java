package edu.bupt.contacts.numberlocate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountryCodeDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "country_calling_codes";
    public static final String TB_NAME = "international_phonecode";

    // ddd start
    private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
    // ddd end

    private static final int VERSION = 1;
    private static final String TAG = "CountryCodeDBHelper";
    private Context mContext;

    /** zzz */
    public CountryCodeDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    /** zzz */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        executeSQLScript(database, "db/international_phonecode_add.sql");
    }

    /** zzz */
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

    /** zzz */
    @Override
    public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
        database.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        executeSQLScript(database, "SQLiteDatabase");
    }

    // private boolean tabbleIsExist(String tableName) {
    // boolean result = false;
    // if (tableName == null) {
    // return false;
    // }
    // SQLiteDatabase db = null;
    // Cursor cursor = null;
    // try {
    // db = this.getReadableDatabase();
    // String sql =
    // "select count(*) as c from sqlite_master where type ='table' and name ='"
    // + tableName.trim()
    // + "' ";
    // cursor = db.rawQuery(sql, null);
    // if (cursor.moveToNext()) {
    // int count = cursor.getInt(0);
    // if (count > 0) {
    // result = true;
    // }
    // }
    // cursor.close();
    //
    // } catch (Exception e) {
    // Log.e(TAG, e.toString());
    // }
    // return result;
    // }

    // northamerica 1
    // africa 2
    // europe 3(347)
    // northamerica 5
    // oceania 6
    // asia 8(698)

    public ArrayList<Map<String, String>> getCountry(int continent) {

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Cursor cursor = null;
        String current_sql_sel;
        // switch(continent){
        // case 1:

        current_sql_sel = "SELECT  * FROM " + TB_NAME + " where " + "continent='" + continent
                + "'AND code!='' order by code";
        cursor = getWritableDatabase().rawQuery(current_sql_sel, null);
        // break;
        // case 2:
        // current_sql_sel = "SELECT  * FROM "+TB_NAME
        // +" where "+"code"+" like '2%'";
        // cursor = getWritableDatabase().rawQuery(current_sql_sel, null);
        // break;
        // case 3:
        // current_sql_sel = "SELECT  * FROM "+TB_NAME
        // +" where "+"code"+" like '[347]%'";
        // cursor = getWritableDatabase().rawQuery(current_sql_sel, null);
        // break;
        // case 5:
        // current_sql_sel = "SELECT  * FROM "+TB_NAME
        // +" where "+"code"+" like '5%'";
        // cursor = getWritableDatabase().rawQuery(current_sql_sel, null);
        // break;
        // case 6:
        // current_sql_sel = "SELECT  * FROM "+TB_NAME
        // +" where "+"code"+" like '6%'";
        // cursor = getWritableDatabase().rawQuery(current_sql_sel, null);
        // break;
        //
        //
        // case 8:
        //
        // try{current_sql_sel = "SELECT  * FROM "+TB_NAME
        // +" where "+"code"+" like '[689]%'";
        // cursor = getWritableDatabase().rawQuery(current_sql_sel, null);}
        // catch (SQLException e) {
        // Log.e(TAG, e.toString());
        // }
        // break;
        //
        //
        // }

        while (cursor != null && cursor.moveToNext()) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("cn_name", cursor.getString(cursor.getColumnIndex("cn_name")));
            item.put("en_name", cursor.getString(cursor.getColumnIndex("en_name")));
            item.put("code", cursor.getString(cursor.getColumnIndex("code")));
            item.put("countryiso", cursor.getString(cursor.getColumnIndex("countryiso")));
            item.put("continent", cursor.getString(cursor.getColumnIndex("continent")));
            item.put("call_prefix", cursor.getString(cursor.getColumnIndex("call_prefix")));

            list.add(item);
        }
        cursor.close();

        return list;
    }

    public ArrayList<Map<String, String>> searchCountry(String countryName) {

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Cursor cursor = null;
        String current_sql_sel;
        current_sql_sel = "SELECT  * FROM " + TB_NAME + " where " + "cn_name= '" + countryName + "'";
        cursor = getWritableDatabase().rawQuery(current_sql_sel, null);

        while (cursor.moveToNext()) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("cn_name", cursor.getString(cursor.getColumnIndex("cn_name")));
            item.put("en_name", cursor.getString(cursor.getColumnIndex("en_name")));
            item.put("code", cursor.getString(cursor.getColumnIndex("code")));
            item.put("countryiso", cursor.getString(cursor.getColumnIndex("countryiso")));
            item.put("continent", cursor.getString(cursor.getColumnIndex("continent")));
            item.put("call_prefix", cursor.getString(cursor.getColumnIndex("call_prefix")));

            list.add(item);
        }

        cursor.close();
        return list;
    }

    /** zzz */
    public String queryCallPrefix(String countryIso) {
        String ret = null;
        Cursor cursor = null;
        cursor = getWritableDatabase().query(TB_NAME, new String[] { "call_prefix" }, "countryiso = ?",
                new String[] { countryIso.toUpperCase() }, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getString(0);
        }
        cursor.close();
        ret = ret == null ? "00" : ret;
        Log.v(TAG, "got call prefix " + ret);
        return ret;
    }

    /** zzz */
    public ContentValues queryLocalCountryCodeAndName(String countryIso) {
        ContentValues ret = new ContentValues();
        Cursor cursor = null;
        cursor = getWritableDatabase().query(TB_NAME, new String[] { "cn_name", "code" }, "countryiso = ?",
                new String[] { countryIso.toUpperCase() }, null, null, null);
        if (cursor.moveToFirst()) {
            if (cursor.getString(0) != null) {
                ret.put("name", cursor.getString(0));
            } else {
                ret.put("name", "unknown");
            }

            if (cursor.getString(1) != null) {
                ret.put("code", cursor.getString(1));
            } else {
                ret.put("code", "");
            }
        }
        cursor.close();
        return ret;
    }

    /** zzz */
    public boolean queryIs133Enabled(String countryIso) {
        boolean ret = false;
        Cursor cursor = null;
        cursor = getWritableDatabase().query(TB_NAME, new String[] { "support_esurfing" }, "countryiso = ?",
                new String[] { countryIso.toUpperCase() }, null, null, null);
        if (cursor.moveToFirst()) {
            ret = cursor.getString(0).equals("Y") ? true : false;
        }
        cursor.close();
        return ret;
    }
}
