package com.example.caitzh.minichat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by littlestar on 2017/6/21.
 */
public class recordDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "DataDB";
    private static final String TABLE_NAME = "RECORD";
    private static final int DB_VERSION = 1;

    public recordDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " (type INTEGER, sender TEXT, receiver TEXT, content TEXT)";
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 使得外键依赖成立
        if(!db.isReadOnly()) { // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     *
     * @param type 消息类型 0为时间，1为发送， 2为接受
     * @param sender 发送者ID，即用户ID
     * @param receiver 接受者ID
     * @param content 消息内容
     * @return
     */
    public boolean insertOne(int type, String sender, String receiver, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("sender", sender);
        cv.put("receiver", receiver);
        cv.put("content", content);
        long result = 0;
        try {
            result = db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            Log.e("Insert RecordDB Error", e.toString());
        }
        db.close();
        return result == 1;
    }

    /**
     * 获取用户与好友的聊天记录
     * @param sender
     * @param receiver
     * @return
     */
    public Cursor getItems(String sender, String receiver) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "sender=? AND receiver=?", new String[]{sender, receiver},null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}
