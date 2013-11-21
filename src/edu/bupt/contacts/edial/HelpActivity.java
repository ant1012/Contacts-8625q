package edu.bupt.contacts.edial;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.contacts.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** zzz */
public class HelpActivity extends Activity implements OnPageChangeListener, OnClickListener {
    private final static String TAG = "HelpActivity";
    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private TextView tv_skip;
    private String digit;

    private final String[] pics = { "pic/edial_help1.png", "pic/edial_help2.png", "pic/edial_help3.png",
            "pic/edial_help4.png", "pic/edial_help5.png", "pic/edial_help6.png" };
    private ImageView[] dots;

    private int currentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        views = new ArrayList<View>();

        Intent intent = getIntent();
        digit = intent.getStringExtra("digit");

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageBitmap(getImageFromAssetsFile(pics[i]));
            views.add(iv);
        }
        vp = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

        tv_skip = (TextView) findViewById(R.id.button_skip);
        tv_skip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (digit != null) {
                    Intent intent = new Intent();
                    intent.setAction("edu.bupt.action.EDIAL");
                    intent.putExtra("digit", digit);
                    startService(intent);
                }
                finish();
            }

        });

        initDots();

        setShouldShowHelp(false);
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[pics.length];

        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
    }

    @Override
    public void onClick(View arg0) {
        int position = (Integer) arg0.getTag();
        setCurView(position);
        setCurDot(position);
    }

    private void setCurDot(int position) {
        if (position < 0 || position > pics.length - 1 || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // Log.v(TAG, "arg0 - " + arg0);
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // Log.v(TAG, "arg0 - " + arg0);
        // Log.v(TAG, "arg1 - " + arg1);
        // Log.v(TAG, "arg2 - " + arg2);
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurDot(arg0);
    }

    private void setShouldShowHelp(boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putBoolean("ShouldShowHelpPreference", b);
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG, "keyCode - " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (digit != null) {
                Intent intent = new Intent();
                intent.setAction("edu.bupt.action.EDIAL");
                intent.putExtra("digit", digit);
                startService(intent);
            }
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
