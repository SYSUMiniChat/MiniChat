package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 页面切换的适配器
 */

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> list;
    private ArrayList<String> titleContainer;
    private Context context;

    public ViewPagerAdapter(ArrayList<View> inputList, ArrayList<String> inputTitleContainer, Context inputContext) {
        this.list = inputList;
        this.titleContainer = inputTitleContainer;
        this.context = inputContext;
    }

    //viewpager中的组件数量
    @Override
    public int getCount() {
        return list.size();
    }
    //滑动切换的时候销毁当前的组件
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        ((ViewPager)container).removeView(list.get(position));
    }
    //每次滑动的时候生成的组件
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(list.get(position));
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleContainer.get(position);
    }
}
