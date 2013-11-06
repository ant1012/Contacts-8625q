package edu.bupt.contacts.edial;

import java.util.ArrayList;
import java.util.Map;

import com.android.internal.telephony.msim.ITelephonyMSim;

import edu.bupt.contacts.R;
import edu.bupt.contacts.blacklist.BlacklistDBHelper;
import a_vcard.android.util.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.ServiceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EdialDialog extends HoloDialog {
	
	 CountryCodeDBHelper mdbHelper;
     Button asiaButton = (Button) this.findViewById(R.id.asiaButton);
     Button europeButton = (Button) this.findViewById(R.id.europeButton);
     Button oceaniaButton = (Button) this.findViewById(R.id.oceaniaButton);
     Button africaButton = (Button) this.findViewById(R.id.africaButton);
     Button northAmericaButton = (Button) this.findViewById(R.id.northAmericaButton);
     Button southAmericaButton = (Button) this.findViewById(R.id.southAmericaButton);
     private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	 private ListView listView;
	 private Cursor cursor;
	 private SimpleAdapter adapter;
	   

    public EdialDialog(final Context context, String digits) {
        super(context);

        // AlertDialog.Builder builder = new AlertDialog.Builder(context);

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

        String sendNumber = digits;
        // Dialog dialog = new Dialog(context);
        this.setContentView(R.layout.dialpad_esurfing);
        this.setTitle("翼拨号");
        RadioGroup radioGroupEsurfing = (RadioGroup) this.findViewById(R.id.radioGroupEsurfing);
        final RadioButton callBackChinaButton = (RadioButton) this.findViewById(R.id.radioButton_callBackChina);
        final RadioButton internationalButton = (RadioButton) this.findViewById(R.id.radioButton_international);
        final RadioButton call133Button = (RadioButton) this.findViewById(R.id.radioButton_133);
        final RadioButton callOtherButton = (RadioButton) this.findViewById(R.id.radioButton_callOther);
        final RadioButton callLocalButton = (RadioButton) this.findViewById(R.id.radioButton_callLocal);
       
//        final Button asiaButton = (Button) this.findViewById(R.id.asiaButton);
//        final Button europeButton = (Button) this.findViewById(R.id.europeButton);
//        final Button oceaniaButton = (Button) this.findViewById(R.id.oceaniaButton);
//        final Button africaButton = (Button) this.findViewById(R.id.africaButton);
//        final Button northAmericaButton = (Button) this.findViewById(R.id.northAmericaButton);
//        final Button southAmericaButton = (Button) this.findViewById(R.id.southAmericaButton);

        final TextView title = (TextView) this.findViewById(R.id.textView_title);
        final TextView pre = (TextView) this.findViewById(R.id.textView_pre);
        final StringBuffer stringPre = new StringBuffer();
        stringPre.append("+86");
        final StringBuffer stringTitle = new StringBuffer();
        stringTitle.append("中国+86");
        final TextView TextViewSuffix = (TextView) this.findViewById(R.id.textView_suffix);
        EditText EditTextNumber = (EditText) this.findViewById(R.id.editTextInputNumber);
        EditTextNumber.setText(sendNumber);

        // dialog.show();

        radioGroupEsurfing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {

                case R.id.radioButton_international:
                    stringTitle.replace(0, stringTitle.length(), "中国+86");
                    stringPre.replace(0, stringPre.length(), "+86");
                    TextViewSuffix.setVisibility(8);
                    callBackChinaButton.setChecked(true);
                    break;

                case R.id.radioButton_133:
                    stringTitle.replace(0, stringTitle.length(), "中国+86");
                    stringPre.replace(0, stringPre.length(), "**133*86");
                    TextViewSuffix.setVisibility(0);
                    callBackChinaButton.setChecked(true);
                    break;

                case R.id.radioButton_callOther:
                    stringTitle.replace(0, stringTitle.length(), "美国+1");
                    stringPre.replace(0, stringPre.length(), "+1");

                    TextViewSuffix.setVisibility(8);

                    callBackChinaButton.setChecked(false);
                    // Context context = getActivity();

                    /** zzz */
                    // Dialog nationalCodeDialog = new Dialog(context);
                    HoloDialog nationalCodeDialog = new HoloDialog(context);

                    nationalCodeDialog.setContentView(R.layout.dialpad_esurfing_national_code);
                    nationalCodeDialog.setTitle("选择目标国家地区");
                    nationalCodeDialog.show();
                    pickCountry();
                    break;

                case R.id.radioButton_callLocal:
                    TextViewSuffix.setVisibility(8);
                    stringTitle.replace(0, stringTitle.length(), "中国+86");
                    stringPre.replace(0, stringPre.length(), "");
                    callBackChinaButton.setChecked(false);
                    break;

                }
                title.setText(stringTitle);
                pre.setText(stringPre);

            }
        });
    }

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
    
    private void pickCountry(){
    	
        listView = (ListView) this.findViewById(R.id.nationalCodeListView);
        mdbHelper=new CountryCodeDBHelper(this.getContext());
        final SQLiteDatabase db = mdbHelper.getReadableDatabase();
        mdbHelper.onCreate(db);


    	africaButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                 Log.i("this is africa", "africa");
				list=mdbHelper.getCountry(1);
				Log.i("list", list.toString());
			
				adapter = new SimpleAdapter(EdialDialog.this.getContext(),list,R.layout.edial_item, new String[]{"cn_name","code"}, new int[]{R.id.edial_item_text1, R.id.edial_item_text2});
		        
               listView.setAdapter(adapter);
				
			}
		});
    	

    	
    }
}


