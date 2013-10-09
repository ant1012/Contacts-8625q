package edu.bupt.contacts.blacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.bupt.contacts.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingsFragment extends Fragment {

    public static final String TAG = "franco--->SettingsFragment";
    private Context context;
    private View view;
    private ListView listView;
    private CheckBox whitecheckBox;
    private CheckBox checkBox;
    private SimpleAdapter adapter;
    private ArrayList<Map<String, Object>> data;
    private Map<String, Object> item_0, item_1;
    private int modePosition, ringtonePosition;
    private int modeLatest, ringtoneLatest;
    private SharedPreferences sp;
    private SharedPreferences whiteMode;

    public SettingsFragment(Context context) {
        this.context = context;
        modePosition = 0;
        ringtonePosition = 0;
        sp = context.getSharedPreferences("blacklist", 0);
        whiteMode = context.getSharedPreferences("whitelist", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.settings, container, false);
        findViewAndSetListener();
        return view;
    }

    private void findViewAndSetListener() {
        
        modePosition = sp.getInt("mode", 0);
        ringtonePosition = sp.getInt("ringtone", 0);
        String[] modeArray = context.getResources()
                .getStringArray(R.array.mode);
        String[] ringtoneArray = context.getResources().getStringArray(
                R.array.ringtone);
        whitecheckBox = (CheckBox) view.findViewById(R.id.white_mode);
        whitecheckBox.setChecked(sp.getBoolean("white_mode", false));
        whitecheckBox.setOnCheckedChangeListener(whitecheckedChangeListener);
        checkBox = (CheckBox) view.findViewById(R.id.block_stranger);
        checkBox.setChecked(sp.getBoolean("blockStranger", false));
        checkBox.setOnCheckedChangeListener(checkedChangeListener);
        listView = (ListView) view.findViewById(android.R.id.list);
        data = new ArrayList<Map<String, Object>>();
        item_0 = new HashMap<String, Object>();
        item_0.put("title",
                context.getResources().getString(R.string.mode_title));
        item_0.put("message", modeArray[modePosition]);
        data.add(item_0);
        item_1 = new HashMap<String, Object>();
        item_1.put("title",
                context.getResources().getString(R.string.ringtone_title));
        item_1.put("message", ringtoneArray[ringtonePosition]);
        data.add(item_1);
        adapter = new SimpleAdapter(context, data, R.layout.settings_item,
                new String[] { "title", "message" }, new int[] { R.id.title,
                        R.id.message });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = 
            new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            switch (arg2) {
            case 0:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle(R.string.mode_title)
                        .setSingleChoiceItems(R.array.mode, modePosition,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub
                                        modeLatest = which;
                                    }
                                })
                        .setNegativeButton(R.string.cancle, null)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
                                        arg0.dismiss();
                                        modePosition = modeLatest;
                                        String mode = context.getResources()
                                                .getStringArray(R.array.mode)[modePosition];
                                        item_0.put("message", mode);
                                        data.clear();
                                        data.add(item_0);
                                        data.add(item_1);
                                        adapter.notifyDataSetChanged();
                                        Log.v(TAG, "mode = " + mode);
                                        save("mode", modePosition);
                                    }
                                }).create().show();
                break;
            case 1:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle(R.string.ringtone_title)
                        .setSingleChoiceItems(R.array.ringtone,
                                ringtonePosition,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub
                                        ringtoneLatest = which;
                                    }
                                })
                        .setNegativeButton(R.string.cancle, null)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
                                        arg0.dismiss();
                                        ringtonePosition = ringtoneLatest;
                                        String ringtone = context
                                                .getResources().getStringArray(
                                                        R.array.ringtone)[ringtonePosition];
                                        item_1.put("message", ringtone);
                                        data.clear();
                                        data.add(item_0);
                                        data.add(item_1);
                                        adapter.notifyDataSetChanged();
                                        Log.v(TAG, "ringtone = " + ringtone);
                                        save("ringtone", ringtonePosition);
                                    }
                                }).create().show();
                break;
            }

        }
    };

    private CheckBox.OnCheckedChangeListener checkedChangeListener = 
            new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
        	sp.edit().putBoolean("blockStranger", arg0.isChecked()).commit();
            Log.v(TAG, "arg0.isChecked() = " + arg0.isChecked());
        }

    };

    private void save(String name, int position) {
        sp.edit().putInt(name, position).commit();
    }
    
    private CheckBox.OnCheckedChangeListener whitecheckedChangeListener = 
            new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
        	whiteMode.edit().putBoolean("white_mode", arg0.isChecked()).commit();
            Log.v(TAG, "arg0.isChecked() = " + arg0.isChecked());
        }

    };
    
    
}
