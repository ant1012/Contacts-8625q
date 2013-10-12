package edu.bupt.contacts.settings;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CalllogSettingActivity extends Activity {

    private TextView textViewCDMAIPSetting;
    private TextView textViewGSMIPSetting;
    SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_setting);

        sp = this.getSharedPreferences("edu.bupt.contacts.ip", MODE_PRIVATE);

        textViewCDMAIPSetting = (TextView) findViewById(R.id.textView_cdma_ip);
        if (!(sp.getString("CDMA_IP_KEY", null) == null)) {
            textViewCDMAIPSetting.setText(sp.getString("CDMA_IP_KEY", null));
        }
        textViewGSMIPSetting = (TextView) findViewById(R.id.textView_gsm_ip);
        if (!(sp.getString("GSM_IP_KEY", null) == null)) {
            textViewGSMIPSetting.setText(sp.getString("GSM_IP_KEY", null));
        }

        textViewCDMAIPSetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDialog(textViewCDMAIPSetting, textViewCDMAIPSetting
                        .getText().toString(), "CDMA_IP_KEY", isCDMAIPEnabled());
            }

        });

        textViewGSMIPSetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDialog(textViewGSMIPSetting, textViewGSMIPSetting.getText()
                        .toString(), "GSM_IP_KEY", isGSMIPEnabled());
            }

        });

    }

    private void showDialog(final TextView textView, String str,
            final String spKey, final boolean flag) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.call_ip_setting_dialog,
                (ViewGroup) findViewById(R.id.call_ip_dialog));
        final EditText ipsetting = (EditText) layout
                .findViewById(R.id.edittext_ip);
        if (flag) {
            ipsetting.setText(str);
        } else {
            ipsetting.setHint(str);
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.call_ip_setting_dialog_title)
                .setView(layout)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                // TODO Auto-generated method stub
                                if (ipsetting.getText().length() > 0) {
                                    Editor editor = sp.edit();
                                    editor.putString(spKey, ipsetting.getText()
                                            .toString());
                                    editor.commit();
                                    textView.setText(ipsetting.getText()
                                            .toString());
                                } else {
                                    if (flag) {
                                        Editor editor = sp.edit();
                                        editor.remove(spKey);
                                        editor.commit();
                                        textView.setText(R.string.default_ip_setting);
                                    }
                                }
                            }

                        }).setNegativeButton(R.string.cancel, null).show();

    }

    public boolean isCDMAIPEnabled() {
        if (!(sp.getString("CDMA_IP_KEY", null) == null)) {
            return true;
        }
        return false;
    }

    public boolean isGSMIPEnabled() {
        if (!(sp.getString("GSM_IP_KEY", null) == null)) {
            return true;
        }
        return false;
    }
}
