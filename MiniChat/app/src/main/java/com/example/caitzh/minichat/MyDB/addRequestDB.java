package com.example.caitzh.minichat.MyDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by littlestar on 2017/6/30.
 */
public class addRequestDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "DataDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "ADDREQUEST";
    public addRequestDB(Context context) { super(context,DB_NAME, null, DB_VERSION); }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (user TEXT, sender TEXT, status INTEGER, finalDate DATETIME, flag INTEGER," +
                "Primary key (user, sender))";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (user TEXT, sender TEXT, status INTEGER, finalDate DATETIME,flag INTEGER," +
                "Primary key (user, sender))";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入好友请求 status = 0表示未添加
     * @param user
     * @param sender
     * @param ftime
     */
    public void insertOne(String user, String sender, String ftime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user", user);
        cv.put("sender", sender);
        cv.put("status", 0);
        cv.put("finalDate", ftime);
        cv.put("flag", 1);
        if (db.insert(TABLE_NAME, null, cv) == -1) {
            ContentValues cv1 = new ContentValues();
            cv1.put("finalDate", ftime);
            cv1.put("status", 0);
            cv1.put("flag", 1);
            Log.e("插入已存在", "更新");
            db.update(TABLE_NAME, cv1,"user=? AND sender=?", new String[] {user, sender});
        }
    }

    /**
     * 更新状态
     * @param user
     * @param sender
     */
    public void updateStatus(String user, String sender) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", 1);
        db.update(TABLE_NAME, cv, "user=? AND sender=?", new String[] {user, sender});
    }

    public void updateAllFlag(String user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("flag", 0);
        db.update(TABLE_NAME, cv, "user=?", new String[] {user});
    }
    public void deleteOne(String user, String sender) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(TABLE_NAME, "user=? AND sender=?", new String[] {user, sender});
    }

    public Cursor getItems(String user) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "user=?", new String[]{user}, null, null, "finalDate DESC");
        cursor.moveToFirst();
        return cursor;
    }
    public int getLastFlag(String user) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "user=?", new String[]{user}, null, null, "finalDate DESC");
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex("flag"));
        } else {
            return 0;
        }

    }
}
