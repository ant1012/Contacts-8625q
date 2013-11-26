package edu.bupt.contacts.settings;

import java.util.ArrayList;
import edu.bupt.contacts.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class IPSelectDialog extends Dialog {
    private static final String TAG = "IPSelectDialog";

    public static final int CDMA = 0x0;
    public static final int GSM = 0x1;

    private Context context;
    private ListView lvDefaults;
    private ArrayList<String> data;

    public IPSelectDialog(final Context context, int sim) {
        super(context);
        this.context = context;

        final boolean isCDMA = sim == CDMA ? true : false;

        this.setContentView(R.layout.preference_ip_select);

        if (isCDMA) {
            this.setTitle(R.string.cdma_ip_setting);
        } else {
            this.setTitle(R.string.gsm_ip_setting);
        }

        lvDefaults = (ListView) findViewById(R.id.lv_ip_defaults);

        data = new ArrayList<String>();

        // defaults
        String[] ipDefaultsArray;
        if (isCDMA) {
            ipDefaultsArray = context.getResources().getStringArray(R.array.cdma_ip_defaults);
        } else {
            ipDefaultsArray = context.getResources().getStringArray(R.array.gsm_ip_defaults);
        }

        for (int i = 0; i < ipDefaultsArray.length; i++) {
            data.add(ipDefaultsArray[i]);
        }

        // edit
        data.add(context.getString(R.string.preference_category_ipcall_else));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);

        // click listener
        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int posision, long id) {
                Log.v(TAG, "posision - " + posision);
                Log.i(TAG, "data selected - " + data.get(posision));

                if (posision != data.size() - 1) {

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    Editor editor = sp.edit();

                    if (isCDMA) {
                        editor.putString("CDMAIPPreference", data.get(posision));
                    } else {
                        editor.putString("GSMIPPreference", data.get(posision));
                    }
                    editor.commit();

                    IPSelectDialog.this.dismiss();
                } else if (posision == data.size() - 1) {
                    final EditText et = new EditText(context);
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new AlertDialog.Builder(context).setTitle(R.string.preference_category_ipcall_else)
                            .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                            .setPositiveButton(R.string.ok, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Log.v(TAG, "setPositiveButton onClick");
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                                    Editor editor = sp.edit();
                                    if (isCDMA) {
                                        editor.putString("CDMAIPPreference", et.getText().toString());
                                    } else {
                                        editor.putString("GSMIPPreference", et.getText().toString());
                                    }
                                    editor.commit();
                                }
                            }).setNegativeButton(R.string.cancel, null).show();
                    IPSelectDialog.this.dismiss();
                }
            }
        };

        lvDefaults.setAdapter(adapter);
        lvDefaults.setOnItemClickListener(listener);
    }

}
