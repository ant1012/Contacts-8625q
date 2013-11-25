package edu.bupt.contacts.numberlocate;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import edu.bupt.contacts.R;
import edu.bupt.contacts.numberlocate.NumberLocateProvider.NumberRegion;

public class NumberLocate {
    private final static String TAG = "NumberLocate";

    private AsyncQueryHandler queryHandler;
    private AsyncQueryHandler queryHandlerCountry;
    private Context mContext;
    private Handler handler;
    private String number;

    public NumberLocate(final Context mContext, final Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
        queryHandler = new AsyncQueryHandler(mContext.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Log.d(TAG, "onQueryComplete");
                String city = null;
                if (cursor != null && cursor.moveToNext()) {
                    /** zzz */
                    // String city = cursor.getString(0);

                    StringBuilder sb = new StringBuilder();

                    String resProvince = cursor.getString(0);
                    String resCity = cursor.getString(1);

                    sb.append(resProvince);

                    if (!resProvince.equals(resCity)) {
                        sb.append(' ');
                        sb.append(resCity);
                    }

                    String resCardp = "";
                    if (cursor.getColumnCount() == 3) {
                        resCardp = cursor.getString(2);
                        if (resCardp.contains(mContext.getString(R.string.cardp_mobile))) {
                            resCardp = mContext.getString(R.string.cardp_mobile);
                        } else if (resCardp.contains(mContext.getString(R.string.cardp_telecom))) {
                            resCardp = mContext.getString(R.string.cardp_telecom);
                        } else if (resCardp.contains(mContext.getString(R.string.cardp_unicom))) {
                            resCardp = mContext.getString(R.string.cardp_unicom);
                        }
                        sb.append(' ');
                        sb.append(resCardp);
                    }

                    city = sb.toString();
                    Log.v(TAG, "city - " + city);

                    cursor.close();
                    // Message msg = new Message();
                    // msg.obj = city;
                    // handler.sendMessage(msg);
                    saveAsCache(mContext, number, city);
                } else {
                    city = mContext.getString(R.string.unknown);
                    Log.v(TAG, "city - " + city);
                }
                Message msg = new Message();
                msg.obj = city;
                handler.sendMessage(msg);
            }
        };

        /** zzz */
        queryHandlerCountry = new AsyncQueryHandler(mContext.getContentResolver()) {

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                // TODO
                Log.d(TAG, "onQueryComplete");
                String city = null;
                if (cursor != null && cursor.moveToNext()) {
                    StringBuilder sb = new StringBuilder(cursor.getString(0));
                    while (cursor.moveToNext()) {
                        sb.append("/").append(cursor.getString(0));
                    }
                    city = sb.toString();

                    // Message msg = new Message();
                    // msg.obj = city;
                    // handler.sendMessage(msg);

                    saveAsCache(mContext, number, city);
                    cursor.close();
                } else {
                    city = mContext.getString(R.string.unknown);
                    Log.v(TAG, "city - " + city);
                }
                Message msg = new Message();
                msg.obj = city;
                handler.sendMessage(msg);
            }
        };
    }

    static boolean getSettingValue(Context context, String key) {
        SharedPreferences cache = context.getSharedPreferences("number_region_setting", Context.MODE_PRIVATE);
        return cache.getBoolean(key, false);
    }

    static void saveSetting(Context context, String key, boolean value) {
        SharedPreferences cache = context.getSharedPreferences("number_region_setting", Context.MODE_PRIVATE);
        cache.edit().putBoolean(key, value).commit();
    }

    public void getLocation(String number) {
        startQuery(mContext, number);
    }

    private void startQuery(Context context, String number) {
        this.number = number;
        String city = queryFromCache(mContext, number);
        // String city = "";
        if (!TextUtils.isEmpty(city)) {
            Message msg = new Message();
            msg.obj = city;
            handler.sendMessage(msg);
        } else {
            // if (number.length() >= 11) {
            String formatNumber = formatNumber(number);
            Log.i(TAG, "formatNumber - " + formatNumber);
            String selection = null;
            String[] projection = null;
            // Uri uri = CityCode.CONTENT_URI;
            // if (formatNumber.length() == 7) {
            // selection = NumberRegion.NUMBER+"="+formatNumber;
            // uri = NumberRegion.CONTENT_URI;
            // projection = new String[]{NumberRegion.CITY};
            // } else {
            // selection = CityCode.CODE+"="+formatNumber+" OR " +
            // CityCode.CODE+"=" + formatNumber.substring(0, 3);
            // uri = CityCode.CONTENT_URI;
            // projection = new String[]{CityCode.CITY};
            // }

            /** zzz */
            Uri uri = NumberRegion.CONTENT_URI;
            if (formatNumber.startsWith("+")) { // foreign
                Log.d(TAG, "foreign");
                // TODO

                // if (formatNumber.length() >= 2) {
                // Log.i(TAG, "formatNumber - " + formatNumber);
                // StringBuilder sb = new StringBuilder(formatNumber.charAt(1));
                // selection = CountryCodeProvider.CODE + " = " + sb.toString();
                // uri = CountryCodeProvider.CONTENT_URI;
                // projection = new String[] { CountryCodeProvider.CN_NAME };
                // queryHandlerCountry.startQuery(0, null, uri, projection,
                // selection, null, null);
                // }

                StringBuilder selectionSB = new StringBuilder();
                try {
                    StringBuilder sb = new StringBuilder(formatNumber);
                    selectionSB.append(CountryCodeProvider.CODE + " = ").append(sb.substring(1, 2));
                    selectionSB.append(" or " + CountryCodeProvider.CODE + " = ").append(sb.substring(1, 3));
                    selectionSB.append(" or " + CountryCodeProvider.CODE + " = ").append(sb.substring(1, 4));
                    selectionSB.append(" or " + CountryCodeProvider.CODE + " = ").append(sb.substring(1, 5));
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
                selection = selectionSB.toString();
                uri = CountryCodeProvider.CONTENT_URI;
                projection = new String[] { CountryCodeProvider.CN_NAME };

                Log.i(TAG, "selection - " + selection);

                queryHandlerCountry.startQuery(0, null, uri, projection, selection, null, null);

                return;
            } else if (formatNumber.length() == 7) { // mobile
                Log.d(TAG, "mobile");
                selection = NumberRegion.NUMBER + "=" + formatNumber;
                uri = NumberRegion.CONTENT_URI;
                projection = new String[] { NumberRegion.PROVINCE, NumberRegion.CITY, NumberRegion.CARD };

                queryHandler.startQuery(0, null, uri, projection, selection, null, null);

            } else { // land line

                /** zzz */
                Log.d(TAG, "land line");
                // if (formatNumber.startsWith("+")) {
                // Log.v(TAG, "startsWith(\"+\")");
                // // TODO
                // return;
                // }

                try {
                    selection = NumberRegion.AREACODE + " = \'" + formatNumber + "\' or " + NumberRegion.AREACODE
                            + " = \'" + formatNumber.substring(0, 3) + "\'";

                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                    selection = NumberRegion.AREACODE + "= 0000";
                }
                Log.i(TAG, "selection - " + selection);
                uri = NumberRegion.CONTENT_URI;
                projection = new String[] { NumberRegion.PROVINCE, NumberRegion.CITY };

                queryHandler.startQuery(0, null, uri, projection, selection, null, null);

            }
            // queryHandler.startQuery(0, null, uri, projection, selection,
            // null, null);
            // }
        }
    }

    static String queryFromCache(Context context, String number) {
        if (TextUtils.isEmpty(number))
            return null;
        SharedPreferences cache = context.getSharedPreferences("number_region", Context.MODE_PRIVATE);
        String formatNumber = formatNumber(number);
        Log.v(TAG, "formatNumber - " + formatNumber);
        return cache.getString(formatNumber, null);
    }

    public static String formatNumber(String number) {
        StringBuilder sb = new StringBuilder(number);
        String ret = new String();
        if (sb.charAt(0) == '+') {
            /** zzz */
            Log.v(TAG, "sb.charAt(0) == \'+\'");
            if (sb.length() >= 3 && sb.substring(1, 3).equals("86")) { // +86
                try {
                    ret = sb.substring(3, 10).toString();
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                    ret = sb.substring(3).toString();
                }
            } else {
                // ret = sb.toString(); // foreign
                try { // foreign
                    ret = sb.substring(0, 5).toString();
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                    ret = "";
                }
            }
            // return sb.delete(0, 3).substring(0, 7).toString();
        } else
        // //zaizhe
        // if(sb.indexOf("010")==0){
        // return sb.substring(0, 3);
        // }
        //
        // if(sb.indexOf("020")==0){
        // return sb.substring(0, 3);
        // }
        //
        // if(sb.indexOf("030")==0){
        // return sb.substring(0, 3);
        // }
        //
        // if(sb.indexOf("040")==0){
        // return sb.substring(0, 3);
        // }
        // //zaizhe
        if (sb.charAt(0) == '0') {
            Log.v(TAG, "sb.charAt(0) == 0");
            ret = sb.substring(0, 4);
        } else if (sb.length() < 7) {
            Log.v(TAG, "sb.length() < 7");
            ret = sb.toString();
        } else {
            // return sb.substring(0, 7).toString();
            Log.v(TAG, "defalt");
            ret = sb.substring(0, 7).toString();
        }
        Log.v(TAG, "ret - " + ret);
        return ret;
    }

    public static void saveAsCache(Context context, String number, String city) {
        if (!TextUtils.isEmpty(city) && number.length() >= 11) {
            String formatNumber = NumberLocate.formatNumber(number);
            SharedPreferences cache = context.getSharedPreferences("number_region", Context.MODE_PRIVATE);
            cache.edit().putString(formatNumber, city).commit();
        }
    }

    // public static String queryFromCache(Context context, String number) {
    // if (TextUtils.isEmpty(number))
    // return null;
    // SharedPreferences cache = context.getSharedPreferences("number_region",
    // Context.MODE_PRIVATE);
    // String formatNumber = NumberLocate.formatNumber(number);
    // return cache.getString(formatNumber, null);
    // // return null;
    // }
}
