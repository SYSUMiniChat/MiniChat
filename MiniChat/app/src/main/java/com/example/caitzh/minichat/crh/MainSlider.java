package com.example.caitzh.minichat.crh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.caitzh.minichat.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/12 0012.
 */
// TODO:加入图片和tab名称
public class MainSlider extends AppCompatActivity {
    ViewPager pager = null;
    PagerTabStrip pagerTabStrip = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    ArrayList<String> titleContainer = new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        pager = (ViewPager)this.findViewById(R.id.viewPager);
        View chatWindowView = LayoutInflater.from(this).inflate(R.layout.activity_chat_window,null);
        View personalInformationView = LayoutInflater.from(this).inflate
                (R.layout.activity_personal_information, null);
        viewContainter.add(chatWindowView);
        viewContainter.add(personalInformationView);
        pager.setAdapter(new ViewPagerAdapter(viewContainter, titleContainer, MainSlider.this));
    }
}
