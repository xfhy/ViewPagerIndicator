# ViewPagerIndicator

自定义的ViewPagerIndicator,实现ViewPager的tab切换

效果:
![](http://olg7c0d2n.bkt.clouddn.com/17-6-6/90389139.jpg)

## 1. 用法

1.首先需要准备一个ViewPager,然后几个Fragment,用于显示页签,也就是屏幕中间显示的内容.
2.配置:

		//动态地设置可见tab的个数,必须大于0
        mIndicator.setmTabVisibleCount(4);
        //动态设置tab
        mIndicator.setTabItemTitles(mTitles);
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

3.一些配置也可以在布局中进行配置

		<!--整个控件的背景颜色-->
		android:background="@color/colorPrimary"
		<!--可见tab的个数-->
        app:visible_tab_count="4"
		<!--指示器的颜色-->
        app:indicator_color="@color/colorAccent"

## 2. 难点解读

1.首先就是布局,就是继承LinearLayout,重写.然后指示器是矩形,自己绘制出来的,就在每个tab的下面,绘制自己的孩子通过dispatchDraw（canvas）实现.

2.在dispatchDraw()之前,需要在onSizeChanged()计算每一个矩形宽.矩形的高度是我自己设定好了的常量值.

3.移动指示器:监听ViewPager的滑动事件,根据ViewPager的滑动比例设置当前ViewGroup的滚动位置,相当于就滑动了指示器,指示器的初始x坐标是0.
4.然后就是自定义属性,定义可见tab的个数和指示器颜色.

5.动态添加tab,给自定义控件传入一个String数组,就是tab标题的集合,然后根据传入集合,觉得有多少个tab.

6.设置ViewPager给自定义控件,那么开发者在在使用该控件的时候就不需要再自己写ViewPager的控制了,交给自定义控件去控制.但是,需要给ViewPager的滑动事件监听器暴露接口,不然当开发者需要使用到监听器的时候,就不能用了. 这里的自定义控件暴露接口的方式参考源码的,nice.

7.最后需要将滑动到的那个位置的tab标题高亮,然后需要给每个tab设置点击事件.

## 3. 感谢

在看了慕课网老师的自定义控件解读之后,颇有收获,在此,感谢老师!
在此之上我稍微优化了一下,并且将指示器改为自己想要的.并且可以自定义颜色等.
课程地址:http://www.imooc.com/video/11304
