package com.example.blacklist;


import edu.bupt.contacts.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends FragmentActivity implements RadioButtons.SwitchTabs {

    public static final String TAG = "franco--->Main";
    public static final String BACKGROUND_LISTEN_SERVICE = "listen_in_background";
    private Fragment radioButtons;
    private Fragment mainView;
    private Fragment msgBlock;
    private Fragment callBlock;
    private Fragment blackList;
    private Fragment whiteList;
    private Fragment settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_black_list);
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle savedInstanceState) {

        FragmentManager fragmentManager;
        if (findViewById(R.id.radio_buttons) != null) {
            if (savedInstanceState != null) {
                return;
            }
            radioButtons = new RadioButtons(this);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.radio_buttons, radioButtons).commit();
        }

        if (findViewById(R.id.main_view) != null) {
            if (savedInstanceState != null) {
                return;
            }
            msgBlock = new MsgBlock(this);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.main_view, msgBlock)
                    .commit();
            mainView = msgBlock;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, 1, R.string.exit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void msgBlock() {
        updatefragment(msgBlock);
    }

    @Override
    public void callBlock() {
        callBlock = new CallBlock(this);
        updatefragment(callBlock);
    }

    @Override
    public void blackList() {
        blackList = new BlackList(this);
        updatefragment(blackList);
    }
    
    public void whiteList() {
        whiteList = new WhiteList(this);
        updatefragment(whiteList);
    }

    @Override
    public void settings() {
        settings = new Settings(this);
        updatefragment(settings);
    }

    private void updatefragment(Fragment fragment) {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(mainView)
                .add(R.id.main_view, fragment).commit();
        mainView = fragment;
    }

}