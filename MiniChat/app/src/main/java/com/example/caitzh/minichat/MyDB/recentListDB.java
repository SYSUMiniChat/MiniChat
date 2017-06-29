package com.example.caitzh.minichat.MyDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by littlestar on 2017/6/23.
 */
public class recentListDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "DataDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "RECENTLIST";

    /**
     * 构造函数
     * @param context
     */
    public recentListDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (sender TEXT, receiver TEXT, fdate DATETIME" +
                " )";
        db.execSQL(CREATE_TABLE);
        Log.e("RecentDB", TABLE_NAME);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (sender TEXT, receiver TEXT" +
                " )";
        db.execSQL(CREATE_TABLE);
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入接口
     * @param sender
     * @param receiver
     * @return
     */
    public void insertOne(String sender, String receiver, String ftime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sender", sender);
        cv.put("receiver", receiver);
        cv.put("fdate", ftime);
        if (ifExist(sender, receiver)) {
            db.update(TABLE_NAME, cv, "sender=? AND receiver=?", new String[] {sender, receiver});
        } else {
            db.insert(TABLE_NAME, null, cv);
        }
        db.close();
    }

    /**
     * 获取用户最近联系人
     * @param sender
     * @return
     */
    public Cursor getItems(String sender) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "sender=?", new String[]{sender},null, null, "fdate ASC");
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * 删除记录，根据用户id和对面id删除
     * @param sender
     * @param receiver
     */
    public void deleteItem(String sender, String receiver) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "sender=? AND receiver=?", new String[] {sender, receiver});
    }

    /**
     * 搜索记录，根据用户id和对面id搜索
     * @param sender
     * @param receiver
     */
    public boolean ifExist(String sender, String receiver) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(TABLE_NAME, null, "sender=? AND receiver=?", new String[]{sender, receiver},null, null, null).moveToFirst();
    }
}
