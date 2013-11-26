package edu.bupt.contacts.settings;

import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import edu.bupt.contacts.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class DialpadSettingAcitivity extends Activity {
    private final String TAG = "DialpadSettingAcitivity";

    private DialpadCommonPreferenceFragment commonPreferenceFragment = new DialpadCommonPreferenceFragment();
    private DialpadCDMAPreferenceFragment cdmaPreferenceFragment = new DialpadCDMAPreferenceFragment();
    private DialpadGSMPreferenceFragment gsmPreferenceFragment = new DialpadGSMPreferenceFragment();
    private static final int TAB_INDEX_COUNT = 3;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabPageIndicator titleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialpad_setting);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager()); // instead
                                                                                // of
                                                                                // getSupportFragmentMangager

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.setting_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        titleIndicator = (TabPageIndicator) findViewById(R.id.setting_indicator);
        titleIndicator.setViewPager(mViewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return commonPreferenceFragment;
            case 1:
                return cdmaPreferenceFragment;
            case 2:
                return gsmPreferenceFragment;
            }
            throw new IllegalStateException("No fragment at position " + position);
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabLabel = null;
            switch (position) {
            case 0:
                tabLabel = getString(R.string.preference_tab_common);
                break;
            case 1:
                tabLabel = getString(R.string.preference_tab_cdma);
                break;
            case 2:
                tabLabel = getString(R.string.preference_tab_gsm);
                break;
            }
            return tabLabel;
        }
    }

}
