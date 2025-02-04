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

import com.android.common.widget.GroupingListAdapter;
import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.CallDetailActivity;
import edu.bupt.contacts.ContactPhotoManager;
import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.PhoneCallDetails;
import edu.bupt.contacts.PhoneCallDetailsHelper;
import edu.bupt.contacts.R;
import edu.bupt.contacts.CallDetailActivity.Tasks;
import edu.bupt.contacts.activities.DialtactsActivity;
import edu.bupt.contacts.format.FormatUtils;
import edu.bupt.contacts.ipcall.IPCall;
import edu.bupt.contacts.util.ExpirableCache;
import edu.bupt.contacts.util.UriUtils;

import com.google.common.annotations.VisibleForTesting;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.LinkedList;

import libcore.util.Objects;

/**
 * 北邮ANT实验室
 * ddd
 * 
 * 电话模块，显示历史记录列表
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */

/**
 * Adapter class to fill in data for the Call Log.
 */
/* package */class CallLogAdapter extends GroupingListAdapter implements ViewTreeObserver.OnPreDrawListener,
        CallLogGroupBuilder.GroupCreator {

    protected static final String TAG = "CallLogAdapter";

    /** Interface used to initiate a refresh of the content. */
    public interface CallFetcher {
        public void fetchCalls();
    }

    /**
     * Stores a phone number of a call with the country code where it originally
     * occurred.
     * <p>
     * Note the country does not necessarily specifies the country of the phone
     * number itself, but it is the country in which the user was in when the
     * call was placed or received.
     */
    private static final class NumberWithCountryIso {
        public final String number;
        public final String countryIso;

        public NumberWithCountryIso(String number, String countryIso) {
            this.number = number;
            this.countryIso = countryIso;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (!(o instanceof NumberWithCountryIso))
                return false;
            NumberWithCountryIso other = (NumberWithCountryIso) o;
            return TextUtils.equals(number, other.number) && TextUtils.equals(countryIso, other.countryIso);
        }

        @Override
        public int hashCode() {
            return (number == null ? 0 : number.hashCode()) ^ (countryIso == null ? 0 : countryIso.hashCode());
        }
    }

    /** The time in millis to delay starting the thread processing requests. */
    private static final int START_PROCESSING_REQUESTS_DELAY_MILLIS = 1000;

    /** The size of the cache of contact info. */
    private static final int CONTACT_INFO_CACHE_SIZE = 100;

    public static  Context mContext;
    private final ContactInfoHelper mContactInfoHelper;
    private final CallFetcher mCallFetcher;
    private ViewTreeObserver mViewTreeObserver = null;

    /**
     * A cache of the contact details for the phone numbers in the call log.
     * <p>
     * The content of the cache is expired (but not purged) whenever the
     * application comes to the foreground.
     * <p>
     * The key is number with the country in which the call was placed or
     * received.
     */
    private ExpirableCache<NumberWithCountryIso, ContactInfo> mContactInfoCache;

    /**
     * A request for contact details for the given number.
     */
    private static final class ContactInfoRequest {
        /** The number to look-up. */
        public final String number;
        /**
         * The country in which a call to or from this number was placed or
         * received.
         */
        public final String countryIso;
        /** The cached contact information stored in the call log. */
        public final ContactInfo callLogInfo;

        public ContactInfoRequest(String number, String countryIso, ContactInfo callLogInfo) {
            this.number = number;
            this.countryIso = countryIso;
            this.callLogInfo = callLogInfo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof ContactInfoRequest))
                return false;

            ContactInfoRequest other = (ContactInfoRequest) obj;

            if (!TextUtils.equals(number, other.number))
                return false;
            if (!TextUtils.equals(countryIso, other.countryIso))
                return false;
            if (!Objects.equal(callLogInfo, other.callLogInfo))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((callLogInfo == null) ? 0 : callLogInfo.hashCode());
            result = prime * result + ((countryIso == null) ? 0 : countryIso.hashCode());
            result = prime * result + ((number == null) ? 0 : number.hashCode());
            return result;
        }
    }

    /**
     * List of requests to update contact details.
     * <p>
     * Each request is made of a phone number to look up, and the contact info
     * currently stored in the call log for this number.
     * <p>
     * The requests are added when displaying the contacts and are processed by
     * a background thread.
     */
    private final LinkedList<ContactInfoRequest> mRequests;

    private boolean mLoading = true;
    private static final int REDRAW = 1;
    private static final int START_THREAD = 2;

    private QueryThread mCallerIdThread;

    /** Instance of helper class for managing views. */
    private final CallLogListItemHelper mCallLogViewsHelper;

    /** Helper to set up contact photos. */
    private final ContactPhotoManager mContactPhotoManager;
    /** Helper to parse and process phone numbers. */
    private PhoneNumberHelper mPhoneNumberHelper;
    /** Helper to group call log entries. */
    private final CallLogGroupBuilder mCallLogGroupBuilder;

    /** Can be set to true by tests to disable processing of requests. */
    private volatile boolean mRequestProcessingDisabled = false;

    /** Listener for the primary action in the list, opens the call details. */
    private final View.OnClickListener mPrimaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // by yuan 在历史记录列表中，点击某一项，弹出联系人历史记录详情页面 ddd
            IntentProvider intentProvider = (IntentProvider) view.getTag();
            Log.i(TAG,"callLogAdapter--"+intentProvider.getIntent(mContext).toString());
            if (intentProvider != null) {
                mContext.startActivity(intentProvider.getIntent(mContext));
            }

        }
    };

    private final View.OnLongClickListener mPrimaryLongActionListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            /** zzz */
            // ip call, maybe coded by yuan?
            // final IPCall ipcall = new IPCall(mContext);
            // String[] itemchoice = null;
            // if (!ipcall.isCDMAIPEnabled() && !ipcall.isGSMIPEnabled()) {
            // return false;
            // }
            // if (ipcall.isCDMAIPEnabled() && ipcall.isGSMIPEnabled()) {
            // itemchoice = new String[] {
            // mContext.getString(R.string.ip_call_one),
            // mContext.getString(R.string.ip_call_two) };
            // } else {
            // if (ipcall.isCDMAIPEnabled()) {
            // itemchoice = new String[] {
            // mContext.getString(R.string.ip_call_one) };
            // }
            // if (ipcall.isGSMIPEnabled()) {
            // itemchoice = new String[] {
            // mContext.getString(R.string.ip_call_two) };
            // }
            // }
            // final int length = itemchoice.length;
            // final boolean isCDMA = ipcall.isCDMAIPEnabled();
            // final boolean isGSM = ipcall.isGSMIPEnabled();
            // String phoneNumber1 = null;
            // IntentProvider intentProvider = (IntentProvider) view.getTag();
            // Log.v("longclick",
            // intentProvider.getIntent(mContext).getData().toString());
            // if (intentProvider != null) {
            // phoneNumber1 =
            // getPhoneNumberForUri(intentProvider.getIntent(mContext).getData());
            // }
            // final String phoneNumber = phoneNumber1;
            // new AlertDialog.Builder(mContext,
            // 0).setTitle(R.string.call_ip_dialog_title)
            // .setItems(itemchoice, new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int which) {
            // if (length == 2) {
            // if (which == 0) {
            // call(ipcall.getCDMAIPCode() + phoneNumber);
            // } else {
            // call(ipcall.getGSMIPCode() + phoneNumber);
            // }
            // } else {
            // if (isCDMA) {
            // call(ipcall.getCDMAIPCode() + phoneNumber);
            // }
            // if (isGSM) {
            // call(ipcall.getGSMIPCode() + phoneNumber);
            // }
            // }
            // }
            // }).setNegativeButton(R.string.menu_doNotSave, null).show();
            // return false;

            /** zzz */
//        	在通话记录列表长按某个记录，实现删除该记录联系人的所有通话记录的功能  电话模块功能7
            String s = null;
            IntentProvider intentProvider = (IntentProvider) view.getTag();
            if (intentProvider == null) {
                Log.w(TAG, "intentProvider == null");
                return false;
            }
//            在数据库中，根据通话记录id，得到该记录电话号码
            if (intentProvider.getIntent(mContext).getData() != null) {
                s = getPhoneNumberForUri(intentProvider.getIntent(mContext).getData());
            } else if (intentProvider.getIntent(mContext).hasExtra(CallDetailActivity.EXTRA_CALL_LOG_IDS)) {

                long id = intentProvider.getIntent(mContext).getLongArrayExtra(CallDetailActivity.EXTRA_CALL_LOG_IDS)[0];
                s = getPhoneNumberForUri(ContentUris.withAppendedId(Calls.CONTENT_URI, id));
            }
            final String phoneNumber = s;

            Log.i(TAG, "phoneNumber - " + phoneNumber);
            String[] itemchoice = new String[] { mContext.getString(R.string.calllog_delete_all_of_this_number) };
            
//            弹出是否删除该人通话记录对话框
            
            new AlertDialog.Builder(mContext, 0).setTitle(R.string.calllog_options)
                    .setItems(itemchoice, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(TAG, "onClick");

                            // final String phoneNumber =
                            // getPhoneCallDetailsForUri(callUris[0]).number.toString();
                            //在通话记录中，根据该通话记录id删除通话记录
                            ContentResolver cr = mContext.getContentResolver();
                            Cursor cursor = cr.query(Calls.CONTENT_URI, new String[] { Calls.NUMBER, Calls._ID }, null,
                                    null, null);
                            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                String number = cursor.getString(0);
                                String id = cursor.getString(1);
                                
                                if (!number.isEmpty() && number.endsWith(phoneNumber)) {
                                    cr.delete(Calls.CONTENT_URI, Calls._ID + " = " + id, null);
                                }
                            }
                            cursor.close();
                            // cr.delete(Calls.CONTENT_URI, Calls.NUMBER + " = "
                            // + phoneNumber, null);
                        }
                    }).setNegativeButton(R.string.cancel, null).show();
            return false;

            // return false;
        }
    };

    // by yuan
//    通过URI获取电话号码
    /** Return the phone call details for a given call log URI. */
    private String getPhoneNumberForUri(Uri callUri) {

        final String[] CALL_LOG_PROJECTION = new String[] { CallLog.Calls.NUMBER };
        ContentResolver resolver = mContext.getContentResolver();

        Cursor callCursor = resolver.query(callUri, CALL_LOG_PROJECTION, null, null, null);
        try {
            if (callCursor == null || !callCursor.moveToFirst()) {
                throw new IllegalArgumentException("Cannot find content: " + callUri);
            }
            return callCursor.getString(0);

        } finally {
            if (callCursor != null) {
                callCursor.close();
            }
        }
    }
//调起拨号 电话模块功能5
    public void call(String number) {
        // try {
        // ITelephonyMSim telephony =
        // ITelephonyMSim.Stub.asInterface(ServiceManager
        // .getService(Context.MSIM_TELEPHONY_SERVICE));
        // telephony.call(number, 0);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        /** zzz */
        Intent intent = new Intent();
        intent.setAction("edu.bupt.action.EDIAL");
        intent.putExtra("digit", number);
        mContext.startService(intent);

    }

    // edited by yuan
    //点击通话记录列表中某项的拨号图标，若设置IP拨号模式，弹出IP拨号选择框，选择卡一IP拨号，卡二IP拨号，或直接拨号。
    /** Listener for the secondary action in the list, either call or play. */
    private final View.OnClickListener mSecondaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final String phoneNumber = (String) view.getTag();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.call_ip_choose_dialog, null);
            Builder dialogBuilder = new AlertDialog.Builder(mContext).setTitle(R.string.call_ip_dialog_title)
                    .setView(layout).setNegativeButton(R.string.cancel, null);
            final AlertDialog mDialog = dialogBuilder.create();
            layout.findViewById(R.id.imageButton_call).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // try {
                    // ITelephonyMSim telephony =
                    // ITelephonyMSim.Stub.asInterface(ServiceManager
                    // .getService(Context.MSIM_TELEPHONY_SERVICE));
                    // telephony.call(phoneNumber, 0);
                    // mDialog.dismiss();
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }

                    /** zzz */
                    Intent intent = new Intent();
                    intent.setAction("edu.bupt.action.EDIAL");
                    intent.putExtra("digit", phoneNumber);
                    mContext.startService(intent);
                }

            });
            final IPCall ipcall = new IPCall(mContext);
            //选择卡一IP拨号，在拨叫号码加上相应拨号前缀，调起拨号方法
            if (ipcall.isCDMAIPEnabled()) {
                layout.findViewById(R.id.imageButton_ipcall_one).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.imageButton_ipcall_one).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        mDialog.dismiss();
                        call(ipcall.getCDMAIPCode() + phoneNumber);
                    }
                });

            }
            
            //选择卡二IP拨号，在拨叫号码加上相应拨号前缀，调起拨号方法
            if (ipcall.isGSMIPEnabled()) {
                layout.findViewById(R.id.imageButton_ipcall_two).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.imageButton_ipcall_two).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        mDialog.dismiss();
                        call(ipcall.getGSMIPCode() + phoneNumber);
                    }
                });

            }
//若选择普通拨号，直接调起拨号方法
            if (!ipcall.isCDMAIPEnabled() && !ipcall.isGSMIPEnabled()) {
                // try {
                // ITelephonyMSim telephony =
                // ITelephonyMSim.Stub.asInterface(ServiceManager
                // .getService(Context.MSIM_TELEPHONY_SERVICE));
                // telephony.call((String) view.getTag(), 0);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }

                /** zzz */
                Intent intent = new Intent();
                intent.setAction("edu.bupt.action.EDIAL");
                intent.putExtra("digit", (String) view.getTag());
                mContext.startService(intent);
            } else {
                mDialog.show();
            }
        }
    };

    private final View.OnClickListener mThirdaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mContext.startActivity(((ViewEntry) view.getTag()).secondaryIntent);
        }
    };

    @Override
    public boolean onPreDraw() {
        // We only wanted to listen for the first draw (and this is it).
        unregisterPreDrawListener();

        // Only schedule a thread-creation message if the thread hasn't been
        // created yet. This is purely an optimization, to queue fewer messages.
        if (mCallerIdThread == null) {
            mHandler.sendEmptyMessageDelayed(START_THREAD, START_PROCESSING_REQUESTS_DELAY_MILLIS);
        }

        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REDRAW:
                notifyDataSetChanged();
                break;
            case START_THREAD:
                startRequestProcessing();
                break;
            }
        }
    };

    CallLogAdapter(Context context, CallFetcher callFetcher, ContactInfoHelper contactInfoHelper) {
        super(context);

        mContext = context;
        mCallFetcher = callFetcher;
        mContactInfoHelper = contactInfoHelper;

        mContactInfoCache = ExpirableCache.create(CONTACT_INFO_CACHE_SIZE);
        mRequests = new LinkedList<ContactInfoRequest>();

        Resources resources = mContext.getResources();
        CallTypeHelper callTypeHelper = new CallTypeHelper(resources);

        mContactPhotoManager = ContactPhotoManager.getInstance(mContext);
        mPhoneNumberHelper = new PhoneNumberHelper(resources);
        PhoneCallDetailsHelper phoneCallDetailsHelper = new PhoneCallDetailsHelper(resources, callTypeHelper,
                mPhoneNumberHelper);
        mCallLogViewsHelper = new CallLogListItemHelper(phoneCallDetailsHelper, mPhoneNumberHelper, resources);
        mCallLogGroupBuilder = new CallLogGroupBuilder(this);
    }

    /**
     * Requery on background thread when {@link Cursor} changes.
     */
    @Override
    protected void onContentChanged() {
        mCallFetcher.fetchCalls();
    }

    void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public boolean isEmpty() {
        if (mLoading) {
            // We don't want the empty state to show when loading.
            return false;
        } else {
            return super.isEmpty();
        }
    }

    /**
     * Starts a background thread to process contact-lookup requests, unless one
     * has already been started.
     */
    private synchronized void startRequestProcessing() {
        // For unit-testing.
        if (mRequestProcessingDisabled)
            return;

        // Idempotence... if a thread is already started, don't start another.
        if (mCallerIdThread != null)
            return;

        mCallerIdThread = new QueryThread();
        mCallerIdThread.setPriority(Thread.MIN_PRIORITY);
        mCallerIdThread.start();
    }

    /**
     * Stops the background thread that processes updates and cancels any
     * pending requests to start it.
     */
    public synchronized void stopRequestProcessing() {
        // Remove any pending requests to start the processing thread.
        mHandler.removeMessages(START_THREAD);
        if (mCallerIdThread != null) {
            // Stop the thread; we are finished with it.
            mCallerIdThread.stopProcessing();
            mCallerIdThread.interrupt();
            mCallerIdThread = null;
        }
    }

    /**
     * Stop receiving onPreDraw() notifications.
     */
    private void unregisterPreDrawListener() {
        if (mViewTreeObserver != null && mViewTreeObserver.isAlive()) {
            mViewTreeObserver.removeOnPreDrawListener(this);
        }
        mViewTreeObserver = null;
    }

    public void invalidateCache() {
        mContactInfoCache.expireAll();

        // Restart the request-processing thread after the next draw.
        stopRequestProcessing();
        unregisterPreDrawListener();
    }

    /**
     * Enqueues a request to look up the contact details for the given phone
     * number.
     * <p>
     * It also provides the current contact info stored in the call log for this
     * number.
     * <p>
     * If the {@code immediate} parameter is true, it will start immediately the
     * thread that looks up the contact information (if it has not been already
     * started). Otherwise, it will be started with a delay. See
     * {@link #START_PROCESSING_REQUESTS_DELAY_MILLIS}.
     */
    @VisibleForTesting
    void enqueueRequest(String number, String countryIso, ContactInfo callLogInfo, boolean immediate) {
        ContactInfoRequest request = new ContactInfoRequest(number, countryIso, callLogInfo);
        synchronized (mRequests) {
            if (!mRequests.contains(request)) {
                mRequests.add(request);
                mRequests.notifyAll();
            }
        }
        if (immediate)
            startRequestProcessing();
    }

    /**
     * Queries the appropriate content provider for the contact associated with
     * the number.
     * <p>
     * Upon completion it also updates the cache in the call log, if it is
     * different from {@code callLogInfo}.
     * <p>
     * The number might be either a SIP address or a phone number.
     * <p>
     * It returns true if it updated the content of the cache and we should
     * therefore tell the view to update its content.
     */
    private boolean queryContactInfo(String number, String countryIso, ContactInfo callLogInfo) {
        final ContactInfo info = mContactInfoHelper.lookupNumber(number, countryIso);

        if (info == null) {
            // The lookup failed, just return without requesting to update the
            // view.
            return false;
        }

        // Check the existing entry in the cache: only if it has changed we
        // should update the
        // view.
        NumberWithCountryIso numberCountryIso = new NumberWithCountryIso(number, countryIso);
        ContactInfo existingInfo = mContactInfoCache.getPossiblyExpired(numberCountryIso);
        boolean updated = (existingInfo != ContactInfo.EMPTY) && !info.equals(existingInfo);

        // Store the data in the cache so that the UI thread can use to display
        // it. Store it
        // even if it has not changed so that it is marked as not expired.
        mContactInfoCache.put(numberCountryIso, info);
        // Update the call log even if the cache it is up-to-date: it is
        // possible that the cache
        // contains the value from a different call log entry.
        updateCallLogContactInfoCache(number, countryIso, info, callLogInfo);
        return updated;
    }

    /*
     * Handles requests for contact name and number type.
     */
    private class QueryThread extends Thread {
        private volatile boolean mDone = false;

        public QueryThread() {
            super("CallLogAdapter.QueryThread");
        }

        public void stopProcessing() {
            mDone = true;
        }

        @Override
        public void run() {
            boolean needRedraw = false;
            while (true) {
                // Check if thread is finished, and if so return immediately.
                if (mDone)
                    return;

                // Obtain next request, if any is available.
                // Keep synchronized section small.
                ContactInfoRequest req = null;
                synchronized (mRequests) {
                    if (!mRequests.isEmpty()) {
                        req = mRequests.removeFirst();
                    }
                }

                if (req != null) {
                    // Process the request. If the lookup succeeds, schedule a
                    // redraw.
                    needRedraw |= queryContactInfo(req.number, req.countryIso, req.callLogInfo);
                } else {
                    // Throttle redraw rate by only sending them when there are
                    // more requests.
                    if (needRedraw) {
                        needRedraw = false;
                        mHandler.sendEmptyMessage(REDRAW);
                    }

                    // Wait until another request is available, or until this
                    // thread is no longer needed (as indicated by being
                    // interrupted).
                    try {
                        synchronized (mRequests) {
                            mRequests.wait(1000);
                        }
                    } catch (InterruptedException ie) {
                        // Ignore, and attempt to continue processing requests.
                    }
                }
            }
        }
    }

    @Override
    protected void addGroups(Cursor cursor) {
        mCallLogGroupBuilder.addGroups(cursor);
    }

    @Override
    protected View newStandAloneView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_log_list_item, parent, false);
        findAndCacheViews(view);
        return view;
    }

    @Override
    protected void bindStandAloneView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, 1);
    }

    @Override
    protected View newChildView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_log_list_item, parent, false);
        findAndCacheViews(view);
        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, 1);
    }

    @Override
    protected View newGroupView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_log_list_item, parent, false);
        findAndCacheViews(view);
        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, int groupSize, boolean expanded) {
        bindView(view, cursor, groupSize);
    }

    private void findAndCacheViews(View view) {
        // Get the views to bind to.
        CallLogListItemViews views = CallLogListItemViews.fromView(view);
        views.primaryActionView.setOnClickListener(mPrimaryActionListener);

        /** zzz */
        //绑定listener
        views.primaryActionView.setOnLongClickListener(mPrimaryLongActionListener);

        views.secondaryActionView.setOnClickListener(mSecondaryActionListener);
        views.thirdaryActionView.setOnClickListener(mThirdaryActionListener);
        view.setTag(views);
    }

    /**
     * Binds the views in the entry to the data in the call log.
     * 
     * @param view
     *            the view corresponding to this entry
     * @param c
     *            the cursor pointing to the entry in the call log
     * @param count
     *            the number of entries in the current item, greater than 1 if
     *            it is a group
     */
    private void bindView(View view, Cursor c, int count) {
        final CallLogListItemViews views = (CallLogListItemViews) view.getTag();
        final int section = c.getInt(CallLogQuery.SECTION);

        // This might be a header: check the value of the section column in the
        // cursor.
        if (section == CallLogQuery.SECTION_NEW_HEADER || section == CallLogQuery.SECTION_OLD_HEADER) {
            views.primaryActionView.setVisibility(View.GONE);
            views.bottomDivider.setVisibility(View.GONE);
            views.listHeaderTextView.setVisibility(View.VISIBLE);
            views.listHeaderTextView.setText(section == CallLogQuery.SECTION_NEW_HEADER ? R.string.call_log_new_header
                    : R.string.call_log_old_header);
            // Nothing else to set up for a header.
            return;
        }
        // Default case: an item in the call log.
        views.primaryActionView.setVisibility(View.VISIBLE);
        views.bottomDivider.setVisibility(isLastOfSection(c) ? View.GONE : View.VISIBLE);
        views.listHeaderTextView.setVisibility(View.GONE);

        final String number = c.getString(CallLogQuery.NUMBER);
        final long date = c.getLong(CallLogQuery.DATE);
        final long duration = c.getLong(CallLogQuery.DURATION);
        final int callType = c.getInt(CallLogQuery.CALL_TYPE);
        Log.i(TAG,"callType-ddd--"+String.valueOf(callType));
        final String countryIso = c.getString(CallLogQuery.COUNTRY_ISO);

        final ContactInfo cachedContactInfo = getContactInfoFromCallLog(c);

        views.primaryActionView.setTag(IntentProvider.getCallDetailIntentProvider(this, c.getPosition(),
                c.getLong(CallLogQuery.ID), count));
        Log.i(TAG,"position--"+c.getPosition());
        Log.i(TAG,"ID--"+c.getLong(CallLogQuery.ID));
        Log.i(TAG,"count--"+count);
        
        // Log.v("eeeee",IntentProvider.getCallDetailIntentProvider(this,
        // c.getPosition(), c.getLong(CallLogQuery.ID),
        // count).getIntent(mContext).getDataString());
        // Store away the voicemail information so we can play it directly.
        if (callType == Calls.VOICEMAIL_TYPE) {
            String voicemailUri = c.getString(CallLogQuery.VOICEMAIL_URI);
            final long rowId = c.getLong(CallLogQuery.ID);
            // views.secondaryActionView.setTag(IntentProvider.getPlayVoicemailIntentProvider(rowId,
            // voicemailUri));
        } else if (!TextUtils.isEmpty(number)) {
            // Store away the number so we can call it directly if you click on
            // the call icon.
            // views.secondaryActionView.setTag(IntentProvider.getReturnCallIntentProvider(number));
            views.secondaryActionView.setTag(number);


            ViewEntry entry = new ViewEntry(mContext.getString(R.string.menu_callNumber,
                    FormatUtils.forceLeftToRight(number)), ContactsUtils.getCallIntent(number), mContext.getString(
                    R.string.description_call, number));
            entry.setSecondaryAction(R.drawable.ic_text_holo_dark,
                    new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", number, null)),
                    mContext.getString(R.string.description_send_text_message, number));
            views.thirdaryActionView.setTag(entry);

        } else {
            // No action enabled.
            views.secondaryActionView.setTag(null);
            views.thirdaryActionView.setTag(null);
        }

        // Lookup contacts with this number
        NumberWithCountryIso numberCountryIso = new NumberWithCountryIso(number, countryIso);
        ExpirableCache.CachedValue<ContactInfo> cachedInfo = mContactInfoCache.getCachedValue(numberCountryIso);
        ContactInfo info = cachedInfo == null ? null : cachedInfo.getValue();
        if (!mPhoneNumberHelper.canPlaceCallsTo(number) || mPhoneNumberHelper.isVoicemailNumber(number)) {
            // If this is a number that cannot be dialed, there is no point in
            // looking up a contact
            // for it.
            info = ContactInfo.EMPTY;
        } else if (cachedInfo == null) {
            mContactInfoCache.put(numberCountryIso, ContactInfo.EMPTY);
            // Use the cached contact info from the call log.
            info = cachedContactInfo;
            // The db request should happen on a non-UI thread.
            // Request the contact details immediately since they are currently
            // missing.
            enqueueRequest(number, countryIso, cachedContactInfo, true);
            // We will format the phone number when we make the background
            // request.
        } else {
            if (cachedInfo.isExpired()) {
                // The contact info is no longer up to date, we should request
                // it. However, we
                // do not need to request them immediately.
                enqueueRequest(number, countryIso, cachedContactInfo, false);
            } else if (!callLogInfoMatches(cachedContactInfo, info)) {
                // The call log information does not match the one we have, look
                // it up again.
                // We could simply update the call log directly, but that needs
                // to be done in a
                // background thread, so it is easier to simply request a new
                // lookup, which will, as
                // a side-effect, update the call log.
                enqueueRequest(number, countryIso, cachedContactInfo, false);
            }

            if (info == ContactInfo.EMPTY) {
                // Use the cached contact info from the call log.
                info = cachedContactInfo;
            }
        }

        final Uri lookupUri = info.lookupUri;
        final String name = info.name;
        final int ntype = info.type;
        final String label = info.label;
        final long photoId = info.photoId;
        CharSequence formattedNumber = info.formattedNumber;
        final int[] callTypes = getCallTypes(c, count);
        final String geocode = c.getString(CallLogQuery.GEOCODED_LOCATION);
        final PhoneCallDetails details;
        Log.i(TAG,"label-ddd--"+label);
        Log.i(TAG,"info-ddd--"+info.toString());
        if (TextUtils.isEmpty(name)) {
            details = new PhoneCallDetails(number, formattedNumber, countryIso, geocode, callTypes, date, duration,
                    c.getInt(CallLogQuery.SUB_ID));
        } else {
            // We do not pass a photo id since we do not need the high-res
            // picture.
            details = new PhoneCallDetails(number, formattedNumber, countryIso, geocode, callTypes, date, duration,
                    name, ntype, label, lookupUri, null, c.getInt(CallLogQuery.SUB_ID)); // by
                                                                                         // yuan
        }

        final boolean isNew = c.getInt(CallLogQuery.IS_READ) == 0;
        // New items also use the highlighted version of the text.
        final boolean isHighlighted = isNew;
        mCallLogViewsHelper.setPhoneCallDetails(views, details, isHighlighted);

        /** zzz */
        // add arg number
        setPhoto(views, photoId, lookupUri, number);

        // Listen for the first draw
        if (mViewTreeObserver == null) {
            mViewTreeObserver = view.getViewTreeObserver();
            mViewTreeObserver.addOnPreDrawListener(this);
        }
    }

    // by yuan
    //描述一级、二级动作
    static final class ViewEntry {
        public final String text;
        public final Intent primaryIntent;
        /** The description for accessibility of the primary action. */
        public final String primaryDescription;

        public CharSequence label = null;
        /** Icon for the secondary action. */
        public int secondaryIcon = 0;
        /**
         * Intent for the secondary action. If not null, an icon must be
         * defined.
         */
        public Intent secondaryIntent = null;
        /** The description for accessibility of the secondary action. */
        public String secondaryDescription = null;

        public ViewEntry(String text, Intent intent, String description) {
            this.text = text;
            primaryIntent = intent;
            primaryDescription = description;
        }

        public void setSecondaryAction(int icon, Intent intent, String description) {
            secondaryIcon = icon;
            secondaryIntent = intent;
            secondaryDescription = description;
        }
    }

    /** Returns true if this is the last item of a section. */
    //判断是否是最后一项通话记录，若是，返回true
    private boolean isLastOfSection(Cursor c) {
        if (c.isLast())
            return true;
        final int section = c.getInt(CallLogQuery.SECTION);
        if (!c.moveToNext())
            return true;
        final int nextSection = c.getInt(CallLogQuery.SECTION);
        c.moveToPrevious();
        return section != nextSection;
    }

    /**
     * Checks whether the contact info from the call log matches the one from
     * the contacts db.
     */
    private boolean callLogInfoMatches(ContactInfo callLogInfo, ContactInfo info) {
        // The call log only contains a subset of the fields in the contacts db.
        // Only check those.
        return TextUtils.equals(callLogInfo.name, info.name) && callLogInfo.type == info.type
                && TextUtils.equals(callLogInfo.label, info.label);
    }

    /**
     * Stores the updated contact info in the call log if it is different from
     * the current one.
     */
    private void updateCallLogContactInfoCache(String number, String countryIso, ContactInfo updatedInfo,
            ContactInfo callLogInfo) {
        final ContentValues values = new ContentValues();
        boolean needsUpdate = false;

        if (callLogInfo != null) {
            if (!TextUtils.equals(updatedInfo.name, callLogInfo.name)) {
                values.put(Calls.CACHED_NAME, updatedInfo.name);
                needsUpdate = true;
            }

            if (updatedInfo.type != callLogInfo.type) {
                values.put(Calls.CACHED_NUMBER_TYPE, updatedInfo.type);
                needsUpdate = true;
            }

            if (!TextUtils.equals(updatedInfo.label, callLogInfo.label)) {
                values.put(Calls.CACHED_NUMBER_LABEL, updatedInfo.label);
                needsUpdate = true;
            }
            if (!UriUtils.areEqual(updatedInfo.lookupUri, callLogInfo.lookupUri)) {
                values.put(Calls.CACHED_LOOKUP_URI, UriUtils.uriToString(updatedInfo.lookupUri));
                needsUpdate = true;
            }
            if (!TextUtils.equals(updatedInfo.normalizedNumber, callLogInfo.normalizedNumber)) {
                values.put(Calls.CACHED_NORMALIZED_NUMBER, updatedInfo.normalizedNumber);
                needsUpdate = true;
            }
            if (!TextUtils.equals(updatedInfo.number, callLogInfo.number)) {
                values.put(Calls.CACHED_MATCHED_NUMBER, updatedInfo.number);
                needsUpdate = true;
            }
            if (updatedInfo.photoId != callLogInfo.photoId) {
                values.put(Calls.CACHED_PHOTO_ID, updatedInfo.photoId);
                needsUpdate = true;
            }
            if (!TextUtils.equals(updatedInfo.formattedNumber, callLogInfo.formattedNumber)) {
                values.put(Calls.CACHED_FORMATTED_NUMBER, updatedInfo.formattedNumber);
                needsUpdate = true;
            }
        } else {
            // No previous values, store all of them.
            values.put(Calls.CACHED_NAME, updatedInfo.name);
            values.put(Calls.CACHED_NUMBER_TYPE, updatedInfo.type);
            values.put(Calls.CACHED_NUMBER_LABEL, updatedInfo.label);
            values.put(Calls.CACHED_LOOKUP_URI, UriUtils.uriToString(updatedInfo.lookupUri));
            values.put(Calls.CACHED_MATCHED_NUMBER, updatedInfo.number);
            values.put(Calls.CACHED_NORMALIZED_NUMBER, updatedInfo.normalizedNumber);
            values.put(Calls.CACHED_PHOTO_ID, updatedInfo.photoId);
            values.put(Calls.CACHED_FORMATTED_NUMBER, updatedInfo.formattedNumber);
            needsUpdate = true;
        }

        if (!needsUpdate)
            return;

        if (countryIso == null) {
            mContext.getContentResolver().update(Calls.CONTENT_URI, values,
                    Calls.NUMBER + " = ? AND " + Calls.COUNTRY_ISO + " IS NULL", new String[] { number });
        } else {
            mContext.getContentResolver().update(Calls.CONTENT_URI, values,
                    Calls.NUMBER + " = ? AND " + Calls.COUNTRY_ISO + " = ?", new String[] { number, countryIso });
        }
    }

    /** Returns the contact information as stored in the call log. */
    private ContactInfo getContactInfoFromCallLog(Cursor c) {
        ContactInfo info = new ContactInfo();
        info.lookupUri = UriUtils.parseUriOrNull(c.getString(CallLogQuery.CACHED_LOOKUP_URI));
        info.name = c.getString(CallLogQuery.CACHED_NAME);
        info.type = c.getInt(CallLogQuery.CACHED_NUMBER_TYPE);
        info.label = c.getString(CallLogQuery.CACHED_NUMBER_LABEL);
        String matchedNumber = c.getString(CallLogQuery.CACHED_MATCHED_NUMBER);
        info.number = matchedNumber == null ? c.getString(CallLogQuery.NUMBER) : matchedNumber;
        info.normalizedNumber = c.getString(CallLogQuery.CACHED_NORMALIZED_NUMBER);
        info.photoId = c.getLong(CallLogQuery.CACHED_PHOTO_ID);
        info.photoUri = null; // We do not cache the photo URI.
        info.formattedNumber = c.getString(CallLogQuery.CACHED_FORMATTED_NUMBER);
        return info;
    }

    /**
     * Returns the call types for the given number of items in the cursor.
     * <p>
     * It uses the next {@code count} rows in the cursor to extract the types.
     * <p>
     * It position in the cursor is unchanged by this function.
     */
    private int[] getCallTypes(Cursor cursor, int count) {
        int position = cursor.getPosition();
        int[] callTypes = new int[count];
        for (int index = 0; index < count; ++index) {
            callTypes[index] = cursor.getInt(CallLogQuery.CALL_TYPE);
            cursor.moveToNext();
        }
        cursor.moveToPosition(position);
        return callTypes;
    }

    // add arg number
    private void setPhoto(CallLogListItemViews views, long photoId, final Uri contactUri, final String number) {

        /** zzz */
        // views.quickContactView.assignContactUri(contactUri);

        mContactPhotoManager.loadThumbnail(views.quickContactView, photoId, true);

        views.quickContactView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.v(TAG, "onClick");
                Log.i(TAG, "contactUri - " + contactUri);
                if (contactUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(contactUri);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    // TODO
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    /**
     * Sets whether processing of requests for contact details should be
     * enabled.
     * <p>
     * This method should be called in tests to disable such processing of
     * requests when not needed.
     */
    @VisibleForTesting
    void disableRequestProcessingForTest() {
        mRequestProcessingDisabled = true;
    }

    @VisibleForTesting
    void injectContactInfoForTest(String number, String countryIso, ContactInfo contactInfo) {
        NumberWithCountryIso numberCountryIso = new NumberWithCountryIso(number, countryIso);
        mContactInfoCache.put(numberCountryIso, contactInfo);
    }

    @Override
    public void addGroup(int cursorPosition, int size, boolean expanded) {
        super.addGroup(cursorPosition, size, expanded);
    }

    /*
     * Get the number from the Contacts, if available, since sometimes the
     * number provided by caller id may not be formatted properly depending on
     * the carrier (roaming) in use at the time of the incoming call. Logic : If
     * the caller-id number starts with a "+", use it Else if the number in the
     * contacts starts with a "+", use that one Else if the number in the
     * contacts is longer, use that one
     */
    public String getBetterNumberFromContacts(String number, String countryIso) {
        String matchingNumber = null;
        // Look in the cache first. If it's not found then query the Phones db
        NumberWithCountryIso numberCountryIso = new NumberWithCountryIso(number, countryIso);
        ContactInfo ci = mContactInfoCache.getPossiblyExpired(numberCountryIso);
        if (ci != null && ci != ContactInfo.EMPTY) {
            matchingNumber = ci.number;
        } else {
            try {
                Cursor phonesCursor = mContext.getContentResolver().query(
                        Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, number), PhoneQuery._PROJECTION, null,
                        null, null);
                if (phonesCursor != null) {
                    if (phonesCursor.moveToFirst()) {
                        matchingNumber = phonesCursor.getString(PhoneQuery.MATCHED_NUMBER);
                    }
                    phonesCursor.close();
                }
            } catch (Exception e) {
                // Use the number from the call log
            }
        }
        if (!TextUtils.isEmpty(matchingNumber)
                && (matchingNumber.startsWith("+") || matchingNumber.length() > number.length())) {
            number = matchingNumber;
        }
        return number;
    }
}
