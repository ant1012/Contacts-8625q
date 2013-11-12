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
    private final String TAG = "NumberLocate";

    private AsyncQueryHandler queryHandler;
    private Context mContext;
    private Handler handler;
    private String number;

    public NumberLocate(final Context mContext, final Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
        queryHandler = new AsyncQueryHandler(mContext.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
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

                    String city = sb.toString();
                    Log.v(TAG, "city - " + city);

                    cursor.close();
                    Message msg = new Message();
                    msg.obj = city;
                    handler.sendMessage(msg);
                    PhoneStatusRecevier.saveAsCache(mContext, number, city);
                }
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
        if (!TextUtils.isEmpty(city)) {
            Message msg = new Message();
            msg.obj = city;
            handler.sendMessage(msg);
        } else {
            if (number.length() >= 11) {
                String formatNumber = PhoneStatusRecevier.formatNumber(number);
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
                if (formatNumber.startsWith("+")) {
                    Log.v(TAG, "startsWith(" + ")");
                    // TODO
                    return;
                } else if (formatNumber.length() == 7) {
                    selection = NumberRegion.NUMBER + "=" + formatNumber;
                    uri = NumberRegion.CONTENT_URI;
                    projection = new String[] { NumberRegion.PROVINCE, NumberRegion.CITY, NumberRegion.CARD };
                } else {

                    /** zzz */
                    if (formatNumber.startsWith("+")) {
                        Log.v(TAG, "startsWith(\"+\")");
                        // TODO
                        return;
                    }

                    selection = NumberRegion.AREACODE + "=" + formatNumber + " OR " + NumberRegion.AREACODE + "="
                            + formatNumber.substring(0, 3);
                    uri = NumberRegion.CONTENT_URI;
                    projection = new String[] { NumberRegion.PROVINCE, NumberRegion.CITY };
                }
                queryHandler.startQuery(0, null, uri, projection, selection, null, null);
            }
        }
    }

    static String queryFromCache(Context context, String number) {
        if (TextUtils.isEmpty(number))
            return null;
        SharedPreferences cache = context.getSharedPreferences("number_region", Context.MODE_PRIVATE);
        String formatNumber = PhoneStatusRecevier.formatNumber(number);
        return cache.getString(formatNumber, null);
    }
}
