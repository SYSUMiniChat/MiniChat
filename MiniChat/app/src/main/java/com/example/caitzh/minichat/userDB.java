package com.example.caitzh.minichat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by littlestar on 2017/6/18.
 */
public class userDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "DataDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "USER1";
    public userDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    // 重载的create函数
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 其中 ID即为账号
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (ID TEXT PRIMARY KEY, nickname TEXT, password TEXT)";
        db.execSQL(CREATE_TABLE);
    }
    // 数据库open时
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 使得外键依赖成立
        if(!db.isReadOnly()) { // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    // Upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do sth
    }
    // 创建新用户，注册时

    /**
     * 插入函数
     * @param nickname
     * @param id
     * @param password
     * @return 是否插入成功
     */
    public boolean insert2Table(String nickname, String id, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", id);
        cv.put("password", password);
        cv.put("nickname", nickname);
        long result = 0;
        try {
            result = db.insert(TABLE_NAME, null, cv);
            db.close();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        db.close();
        Log.e("result = ", " "+result);
        return result == 1;
    }

    /**
     * 查找函数
     * @param id
     * @return 根据账号返回对应信息
     */
    public Cursor findOneByNumber(String id) {
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "ID=?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
}
