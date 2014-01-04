package edu.bupt.contacts.blacklist;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * 北邮ANT实验室
 * zzz
 * 
 * BlacklistMainActivity页面下方的RadioBurron的Fragment
 * 
 * */


public class RadioButtonsFragment extends Fragment {

    private RadioGroup mRadioGroup;
    private RadioButton msgBlock;
    private RadioButton callBlock;
    private RadioButton blackList;
    private RadioButton whiteList;
    private RadioButton settings;
    private Context context;
    private View view;
    private SwitchTabs mCallback;

    public RadioButtonsFragment(Context context) {
        this.context = context;
    }

    public interface SwitchTabs {
        public void msgBlock();

        public void callBlock();

        public void blackList();

        public void whiteList();

        public void settings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.blacklist_radiobuttons, container,
                false);
        findViewAndSetListener();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mCallback = (SwitchTabs) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement interface");
        }
    }

    // zzz 初始化按钮
    private void findViewAndSetListener() {
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        msgBlock = (RadioButton) view.findViewById(R.id.msg_block); // zzz 已废弃
        callBlock = (RadioButton) view.findViewById(R.id.call_block);
        blackList = (RadioButton) view.findViewById(R.id.black_list);
        whiteList = (RadioButton) view.findViewById(R.id.white_list);
        settings = (RadioButton) view.findViewById(R.id.settings); // zzz 已废弃
        mRadioGroup.setOnCheckedChangeListener(mChangeRadio);
    }

    private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (checkedId == msgBlock.getId()) {
                mCallback.msgBlock(); // zzz 已废弃
                return;
            }

            if (checkedId == callBlock.getId()) {
                mCallback.callBlock();
                return;
            }

            if (checkedId == blackList.getId()) {
                mCallback.blackList();
                return;
            }
            if (checkedId == whiteList.getId()) {
                mCallback.whiteList();
                return;
            }
            if (checkedId == settings.getId()) {
                mCallback.settings(); // zzz 已废弃
            }
        }

    };

    public void setWhiteListChecked() {
        new Handler().post(new Runnable()
        {
            public void run() 
            {
                mRadioGroup.check(R.id.white_list);
            }
        });
    }


}
