package edu.bupt.contacts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * 北邮ANT实验室 zzz
 * 
 * 处理电话号码的工具类
 * 
 * */

public class PhoneQueryUtils {
    private static final String TAG = "PhoneNumberUtils";

    /**
     * 北邮ANT实验室 zzz
     * 
     * 用连字符‘-’格式化号码
     * 
     * 用于在数据中根据号码查询参与人姓名
     * 
     * */
    public static String fomatNumberWithDash(String input) {
        if (input.startsWith("1")) {
            if (input.length() == 1) {
                return input;
            } else if (input.length() > 1 && input.length() < 5) {
                return input.substring(0, 1) + "-" + input.substring(1, input.length());
            } else if (input.length() >= 5 && input.length() < 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4) + "-" + input.substring(4, input.length());
            } else if (input.length() >= 8) {
                return input.substring(0, 1) + "-" + input.substring(1, 4) + "-" + input.substring(4, 7) + "-"
                        + input.substring(7, input.length());
            }
        } else {
            if (input.length() <= 3) {
                return input;
            } else if (input.length() > 3 && input.length() < 7) {
                return input.substring(0, 3) + "-" + input.substring(3, input.length());
            } else if (input.length() >= 7) {
                return input.substring(0, 3) + "-" + input.substring(3, 6) + "-" + input.substring(6, input.length());
            }
        }
        return "";
    }

    /**
     * 北邮ANT实验室 zzz
     * 
     * 用空格‘ ’格式化号码，只针对11位号码
     * 
     * 用于在数据中根据号码查询参与人姓名
     * 
     * */
    public static String fomatNumberWithSpace(String input) {
        if (input.startsWith("1")) {
            if (input.length() == 11) {
                return input.substring(0, 3) + ' ' + input.substring(3, 7) + ' ' + input.substring(7, 11);
            } else {
                return input;
            }
        } else {
            return input;
        }
    }

    /**
     * ddd 将数字中加入空格
     * 
     * TODO 标准是什么？
     * 
     * */
    private static String fomatBlankNumber(String input) {
        if (input.length() <= 3) {
            return input;
        } else if (input.length() > 3 && input.length() < 7) {
            return input.substring(0, 3) + " " + input.substring(3, input.length());
        } else if (input.length() >= 7 && input.length() <= 11) {
            return input.substring(0, 3) + " " + input.substring(3, 7) + " " + input.substring(7, input.length());
        } else if (input.length() > 11) {
            return input;

        }
        return "";
    }

    /**
     * 北邮ANT实验室 zzz
     * 
     * 替换字符串
     * 
     * */
    public static String replacePattern(String origin, String pattern, String replace) {
        Log.i(TAG, "replacePattern : origin - " + origin);
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replace);
        }

        m.appendTail(sb);
        Log.i(TAG, "replacePattern : sb.toString() - " + sb.toString());
        return sb.toString();
    }

    /**
     * 北邮ANT实验室 zzz
     * 
     * 根据号码查询联系人姓名
     * 
     * */
    public static ArrayList<String> getDispNameFromNumber(String phoneNumber, Context context) {
        Log.d(TAG, "getDispNameFromNumber");
        ArrayList<String> ret = new ArrayList<String>();
        // zzz 去掉号码中可能存在的“+86”后再进行匹配
        phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                               // +86

        Log.i(TAG, "phoneNumber - " + phoneNumber);
        Log.i(TAG, "fomatNumberWithDash - " + fomatNumberWithDash(phoneNumber));
        Log.i(TAG, "fomatNumberWithSpace - " + fomatNumberWithSpace(phoneNumber));

        // zzz 需要考虑多种情况，因为在数据库中存储的号码可能是正常号码，也可能用‘-’分隔，也可能用‘ ’分隔
        StringBuilder selectionSB = new StringBuilder();
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 原始
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 减号
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?"); // zzz
                                                                                       // 空格

        String[] selectionArgsSB = new String[] { "%" + phoneNumber,// zzz
                                                                    // 原始
                "%" + fomatNumberWithDash(phoneNumber), // zzz 减号
                "%" + fomatNumberWithSpace(phoneNumber) }; // zzz 空格

        Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                selectionSB.toString(), selectionArgsSB, null);
        while (pCur.moveToNext()) {
            ret.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            Log.d(TAG, "GOT");
        }
        pCur.close();
        return ret;
    }

    /**
     * 北邮ANT实验室 zzz
     * 
     * 根据号码查询联系人contact_id
     * 
     * */
    public static ArrayList<String> getContactidFromNumber(String phoneNumber, Context context) {
        Log.d(TAG, "getContactidFromNumber");
        ArrayList<String> ret = new ArrayList<String>();
        // zzz 去掉号码中可能存在的“+86”后再进行匹配
        phoneNumber = replacePattern(phoneNumber, "^((\\+{0,1}86){0,1})", ""); // strip
                                                                               // +86

        Log.i(TAG, "phoneNumber - " + phoneNumber);
        Log.i(TAG, "fomatNumberWithDash - " + fomatNumberWithDash(phoneNumber));
        Log.i(TAG, "fomatNumberWithSpace - " + fomatNumberWithSpace(phoneNumber));

        // zzz 需要考虑多种情况，因为在数据库中存储的号码可能是正常号码，也可能用‘-’分隔，也可能用‘ ’分隔
        StringBuilder selectionSB = new StringBuilder();
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 原始
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 减号
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?"); // zzz
                                                                                       // 空格

        String[] selectionArgsSB = new String[] { "%" + phoneNumber,// zzz
                                                                    // 原始
                "%" + fomatNumberWithDash(phoneNumber), // zzz 减号
                "%" + fomatNumberWithSpace(phoneNumber) }; // zzz 空格

        Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                selectionSB.toString(), selectionArgsSB, null);
        while (pCur.moveToNext()) {
            ret.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            Log.d(TAG, "GOT");
        }
        pCur.close();
        return ret;
    }

    /**
     * 北邮ANT实验室 zzz
     * 
     * 根据号码后10位查询联系人contact_id和display_name
     * 
     * */
    public static ArrayList<Map<String, String>> getContactidFromCutNumber(String phoneNumber, Context context) {
        Log.i(TAG, "phone-number--" + phoneNumber);
        ArrayList<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        String numberCut10 = null;
        String numberSpace10 = null;
        String numberDash10 = null;

        if (phoneNumber.length() < 11) {
            Log.v(TAG, "phoneNumber.length() < 11");
            numberCut10 = phoneNumber;
            numberSpace10 = phoneNumber;
            numberDash10 = phoneNumber;
        } else {
            Log.v(TAG, "phoneNumber.length() >= 11");
            // zzz 后10位
            numberCut10 = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
            // zzz 后11位，用于格式化之后再取后10位
            String numberCut11 = phoneNumber.substring(phoneNumber.length() - 11, phoneNumber.length());
            // zzz 后11位加空格后再取后12位，应该包涵10个数字2个空格
            numberSpace10 = fomatBlankNumber(numberCut11).substring(fomatBlankNumber(numberCut11).length() - 12,
                    fomatBlankNumber(numberCut11).length());
            // zzz 后11位加短线后再取后13位，应该包涵10个数字3个空格
            numberDash10 = fomatNumberWithDash(numberCut11).substring(fomatNumberWithDash(numberCut11).length() - 13,
                    fomatNumberWithDash(numberCut11).length());
        }

        Log.i(TAG, "numberCut10 - " + numberCut10);
        Log.i(TAG, "numberSpace10 -" + numberSpace10);
        Log.i(TAG, "numberDash10 -" + numberDash10);

        // zzz 需要考虑多种情况，因为在数据库中存储的号码可能是正常号码，也可能用‘-’分隔，也可能用‘ ’分隔
        StringBuilder selectionSB = new StringBuilder();
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 原始
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? or "); // zzz
                                                                                           // 减号
        selectionSB.append(ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?"); // zzz
                                                                                       // 空格

        String[] selectionArgsSB = new String[] { "%" + numberCut10,// zzz
                                                                    // 原始
                "%" + numberDash10, // zzz 减号
                "%" + numberSpace10 }; // zzz 空格

        Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                selectionSB.toString(), selectionArgsSB, null);
        while (pCur.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("contact_id",
                    pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            map.put("number", pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            map.put("display_name",
                    pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            Log.v(TAG,
                    "Phone.NUMBER -"
                            + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            ret.add(map);
            Log.v(TAG, "GOT");
        }
        pCur.close();
        return ret;

    }
}
