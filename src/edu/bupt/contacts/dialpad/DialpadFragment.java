/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (c) 2011-12, The Linux Foundation. All rights reserved.
 * Not a Contribution, Apache license notifications and license are retained
 * for attribution purposes only
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

package edu.bupt.contacts.dialpad;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Contacts.Intents.Insert;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.Contacts.PhonesColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.MSimTelephonyManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;
import edu.bupt.contacts.SpecialCharSequenceMgr;
import edu.bupt.contacts.activities.DialtactsActivity;
import edu.bupt.contacts.msim.MultiSimConfig;
import edu.bupt.contacts.util.Constants;
import edu.bupt.contacts.util.PhoneNumberFormatter;
import edu.bupt.contacts.util.StopWatch;

import com.android.internal.telephony.ITelephony;
import edu.bupt.contacts.ipcall.IPCall;
import edu.bupt.contacts.phone.CallLogAsync;
import edu.bupt.contacts.phone.HapticFeedback;
import edu.bupt.contacts.settings.*;


/**
 * 北邮ANT实验室
 * ddd
 * 
 * 拨号盘
 * 在拨号盘界面用户输入号码后，用户能够快捷（不超过两步）发送信息    电话功能31
 * 在拨号盘界面用户输入号码后，用户能够快捷（不超过两步）发起IP拨号呼叫    电话功能30
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */


/**
 * Fragment that displays a twelve-key phone dialpad.
 */
public class DialpadFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener,
        View.OnKeyListener, AdapterView.OnItemClickListener, TextWatcher, PopupMenu.OnMenuItemClickListener,
        DialpadImageButton.OnPressedListener {
    private static final String TAG = DialpadFragment.class.getSimpleName();

    private static final boolean DEBUG = DialtactsActivity.DEBUG;

    private static final String SUBSCRIPTION_KEY = "subscription";
    private static final String EMPTY_NUMBER = "";

    /** The length of DTMF tones in milliseconds */
    private static final int TONE_LENGTH_MS = 150;
    private static final int TONE_LENGTH_INFINITE = -1;

    /** The DTMF tone volume relative to other sounds in the stream */
    private static final int TONE_RELATIVE_VOLUME = 80;

    /**
     * Stream type used to play the DTMF tones off call, and mapped to the
     * volume control keys
     */
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

    /**
     * View (usually FrameLayout) containing mDigits field. This can be null, in
     * which mDigits isn't enclosed by the container.
     */
    private View mDigitsContainer;
    private EditText mDigits;
    private TextView mMatchedPhones;
    private TextView mMatchedName;

    /**
     * Remembers if we need to clear digits field when the screen is completely
     * gone.
     */
    private boolean mClearDigitsOnStop;

    private View mDelete;
    private ToneGenerator mToneGenerator;
    private final Object mToneGeneratorLock = new Object();
    private View mDialpad;
    private View mMatchList;
    private ListView matchListView;

    /**
     * Remembers the number of dialpad buttons which are pressed at this moment.
     * If it becomes 0, meaning no buttons are pressed, we'll call
     * {@link ToneGenerator#stopTone()}; the method shouldn't be called unless
     * the last key is released.
     */
    private int mDialpadPressCount;

    private View mDialButtonContainer;
    private View mDialButton;
    private ListView mDialpadChooser;
    private DialpadChooserAdapter mDialpadChooserAdapter;
    // ddd  拨号盘添加发送短信按钮
    private View mSmsButton;
    private Button asiaButton;
    private Button northAmericaButton;
    private Button southAmericaButton;
    /**
     * Regular expression prohibiting manual phone call. Can be empty, which
     * means "no rule".
     */
    private String mProhibitedPhoneNumberRegexp;

    private int mSubscription = 0;

    // Last number dialed, retrieved asynchronously from the call DB
    // in onCreate. This number is displayed when the user hits the
    // send key and cleared in onPause.
    private final CallLogAsync mCallLog = new CallLogAsync();
    private String mLastNumberDialed = EMPTY_NUMBER;

    // determines if we want to playback local DTMF tones.
    private boolean mDTMFToneEnabled;

    // Vibration (haptic feedback) for dialer key presses.
    private final HapticFeedback mHaptic = new HapticFeedback();

    /** Identifier for the "Add Call" intent extra. */
    private static final String ADD_CALL_MODE_KEY = "add_call_mode";

    /**
     * Identifier for intent extra for sending an empty Flash message for CDMA
     * networks. This message is used by the network to simulate a press/depress
     * of the "hookswitch" of a landline phone. Aka "empty flash".
     * 
     * TODO: Using an intent extra to tell the phone to send this flash is a
     * temporary measure. To be replaced with an ITelephony call in the future.
     * TODO: Keep in sync with the string defined in
     * OutgoingCallBroadcaster.java in Phone app until this is replaced with the
     * ITelephony API.
     */
    private static final String EXTRA_SEND_EMPTY_FLASH = "com.android.phone.extra.SEND_EMPTY_FLASH";

    private String mCurrentCountryIso;

    /*
     * added by yuan 拨号盘输入电话号码后补全、匹配联系人
     */
    private List<CallResearchModel> allContactList, totalMatchedList;

    private List<CallResearchModel> rawThreeDaysList, rawThirtyDaysList, rawHistoryList;

    private initQueryDataThread rawThreeDaysListThread;
    private initQueryDataThread rawThirtyDaysListThread;
    private initQueryDataThread rawHistoryListThread;

    private Button mMatchMore, mMatchAddNew;

    ArrayList<String> numbers;

    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        /**
         * Listen for phone state changes so that we can take down the
         * "dialpad chooser" if the phone becomes idle while the chooser UI is
         * visible.
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Log.i(TAG, "PhoneStateListener.onCallStateChanged: "
            // + state + ", '" + incomingNumber + "'");
            if ((state == TelephonyManager.CALL_STATE_IDLE) && dialpadChooserVisible()) {
                // Log.i(TAG,
                // "Call ended with dialpad chooser visible!  Taking it down...");
                // Note there's a race condition in the UI here: the
                // dialpad chooser could conceivably disappear (on its
                // own) at the exact moment the user was trying to select
                // one of the choices, which would be confusing. (But at
                // least that's better than leaving the dialpad chooser
                // onscreen, but useless...)
                showDialpadChooser(false);
            }
        }
    };

    private boolean mWasEmptyBeforeTextChange;

    private CallResearchAdapter adapter;

    /**
     * This field is set to true while processing an incoming DIAL intent, in
     * order to make sure that SpecialCharSequenceMgr actions can be triggered
     * by user input but *not* by a tel: URI passed by some other app. It will
     * be set to false when all digits are cleared.
     */
    private boolean mDigitsFilledByIntent;

    private static final String PREF_DIGITS_FILLED_BY_INTENT = "pref_digits_filled_by_intent";

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        /** zzz */
        Log.d(TAG, "beforeTextChanged");
        Log.i(TAG, " " + mDigits.getText().toString());

        mWasEmptyBeforeTextChange = TextUtils.isEmpty(s);
    }

    @Override
    public void onTextChanged(CharSequence input, int start, int before, int changeCount) {

        /** zzz */
        Log.d(TAG, "onTextChanged");
        Log.i(TAG, " " + mDigits.getText().toString());

        if (mWasEmptyBeforeTextChange != TextUtils.isEmpty(input)) {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        }

        // DTMF Tones do not need to be played here any longer -
        // the DTMF dialer handles that functionality now.
    }

    @Override
    public void afterTextChanged(Editable input) {
        // When DTMF dialpad buttons are being pressed, we delay
        // SpecialCharSequencMgr sequence,
        // since some of SpecialCharSequenceMgr's behavior is too abrupt for the
        // "touch-down"
        // behavior.

        /** zzz */
        Log.d(TAG, "afterTextChanged");
        Log.i(TAG, " " + mDigits.getText().toString());

        if (!mDigitsFilledByIntent && SpecialCharSequenceMgr.handleChars(getActivity(), input.toString(), mDigits)) {
            // A special sequence was entered, clear the digits
            mDigits.getText().clear();
        }

        if (isDigitsEmpty()) {
            mDigitsFilledByIntent = false;
            mDigits.setCursorVisible(false);
        }

        updateDialAndDeleteButtonEnabledState();
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // added by yuan 拨号盘输入电话号码时补全、匹配联系人

        allContactList = new ArrayList<CallResearchModel>();
        totalMatchedList = new ArrayList<CallResearchModel>();

        rawThreeDaysList = new ArrayList<CallResearchModel>();
        rawThirtyDaysList = new ArrayList<CallResearchModel>();
        rawHistoryList = new ArrayList<CallResearchModel>();
        numbers = new ArrayList<String>();

        adapter = new CallResearchAdapter(getActivity());

        initQueryData();
        mCurrentCountryIso = ContactsUtils.getCurrentCountryIso(getActivity());

        try {
            mHaptic.init(getActivity(), getResources().getBoolean(R.bool.config_enable_dialer_key_vibration));
        } catch (Resources.NotFoundException nfe) {
            Log.e(TAG, "Vibrate control bool missing.", nfe);
        }

        setHasOptionsMenu(true);

        mProhibitedPhoneNumberRegexp = getResources().getString(R.string.config_prohibited_phone_number_regexp);

        if (state != null) {
            mDigitsFilledByIntent = state.getBoolean(PREF_DIGITS_FILLED_BY_INTENT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View fragmentView = inflater.inflate(R.layout.dialpad_fragment, container, false);

        // Load up the resources for the text field.
        Resources r = getResources();

        mDigitsContainer = fragmentView.findViewById(R.id.digits_container);
        mDigits = (EditText) fragmentView.findViewById(R.id.digits);
        mDigits.setKeyListener(DialerKeyListener.getInstance());
        mDigits.setOnClickListener(this);
        mDigits.setOnKeyListener(this);
        mDigits.setOnLongClickListener(this);
        mDigits.addTextChangedListener(this);

        // added by yuan 匹配到的电话号码和联系人姓名
        mMatchedPhones = (TextView) fragmentView.findViewById(R.id.textView_match_phonenumber);
        mMatchedName = (TextView) fragmentView.findViewById(R.id.textView_match_name);
        mMatchedPhones.setClickable(true);
        //点击电话号码的OnClickListener
        mMatchedPhones.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mDigits.getText().clear();

                if (mMatchList.isShown()) {
                    mMatchList.setVisibility(View.GONE);
                    mDialpad.setVisibility(View.VISIBLE);
                }

                if (numbers != null && numbers.size() > 1) {//若一个联系人匹配不止一个电话号码，则弹出dialog，提供号码选择
                    final String sequence[] = (String[]) numbers.toArray(new String[numbers.size()]);
                    Builder dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.call_disambig_title)
                            .setItems(sequence, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    mDigits.getText().append(sequence[which]);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {//取消
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.create().show();
                    return;
                }

                if (numbers != null && numbers.size() == 1) {
                    mDigits.getText().append(numbers.get(0).toString());//若一个联系人只匹配一个电话号码，点击后，将该号码显示到输入框中
                }
            }

        });

        PhoneNumberFormatter.setPhoneNumberFormattingTextWatcher(getActivity(), mDigits);

        // Check for the presence of the keypad
        View oneButton = fragmentView.findViewById(R.id.one);
        if (oneButton != null) {
            setupKeypad(fragmentView);
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int minCellSize = (int) (56 * dm.density); // 56dip == minimum size of
                                                   // menu buttons
        int cellCount = dm.widthPixels / minCellSize;
        int fakeMenuItemWidth = dm.widthPixels / cellCount;
        mDialButtonContainer = fragmentView.findViewById(R.id.dialButtonContainer);
        if (mDialButtonContainer != null) {
            mDialButtonContainer.setPadding(fakeMenuItemWidth, mDialButtonContainer.getPaddingTop(), fakeMenuItemWidth,
                    mDialButtonContainer.getPaddingBottom());
        }
        mDialButton = fragmentView.findViewById(R.id.dialButton);

        // ddd start  在拨号盘界面用户输入号码后，用户能够快捷（不超过两步）发送信息
        mSmsButton = fragmentView.findViewById(R.id.smsButton);
        if (mSmsButton != null) {
            mSmsButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // 输入的获取手机号码
                    String sendNumber = mDigits.getText().toString();
                    Uri uri = Uri.parse("smsto:" + sendNumber);
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(smsIntent); //调起发送短信的activity

                }

            });
        }
        // ddd end
        if (r.getBoolean(R.bool.config_show_onscreen_dial_button)) {
            mDialButton.setOnClickListener(this);
            mDialButton.setOnLongClickListener(this);
        } else {
            mDialButton.setVisibility(View.GONE); // It's VISIBLE by default

            mDialButton = null;
        }

        mDelete = fragmentView.findViewById(R.id.deleteButton);
        if (mDelete != null) {
            mDelete.setOnClickListener(this);
            mDelete.setOnLongClickListener(this);
        }

        mDialpad = fragmentView.findViewById(R.id.dialpad); // This is null in
                                                            // landscape mode.

        // added by yuan 电话号码匹配项结果多于一条时，显示“more”button
        mMatchList = fragmentView.findViewById(R.id.dialpad_match_list);
        matchListView = (ListView) mMatchList.findViewById(R.id.listView_match_list);
        matchListView.setAdapter(adapter);
        mMatchList.setVisibility(View.GONE);

        mMatchAddNew = (Button) fragmentView.findViewById(R.id.button_add_new);
        mMatchAddNew.setVisibility(View.GONE);
        mMatchAddNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final CharSequence digits = mDigits.getText();
                startActivity(getAddToContactIntent(digits));
                Log.v("mMatchAddNew", "mMatchAddNew");
            }
        });

        mMatchMore = (Button) fragmentView.findViewById(R.id.button_more);
        mMatchMore.setVisibility(View.GONE);
        mMatchMore.setOnClickListener(new OnClickListener() {//点击“more”button，列表显示所有的匹配项

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mMatchList.isShown()) {
                    mMatchList.setVisibility(View.GONE);
                    mDialpad.setVisibility(View.VISIBLE);
                } else {
                    mMatchList.setVisibility(View.VISIBLE);
                    mDialpad.setVisibility(View.GONE);
                }

                // Intent intent = new Intent(getActivity(),
                // MultiSimSettings.class);
                // startActivity(intent);
            }
        });

        // by yuan 点击某个匹配项，将其显示在输入框中，完成电话号码搜索匹配
        matchListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                // Log.v("yuanyetao",""+adapter.getAdapterList().get(arg2).telnum);
                mDigits.getText().clear();
                mDigits.getText().append(adapter.getAdapterList().get(arg2).telnum);

                mMatchedName.setText(adapter.getAdapterList().get(arg2).name);
                mMatchList.setVisibility(View.GONE);
                mDialpad.setVisibility(View.VISIBLE);

                StringBuffer sb = new StringBuffer();
                numbers = getPhonenumbersFromName(totalMatchedList, adapter.getAdapterList().get(arg2).name.toString());
                if (numbers != null && numbers.size() > 1) {
                    for (int i = 0; i < numbers.size(); i++) {
                        sb.append(numbers.get(i).toString()).append('/');
                    }
                    sb.deleteCharAt(sb.length() - 1);
                } else if (numbers != null && numbers.size() == 1) {
                    sb.append(numbers.get(0).toString());
                }
                mMatchedPhones.setText(sb.toString());

            }

        });

        // In landscape we put the keyboard in phone mode.
        if (null == mDialpad) {
            mDigits.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        } else {
            mDigits.setCursorVisible(false);
        }

        // Set up the "dialpad chooser" UI; see showDialpadChooser().
        mDialpadChooser = (ListView) fragmentView.findViewById(R.id.dialpadChooser);
        mDialpadChooser.setOnItemClickListener(this);

        configureScreenFromIntent(getActivity().getIntent());

        return fragmentView;
    }

    private boolean isLayoutReady() {
        return mDigits != null;
    }

    public EditText getDigitsWidget() {
        return mDigits;
    }

    /**
     * @return true when {@link #mDigits} is actually filled by the Intent.
     */
    private boolean fillDigitsIfNecessary(Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_DIAL.equals(action) || Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                if (Constants.SCHEME_TEL.equals(uri.getScheme())) {
                    // Put the requested number into the input area
                    String data = uri.getSchemeSpecificPart();
                    // Remember it is filled via Intent.
                    mDigitsFilledByIntent = true;
                    setFormattedDigits(data, null);
                    return true;
                } else {
                    String type = intent.getType();
                    if (People.CONTENT_ITEM_TYPE.equals(type) || Phones.CONTENT_ITEM_TYPE.equals(type)) {
                        // Query the phone number
                        Cursor c = getActivity().getContentResolver().query(intent.getData(),
                                new String[] { PhonesColumns.NUMBER, PhonesColumns.NUMBER_KEY }, null, null, null);
                        if (c != null) {
                            try {
                                if (c.moveToFirst()) {
                                    // Remember it is filled via Intent.
                                    mDigitsFilledByIntent = true;
                                    // Put the number into the input area
                                    setFormattedDigits(c.getString(0), c.getString(1));
                                    return true;
                                }
                            } finally {
                                c.close();
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * @see #showDialpadChooser(boolean)
     */
    private static boolean needToShowDialpadChooser(Intent intent, boolean isAddCallMode) {
        final String action = intent.getAction();

        boolean needToShowDialpadChooser = false;

        if (Intent.ACTION_DIAL.equals(action) || Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri == null) {
                // ACTION_DIAL or ACTION_VIEW with no data.
                // This behaves basically like ACTION_MAIN: If there's
                // already an active call, bring up an intermediate UI to
                // make the user confirm what they really want to do.
                // Be sure *not* to show the dialpad chooser if this is an
                // explicit "Add call" action, though.
                if (!isAddCallMode && phoneIsInUse()) {
                    needToShowDialpadChooser = true;
                }
            }
        } else if (Intent.ACTION_MAIN.equals(action)) {
            // The MAIN action means we're bringing up a blank dialer
            // (e.g. by selecting the Home shortcut, or tabbing over from
            // Contacts or Call log.)
            //
            // At this point, IF there's already an active call, there's a
            // good chance that the user got here accidentally (but really
            // wanted the in-call dialpad instead). So we bring up an
            // intermediate UI to make the user confirm what they really
            // want to do.
            if (phoneIsInUse()) {
                // Log.i(TAG,
                // "resolveIntent(): phone is in use; showing dialpad chooser!");
                needToShowDialpadChooser = true;
            }
        }

        return needToShowDialpadChooser;
    }

    private static boolean isAddCallMode(Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_DIAL.equals(action) || Intent.ACTION_VIEW.equals(action)) {
            // see if we are "adding a call" from the InCallScreen; false by
            // default.
            return intent.getBooleanExtra(ADD_CALL_MODE_KEY, false);
        } else {
            return false;
        }
    }

    /**
     * Checks the given Intent and changes dialpad's UI state. For example, if
     * the Intent requires the screen to enter "Add Call" mode, this method will
     * show correct UI for the mode.
     */
    public void configureScreenFromIntent(Intent intent) {
        if (!isLayoutReady()) {
            // This happens typically when parent's Activity#onNewIntent() is
            // called while
            // Fragment#onCreateView() isn't called yet, and thus we cannot
            // configure Views at
            // this point. onViewCreate() should call this method after
            // preparing layouts, so
            // just ignore this call now.
            Log.i(TAG, "Screen configuration is requested before onCreateView() is called. Ignored");
            return;
        }

        boolean needToShowDialpadChooser = false;

        final boolean isAddCallMode = isAddCallMode(intent);
        if (!isAddCallMode) {
            final boolean digitsFilled = fillDigitsIfNecessary(intent);
            if (!digitsFilled) {
                needToShowDialpadChooser = needToShowDialpadChooser(intent, isAddCallMode);
            }
        }
        showDialpadChooser(needToShowDialpadChooser);
    }

    /**
     * Sets formatted digits to digits field.
     */
    private void setFormattedDigits(String data, String normalizedNumber) {
        // strip the non-dialable numbers out of the data string.
        String dialString = PhoneNumberUtils.extractNetworkPortion(data);
        dialString = PhoneNumberUtils.formatNumber(dialString, normalizedNumber, mCurrentCountryIso);
        if (!TextUtils.isEmpty(dialString)) {

            /** zzz */
            // TODO

            Editable digits = mDigits.getText();
            digits.replace(0, digits.length(), dialString);
            // for some reason this isn't getting called in the digits.replace
            // call above..
            // but in any case, this will make sure the background drawable
            // looks right
            afterTextChanged(digits);
        }
    }

    private void setupKeypad(View fragmentView) {
        int[] buttonIds = new int[] { R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven,
                R.id.eight, R.id.nine, R.id.zero, R.id.star, R.id.pound };
        for (int id : buttonIds) {
            ((DialpadImageButton) fragmentView.findViewById(id)).setOnPressedListener(this);
        }

        // Long-pressing one button will initiate Voicemail.
        fragmentView.findViewById(R.id.one).setOnLongClickListener(this);

        // Long-pressing zero button will enter '+' instead.
        fragmentView.findViewById(R.id.zero).setOnLongClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        final StopWatch stopWatch = StopWatch.start("Dialpad.onResume");

        // Query the last dialed number. Do it first because hitting
        // the DB is 'slow'. This call is asynchronous.
        queryLastOutgoingCall();

        stopWatch.lap("qloc");

        // retrieve the DTMF tone play back setting.
        mDTMFToneEnabled = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;

        stopWatch.lap("dtwd");

        // Retrieve the haptic feedback setting.
        mHaptic.checkSystemSetting();

        stopWatch.lap("hptc");

        // if the mToneGenerator creation fails, just continue without it. It is
        // a local audio signal, and is not as important as the dtmf tone
        // itself.
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
                } catch (RuntimeException e) {
                    Log.w(TAG, "Exception caught while creating local tone generator: " + e);
                    mToneGenerator = null;
                }
            }
        }
        stopWatch.lap("tg");
        // Prevent unnecessary confusion. Reset the press count anyway.
        mDialpadPressCount = 0;

        Activity parent = getActivity();
        if (parent instanceof DialtactsActivity) {
            // See if we were invoked with a DIAL intent. If we were, fill in
            // the appropriate
            // digits in the dialer field.
            fillDigitsIfNecessary(parent.getIntent());
        }

        stopWatch.lap("fdin");

        // While we're in the foreground, listen for phone state changes,
        // purely so that we can take down the "dialpad chooser" if the
        // phone becomes idle while the chooser UI is visible.
        TelephonyManager telephonyManager = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        stopWatch.lap("tm");

        // Potentially show hint text in the mDigits field when the user
        // hasn't typed any digits yet. (If there's already an active call,
        // this hint text will remind the user that he's about to add a new
        // call.)
        //
        // TODO: consider adding better UI for the case where *both* lines
        // are currently in use. (Right now we let the user try to add
        // another call, but that call is guaranteed to fail. Perhaps the
        // entire dialer UI should be disabled instead.)
        if (phoneIsInUse()) {
            final SpannableString hint = new SpannableString(getActivity().getString(R.string.dialerDialpadHintText));
            hint.setSpan(new RelativeSizeSpan(0.8f), 0, hint.length(), 0);
            mDigits.setHint(hint);
        } else {
            // Common case; no hint necessary.
            mDigits.setHint(null);

            // Also, a sanity-check: the "dialpad chooser" UI should NEVER
            // be visible if the phone is idle!
            showDialpadChooser(false);
        }

        stopWatch.lap("hnt");

        updateDialAndDeleteButtonEnabledState();

        stopWatch.lap("bes");

        stopWatch.stopAndLog(TAG, 50);

        // added by yuan

        /** zzz */
        // commit this out
        // why clear digits here???
        // mDigits.getText().clear();
        //初始化电话号码匹配搜索模块 
        mMatchedPhones.setText("");  //匹配电话号码
        mMatchedName.setText("");    //匹配联系人姓名
        mMatchList.setVisibility(View.GONE); //匹配结果列表
        mDialpad.setVisibility(View.VISIBLE);//拨号盘
        mMatchMore.setVisibility(View.GONE); //“more”button
        mMatchAddNew.setVisibility(View.GONE);//添加联系人button

    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop listening for phone state changes.
        TelephonyManager telephonyManager = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);

        // Make sure we don't leave this activity with a tone still playing.
        stopTone();
        // Just in case reset the counter too.
        mDialpadPressCount = 0;

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator != null) {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }
        // TODO: I wonder if we should not check if the AsyncTask that
        // lookup the last dialed number has completed.
        mLastNumberDialed = EMPTY_NUMBER; // Since we are going to query again,
                                          // free stale number.

        SpecialCharSequenceMgr.cleanup();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mClearDigitsOnStop) {
            mClearDigitsOnStop = false;
            mDigits.getText().clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PREF_DIGITS_FILLED_BY_INTENT, mDigitsFilledByIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (ViewConfiguration.get(getActivity()).hasPermanentMenuKey() && isLayoutReady() && mDialpadChooser != null) {
            inflater.inflate(R.menu.dialpad_options, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hardware menu key should be available and Views should already be
        // ready.
        if (ViewConfiguration.get(getActivity()).hasPermanentMenuKey() && isLayoutReady() && mDialpadChooser != null) {
            setupMenuItems(menu);
        }
    }

    private void setupMenuItems(Menu menu) {
        final MenuItem callSettingsMenuItem = menu.findItem(R.id.menu_call_settings_dialpad);
        final MenuItem addToContactMenuItem = menu.findItem(R.id.menu_add_contacts);
        final MenuItem twoSecPauseMenuItem = menu.findItem(R.id.menu_2s_pause);
        final MenuItem waitMenuItem = menu.findItem(R.id.menu_add_wait);
        final MenuItem sendSMSMenuItem = menu.findItem(R.id.menu_send_sms);
        // ddd
        // modified by zzz
        // final MenuItem esurfingDialItem =
        // menu.findItem(R.id.menu_esurfing_dial);

        final MenuItem callLogSettingMenuItem = menu.findItem(R.id.menu_call_setting);

        final MenuItem callIPOneMenuItem = menu.findItem(R.id.menu_ip_call_one);
        final MenuItem callIPTwoMenuItem = menu.findItem(R.id.menu_ip_call_two);
        // by yuan  IP拨号

        final IPCall ipcall = new IPCall(getActivity());

        /** zzz */
        //设置是否启用翼拨号
        final MenuItem ifShowEdialMenuItem = menu.findItem(R.id.menu_if_show_edial);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp.getString("EDialPreference", "0").equals("2")) {
            ifShowEdialMenuItem.setChecked(false);
        } else {
            ifShowEdialMenuItem.setChecked(true);
        }

        ifShowEdialMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                Log.v(TAG, "onMenuItemClick");
                boolean checked = !arg0.isChecked();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("EDialPreference", checked ? "0" : "2");
                editor.commit();

                return false;
            }
        });

        // ddd start

        /*
         * esurfingDialItem.setOnMenuItemClickListener(new
         * OnMenuItemClickListener() {
         * 
         * @Override public boolean onMenuItemClick(MenuItem arg0) { // TODO
         * Auto-generated method stub AlertDialog.Builder builder = new
         * AlertDialog.Builder(getActivity());
         * 
         * // builder.setTitle("翼拨号"); //
         * builder.setIcon(R.drawable.ic_ab_dialer_holo_blue); //
         * builder.setPositiveButton("拨号",null); //
         * builder.setSingleChoiceItems(R.array.esurfing_options,0,new //
         * android.content.DialogInterface.OnClickListener(){ // // @Override //
         * public void onClick(DialogInterface arg0, int arg1) { // // TODO
         * Auto-generated method stub // // }}); // builder.create(); //
         * builder.show(); // return false; // Context mContext = getActivity();
         * 
         * Context context = getActivity(); if (context == null) { return false;
         * 
         * }
         * 
         * String sendNumber = mDigits.getText().toString(); Dialog dialog = new
         * Dialog(context); dialog.setContentView(R.layout.dialpad_esurfing);
         * 
         * RadioGroup radioGroupEsurfing = (RadioGroup)
         * dialog.findViewById(R.id.radioGroupEsurfing); final RadioButton
         * callBackChinaButton = (RadioButton) dialog
         * .findViewById(R.id.radioButton_callBackChina); final RadioButton
         * internationalButton = (RadioButton) dialog
         * .findViewById(R.id.radioButton_international); final RadioButton
         * call133Button = (RadioButton)
         * dialog.findViewById(R.id.radioButton_133); final RadioButton
         * callOtherButton = (RadioButton)
         * dialog.findViewById(R.id.radioButton_callOther); final RadioButton
         * callLocalButton = (RadioButton)
         * dialog.findViewById(R.id.radioButton_callLocal);
         * 
         * final TextView title = (TextView)
         * dialog.findViewById(R.id.textView_title); final TextView pre =
         * (TextView) dialog.findViewById(R.id.textView_pre); final StringBuffer
         * stringPre = new StringBuffer(); stringPre.append("+86"); final
         * StringBuffer stringTitle = new StringBuffer();
         * stringTitle.append("中国+86");
         * 
         * EditText EditTextNumber = (EditText)
         * dialog.findViewById(R.id.editTextInputNumber);
         * EditTextNumber.setText(sendNumber); dialog.setTitle("翼拨号");
         * dialog.show();
         * 
         * radioGroupEsurfing.setOnCheckedChangeListener(new
         * OnCheckedChangeListener() {
         * 
         * @Override public void onCheckedChanged(RadioGroup group, int
         * checkedId) { // TODO Auto-generated method stub switch (checkedId) {
         * 
         * case R.id.radioButton_international: stringTitle.replace(0,
         * stringTitle.length(), "中国+86"); stringPre.replace(0,
         * stringPre.length(), "+86"); callBackChinaButton.setChecked(true);
         * break;
         * 
         * case R.id.radioButton_133: stringTitle.replace(0,
         * stringTitle.length(), "中国+86"); stringPre.replace(0,
         * stringPre.length(), "**133*86");
         * callBackChinaButton.setChecked(true); break;
         * 
         * case R.id.radioButton_callOther: stringTitle.replace(0,
         * stringTitle.length(), "美国+1"); stringPre.replace(0,
         * stringPre.length(), "+1"); callBackChinaButton.setChecked(false);
         * break;
         * 
         * case R.id.radioButton_callLocal: stringTitle.replace(0,
         * stringTitle.length(), "中国+86"); stringPre.replace(0,
         * stringPre.length(), ""); callBackChinaButton.setChecked(false);
         * break;
         * 
         * } title.setText(stringTitle); pre.setText(stringPre);
         * 
         * } });
         * 
         * return false; }
         * 
         * });
         */

        // ddd end
        //卡一IP拨号
        callIPOneMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                // TODO Auto-generated method stub
                final String number = mDigits.getText().toString();
                if (number != null) {
                    ((DialtactsActivity) getActivity()).call(ipcall.getCDMAIPCode() + number);
                }
                return false;
            }

        });
        //卡二IP拨号
        callIPTwoMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                // TODO Auto-generated method stub
                final String number = mDigits.getText().toString();
                if (number != null) {
                    ((DialtactsActivity) getActivity()).call(ipcall.getGSMIPCode() + number);
                }
                return false;
            }

        });

        // Check if all the menu items are inflated correctly. As a shortcut, we
        // assume all menu
        // items are ready if the first item is non-null.
        if (callSettingsMenuItem == null) {
            return;
        }

        final Activity activity = getActivity();
        if (activity != null && ViewConfiguration.get(activity).hasPermanentMenuKey()) {
            // Call settings should be available via its parent Activity.
            callSettingsMenuItem.setVisible(false);
            // callLogSettingMenuItem.setVisible(false);
        } else {
            callSettingsMenuItem.setVisible(true);
            callSettingsMenuItem.setIntent(DialtactsActivity.getCallSettingsIntent());
            // callLogSettingMenuItem.setVisible(true);

        }
        callLogSettingMenuItem.setVisible(true);
        callLogSettingMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                // TODO Auto-generated method stub

                /** zzz */
                // final Intent intent = new Intent(getActivity(),
                // CalllogSettingActivity.class);

                // final Intent intent = new Intent(getActivity(),
                // DialpadPreferenceActivity.class);

                final Intent intent = new Intent(getActivity(), DialpadSettingAcitivity.class);

                startActivity(intent);
                return false;
            }

        });

        // We show "add to contacts", "2sec pause", and "add wait" menus only
        // when the user is
        // seeing usual dialpads and has typed at least one digit.
        // We never show a menu if the "choose dialpad" UI is up.
        if (dialpadChooserVisible() || isDigitsEmpty()) {
            // ddd
            // modified by zzz
            // esurfingDialItem.setVisible(false);
            addToContactMenuItem.setVisible(false);
            sendSMSMenuItem.setVisible(false);
            twoSecPauseMenuItem.setVisible(false);
            waitMenuItem.setVisible(false);
            callIPOneMenuItem.setVisible(false);
            callIPTwoMenuItem.setVisible(false);
        } else {

            if (ipcall.isCDMAIPEnabled()) {
                callIPOneMenuItem.setVisible(true);
            } else {
                callIPOneMenuItem.setVisible(false);
            }
            if (ipcall.isGSMIPEnabled()) {
                callIPTwoMenuItem.setVisible(true);
            } else {
                callIPTwoMenuItem.setVisible(false);
            }

            final CharSequence digits = mDigits.getText();

            // Put the current digits string into an intent
            addToContactMenuItem.setIntent(getAddToContactIntent(digits));

            addToContactMenuItem.setVisible(true);

            sendSMSMenuItem.setIntent(getSendSMSIntent(digits));

            sendSMSMenuItem.setVisible(true);

            // Check out whether to show Pause & Wait option menu items
            int selectionStart;
            int selectionEnd;
            String strDigits = digits.toString();
            selectionStart = mDigits.getSelectionStart();
            selectionEnd = mDigits.getSelectionEnd();

            if (selectionStart != -1) {
                if (selectionStart > selectionEnd) {
                    // swap it as we want start to be less then end
                    int tmp = selectionStart;
                    selectionStart = selectionEnd;
                    selectionEnd = tmp;
                }

                if (selectionStart != 0) {
                    // Pause can be visible if cursor is not in the begining
                    twoSecPauseMenuItem.setVisible(true);

                    // For Wait to be visible set of condition to meet
                    waitMenuItem.setVisible(showWait(selectionStart, selectionEnd, strDigits));
                } else {
                    // cursor in the beginning both pause and wait to be
                    // invisible
                    twoSecPauseMenuItem.setVisible(false);
                    waitMenuItem.setVisible(false);
                }
            } else {
                twoSecPauseMenuItem.setVisible(true);

                // cursor is not selected so assume new digit is added to the
                // end
                int strLength = strDigits.length();
                waitMenuItem.setVisible(showWait(strLength, strLength, strDigits));
            }
        }
    }

    private static Intent getAddToContactIntent(CharSequence digits) {
        final Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.putExtra(Insert.PHONE, digits);
        intent.setType(People.CONTENT_ITEM_TYPE);
        return intent;
    }

    private static Intent getSendSMSIntent(CharSequence digits) {
        Uri uri = Uri.parse("smsto:" + digits);
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        // intent.putExtra(Insert.PHONE, digits);
        // intent.setType(People.CONTENT_ITEM_TYPE);
        return intent;
    }

    private void keyPressed(int keyCode) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_1:
            playTone(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_2:
            playTone(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_3:
            playTone(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_4:
            playTone(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_5:
            playTone(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_6:
            playTone(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_7:
            playTone(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_8:
            playTone(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_9:
            playTone(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_0:
            playTone(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_POUND:
            playTone(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
            break;
        case KeyEvent.KEYCODE_STAR:
            playTone(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
            break;
        default:
            break;
        }

        mHaptic.vibrate();
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);
        // matchPhoneNumber(mDigits.getText().toString());
        myHandler.sendEmptyMessage(0);

        // If the cursor is at the end of the text we hide it.
        final int length = mDigits.length();

        if (length == mDigits.getSelectionStart() && length == mDigits.getSelectionEnd()) {
            mDigits.setCursorVisible(false);
        }
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            matchPhoneNumber(mDigits.getText().toString());
        }
    };

    /*
     * 
     * added by yuan  匹配电话号码
     */
    private void matchPhoneNumber(String phonenumber) {
        // TODO Auto-generated method stub
        if (phonenumber.length() < 1) {
            mMatchedPhones.setText("");
            mMatchedName.setText("");
            adapter.refresh(allContactList, true);
            mMatchMore.setVisibility(View.GONE);
            mMatchList.setVisibility(View.GONE);
            mMatchAddNew.setVisibility(View.GONE);
            mDialpad.setVisibility(View.VISIBLE);
        } else {
            totalMatchedList.clear(); //清空匹配list
            mMatchAddNew.setVisibility(View.GONE);
            // Log.v("aaaa",""+rawThreeDaysListThread.get_state());
            if (rawThreeDaysListThread != null && rawThreeDaysListThread.get_state() && rawThreeDaysList.size() > 0) {
                search(totalMatchedList, rawThreeDaysList, phonenumber.replace(" ", ""));
                // Log.v("aa1","bb");
            }
            if (rawThirtyDaysListThread != null && rawThirtyDaysListThread.get_state() && rawThirtyDaysList.size() > 0) {
                search(totalMatchedList, rawThirtyDaysList, phonenumber.replace(" ", ""));
                // Log.v("aa2","bb");
            }
            if (rawHistoryListThread != null && rawHistoryListThread.get_state() && rawHistoryList.size() > 0) {
                search(totalMatchedList, rawHistoryList, phonenumber.replace(" ", ""));
                // Log.v("aa3","bb");
            }
            if (totalMatchedList.size() < 1) {
                mMatchAddNew.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 根据名字中的某一个字进行模糊查询
     * 
     * @param key
     */
    private boolean getFuzzyQuery(List<CallResearchModel> dataList, long startTime, long endTime) {

        StringBuilder sb = new StringBuilder();
        ContentResolver cr = getActivity().getContentResolver();
        // Uri uri =
        // Uri.parse("content://com.android.contacts/data/phones/filter/"+key);
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/");
        String selection = "(( last_time_contacted  >= ?) AND ( last_time_contacted < ?))";
        String[] selectionArgs = new String[] { String.valueOf(startTime), String.valueOf(endTime) };
        Cursor cursor = cr.query(uri, new String[] { ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, "last_time_contacted", "times_contacted" }, selection,
                selectionArgs, "times_contacted DESC");
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            CallResearchModel m = new CallResearchModel(cursor.getString(0), cursor.getString(1));
            // Log.v("dddd",""+cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2)+":"+cursor.getString(3));
            dataList.add(m);
        }
        cursor.close();
        return true;
    }

    /*
     * 
     * added by yuan 初始查询数据
     */

    public void initQueryData() {
        long currentTime = System.currentTimeMillis();
        long threeDaysBeforeTime = System.currentTimeMillis() - 3 * 24 * 3600 * 1000L;
        long thirtyDaysBeforeTime = System.currentTimeMillis() - 2592000000L;
        // Log.v("dddd",new Date((long)
        // currentTime).toLocaleString()+currentTime);
        // Log.v("dddd",new Date((long)
        // threeDaysBeforeTime).toLocaleString()+threeDaysBeforeTime);
        // Log.v("dddd",new Date((long)
        // thirtyDaysBeforeTime).toLocaleString()+thirtyDaysBeforeTime);

        rawThreeDaysListThread = new initQueryDataThread(rawThreeDaysList, threeDaysBeforeTime, currentTime);
        rawThreeDaysListThread.start();
        rawThirtyDaysListThread = new initQueryDataThread(rawThirtyDaysList, thirtyDaysBeforeTime, threeDaysBeforeTime);
        rawThirtyDaysListThread.start();
        rawHistoryListThread = new initQueryDataThread(rawHistoryList, 0, thirtyDaysBeforeTime);
        rawHistoryListThread.start();

    }

    public class initQueryDataThread extends Thread {

        List<CallResearchModel> dataList;
        long startTime, endTime;
        public boolean state;

        public initQueryDataThread(List<CallResearchModel> dataList, long startTime, long endTime) {
            this.dataList = dataList;
            this.startTime = startTime;
            this.endTime = endTime;
            this.state = false;
        }

        public void run() {
            state = getFuzzyQuery(dataList, startTime, endTime);
            // Log.v("yuantest","加载完成"+dataList.size());
            /** zzz */
            Log.v("yuantest", "load complete " + dataList.size());
        }

        public boolean get_state() {
            return state;
        }
    }

    /**
     * 按号码-拼音搜索联系人
     * 
     * @param str
     */
    public void search(List<CallResearchModel> totalMatchedList, List<CallResearchModel> rawDataList, String str) {

        List<CallResearchModel> headPinyinMatchedList = new ArrayList<CallResearchModel>();
        List<CallResearchModel> partPinyinMatchedList = new ArrayList<CallResearchModel>();
        List<CallResearchModel> phoneNumberMatchedList = new ArrayList<CallResearchModel>();
        String strForMatch = str;

        /** zzz */
        boolean searchOnlyNumberMatched = false;
        if (str.toString().contains("*") //
                || str.toString().contains("+") //
                || str.toString().contains("1") //

        ) {
            searchOnlyNumberMatched = true;
        }

        /** zzz */
        // why not search '+'?
        // if (str.toString().contains("+")) {
        // // Log.v("aaa1",""+str.length());
        // strForMatch = str.replace("+", "");
        // Log.v("aaa2", strForMatch + ":" + strForMatch.length());
        // }

        /** zzz */
        // if (str.toString().contains("*")) {
        // // Log.v("aaa1",""+str.length());
        // strForMatch = str.replace("*", "");
        // Log.v(TAG, strForMatch + ":" + strForMatch.length());
        // }

        StringBuffer T9pinyin = new StringBuffer();
        // 获取每一个数字对应的字母列表并以'-'隔开
        for (int i = 0; i < strForMatch.length(); i++) {
            T9pinyin.append((strForMatch.charAt(i) <= '9' && strForMatch.charAt(i) >= '0') ? BaseUtil.STRS[strForMatch
                    .charAt(i) - '0'] : strForMatch.charAt(i));
            if (i != strForMatch.length() - 1) {
                T9pinyin.append("-");
            }
        }

        // 如果搜索条件以0 1 +开头则按号码搜索
        // if(str.toString().startsWith("0") || str.toString().startsWith("1")||
        // str.toString().startsWith("+")){

        if (rawDataList.size() > 0 && strForMatch.length() > 0) {
            for (CallResearchModel model : rawDataList) {

                /** zzz */
                // if contains '1' '+' '*', no not search as name
                if (!searchOnlyNumberMatched
                        && pinyinMatched(T9pinyin.toString(), model, strForMatch, headPinyinMatchedList,
                                partPinyinMatchedList)) {
                    continue;
                } else if (model.searchnum.contains(strForMatch)) {
                    model.group = strForMatch;
                    phoneNumberMatchedList.add(model);
                }
            }
            totalMatchedList.addAll(headPinyinMatchedList);
            totalMatchedList.addAll(partPinyinMatchedList);
            totalMatchedList.addAll(phoneNumberMatchedList);
            if (totalMatchedList.size() > 0) {

                if (totalMatchedList.get(0).name != "") {
                    mMatchedName.setText(totalMatchedList.get(0).name.toString());
                    StringBuffer sb = new StringBuffer();
                    sb.append("");
                    numbers = getPhonenumbersFromName(totalMatchedList, totalMatchedList.get(0).name.toString());
                    if (numbers != null && numbers.size() > 1) {
                        for (int i = 0; i < numbers.size(); i++) {
                            sb.append(numbers.get(i).toString()).append('/');
                        }
                        sb.deleteCharAt(sb.length() - 1);
                    } else if (numbers != null && numbers.size() == 1) {
                        sb.append(numbers.get(0).toString());
                    }
                    mMatchedPhones.setText(sb.toString());

                } else {
                    mMatchedName.setText(totalMatchedList.get(0).telnum.toString());
                    mMatchedPhones.setText(totalMatchedList.get(0).telnum.toString());
                }
            } else {
                mMatchedPhones.setText("");
                mMatchedName.setText("");
            }
            if (totalMatchedList.size() > 1) {
                mMatchMore.setVisibility(View.VISIBLE);
            } else {
                mMatchMore.setVisibility(View.GONE);
            }
            adapter.refresh(totalMatchedList, false);
            return;
        }
    }

    /**
     * 根据名字查询所有号码
     */

    public ArrayList<String> getPhonenumbersFromName(List<CallResearchModel> totalMatchedList, String name) {
        ArrayList<String> numbers = new ArrayList<String>();
        for (CallResearchModel model : totalMatchedList) {
            if (model.name.equals(name)) {
                numbers.add(model.telnum);
            }
        }
        return numbers;
    }

    /**
     * 根据拼音搜索
     * 
     * @param str
     *            正则表达式
     * @param pyName
     *            拼音
     * @param isIncludsive
     *            搜索条件是否大于6个字符
     * @return
     */
    public boolean pinyinMatched(String str, CallResearchModel model, String search,
            List<CallResearchModel> headPinyinMatchedList, List<CallResearchModel> partPinyinMatchedList) {
        if (TextUtils.isEmpty(model.pyname)) {
            return false;
        }

        String tempStr1 = str;
        model.group = "";
        // 搜索条件大于6个字符将不按拼音首字母查询
        if (search.length() < 6 && tempStr1.length() > 0) {
            // 根据首字母进行模糊查询
            // Pattern pattern = Pattern.compile("^" +
            // tempStr1.toUpperCase().replace("-", "[*+#a-z]*"));
            // modified by zzz
            Pattern pattern = Pattern.compile(tempStr1.toUpperCase().replace("-", "[*+#a-z]*"));
            Matcher matcher = pattern.matcher(model.pyname);

            if (matcher.find()) {
                String tempStr = matcher.group();
                for (int i = 0; i < tempStr.length(); i++) {
                    if (tempStr.charAt(i) >= 'A' && tempStr.charAt(i) <= 'Z') {
                        model.group += tempStr.charAt(i);
                    }
                }
                headPinyinMatchedList.add(model);
                return true;
            }
        }

        // 根据全拼查询
        Pattern pattern = Pattern.compile(str.replace("-", ""), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(model.pyname);
        boolean flag = matcher.find();
        if (flag) {
            model.group = matcher.group();
            partPinyinMatchedList.add(model);
        }
        return flag;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch (view.getId()) {
        case R.id.digits:
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                dialButtonPressed();
                return true;
            }
            break;
        }
        return false;
    }

    /**
     * When a key is pressed, we start playing DTMF tone, do vibration, and
     * enter the digit immediately. When a key is released, we stop the tone.
     * Note that the "key press" event will be delivered by the system with
     * certain amount of delay, it won't be synced with user's actual
     * "touch-down" behavior.
     */
    @Override
    public void onPressed(View view, boolean pressed) {
        if (DEBUG)
            Log.d(TAG, "onPressed(). view: " + view + ", pressed: " + pressed);
        if (pressed) {
            switch (view.getId()) {
            case R.id.one: {
                keyPressed(KeyEvent.KEYCODE_1);
                break;
            }
            case R.id.two: {
                keyPressed(KeyEvent.KEYCODE_2);
                break;
            }
            case R.id.three: {
                keyPressed(KeyEvent.KEYCODE_3);
                break;
            }
            case R.id.four: {
                keyPressed(KeyEvent.KEYCODE_4);
                break;
            }
            case R.id.five: {
                keyPressed(KeyEvent.KEYCODE_5);
                break;
            }
            case R.id.six: {
                keyPressed(KeyEvent.KEYCODE_6);
                break;
            }
            case R.id.seven: {
                keyPressed(KeyEvent.KEYCODE_7);
                break;
            }
            case R.id.eight: {
                keyPressed(KeyEvent.KEYCODE_8);
                break;
            }
            case R.id.nine: {
                keyPressed(KeyEvent.KEYCODE_9);
                break;
            }
            case R.id.zero: {
                keyPressed(KeyEvent.KEYCODE_0);
                break;
            }
            case R.id.pound: {
                keyPressed(KeyEvent.KEYCODE_POUND);
                break;
            }
            case R.id.star: {
                keyPressed(KeyEvent.KEYCODE_STAR);
                break;
            }
            default: {
                Log.wtf(TAG, "Unexpected onTouch(ACTION_DOWN) event from: " + view);
                break;
            }
            }
            mDialpadPressCount++;
        } else {
            view.jumpDrawablesToCurrentState();
            mDialpadPressCount--;
            if (mDialpadPressCount < 0) {
                // e.g.
                // - when the user action is detected as horizontal swipe, at
                // which only
                // "up" event is thrown.
                // - when the user long-press '0' button, at which dialpad will
                // decrease this count
                // while it still gets press-up event here.
                if (DEBUG)
                    Log.d(TAG, "mKeyPressCount become negative.");
                stopTone();
                mDialpadPressCount = 0;
            } else if (mDialpadPressCount == 0) {
                stopTone();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.deleteButton: {
            keyPressed(KeyEvent.KEYCODE_DEL);
            return;
        }
        case R.id.dialButton: {
            mHaptic.vibrate(); // Vibrate here too, just like we do for the
                               // regular keys
            dialButtonPressed();
            return;
        }
        case R.id.digits: {
            if (!isDigitsEmpty()) {
                mDigits.setCursorVisible(true);
            }
            return;
        }
        default: {
            Log.wtf(TAG, "Unexpected onClick() event from: " + view);
            return;
        }
        }
    }

    public PopupMenu constructPopupMenu(View anchorView) {
        final Context context = getActivity();
        if (context == null) {
            return null;
        }
        final PopupMenu popupMenu = new PopupMenu(context, anchorView);
        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.dialpad_options);
        popupMenu.setOnMenuItemClickListener(this);
        setupMenuItems(menu);
        return popupMenu;
    }

    @Override
    public boolean onLongClick(View view) {

        final Editable digits = mDigits.getText();
        final int id = view.getId();
        switch (id) {
        case R.id.deleteButton: {
            digits.clear();
            // TODO: The framework forgets to clear the pressed
            // status of disabled button. Until this is fixed,
            // clear manually the pressed status. b/2133127
            myHandler.sendEmptyMessage(0);
            mDelete.setPressed(false);
            return true;
        }
        case R.id.one: {
            // '1' may be already entered since we rely on onTouch() event for
            // numeric buttons.
            // Just for safety we also check if the digits field is empty or
            // not.
            // if (isDigitsEmpty() || TextUtils.equals(mDigits.getText(), "1"))
            // {
            // // We'll try to initiate voicemail and thus we want to remove
            // irrelevant string.
            // removePreviousDigitIfPossible();
            //
            // if (isVoicemailAvailable()) {
            // callVoicemail();
            // } else if (getActivity() != null) {
            // // Voicemail is unavailable maybe because Airplane mode is turned
            // on.
            // // Check the current status and show the most appropriate error
            // message.
            // final boolean isAirplaneModeOn =
            // Settings.System.getInt(getActivity().getContentResolver(),
            // Settings.System.AIRPLANE_MODE_ON, 0) != 0;
            // if (isAirplaneModeOn) {
            // DialogFragment dialogFragment = ErrorDialogFragment.newInstance(
            // R.string.dialog_voicemail_airplane_mode_message);
            // dialogFragment.show(getFragmentManager(),
            // "voicemail_request_during_airplane_mode");
            // } else {
            // DialogFragment dialogFragment = ErrorDialogFragment.newInstance(
            // R.string.dialog_voicemail_not_ready_message);
            // dialogFragment.show(getFragmentManager(), "voicemail_not_ready");
            // }
            // }
            // return true;
            // }
            return false;
        }
        case R.id.zero: {
            // Remove tentative input ('0') done by onTouch().
            removePreviousDigitIfPossible();
            keyPressed(KeyEvent.KEYCODE_PLUS);

            // Stop tone immediately and decrease the press count, so that
            // possible subsequent
            // dial button presses won't honor the 0 click any more.
            // Note: this *will* make mDialpadPressCount negative when the 0 key
            // is released,
            // which should be handled appropriately.
            stopTone();
            if (mDialpadPressCount > 0)
                mDialpadPressCount--;

            return true;
        }
        case R.id.digits: {
            // Right now EditText does not show the "paste" option when cursor
            // is not visible.
            // To show that, make the cursor visible, and return false, letting
            // the EditText
            // show the option by itself.
            mDigits.setCursorVisible(true);
            return false;
        }
        case R.id.dialButton: {
            if (isDigitsEmpty()) {
                handleDialButtonClickWithEmptyDigits();
                // This event should be consumed so that onClick() won't do the
                // exactly same
                // thing.
                return true;
            } else {
                return false;
            }
        }
        }
        return false;
    }

    /**
     * Remove the digit just before the current position. This can be used if we
     * want to replace the previous digit or cancel previously entered
     * character.
     */
    private void removePreviousDigitIfPossible() {
        final Editable editable = mDigits.getText();
        final int currentPosition = mDigits.getSelectionStart();
        if (currentPosition > 0) {
            mDigits.setSelection(currentPosition);
            mDigits.getText().delete(currentPosition - 1, currentPosition);
        }
    }

    public void callVoicemail() {
        startActivity(ContactsUtils.getVoicemailIntent());
        mClearDigitsOnStop = true;
        getActivity().finish();
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private int mTitleResId;
        private int mMessageResId;

        private static final String ARG_TITLE_RES_ID = "argTitleResId";
        private static final String ARG_MESSAGE_RES_ID = "argMessageResId";

        public static ErrorDialogFragment newInstance(int messageResId) {
            return newInstance(0, messageResId);
        }

        public static ErrorDialogFragment newInstance(int titleResId, int messageResId) {
            final ErrorDialogFragment fragment = new ErrorDialogFragment();
            final Bundle args = new Bundle();
            args.putInt(ARG_TITLE_RES_ID, titleResId);
            args.putInt(ARG_MESSAGE_RES_ID, messageResId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mTitleResId = getArguments().getInt(ARG_TITLE_RES_ID);
            mMessageResId = getArguments().getInt(ARG_MESSAGE_RES_ID);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (mTitleResId != 0) {
                builder.setTitle(mTitleResId);
            }
            if (mMessageResId != 0) {
                builder.setMessage(mMessageResId);
            }
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }

    /**
     * In most cases, when the dial button is pressed, there is a number in
     * digits area. Pack it in the intent, start the outgoing call broadcast as
     * a separate task and finish this activity.
     * 
     * When there is no digit and the phone is CDMA and off hook, we're sending
     * a blank flash for CDMA. CDMA networks use Flash messages when special
     * processing needs to be done, mainly for 3-way or call waiting scenarios.
     * Presumably, here we're in a special 3-way scenario where the network
     * needs a blank flash before being able to add the new participant. (This
     * is not the case with all 3-way calls, just certain CDMA infrastructures.)
     * 
     * Otherwise, there is no digit, display the last dialed number. Don't
     * finish since the user may want to edit it. The user needs to press the
     * dial button again, to dial it (general case described above).
     */
    public void dialButtonPressed() {
        if (isDigitsEmpty()) { // No number entered.
            handleDialButtonClickWithEmptyDigits();
        } else {
            final String number = mDigits.getText().toString();
            // "persist.radio.otaspdial" is a temporary hack needed for one
            // carrier's automated
            // test equipment.
            // TODO: clean it up.
            if (number != null && !TextUtils.isEmpty(mProhibitedPhoneNumberRegexp)
                    && number.matches(mProhibitedPhoneNumberRegexp)
                    && (SystemProperties.getInt("persist.radio.otaspdial", 0) != 1)) {
                Log.i(TAG, "The phone number is prohibited explicitly by a rule.");
                if (getActivity() != null) {
                    DialogFragment dialogFragment = ErrorDialogFragment
                            .newInstance(R.string.dialog_phone_call_prohibited_message);
                    dialogFragment.show(getFragmentManager(), "phone_prohibited_dialog");
                }

                // Clear the digits just in case.
                mDigits.getText().clear();
            } else {
                // final Intent intent =
                // ContactsUtils.getCallIntent(number,(getActivity() instanceof
                // DialtactsActivity ?
                // ((DialtactsActivity)getActivity()).getCallOrigin() : null));
                // startActivity(intent);
                // mClearDigitsOnStop = true;
                // getActivity().finish();

                // ((DialtactsActivity) getActivity()).call(number); // by yuan

                /** zzz */

                // ddd start

                // AlertDialog.Builder builder = new
                // AlertDialog.Builder(getActivity());

                // builder.setTitle("翼拨号");
                // builder.setIcon(R.drawable.ic_ab_dialer_holo_blue);
                // builder.setPositiveButton("拨号",null);
                // builder.setSingleChoiceItems(R.array.esurfing_options,0,new
                // android.content.DialogInterface.OnClickListener(){
                //
                // @Override
                // public void onClick(DialogInterface arg0, int arg1) {
                // // TODO Auto-generated method stub
                //
                // }});
                // builder.create();
                // builder.show();
                // return false;
                // Context mContext = getActivity();

                // Context context = getActivity();
                //
                // String sendNumber = mDigits.getText().toString();
                // Dialog dialog = new Dialog(context);
                // dialog.setContentView(R.layout.dialpad_esurfing);
                // dialog.setTitle("翼拨号");
                // RadioGroup radioGroupEsurfing = (RadioGroup)
                // dialog.findViewById(R.id.radioGroupEsurfing);
                // final RadioButton callBackChinaButton = (RadioButton) dialog
                // .findViewById(R.id.radioButton_callBackChina);
                // final RadioButton internationalButton = (RadioButton) dialog
                // .findViewById(R.id.radioButton_international);
                // final RadioButton call133Button = (RadioButton)
                // dialog.findViewById(R.id.radioButton_133);
                // final RadioButton callOtherButton = (RadioButton)
                // dialog.findViewById(R.id.radioButton_callOther);
                // final RadioButton callLocalButton = (RadioButton)
                // dialog.findViewById(R.id.radioButton_callLocal);
                //
                // final TextView title = (TextView)
                // dialog.findViewById(R.id.textView_title);
                // final TextView pre = (TextView)
                // dialog.findViewById(R.id.textView_pre);
                // final StringBuffer stringPre = new StringBuffer();
                // stringPre.append("+86");
                // final StringBuffer stringTitle = new StringBuffer();
                // stringTitle.append("中国+86");
                // final TextView
                // TextViewSuffix=(TextView)dialog.findViewById(R.id.textView_suffix);
                // EditText EditTextNumber = (EditText)
                // dialog.findViewById(R.id.editTextInputNumber);
                // EditTextNumber.setText(sendNumber);
                //
                // // dialog.show();
                //
                // radioGroupEsurfing.setOnCheckedChangeListener(new
                // OnCheckedChangeListener() {
                //
                // @Override
                // public void onCheckedChanged(RadioGroup group, int checkedId)
                // {
                // // TODO Auto-generated method stub
                // switch (checkedId) {
                //
                // case R.id.radioButton_international:
                // stringTitle.replace(0, stringTitle.length(), "中国+86");
                // stringPre.replace(0, stringPre.length(), "+86");
                // TextViewSuffix.setVisibility(8);
                // callBackChinaButton.setChecked(true);
                // break;
                //
                // case R.id.radioButton_133:
                // stringTitle.replace(0, stringTitle.length(), "中国+86");
                // stringPre.replace(0, stringPre.length(), "**133*86");
                // TextViewSuffix.setVisibility(0);
                // callBackChinaButton.setChecked(true);
                // break;
                //
                // case R.id.radioButton_callOther:
                // stringTitle.replace(0, stringTitle.length(), "美国+1");
                // stringPre.replace(0, stringPre.length(), "+1");
                //
                // TextViewSuffix.setVisibility(8);
                //
                // callBackChinaButton.setChecked(false);
                // Context context = getActivity();
                // Dialog nationalCodeDialog = new Dialog(context);
                // nationalCodeDialog.setContentView(R.layout.dialpad_esurfing_national_code);
                // nationalCodeDialog.setTitle("选择目标国家地区");
                // nationalCodeDialog.show();
                // break;
                //
                // case R.id.radioButton_callLocal:
                // TextViewSuffix.setVisibility(8);
                // stringTitle.replace(0, stringTitle.length(), "中国+86");
                // stringPre.replace(0, stringPre.length(), "");
                // callBackChinaButton.setChecked(false);
                // break;
                //
                // }
                // title.setText(stringTitle);
                // pre.setText(stringPre);
                //
                // }
                // });

                // ddd end

                /** zzz */
                // EdialDialog edialDialog = new EdialDialog(getActivity(),
                // mDigits.getText().toString());
                //
                // SharedPreferences sp =
                // PreferenceManager.getDefaultSharedPreferences(getActivity());
                //
                // if (sp.getString("EDialPreference", "0").equals("0")) {
                // Log.v(TAG,
                // "sp.getString(\"EDialPreference\", \"0\").equals(\"0\")");
                // TelephonyManager tm = (TelephonyManager)
                // getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                // if (tm.isNetworkRoaming()) {
                // Log.v(TAG, "tm.isNetworkRoaming()");
                // // show dialog here
                // edialDialog.show();
                // } else {
                // ((DialtactsActivity) getActivity()).call(number);
                // }
                // } else if (sp.getString("EDialPreference", "0").equals("1"))
                // {
                // Log.v(TAG,
                // "sp.getString(\"EDialPreference\", \"0\").equals(\"1\")");
                // // show dialog here
                // edialDialog.show();
                // } else if (sp.getString("EDialPreference", "0").equals("2"))
                // {
                // Log.v(TAG,
                // "sp.getString(\"EDialPreference\", \"0\").equals(\"2\")");
                // ((DialtactsActivity) getActivity()).call(number);
                // } else {
                // Log.e(TAG, "sharedPreferences error");
                // }

                /** zzz */
                Intent intent = new Intent();
                intent.setAction("edu.bupt.action.EDIAL");
                intent.putExtra("digit", number);
                getActivity().startService(intent);
            }
        }
    }

    private void handleDialButtonClickWithEmptyDigits() {
        if (phoneIsCdma() && phoneIsOffhook()) {
            // This is really CDMA specific. On GSM is it possible
            // to be off hook and wanted to add a 3rd party using
            // the redial feature.
            startActivity(newFlashIntent());
        } else {
            if (!TextUtils.isEmpty(mLastNumberDialed)) {
                // Recall the last number dialed.
                mDigits.setText(mLastNumberDialed);

                // ...and move the cursor to the end of the digits string,
                // so you'll be able to delete digits using the Delete
                // button (just as if you had typed the number manually.)
                //
                // Note we use mDigits.getText().length() here, not
                // mLastNumberDialed.length(), since the EditText widget now
                // contains a *formatted* version of mLastNumberDialed (due to
                // mTextWatcher) and its length may have changed.
                mDigits.setSelection(mDigits.getText().length());
            } else {
                // There's no "last number dialed" or the
                // background query is still running. There's
                // nothing useful for the Dial button to do in
                // this case. Note: with a soft dial button, this
                // can never happens since the dial button is
                // disabled under these conditons.
                playTone(ToneGenerator.TONE_PROP_NACK);
            }
        }
    }

    /**
     * Plays the specified tone for TONE_LENGTH_MS milliseconds.
     */
    private void playTone(int tone) {
        playTone(tone, TONE_LENGTH_MS);
    }

    /**
     * Play the specified tone for the specified milliseconds
     * 
     * The tone is played locally, using the audio stream for phone calls. Tones
     * are played only if the "Audible touch tones" user preference is checked,
     * and are NOT played if the device is in silent mode.
     * 
     * The tone length can be -1, meaning "keep playing the tone." If the caller
     * does so, it should call stopTone() afterward.
     * 
     * @param tone
     *            a tone code from {@link ToneGenerator}
     * @param durationMs
     *            tone length.
     */
    private void playTone(int tone, int durationMs) {
        // if local tone playback is disabled, just return.
        if (!mDTMFToneEnabled) {
            return;
        }

        // Also do nothing if the phone is in silent mode.
        // We need to re-check the ringer mode for *every* playTone()
        // call, rather than keeping a local flag that's updated in
        // onResume(), since it's possible to toggle silent mode without
        // leaving the current activity (via the ENDCALL-longpress menu.)
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }

            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, durationMs);
        }
    }

    /**
     * Stop the tone if it is played.
     */
    private void stopTone() {
        // if local tone playback is disabled, just return.
        if (!mDTMFToneEnabled) {
            return;
        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "stopTone: mToneGenerator == null");
                return;
            }
            mToneGenerator.stopTone();
        }
    }

    /**
     * Brings up the "dialpad chooser" UI in place of the usual Dialer elements
     * (the textfield/button and the dialpad underneath).
     * 
     * We show this UI if the user brings up the Dialer while a call is already
     * in progress, since there's a good chance we got here accidentally (and
     * the user really wanted the in-call dialpad instead). So in this situation
     * we display an intermediate UI that lets the user explicitly choose
     * between the in-call dialpad ("Use touch tone
     * keypad") and the regular Dialer ("Add call").  (Or, the option "Return to
     * call in progress" just goes back to the in-call UI with no dialpad at
     * all.)
     * 
     * @param enabled
     *            If true, show the "dialpad chooser" instead of the regular
     *            Dialer UI
     */
    private void showDialpadChooser(boolean enabled) {
        // Check if onCreateView() is already called by checking one of View
        // objects.
        if (!isLayoutReady()) {
            return;
        }

        if (enabled) {
            // Log.i(TAG, "Showing dialpad chooser!");
            if (mDigitsContainer != null) {
                mDigitsContainer.setVisibility(View.GONE);
            } else {
                // mDigits is not enclosed by the container. Make the digits
                // field itself gone.
                mDigits.setVisibility(View.GONE);
            }
            if (mDialpad != null)
                mDialpad.setVisibility(View.GONE);
            if (mDialButtonContainer != null)
                mDialButtonContainer.setVisibility(View.GONE);

            mDialpadChooser.setVisibility(View.VISIBLE);

            // Instantiate the DialpadChooserAdapter and hook it up to the
            // ListView. We do this only once.
            if (mDialpadChooserAdapter == null) {
                mDialpadChooserAdapter = new DialpadChooserAdapter(getActivity());
            }
            mDialpadChooser.setAdapter(mDialpadChooserAdapter);
        } else {
            // Log.i(TAG, "Displaying normal Dialer UI.");
            if (mDigitsContainer != null) {
                mDigitsContainer.setVisibility(View.VISIBLE);
            } else {
                mDigits.setVisibility(View.VISIBLE);
            }
            if (mDialpad != null)
                mDialpad.setVisibility(View.VISIBLE);
            if (mDialButtonContainer != null)
                mDialButtonContainer.setVisibility(View.VISIBLE);
            mDialpadChooser.setVisibility(View.GONE);
        }
    }

    /**
     * @return true if we're currently showing the "dialpad chooser" UI.
     */
    private boolean dialpadChooserVisible() {
        return mDialpadChooser.getVisibility() == View.VISIBLE;
    }

    /**
     * Simple list adapter, binding to an icon + text label for each item in the
     * "dialpad chooser" list.
     */
    private static class DialpadChooserAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        // Simple struct for a single "choice" item.
        static class ChoiceItem {
            String text;
            Bitmap icon;
            int id;

            public ChoiceItem(String s, Bitmap b, int i) {
                text = s;
                icon = b;
                id = i;
            }
        }

        // IDs for the possible "choices":
        static final int DIALPAD_CHOICE_USE_DTMF_DIALPAD = 101;
        static final int DIALPAD_CHOICE_RETURN_TO_CALL = 102;
        static final int DIALPAD_CHOICE_ADD_NEW_CALL = 103;

        private static final int NUM_ITEMS = 3;
        private ChoiceItem mChoiceItems[] = new ChoiceItem[NUM_ITEMS];

        public DialpadChooserAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Initialize the possible choices.
            // TODO: could this be specified entirely in XML?

            // - "Use touch tone keypad"
            mChoiceItems[0] = new ChoiceItem(context.getString(R.string.dialer_useDtmfDialpad),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialer_fork_tt_keypad),
                    DIALPAD_CHOICE_USE_DTMF_DIALPAD);

            // - "Return to call in progress"
            mChoiceItems[1] = new ChoiceItem(context.getString(R.string.dialer_returnToInCallScreen),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialer_fork_current_call),
                    DIALPAD_CHOICE_RETURN_TO_CALL);

            // - "Add call"
            mChoiceItems[2] = new ChoiceItem(context.getString(R.string.dialer_addAnotherCall),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialer_fork_add_call),
                    DIALPAD_CHOICE_ADD_NEW_CALL);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        /**
         * Return the ChoiceItem for a given position.
         */
        @Override
        public Object getItem(int position) {
            return mChoiceItems[position];
        }

        /**
         * Return a unique ID for each possible choice.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view for each row.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // When convertView is non-null, we can reuse it (there's no need
            // to reinflate it.)
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dialpad_chooser_list_item, null);
            }

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(mChoiceItems[position].text);

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageBitmap(mChoiceItems[position].icon);

            return convertView;
        }
    }

    /**
     * Handle clicks from the dialpad chooser.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        DialpadChooserAdapter.ChoiceItem item = (DialpadChooserAdapter.ChoiceItem) parent.getItemAtPosition(position);
        int itemId = item.id;
        switch (itemId) {
        case DialpadChooserAdapter.DIALPAD_CHOICE_USE_DTMF_DIALPAD:
            // Log.i(TAG, "DIALPAD_CHOICE_USE_DTMF_DIALPAD");
            // Fire off an intent to go back to the in-call UI
            // with the dialpad visible.
            returnToInCallScreen(true);
            break;

        case DialpadChooserAdapter.DIALPAD_CHOICE_RETURN_TO_CALL:
            // Log.i(TAG, "DIALPAD_CHOICE_RETURN_TO_CALL");
            // Fire off an intent to go back to the in-call UI
            // (with the dialpad hidden).
            returnToInCallScreen(false);
            break;

        case DialpadChooserAdapter.DIALPAD_CHOICE_ADD_NEW_CALL:
            // Log.i(TAG, "DIALPAD_CHOICE_ADD_NEW_CALL");
            // Ok, guess the user really did want to be here (in the
            // regular Dialer) after all. Bring back the normal Dialer UI.
            showDialpadChooser(false);
            break;

        default:
            Log.w(TAG, "onItemClick: unexpected itemId: " + itemId);
            break;
        }
    }

    /**
     * Returns to the in-call UI (where there's presumably a call in progress)
     * in response to the user selecting "use touch tone keypad" or
     * "return to call" from the dialpad chooser.
     */
    private void returnToInCallScreen(boolean showDialpad) {
        try {
            ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null)
                phone.showCallScreenWithDialpad(showDialpad);
        } catch (RemoteException e) {
            Log.w(TAG, "phone.showCallScreenWithDialpad() failed", e);
        }

        // Finally, finish() ourselves so that we don't stay on the
        // activity stack.
        // Note that we do this whether or not the showCallScreenWithDialpad()
        // call above had any effect or not! (That call is a no-op if the
        // phone is idle, which can happen if the current call ends while
        // the dialpad chooser is up. In this case we can't show the
        // InCallScreen, and there's no point staying here in the Dialer,
        // so we just take the user back where he came from...)
        getActivity().finish();
    }

    /**
     * @return true if the phone is "in use", meaning that at least one line is
     *         active (ie. off hook or ringing or dialing).
     */
    public static boolean phoneIsInUse() {
        boolean phoneInUse = false;
        try {
            ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null)
                phoneInUse = !phone.isIdle();
        } catch (RemoteException e) {
            Log.w(TAG, "phone.isIdle() failed", e);
        }
        return phoneInUse;
    }

    /**
     * @return true if the phone is a CDMA phone type
     */
    private boolean phoneIsCdma() {
        boolean isCdma = false;
        try {
            ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null) {
                isCdma = (phone.getActivePhoneType() == TelephonyManager.PHONE_TYPE_CDMA);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "phone.getActivePhoneType() failed", e);
        }
        return isCdma;
    }

    /**
     * @return true if the phone state is OFFHOOK
     */
    private boolean phoneIsOffhook() {
        boolean phoneOffhook = false;
        try {
            ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null)
                phoneOffhook = phone.isOffhook();
        } catch (RemoteException e) {
            Log.w(TAG, "phone.isOffhook() failed", e);
        }
        return phoneOffhook;
    }

    /**
     * Returns true whenever any one of the options from the menu is selected.
     * Code changes to support dialpad options
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_2s_pause:
            updateDialString(",");
            return true;
        case R.id.menu_add_wait:
            updateDialString(";");
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    /**
     * Updates the dial string (mDigits) after inserting a Pause character (,)
     * or Wait character (;).
     */
    private void updateDialString(String newDigits) {
        int selectionStart;
        int selectionEnd;

        // SpannableStringBuilder editable_text = new
        // SpannableStringBuilder(mDigits.getText());
        int anchor = mDigits.getSelectionStart();
        int point = mDigits.getSelectionEnd();

        selectionStart = Math.min(anchor, point);
        selectionEnd = Math.max(anchor, point);

        Editable digits = mDigits.getText();
        if (selectionStart != -1) {
            if (selectionStart == selectionEnd) {
                // then there is no selection. So insert the pause at this
                // position and update the mDigits.
                digits.replace(selectionStart, selectionStart, newDigits);
            } else {
                digits.replace(selectionStart, selectionEnd, newDigits);
                // Unselect: back to a regular cursor, just pass the character
                // inserted.
                mDigits.setSelection(selectionStart + 1);
            }
        } else {
            int len = mDigits.length();
            digits.replace(len, len, newDigits);
        }
    }

    /**
     * Update the enabledness of the "Dial" and "Backspace" buttons if
     * applicable.
     */
    private void updateDialAndDeleteButtonEnabledState() {
        final boolean digitsNotEmpty = !isDigitsEmpty();

        if (mDialButton != null) {
            // On CDMA phones, if we're already on a call, we *always*
            // enable the Dial button (since you can press it without
            // entering any digits to send an empty flash.)
            if (phoneIsCdma() && phoneIsOffhook()) {
                mDialButton.setEnabled(true);
            } else {
                // Common case: GSM, or CDMA but not on a call.
                // Enable the Dial button if some digits have
                // been entered, or if there is a last dialed number
                // that could be redialed.
                mDialButton.setEnabled(digitsNotEmpty || !TextUtils.isEmpty(mLastNumberDialed));
            }
        }
        mDelete.setEnabled(digitsNotEmpty);
    }

    /**
     * Check if voicemail is enabled/accessible.
     * 
     * @return true if voicemail is enabled and accessibly. Note that this can
     *         be false "temporarily" after the app boot.
     * @see MSimTelephonyManager#getVoiceMailNumber()
     */
    private boolean isVoicemailAvailable() {
        boolean promptEnabled = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MULTI_SIM_VOICE_PROMPT, 0) == 1;
        Log.d(TAG, "prompt enabled :  " + promptEnabled);
        if (promptEnabled) {
            return hasVMNumber();
        } else {
            try {
                mSubscription = MSimTelephonyManager.getDefault().getPreferredVoiceSubscription();
                if (MultiSimConfig.isMultiSimEnabled()) {
                    return (MSimTelephonyManager.getDefault().getVoiceMailNumber(mSubscription) != null);
                } else {
                    return (TelephonyManager.getDefault().getVoiceMailNumber() != null);
                }
            } catch (SecurityException se) {
                // Possibly no READ_PHONE_STATE privilege.
                Log.w(TAG, "SecurityException is thrown. Maybe privilege isn't sufficient.");
            }
        }
        return false;
    }

    private boolean hasVMNumber() {
        boolean hasVMNum = false;
        int phoneCount = MSimTelephonyManager.getDefault().getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            try {
                hasVMNum = MSimTelephonyManager.getDefault().getVoiceMailNumber(i) != null;
            } catch (SecurityException se) {
                // Possibly no READ_PHONE_STATE privilege.
            }
            if (hasVMNum) {
                break;
            }
        }
        return hasVMNum;
    }

    /**
     * This function return true if Wait menu item can be shown otherwise
     * returns false. Assumes the passed string is non-empty and the 0th index
     * check is not required.
     */
    private static boolean showWait(int start, int end, String digits) {
        if (start == end) {
            // visible false in this case
            if (start > digits.length())
                return false;

            // preceding char is ';', so visible should be false
            if (digits.charAt(start - 1) == ';')
                return false;

            // next char is ';', so visible should be false
            if ((digits.length() > start) && (digits.charAt(start) == ';'))
                return false;
        } else {
            // visible false in this case
            if (start > digits.length() || end > digits.length())
                return false;

            // In this case we need to just check for ';' preceding to start
            // or next to end
            if (digits.charAt(start - 1) == ';')
                return false;
        }
        return true;
    }

    /**
     * @return true if the widget with the phone number digits is empty.
     */
    private boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }

    /**
     * Starts the asyn query to get the last dialed/outgoing number. When the
     * background query finishes, mLastNumberDialed is set to the last dialed
     * number or an empty string if none exists yet.
     */
    private void queryLastOutgoingCall() {
        mLastNumberDialed = EMPTY_NUMBER;
        CallLogAsync.GetLastOutgoingCallArgs lastCallArgs = new CallLogAsync.GetLastOutgoingCallArgs(getActivity(),
                new CallLogAsync.OnLastOutgoingCallComplete() {
                    @Override
                    public void lastOutgoingCall(String number) {
                        // TODO: Filter out emergency numbers if
                        // the carrier does not want redial for
                        // these.
                        mLastNumberDialed = number;
                        updateDialAndDeleteButtonEnabledState();
                    }
                });
        mCallLog.getLastOutgoingCall(lastCallArgs);
    }

    private Intent newFlashIntent() {
        final Intent intent = ContactsUtils.getCallIntent(EMPTY_NUMBER);
        intent.putExtra(EXTRA_SEND_EMPTY_FLASH, true);
        intent.putExtra(SUBSCRIPTION_KEY, mSubscription);
        return intent;
    }

    // ddd start pick national code
    private void pickNationalCode() {

        return;
    }

    // ddd end

}
