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
                +" (sender TEXT, receiver TEXT," +
                " Primary key (sender, receiver))";
        db.execSQL(CREATE_TABLE);
        Log.e("RecentDB", TABLE_NAME);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
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
    public boolean insertOne(String sender, String receiver) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sender", sender);
        cv.put("receiver", receiver);
        long result = 0;
        try {
            db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            Log.e("Insert Recent Eroor", e.toString());
        }
        db.close();
        return result == 1;
    }

    /**
     * 获取用户最近联系人
     * @param sender
     * @return
     */
    public Cursor getItems(String sender) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "sender=?", new String[]{sender},null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}
