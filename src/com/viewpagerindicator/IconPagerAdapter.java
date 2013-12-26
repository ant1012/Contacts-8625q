package com.viewpagerindicator;

/**
 * 北邮ANT实验室
 * ddd
 * 
 * 开源库，实现ViewPager的滑动切换tab的效果
 * https://github.com/JakeWharton/Android-ViewPagerIndicator
 * Apache License 2.0
 * 
 * 此文件取自Android-ViewPagerIndicator库，未修改
 * 
 * */

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
