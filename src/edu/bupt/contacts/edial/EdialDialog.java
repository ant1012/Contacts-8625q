package edu.bupt.contacts.edial;

import java.util.ArrayList;
import java.util.Map;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import edu.bupt.contacts.numberlocate.CountryCodeDBHelper;
import a_vcard.android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ServiceManager;
import android.preference.PreferenceManager;
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

/**
 * 北邮ANT实验室
 * ddd
 * 类描述： 翼拨号主窗口，当启动翼拨号功能时，点击拨号按钮启动  
 * 
 * */

public class EdialDialog extends HoloDialog {

    private static final String TAG = "EdialDialog";
    
    /**
     * 翼拨号主窗口  ddd
     * 
     * */
    private Context context;
    //字段描述： 从拨号盘提取的初始拨打号码
    private String callNumber;
    //字段描述： 经过加前后缀等处理的最终拨打号码
    private String sendNumber;
    /** zzz */    
    //字段描述： 单选按钮组
    private RadioGroup radioGroupEsurfing;
    //字段描述： 拨回所在国按钮，默认选中
    private RadioButton callBackChinaButton;
    //字段描述： 选中拨号所在国按钮后，继续选择拨打方式：国际漫游呼叫按钮
    private RadioButton internationalButton;
    //字段描述： 选中拨号所在国按钮后，继续选择拨打方式：**133回拨
    private RadioButton call133Button;
    //字段描述： 拨打其他国家地区按钮
    private RadioButton callOtherButton;
    //字段描述： 拨打本地按钮
    private RadioButton callLocalButton;
    //字段描述： 翼拨号题头文本框，根据不同国家码改变内容
    private TextView title;
    //字段描述： 拨打号码编辑框
    private EditText EditTextNumber;
    //字段描述： 翼拨号国家码文本框，根据选择的不同呼叫国家改变内容
    private TextView TextViewPrefix;
    //字段描述： 当拨叫方式为**133时，拨叫号码添加后缀“#”
    private TextView TextViewSuffix;
    //字段描述： 拨号按钮
    private Button callButton;

    
    /**
     * 选择目标国家地区窗口 ddd
     * 
     * */
    //字段描述： 搜索亚洲地区国家按钮
    private Button asiaButton;
    //字段描述： 搜索欧洲地区国家按钮
    private Button europeButton;
    //字段描述： 搜索大洋洲地区国家按钮
    private Button oceaniaButton;
    //字段描述： 搜索非洲地区国家按钮
    private Button africaButton;
    //字段描述： 搜索南美洲地区国家按钮
    private Button northAmericaButton;
    //字段描述： 搜索北美洲地区国家按钮
    private Button southAmericaButton;
    //字段描述： 搜索结果列表视图
    private ListView listView;
    //字段描述： 列表视图表项格式：国家名称，国家码
    private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
    //字段描述： 搜索国家名称编辑框		
    private EditText searchButtonEditText;
    //字段描述： 搜索按钮
    private Button countrySearchButton;
    //字段描述： 国家名称
    private String countryName;
    //字段描述： 拨叫号码国家码前缀
    private StringBuffer stringTitle;
    //字段描述： 拨叫号码后缀
    private StringBuffer stringPre;
    // private Cursor cursor;
    // private SimpleAdapter adapter;
    
    
    /**
     * 
     * 方法描述： 翼拨号构造函数 ddd
     * 
     * */
    public EdialDialog(final Context context, String digits) {
        super(context);

        /** zzz */
        this.context = context;
        
        /**
         * 初始化变量 ddd
         * */
        
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
        /**
         * 判断是否可以使用**133回拨业务，不能使用时置灰相应按钮 ddd
         * 
         * */
        if (!is133Enabled()) {
            call133Button.setClickable(false);
            call133Button.setTextColor(context.getResources().getColor(R.color.edial_text_color_unclickable));
        }

         stringPre = new StringBuffer();
        // stringPre.append("+86");
         stringTitle = new StringBuffer();
        //stringTitle.append(context.getString(R.string.esurfing_dial_call_title_china));
         
        //ddd change title and pre according to the simcountryiso
         
        /**
         * 根据SIM卡所属国家，获取国家码，更改相应文本框内容 ddd
         * 
         * */
         
        ContentValues simcv = getSimCountryCodeAndName();
        Log.i(TAG,"simcv--"+simcv.toString());
        stringTitle.replace(0, stringTitle.length(), simcv.getAsString("name") + " +" + simcv.getAsString("code"));
        stringPre.replace(0, stringPre.length(), "+" + simcv.getAsString("code"));
        title.setText(stringTitle);
        TextViewPrefix.setText(stringPre);
        callBackChinaButton.setText("拨回"+simcv.getAsString("name"));
        //ddd end
        
        /**
         * 获取拨号盘输入的拨叫号码，添加到号码编辑框内，并且能够修改该号码 ddd
         * */
        
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

            /**
             * 当文本编辑框的内容改变时，将更改后的值赋给sendNumber
             * */
            
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
        
        /**
         * 单选拨叫模式 ddd
         * 
         * */
        radioGroupEsurfing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	ContentValues simcv = getSimCountryCodeAndName();
                
                switch (checkedId) {

                /**
                 * 国际漫游呼叫方式拨回国：获取SIM卡所在国，为拨叫号码添加相应国家码，设置号码后缀为空，默认选中拨回国按钮 ddd
                 * */
                
                case R.id.radioButton_international:
                    //stringTitle.replace(0, stringTitle.length(),context.getString(R.string.esurfing_dial_call_title_china));
                    //stringPre.replace(0, stringPre.length(), "+86");
                    stringTitle.replace(0, stringTitle.length(), simcv.getAsString("name") + " +" + simcv.getAsString("code"));
                    stringPre.replace(0, stringPre.length(), "+" + simcv.getAsString("code"));
                	TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);
                    callBackChinaButton.setChecked(true);
                    // callNumber = "+86" + sendNumber;
                    break;

                    /**
                     * **133方式拨回国： 获取SIM卡所在国，为拨叫号码添加相应国家码，设置号码后缀为#，默认选中拨回国按钮 ddd
                     * 
                     * */
                    
                    
                case R.id.radioButton_133:
                   // stringTitle.replace(0, stringTitle.length(),
                   //         context.getString(R.string.esurfing_dial_call_title_china));
                   // stringPre.replace(0, stringPre.length(), "**133*86");
                    
                    stringTitle.replace(0, stringTitle.length(), simcv.getAsString("name") + " +" + simcv.getAsString("code"));
                    stringPre.replace(0, stringPre.length(), "+" + simcv.getAsString("code"));
                	
                	TextViewSuffix.setText(R.string.esurfing_dial_suffix_hash);
                    callBackChinaButton.setChecked(true);
                    // callNumber = "**133*86" + sendNumber + "%23";
                    break;

                    /**
                     * 拨打其他国家地区：弹出国家选择窗 ddd
                     * */
                    
                case R.id.radioButton_callOther:
                    // stringTitle.replace(0, stringTitle.length(), "美国+1");
                    // stringPre.replace(0, stringPre.length(), "+1");
//                    stringTitle.replace(0, stringTitle.length(),
//                            context.getString(R.string.esurfing_dial_call_title_china));
//                    stringPre.replace(0, stringPre.length(), "+86");

                    stringTitle.replace(0, stringTitle.length(), simcv.getAsString("name") + " +" + simcv.getAsString("code"));
                    stringPre.replace(0, stringPre.length(), "+" + simcv.getAsString("code"));
                	
                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);
                    callBackChinaButton.setChecked(false);
                    // Context context = getActivity();

                    /** zzz */
                    // Dialog nationalCodeDialog = new Dialog(context);
                    final HoloDialog nationalCodeDialog = new HoloDialog(context);
                    /**
                     * 启动国家码选择窗口，初始化洲选择按钮
                     * */
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
                    /**
                     * 调用此函数，当按下某个洲的按钮时，将该洲的国家显示到listView里面
                     * */
                    pickCountry();
                    searchCountry(searchButtonEditText, countrySearchButton);
                    // chooseItem();
                    /**
                     * 当按下listView中的某个国家时，提取该国家的国家名称和国家码
                     * */
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

                    /**
                     * 拨打本地，获取本地国家码，添加相应前缀 ddd
                     * */
                    
                case R.id.radioButton_callLocal:
                    TextViewSuffix.setText(R.string.esurfing_dial_suffix_null);

                    /** zzz */
                                        
                    ContentValues cv = getLocalCountryCodeAndName();
                    Log.i(TAG,"ZZZ CV"+cv.toString());
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
        
        /**
         * 拨打按钮，点击拨号 ddd
         * */

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
    
    /**
     * 方法描述： 拨打电话，调用系统拨打电话方法拨打 ddd
     * */
    
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
    
    /**
     * 方法描述：拨打其他国家地区时，选择国家 ddd
     * */
    private void pickCountry() {
        final CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());

        // 默认显示亚洲地区
        list = mdbHelper.getCountry(8);
        SimpleAdapter adapter = new SimpleAdapter(EdialDialog.this.getContext(), list, R.layout.edial_item,
                new String[] { "cn_name", "code" }, new int[] { R.id.edial_item_text1, R.id.edial_item_text2 });

        listView.setAdapter(adapter);
/**
 * 按下非洲按钮后，从数据库匹配洲分类为非洲的国家，显示在listView中 ddd
 * */
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
        /**
         * 按下北美洲按钮后，从数据库匹配洲分类为北美洲的国家，显示在listView中 ddd
         * */
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

        /**
         * 按下亚洲按钮后，从数据库匹配周分类为亚洲的国家，显示在listView中 ddd
         * */
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

        /**
         * 按下南美洲按钮后，从数据库匹配周分类为南美洲的国家，显示在listView中 ddd
         * */
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
        /**
         * 按下大洋洲按钮后，从数据库匹配周分类为大洋洲的国家，显示在listView中 ddd
         * */
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
        /**
         * 按下欧洲按钮后，从数据库匹配洲分类为欧洲的国家，显示在listView中 ddd
         * */
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

    /**
     * 方法描述： 拨打其他国家时，搜索国家 ddd
     * 
     * */
    
    private void searchCountry(final EditText inputcountry, Button searchButton) {
        final CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());
/**
 * 当搜索按钮被按下时：
 * */
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

    /**
     * 方法描述： 获取本地国家码和国家名称 ddd
     * 
     * */
    private ContentValues getLocalCountryCodeAndName() {
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getNetworkCountryIso();
        Log.i(TAG, countryIso);
        ContentValues ret = mdbHelper.queryLocalCountryCodeAndName(countryIso);

        return ret;
    }
    //ddd
    /**
     * 方法描述： 获取SIM卡所在国家的国家码和名称 ddd
     * 
     * */
    private ContentValues getSimCountryCodeAndName() {
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());
       // TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
       // String countryIso = tm.getNetworkCountryIso();
        TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = manager.getSimCountryIso();
        Log.i(TAG,"countryCode_simType--"+countryIso);

        ContentValues ret = mdbHelper.queryLocalCountryCodeAndName(countryIso);
        return ret;
    }

    /** zzz */
    
    /**
     * 方法描述： 判断是否支持**133回拨功能 ddd
     * */
    private boolean is133Enabled() {
        CountryCodeDBHelper mdbHelper = new CountryCodeDBHelper(this.getContext());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        MSimTelephonyManager mtm = (MSimTelephonyManager) context.getSystemService(context.MSIM_TELEPHONY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // for test
//        if (sp.getBoolean("RoamingTestPreference", false)) {
//            return true;
//        }

        if (!(mtm.isNetworkRoaming(0) || sp.getBoolean("RoamingTestPreference", false))) {
            return false;
        }
        switch (MSimTelephonyManager.getNetworkType(0)) {
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return false;
        default:
            String countryIso = tm.getNetworkCountryIso();
            return mdbHelper.queryIs133Enabled(countryIso);
        }

    }
}
