package com.example.caitzh.minichat.crh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.caitzh.minichat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/12 0012.
 */
// TODO:加入图片和tab名称
public class MainSlider extends AppCompatActivity implements View.OnClickListener {
    /*ViewPager pager = null;
    ArrayList<View> viewContainter = new ArrayList<>();
    ArrayList<String> titleContainer = new ArrayList<>();
    ArrayList<Integer> iconContainer = new ArrayList<>();
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
        titleContainer.add("聊天");
        titleContainer.add("通讯录");
        titleContainer.add("我");
        pager.setAdapter(new ViewPagerAdapter(viewContainter, titleContainer, iconContainer, MainSlider.this));
    }*/
    private ViewPager mViewPager;
    private List<View> mViews = new ArrayList<>();//保存微信，朋友，通讯录，设置4个界面的view
    //底部的四个tab按钮，微信，朋友，通讯录，设置
    private LinearLayout mTabChat;
    private LinearLayout mTabMailList;
    private LinearLayout mTabPersonInformation;

    private ImageButton mChatImg;
    private ImageButton mMailListImg;
    private ImageButton mPersonInformationImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_slider);
        initView();
        initEvents();
    }

    private void initEvents() {
        //给底部的4个LinearLayout即4个导航控件添加点击事件
        mTabChat.setOnClickListener(this);
        mTabMailList.setOnClickListener(this);
        mTabPersonInformation.setOnClickListener(this);
        //viewpager滑动事件
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {//当viewpager滑动时，对应的底部导航按钮的图片要变化
                int currentItem = mViewPager.getCurrentItem();
                resetImg();
                switch (currentItem) {
                    case 0:
                        mChatImg.setImageResource(R.mipmap.chat_icon);
                        break;
                    case 1:
                        mMailListImg.setImageResource(R.mipmap.mail_list_icon);
                        break;
                    case 2:
                        mPersonInformationImg.setImageResource(R.mipmap.personal_information_icon);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initView() {//初始化所有的view
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        //tabs
        mTabChat = (LinearLayout)findViewById(R.id.id_tab_chat);
        mTabMailList = (LinearLayout)findViewById(R.id.id_tab_mail_list);
        mTabPersonInformation = (LinearLayout)findViewById(R.id.id_tab_personal_information);
        //imagebutton
        mChatImg = (ImageButton)findViewById(R.id.id_tab_chat_img);
        mMailListImg = (ImageButton)findViewById(R.id.id_tab_personal_information_img);
        mPersonInformationImg = (ImageButton)findViewById(R.id.id_tab_personal_information_img);
        //通过LayoutInflater引入布局，并将布局转化为view
        LayoutInflater mInflater = LayoutInflater.from(this);//创建一个LayoutInflater对象
        View tab01 = mInflater.inflate(R.layout.activity_chat_window, null);//通过inflate方法动态加载一个布局文件
        // TODO: 更改通讯录以及个人信息的Layout的id
        View tab02 = mInflater.inflate(R.layout.tab02, null);
        View tab03 = mInflater.inflate(R.layout.tab03, null);
        mViews.add(tab01);
        mViews.add(tab02);
        mViews.add(tab03);
        //为ViewPager设置adapter
        mViewPager.setAdapter(new ViewPagerAdapter(mViews));
    }
    @Override
    public void onClick(View v) {
        resetImg();//点击哪个tab,对应的颜色要变亮，因此，在点击具体的tab之前先将所有的图片都重置为未点击的状态，即暗色图片
        switch (v.getId()) {
            case R.id.id_tab_chat:
                mViewPager.setCurrentItem(0);//如果点击的是微信，就将viewpager的当前item设置为0，及切换到微信聊天的viewpager界面
                mChatImg.setImageResource(R.mipmap.chat_icon);//并将按钮颜色点亮
                break;
            case R.id.id_tab_mail_list:
                mViewPager.setCurrentItem(1);
                mMailListImg.setImageResource(R.mipmap.mail_list_icon);
                break;
            case R.id.id_tab_personal_information:
                mViewPager.setCurrentItem(2);
                mPersonInformationImg.setImageResource(R.mipmap.personal_information_icon);
                break;
            default:
                break;
        }

    }
    private void resetImg() {
        // TODO:添加滑动页面可以更改的icon，并进行对应滑动修改
        /*mChatImg.setImageResource(R.drawable.tab_Chat_normal);
        mMailListImg.setImageResource(R.drawable.tab_find_MailList_normal);
        mAddressImg.setImageResource(R.drawable.tab_address_normal);
        mSettingImg.setImageResource(R.drawable.tab_settings_normal);*/
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
