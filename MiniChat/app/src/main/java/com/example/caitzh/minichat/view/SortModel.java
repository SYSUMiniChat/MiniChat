package com.example.caitzh.minichat.view;

import android.graphics.Bitmap;

/**
 * Created by littlestar on 2017/6/21.
 */
public class SortModel {
    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母
    private String id;
    private Bitmap bm;

    public void setId(String _id) { id = _id; }
    public String getId() { return id; }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
    public Bitmap getBm() {
        return bm;
    }
}
