package com.xfhy.viewpagerindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xfhy.viewpagerindicator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xfhy on 2017/6/5.
 * 自定义控件 ViewPagerIndicator
 */

public class ViewPagerIndicator extends LinearLayout {
    private static final String TAG = "ViewPagerIndicator";
    private Paint mPaint;// 绘制三角形的画笔
    /**
     * 用于绘制矩形的边
     * 这个东西可以绘制一个封闭的区域.只要把线连起来.比如三角形等都是可以画的.
     */
    private Path mPath;

    /**
     * 矩形的宽
     */
    private int mRectWidth;
    /**
     * 矩形的高
     */
    private static final int RECT_HEIGHT = 6;
    /**
     * 指示器需要偏移的距离
     */
    private int mTranslationX;
    /**
     * 可见tab的数量
     */
    private int mTabVisibleCount;
    /**
     * 默认可见tab为4个
     */
    private static final int COUNT_DEFAULT_TAB = 4;
    /**
     * 指示器颜色
     */
    private static final String INDICATOR_DEFAULT_COLOR = "#FF8247";
    /**
     * 指示器颜色
     */
    private int mColor;
    /**
     * tab标题的默认颜色
     */
    private static final int COLOR_TEXT_NORMAL = Color.parseColor("#77FFFFFF");
    /**
     * tab标题高亮的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHT = Color.parseColor("#FFFFFFFF");
    /**
     * 暴露给开发者的ViewPager的监听器
     * 这里参照了源码ViewPager,需要写多个监听器
     */
    private List<OnPageChangeListener> mOnPageChangeListeners;
    /**
     * 下面的ViewPager  需要切换的
     */
    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // 获取可见tab的数量   自定义属性  获取里面的值
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.ViewPagerIndicator);
        mTabVisibleCount = attributes.getInt(
                R.styleable.ViewPagerIndicator_visible_tab_count,
                COUNT_DEFAULT_TAB);
        mColor = attributes.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color
                .parseColor(INDICATOR_DEFAULT_COLOR));

        //如果有错,则是给一个默认值
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        // 用完必须释放
        attributes.recycle();

        //初始化画笔等数据
        initData();

    }

    /**
     * 初始化画笔等数据
     */
    private void initData() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);  //设置为抗锯齿
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));  //设置三角形不那么尖锐
    }

    /**
     * 绘制矩形
     * 绘制VIew本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现
     * <p>
     * 画完背景后，draw过程会调用onDraw(Canvas canvas)方法，然后就是dispatchDraw(Canvas canvas)方法,
     * dispatchDraw
     * ()主要是分发给子组件进行绘制，我们通常定制组件的时候重写的是onDraw()方法。值得注意的是ViewGroup容器组件的绘制
     * ，当它没有背景时直接调用的是dispatchDraw
     * ()方法,而绕过了draw()方法，当它有背景的时候就调用draw()方法，而draw()方法里包含了
     * dispatchDraw()方法的调用。因此要在ViewGroup上绘制东西的时候往往重写的是
     * dispatchDraw()方法而不是onDraw()方法，或者自定制一个Drawable，重写它的draw(Canvas c)和
     * getIntrinsicWidth(),getIntrinsicHeight()方法，然后设为背景
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        /*
         * save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
         *
         * restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
         *
         * save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。
         */
        canvas.save();

        canvas.translate(mTranslationX, RECT_HEIGHT);
        //绘制一个封闭的区域
        canvas.drawPath(mPath, mPaint);

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //矩形的宽   就是屏幕的宽度/可见个数
        mRectWidth = w / mTabVisibleCount;
        initRect();

    }

    /**
     * 初始化矩形
     */
    private void initRect() {
        //矩形的位置在 子控件的底部
        mPath = new Path();
        mPath.moveTo(0, getHeight());  //将下一个轮廓的开始设置为点（x，y）。
        //从最后一点添加一行到指定点（x，y）。如果没有对此轮廓进行moveTo（）调用，则第一个点为自动设置为（0,0）。
        mPath.lineTo(mRectWidth, getHeight());
        mPath.lineTo(mRectWidth, getHeight() - RECT_HEIGHT - RECT_HEIGHT);
        mPath.lineTo(0, getHeight() - RECT_HEIGHT - RECT_HEIGHT);
        // 关闭当前轮廓，完成闭合
        mPath.close();
    }

    /**
     * 移动指示器
     *
     * @param position  需要移动到的那个位置
     * @param offset 移动偏移量百分比
     */
    public void scroll(int position, float offset) {
        //每个tab的宽度
        int tabWidth = getWidth() / mTabVisibleCount;
        //指示器需要偏移的距离
        mTranslationX = (int) (tabWidth * offset + tabWidth * position);

        //当移动到倒数第二个时,就需要移动ViewPagerIndicator 当前这个ViewGroup了
        if (position >= (mTabVisibleCount - 2) && offset > 0 && getChildCount() >
                mTabVisibleCount) {
            //如果mTabVisibleCount == 1时,mTabVisibleCount - 2是负数
            if (mTabVisibleCount != 1) {

                //如果是移动到所有页签的倒数第二个则不能再移动了
                if (position != getChildCount() - 2) {
                    Log.d(TAG, "scroll: position----"+position);
                    this.scrollTo((int) ((position - (mTabVisibleCount - 2)) * tabWidth + tabWidth *
                            offset), 0);
                    //当移动到界面可见数的最后一个时,直接移动到0   不然会出现第一个显示不全的bug
                    if (position == mTabVisibleCount-2) {
                        Log.d(TAG, "scroll: 0");
                        this.scrollTo(0,0);
                    }
                }

            } else {
                this.scrollTo((int) (position * tabWidth + tabWidth * offset), 0);
            }
        }

        //重绘
        invalidate();
    }

    /**
     * 当xml加载完成之后就会调用此方法,必须调用super()方法
     * 我们需要在这做的事情是 将每个子控件的weight改为0,然后将默认的可以显示的个数的子控件显示出来
     * 通过布局添加tab的时候,调用到此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取子控件个数
        int childCount = getChildCount();
        if (childCount < 0) {
            return;
        }

        //设置每个页签的宽度
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.weight = 0;
            layoutParams.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(layoutParams);
        }

        //设置每个tab的点击事件
        setTabItemClickEvent();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        //拿到WindowManager对象
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context
                .WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 动态地设置可见tab的个数,必须大于0
     * 必须在setTabItemTitles()之前调用此方法才能生效
     *
     * @param mTabVisibleCount
     */
    public void setmTabVisibleCount(int mTabVisibleCount) {
        if (mTabVisibleCount > 0) {
            this.mTabVisibleCount = mTabVisibleCount;
        } else {
            this.mTabVisibleCount = COUNT_DEFAULT_TAB;  //默认值
        }
    }

    /**
     * 动态设置tab
     *
     * @param titles 所有tab的标题
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            //用户是想设置Tab,则无视布局文件中写的TextView,全部移除
            removeAllViews();

            //生成标题
            for (String title : titles) {
                addView(generateTextView(title));
            }
        }

        //设置每个tab的点击事件
        setTabItemClickEvent();
    }

    /**
     * 根据title动态地生成一个tab  其实就是一个TextView,用来显示tab的名称用的
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (mTabVisibleCount == 0) {
            params.width = getScreenWidth() / COUNT_DEFAULT_TAB;
        } else {
            params.width = getScreenWidth() / mTabVisibleCount;
        }

        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setText(title);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * 设置关联的ViewPager
     *
     * @param viewPager
     * @param position  设置当前位置
     */
    public void setViewPager(ViewPager viewPager, int position) {
        this.mViewPager = viewPager;
        //页面改变事件  滑动
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                //移动指示器
                scroll(position, positionOffset);

                //监听器 全部需要回调此方法   参考源码
                if (mOnPageChangeListeners != null) {
                    for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                        OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                        if (listener != null) {
                            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mOnPageChangeListeners != null) {
                    for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                        OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                        if (listener != null) {
                            listener.onPageSelected(position);
                        }
                    }
                }
                //高亮当前选中的位置
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mOnPageChangeListeners != null) {
                    for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                        OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                        if (listener != null) {
                            listener.onPageScrollStateChanged(state);
                        }
                    }
                }
            }
        });
        //设置当前的tab是position位置
        viewPager.setCurrentItem(position);
        //高亮当前选中的位置
        highLightTextView(position);
    }

    /**
     * 需要暴露给开发者的ViewPager的滑动事件 接口  和ViewPager的监听器定义地一模一样
     * 可能开发者也需要用到ViewPager的监听事件
     */
    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    /**
     * 给ViewPager添加监听器
     * 官方已经弃用setOnPageChangeListener()了,取而代之的是
     * Use {@link #addOnPageChangeListener(OnPageChangeListener)}
     * and {@link #removeOnPageChangeListener(OnPageChangeListener)} instead.
     *
     * @param listener
     */
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    /**
     * Remove a listener that was previously added via
     * {@link #addOnPageChangeListener(OnPageChangeListener)}.
     *
     * @param listener listener to remove
     */
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }

    /**
     * Remove all listeners that are notified of any changes in scroll state or position.
     * 还是用英文吧,虽然知道意思,但是硬翻译过来总觉得不舒服
     */
    public void clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.clear();
        }
    }

    /**
     * 高亮显示tab标题
     *
     * @param position 那个需要高亮的位置
     */
    private void highLightTextView(int position) {
        //当前位置高亮,其他位置设置为正常
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (position == i) {
                    textView.setTextColor(COLOR_TEXT_HIGHLIGHT);
                } else {
                    textView.setTextColor(COLOR_TEXT_NORMAL);
                }
            }

        }

    }

    /**
     * 设置每个tab的点击事件
     */
    private void setTabItemClickEvent() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

}
