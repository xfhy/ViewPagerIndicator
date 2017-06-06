package com.xfhy.viewpagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.xfhy.viewpagerindicator.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于写自定义控件ViewPagerIndicator
 *
 * @author xfhy
 *         create at 2017年6月5日10:10:52
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    /**
     * 下面的ViewPager
     */
    private ViewPager mViewPager;
    /**
     * 自定义控件
     */
    private ViewPagerIndicator mIndicator;
    /**
     * 每个tab的标题
     */
    private static final List<String> mTitles = Arrays.asList("短信1", "收藏2", "推荐3", "短信4", "收藏5",
            "推荐6", "短信7", "收藏8", "推荐9");
    /**
     * 中间的Fragment
     */
    private List<VpSimpleFragment> mContents = new ArrayList<>();
    /**
     * ViewPager的适配器
     */
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);

        initUI();
        initData();
    }

    private void initData() {
        //初始化中间的那些Fragment
        for (String title : mTitles) {
            mContents.add(VpSimpleFragment.newInstance(title));
        }

        //动态地设置可见tab的个数,必须大于0
        mIndicator.setmTabVisibleCount(4);
        //动态设置tab
        mIndicator.setTabItemTitles(mTitles);

        //设置适配器
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        //设置关联的ViewPager
        mIndicator.setViewPager(mViewPager,0);

        //给自定义控件设置tab 页签改变的监听器    可以不设置
        mIndicator.addOnPageChangeListener(new ViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(TAG, "onPageScrolled: 1");
            }

            @Override
            public void onPageSelected(int position) {
                //Log.d(TAG, "onPageSelected: 1");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.d(TAG, "onPageScrollStateChanged: 1");
            }
        });

    }

    private void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.vpi_page);
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mContents.get(position);
        }

        @Override
        public int getCount() {
            return mContents.size();
        }
    }

}
