package edu.bupt.contacts.settings;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialpadSettingAcitivity extends FragmentActivity implements TabListener {
    private ViewPager mViewPager;

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    }

}
