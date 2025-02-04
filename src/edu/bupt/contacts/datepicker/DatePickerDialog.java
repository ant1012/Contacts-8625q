/*
 * Copyright (C) 2007 The Android Open Source Project
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

package edu.bupt.contacts.datepicker;

// This is a fork of the standard Android DatePicker that additionally allows toggling the year
// on/off. It uses some private API so that not everything has to be copied.

import edu.bupt.contacts.R;
import edu.bupt.contacts.datepicker.DatePicker.OnDateChangedListener;
import edu.bupt.contacts.util.DateUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未修改
 * 
 * */

/**
 * A simple dialog containing an {@link DatePicker}.
 *
 * <p>See the <a href="{@docRoot}resources/tutorials/views/hello-datepicker.html">Date Picker
 * tutorial</a>.</p>
 */
public class DatePickerDialog extends AlertDialog implements OnClickListener,
        OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String YEAR_OPTIONAL = "year_optional";

    private final DatePicker mDatePicker;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    private final DateFormat mTitleDateFormat;
    private final DateFormat mTitleNoYearDateFormat;

    private int mInitialYear;
    private int mInitialMonth;
    private int mInitialDay;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {
        /**
         * @param view The view associated with this listener.
         * @param year The year that was set or 0 if the user has not specified a year
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, callBack, year, monthOfYear, dayOfMonth, false);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog or 0 if no year has been specified
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     * @param yearOptional Whether the year can be toggled by the user
     */
    public DatePickerDialog(Context context,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth,
            boolean yearOptional) {
        this(context, context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB
                        ? com.android.internal.R.style.Theme_Holo_Light_Dialog_Alert
                        : com.android.internal.R.style.Theme_Dialog_Alert,
                callBack, year, monthOfYear, dayOfMonth, yearOptional);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog or 0 if no year has been specified
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            int theme,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, theme, callBack, year, monthOfYear, dayOfMonth, false);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     * @param yearOptional Whether the year can be toggled by the user
     */
    public DatePickerDialog(Context context,
            int theme,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth,
            boolean yearOptional) {
        super(context, theme);

        mCallBack = callBack;
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;

        mTitleDateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        mTitleNoYearDateFormat = new SimpleDateFormat(
                DateUtils.isMonthBeforeDay(getContext()) ? "MMMM dd" : "dd MMMM");
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialYear, mInitialMonth, mInitialDay);

        setButton(BUTTON_POSITIVE, context.getText(com.android.internal.R.string.date_time_set),
                this);
        setButton(BUTTON_NEGATIVE, context.getText(android.R.string.cancel),
                (OnClickListener) null);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, yearOptional, this);
    }

    @Override
    public void show() {
        super.show();

        /* Sometimes the full month is displayed causing the title
         * to be very long, in those cases ensure it doesn't wrap to
         * 2 lines (as that looks jumpy) and ensure we ellipsize the end.
         */
        TextView title = (TextView) findViewById(com.android.internal.R.id.alertTitle);
        title.setSingleLine();
        title.setEllipsize(TruncateAt.END);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            mDatePicker.clearFocus();
            mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
                    mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        updateTitle(year, month, day);
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    private void updateTitle(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        final DateFormat dateFormat =
                year == 0 ? mTitleNoYearDateFormat : mTitleDateFormat;
        setTitle(dateFormat.format(mCalendar.getTime()));
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        state.putBoolean(YEAR_OPTIONAL, mDatePicker.isYearOptional());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        boolean yearOptional = savedInstanceState.getBoolean(YEAR_OPTIONAL);
        mDatePicker.init(year, month, day, yearOptional, this);
        updateTitle(year, month, day);
    }
}
