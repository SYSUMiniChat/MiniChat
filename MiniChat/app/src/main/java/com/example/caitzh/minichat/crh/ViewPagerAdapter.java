package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面切换的适配器
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViewList = new ArrayList<>();

    public ViewPagerAdapter(List<View> inputViewList) {
        this.mViewList = inputViewList;
    }

    //viewpager中的组件数量
    @Override
    public int getCount() {
        return mViewList.size();
    }
    //滑动切换的时候销毁当前的组件
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        ((ViewPager)container).removeView(mViewList.get(position));
    }
    //每次滑动的时候生成的组件
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
