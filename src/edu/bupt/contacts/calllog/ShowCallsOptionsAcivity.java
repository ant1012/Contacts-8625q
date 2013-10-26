package edu.bupt.contacts.calllog;

import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.ContactsUtils;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import edu.bupt.contacts.R;
import edu.bupt.contacts.activities.DialtactsActivity;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

public class ShowCallsOptionsAcivity extends Activity{

	private RadioGroup callOptionRadiogroup;
	private RadioButton buttonAll,buttonIn,buttonOut,buttonMissed,buttonSim,buttonUim;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_log_show_options);


        callOptionRadiogroup=(RadioGroup)findViewById(R.id.call_log_show_radioGroup);
        buttonAll=(RadioButton)findViewById(R.id.radiobutton_show_all_calls);
        buttonIn=(RadioButton)findViewById(R.id.radiobutton_show_in_calls);
        buttonOut=(RadioButton)findViewById(R.id.radiobutton_show_out_calls);
        buttonMissed=(RadioButton)findViewById(R.id.radiobutton_show_missed_calls);
        buttonSim=(RadioButton)findViewById(R.id.radiobutton_show_sim_calls);
        buttonUim=(RadioButton)findViewById(R.id.radiobutton_show_uim_calls);

        

	}

}
