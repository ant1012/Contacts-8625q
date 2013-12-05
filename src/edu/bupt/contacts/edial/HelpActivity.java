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

/**
 * 类描述： 翼拨号帮助菜单界面 ddd
 * 
 * */

/** zzz */
public class HelpActivity extends Activity implements OnPageChangeListener, OnClickListener {
    private final static String TAG = "HelpActivity";
    //字段描述： 翻页效果视图
    private ViewPager vp;
    //字段描述： 翻页效果视图适配器
    private ViewPagerAdapter vpAdapter;
    //字段描述： 存放界面的列表
    private List<View> views;
    //字段描述： “跳过”文本框
    private TextView tv_skip;
    //字段描述： 电话号码
    private String digit;
    //帮助菜单图片存储地址
    private final String[] pics = { "pic/edial_help1.png", "pic/edial_help2.png", "pic/edial_help3.png",
            "pic/edial_help4.png", "pic/edial_help5.png", "pic/edial_help6.png" };
    //图片查看器
    private ImageView[] dots;
    //当前页序号
    private int currentIndex;

    /**
     * 方法描述： 创建帮助菜单Activity ddd
     * */
    
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
    
/**
 * 方法描述： 获取对应文件地址中的位图 ddd
 * */
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

    /**
     * 方法描述： 初始化描述翻页的圆点 ddd
     * 
     * */
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
    
    /**
     * 方法描述： 点击图片，翻页功能 ddd
     * */

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

    
    /**
     * 方法描述： 继承自OnPageChangeListener，必须实现 ddd
     * */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // Log.v(TAG, "arg0 - " + arg0);
    }

    /**
     * 方法描述： 继承自OnPageChangeListener，必须实现 ddd
     * */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // Log.v(TAG, "arg0 - " + arg0);
        // Log.v(TAG, "arg1 - " + arg1);
        // Log.v(TAG, "arg2 - " + arg2);
    }

    /**
     * 方法描述： 选中某页 ddd
     * */
    @Override
    public void onPageSelected(int arg0) {
        setCurDot(arg0);
    }

    /**
     * 方法描述： 是否启用翼拨号帮助菜单 ddd
     * */
    private void setShouldShowHelp(boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putBoolean("ShouldShowHelpPreference", b);
        editor.commit();
    }

    /**
     * 方法描述： 硬件按下 ddd
     * 
     * */
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
