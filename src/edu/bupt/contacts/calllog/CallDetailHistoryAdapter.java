/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.contacts.calllog;

import java.util.Formatter;

import edu.bupt.contacts.PhoneCallDetails;
import edu.bupt.contacts.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * 北邮ANT实验室
 * ddd
 * 
 * 电话模块显示历史记录
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */




/**
 * Adapter for a ListView containing history items from the details of a call.
 */
public class CallDetailHistoryAdapter extends BaseAdapter {
    /**
     * The top element is a blank header, which is hidden under the rest of the
     * UI.
     */
    private static final int VIEW_TYPE_HEADER = 0;
    /** Each history item shows the detail of a call. */
    private static final int VIEW_TYPE_HISTORY_ITEM = 1;

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final CallTypeHelper mCallTypeHelper;
    private final PhoneCallDetails[] mPhoneCallDetails;
    /** Whether the voicemail controls are shown. */
    private final boolean mShowVoicemail;
    /** Whether the call and SMS controls are shown. */
    private final boolean mShowCallAndSms;
    /** The controls that are shown on top of the history list. */
    private final View mControls;
    /** The listener to changes of focus of the header. */
    private View.OnFocusChangeListener mHeaderFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // When the header is focused, focus the controls above it instead.
            if (hasFocus) {
                mControls.requestFocus();
            }
        }
    };

    public CallDetailHistoryAdapter(Context context, LayoutInflater layoutInflater, CallTypeHelper callTypeHelper,
            PhoneCallDetails[] phoneCallDetails, boolean showVoicemail, boolean showCallAndSms, View controls) {
        mContext = context;
        mLayoutInflater = layoutInflater;
        mCallTypeHelper = callTypeHelper;
        mPhoneCallDetails = phoneCallDetails;
        mShowVoicemail = showVoicemail;
        mShowCallAndSms = showCallAndSms;
        mControls = controls;
    }

    @Override
    public boolean isEnabled(int position) {
        // None of history will be clickable.
        return false;
    }

    @Override
    public int getCount() {
        return mPhoneCallDetails.length + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return mPhoneCallDetails[position - 1];
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return -1;
        }
        return position - 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_HISTORY_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            final View header = convertView == null ? mLayoutInflater.inflate(R.layout.call_detail_history_header,
                    parent, false) : convertView;
            // Voicemail controls are only shown in the main UI if there is a
            // voicemail.
            View voicemailContainer = header.findViewById(R.id.header_voicemail_container);
            voicemailContainer.setVisibility(mShowVoicemail ? View.VISIBLE : View.GONE);
            // Call and SMS controls are only shown in the main UI if there is a
            // known number.
            View callAndSmsContainer = header.findViewById(R.id.header_call_and_sms_container);
            callAndSmsContainer.setVisibility(mShowCallAndSms ? View.VISIBLE : View.GONE);
            header.setFocusable(true);
            header.setOnFocusChangeListener(mHeaderFocusChangeListener);
            return header;
        }

        // Make sure we have a valid convertView to start with
        final View result = convertView == null ? mLayoutInflater.inflate(R.layout.call_detail_history_item, parent,
                false) : convertView;

        PhoneCallDetails details = mPhoneCallDetails[position - 1];
        CallTypeIconsView callTypeIconView = (CallTypeIconsView) result.findViewById(R.id.call_type_icon);
        TextView callTypeTextView = (TextView) result.findViewById(R.id.call_type_text);
        TextView dateView = (TextView) result.findViewById(R.id.date);
        TextView durationView = (TextView) result.findViewById(R.id.duration);
        TextView msimcardView = (TextView) result.findViewById(R.id.msimcard);

        int callType = details.callTypes[0];
        callTypeIconView.clear();
        callTypeIconView.add(callType);
        callTypeTextView.setText(mCallTypeHelper.getCallTypeText(callType));

        /** zzz */
//        设置时间，如需要，可显示北京时间或者当地时间
        // // Set the date.
        // CharSequence dateValue = DateUtils.formatDateRange(mContext,
        // details.date, details.date,
        // DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
        // DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR);
        // dateView.setText(dateValue);

        // if need to show bj time or local time
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean showBJTime = !sp.getString("TimeSettingPreference", "0").equals("0");
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        StringBuilder dateValueSB = new StringBuilder();
        if (tm.isNetworkRoaming() || sp.getBoolean("RoamingTestPreference", false)) {//若在漫游情况下
            String timeLocate = showBJTime ? mContext.getResources().getStringArray(R.array.time_setting)[1] : mContext
                    .getResources().getStringArray(R.array.time_setting)[0];//若选择显示北京时间，则显示北京时间，否则，显示当地时间
            dateValueSB.append(timeLocate);
            dateValueSB.append(' ');
        }

        if (!showBJTime) { // local time 显示当地时间
            dateValueSB.append(DateUtils.formatDateRange(mContext, details.date, details.date,
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR));
        } else { // bj time 显示北京时间
            dateValueSB.append(DateUtils.formatDateRange(
                    mContext,
                    new Formatter(),
                    details.date,
                    details.date,
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR, mContext.getResources().getString(R.string.home_tz)).toString()); // TODO
        }
        dateView.setText(dateValueSB.toString());

        // Set the duration 显示通话时长
        if (callType == Calls.MISSED_TYPE || callType == Calls.VOICEMAIL_TYPE) {
            durationView.setVisibility(View.GONE);
        } else {
            durationView.setVisibility(View.VISIBLE);
            durationView.setText(formatDuration(details.duration));
        }
        
        //ddd 将标示卡一卡二的标签 “GSM”改为“卡二” 
        //判断该条通话记录的卡类型并且显示  电话模块功能4
        if (details.msimType == 0) { // by yuan
            msimcardView.setText(R.string.cdma);
        } else if (details.msimType == 1) {
            msimcardView.setText(R.string.gsm);
        } else {
            msimcardView.setText("unknown");
        }

        return result;
    }

    private String formatDuration(long elapsedSeconds) {
        long minutes = 0;
        long seconds = 0;

        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;

        return mContext.getString(R.string.callDetailsDurationFormat, minutes, seconds);
    }
}
