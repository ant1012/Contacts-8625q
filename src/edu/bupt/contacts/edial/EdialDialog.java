package edu.bupt.contacts.edial;

import java.util.ArrayList;
import java.util.Map;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import a_vcard.android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.os.ServiceManager;
import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class EdialDialog extends HoloDialog {

    private static final String TAG = "EdialDialog";
    private String callNumber;
    private String sendNumber;
    /** zzz */
    private Context context;
    private RadioGroup radioGroupEsurfing;
    private RadioButton callBackChinaButton;
    private RadioButton internationalButton;
    private RadioButton call133Button;
    private RadioButton callOtherButton;
    private RadioButton callLocalButton;
    private TextView title;
    private EditText EditTextNumber;
    private TextView TextViewPrefix;
    private TextView TextViewSuffix;
    private Button callButton;

    // for pickCountry dialog
    private Button asiaButton;
    private Button europeButton;
    private Button oceaniaButton;
    private Button africaButton;
    private Button northAmericaButton;
    private Button southAmericaButton;
    private ListView listView;
    private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private EditText searchButtonEditText;
    private Button countrySearchButton;
    private String countryName;

    // private Cursor cursor;
    // private SimpleAdapter adapter;

    public EdialDialog(final Context context, String digits) {
        super(context);

        /** zzz */
        this.context = context;

        sendNumber = digits;
        // Dialog dialog = new Dialog(context);
        this.setContentView(R.layout.dialpad_esurfing);
        this.setTitle(R.string.esurfing_dial);
        callBackChinaButton = (RadioButton) this.findViewById(R.id.radioButton_callBackChina);
        internationalButton = (RadioButton) this.findViewById(R.id.radioButton_international);
        call133Button = (RadioButton) this.findViewById(R.id.radioButton_133);
        callOtherButton = (RadioButton) this.findViewById(R.id.radioButton_callOther);
        callLocalButton = (RadioButton) this.findViewById(R.id.radioButton_callLocal);
        title = (TextView) this.findViewById(R.id.textView_title);
        TextViewPrefix = (TextView) this.findViewById(R.id.textView_pre);
        TextViewSuffix = (TextView) this.findViewById(R.id.textView_suffix);
        callButton = (Button) this.findViewById(R.id.dialButton);
        radioGroupEsurfing = (RadioGroup) this.findViewById(R.id.radioGroupEsurfing);
        EditTextNumber = (EditText) this.findViewById(R.id.editTextInputNumber);

        // TODO
        if (isC2C()) {
            call133Button.setClickable(false);
            call133Button.setTextColor(context.getResources().getColor(R.color.edial_text_color_unclickable));
        }

        // stringPre = new StringBuffer();
        // stringPre.append("+86");
        // stringTitle = new StringBuffer();
        // stringTitle.append(context.getString(R.string.esurfing_dial_call_title_china));

        EditTextNumber.setText(sendNumber);
        EditTextNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.v(TAG, "onTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.v(TAG, "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                Log.v(TAG, "afterTextChanged");
                sendNumber = EditTextNumber.getText().toString();
                // Toast.makeText(EdialDialog.this.getContext(),sendNumber,
                // Toast.LENGTH_LONG).show();
            }
        });

        callButton = (Button) this.findViewById(R.id.dialButton);
        // 默认拨打国际漫游
        // callNumber = "+86" + sendNumber;
        radioGroupEsurfing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                StringBuffer stringTitle = new StringBuffer();
                StringBuffer stringPre = new StringBuffer();
                switch (checkedId) {

                case R.id.radioButton_international:
                    stringTitle.replace(0, stringTitle.length(),
                            context.getString(R.string.esurfing_dial_call_title_china));
                    stringPre.replace(0, stringPre.length(), "+86");
                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);
                    callBackChinaButton.setChecked(true);
                    // callNumber = "+86" + sendNumber;
                    break;

                case R.id.radioButton_133:
                    stringTitle.replace(0, stringTitle.length(),
                            context.getString(R.string.esurfing_dial_call_title_china));
                    stringPre.replace(0, stringPre.length(), "**133*86");
                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_hash);
                    callBackChinaButton.setChecked(true);
                    // callNumber = "**133*86" + sendNumber + "%23";
                    break;

                case R.id.radioButton_callOther:
                    // stringTitle.replace(0, stringTitle.length(), "美国+1");
                    // stringPre.replace(0, stringPre.length(), "+1");
                    stringTitle.replace(0, stringTitle.length(),
                            context.getString(R.string.esurfing_dial_call_title_china));
                    stringPre.replace(0, stringPre.length(), "+86");

                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);
                    callBackChinaButton.setChecked(false);
                    // Context context = getActivity();

                    /** zzz */
                    // Dialog nationalCodeDialog = new Dialog(context);
                    final HoloDialog nationalCodeDialog = new HoloDialog(context);

                    nationalCodeDialog.setContentView(R.layout.dialpad_esurfing_national_code);
                    asiaButton = (Button) nationalCodeDialog.findViewById(R.id.button_national_picker_asia);
                    europeButton = (Button) nationalCodeDialog.findViewById(R.id.button_national_picker_europe);
                    oceaniaButton = (Button) nationalCodeDialog.findViewById(R.id.button_national_picker_oceania);
                    africaButton = (Button) nationalCodeDialog.findViewById(R.id.button_national_picker_africa);
                    northAmericaButton = (Button) nationalCodeDialog
                            .findViewById(R.id.button_national_picker_northamerica);
                    southAmericaButton = (Button) nationalCodeDialog
                            .findViewById(R.id.button_national_picker_southamerica);
                    listView = (ListView) nationalCodeDialog.findViewById(R.id.nationalCodeListView);

                    searchButtonEditText = (EditText) nationalCodeDialog
                            .findViewById(R.id.edittext_national_picker_search);
                    countrySearchButton = (Button) nationalCodeDialog.findViewById(R.id.button_national_picker_search);

                    pickCountry();
                    searchCountry(searchButtonEditText, countrySearchButton);
                    // chooseItem();

                    listView.setOnItemClickListener(new ListView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            // TODO Auto-generated method stub
                            TextView country = (TextView) arg1.findViewById(R.id.edial_item_text1);
                            TextView code = (TextView) arg1.findViewById(R.id.edial_item_text2);
                            Log.i("tag", country.toString());
                            Log.i("tag", code.toString());
                            String countryname = (String) country.getText();
                            String countrycode = (String) code.getText();
                            // callNumber = "+" + countrycode + sendNumber;
                            title.setText(countryname + "+" + countrycode);
                            TextViewPrefix.setText("+" + countrycode);
                            nationalCodeDialog.dismiss();
                        }
                    });
                    nationalCodeDialog.setTitle(R.string.esurfing_dial_pick_country);
                    nationalCodeDialog.show();

                    break;

                case R.id.radioButton_callLocal:
                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);

                    /** zzz */
                    ContentValues cv = getLocalCountryCodeAndName();
                    stringTitle.replace(0, stringTitle.length(), cv.getAsString("name") + " +" + cv.getAsString("code"));
                    stringPre.replace(0, stringPre.length(), "+" + cv.getAsString("code"));
                    // callNumber = "+" + cv.getAsString("code") + sendNumber;
                    // stringTitle.replace(0, stringTitle.length(),
                    // context.getString(R.string.esurfing_dial_call_title_local));
                    // stringPre.replace(0, stringPre.length(), "");
                    callBackChinaButton.setChecked(false);
                    break;

                }
                Log.i("tag", stringTitle.toString());
                Log.i("tag", stringPre.toString());
                title.setText(stringTitle);
                TextViewPrefix.setText(stringPre);

            }

        });

        callButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String hash = TextViewSuffix.getText().equals("#") ? "%23" : "";
                callNumber = TextViewPrefix.getText().toString() + EditTextNumber.getText().toString() + hash;
                Log.i(TAG, "callNumber - " + callNumber);
                call(callNumber);
                dismiss();

            }
        });
    }

    /** zzz */
    private void call(String number) {
        try {
            ITelephonyMSim telephony = ITelephonyMSim.Stub.asInterface(ServiceManager
                    .getService(Context.MSIM_TELEPHONY_SERVICE));
            telephony.call(number, 0);

            // MSimTelephonyManager m =
            // (MSimTelephonyManager)getSystemService(MSIM_TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // northamerica 1
    // africa 2
    // europe 3(347)
    // southamerica 5
    // oceania 6
    // asia 8(698)
    private void pickCountry() {
        final CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());

        // 默认显示亚洲地区
        list = mdbHelper.getCountry(8);
        SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

        listView.setAdapter(adapter);

        africaButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(2);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        northAmericaButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(1);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        asiaButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(8);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        southAmericaButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(5);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        oceaniaButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(6);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        europeButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Log.i("this is africa", "africa");
                list = mdbHelper.getCountry(3);
                Log.i("list", list.toString());

                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });

        mdbHelper.close();
    }

    private void searchCountry(final EditText inputcountry, Button searchButton) {
        final CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());

        searchButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                countryName = inputcountry.getText().toString();
                Toast.makeText(EdialDialog.this.getContext(), countryName, Toast.LENGTH_LONG).show();

                Log.i("name", countryName);
                list = mdbHelper.searchCountry(countryName);
                SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                        new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

                listView.setAdapter(adapter);

            }
        });
        mdbHelper.close();

    }

    // private void chooseItem() {
    // TextView countryname;
    // TextView code;
    // listView.setClickable(true);
    // listView.setOnItemClickListener(new ListView.OnItemClickListener() {
    //
    // @Override
    // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
    // arg3) {
    // // TODO Auto-generated method stub
    // TextView country = (TextView) arg1.findViewById(R.id.edial_item_text1);
    // TextView code = (TextView) arg1.findViewById(R.id.edial_item_text2);
    // Log.i("tag", country.toString());
    // Log.i("tag", code.toString());
    // String countryname = (String) country.getText();
    // String countrycode = (String) code.getText();
    //
    // stringTitle.replace(0, stringTitle.length(), countryname + countrycode);
    // stringPre.replace(0, stringPre.length(), countrycode);
    //
    // }
    // });
    // }

    /** zzz */
    private ContentValues getLocalCountryCodeAndName() {
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getNetworkCountryIso();
        Log.i(TAG, countryIso);
        ContentValues ret = mdbHelper.queryLocalCountryCodeAndName(countryIso);

        return ret;
    }

    /** zzz */
    private boolean isC2C() {
        MSimTelephonyManager m = (MSimTelephonyManager) context.getSystemService(context.MSIM_TELEPHONY_SERVICE);
        // if (!m.isNetworkRoaming(0)) {
        // return false;
        // }
        switch (MSimTelephonyManager.getNetworkType(0)) {
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return true;
        default:
            return false;
        }

    }
}
