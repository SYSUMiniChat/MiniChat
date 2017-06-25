package com.example.caitzh.minichat.MyDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.caitzh.minichat.User;

/**
 * Created by littlestar on 2017/6/18.
 */
public class userDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "DataDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "USER";
    public userDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    // 重载的create函数
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 其中 ID即为账号
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (ID TEXT PRIMARY KEY, nickname TEXT, sex TEXT, " +
                "city TEXT, signature TEXT, avatar TEXT, finalDate DATETIME)";
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
        String CREATE_TABLE = "CREATE TABLE if not exists "
                +TABLE_NAME
                +" (ID TEXT PRIMARY KEY, nickname TEXT, sex TEXT, " +
                "city TEXT, signature TEXT, avatar TEXT, finalDate DATETIME)";
        db.execSQL(CREATE_TABLE);
    }
    // Upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do sth
    }
    // 创建新用户，注册时

    /**
     * 重载插入 将从服务器获取到的信息存到本地数据库
     * @param id
     * @param nickname
     * @param finalDate
     * @param sex
     * @param city
     * @param signature
     * @param avatar
     * @return
     */
    public boolean insert2Table(String id, String nickname, String sex, String city,
                                String signature, String avatar, String finalDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", id);
        cv.put("nickname", nickname);
        cv.put("sex", sex);
        cv.put("city", city);
        cv.put("signature", signature);
        cv.put("avatar", avatar);
        cv.put("finalDate", finalDate);
        long result = 0;
        try {
            result = db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        db.close();
        Log.e("result = ", " "+result);
        return result != -1;
    }

    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", user.getId());
        cv.put("nickname", user.getNickname());
        cv.put("sex", user.getSex());
        cv.put("city", user.getCity());
        cv.put("signature", user.getSignature());
        cv.put("avatar", user.getAvatar());
        cv.put("finalDate", user.getFinalDate());
        long result = 0;
        try {
            result = db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        db.close();
    }

    /**
     * 查找函数
     * @param id
     * @return 根据账号返回对应信息
     */
    public Cursor findOneByNumber(String id) {
        if (id == null) Log.e("Query in userDB", "id is null");
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "ID=?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }
    public User findUserById(String id_) {
        if (id_ == null) Log.e("Query in userDb", "id is null");
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "ID=?", new String[]{id_}, null, null, null);
        if (cursor.moveToFirst()) {
            String id, nickname, sex, city, signature, avatar, finalDate;
            id = cursor.getString(0);
            nickname = cursor.getString(1);
            sex = cursor.getString(2);
            city = cursor.getString(3);
            signature = cursor.getString(4);
            avatar = cursor.getString(5);
            finalDate = cursor.getString(6);
            User user = new User(id, nickname, sex, city, signature, avatar, finalDate);
            return user;
        } else {
            return null;
        }
    }

    public void updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nickname", user.getNickname());
        cv.put("sex", user.getSex());
        cv.put("city", user.getCity());
        cv.put("signature", user.getSignature());
        cv.put("finalDate", user.getFinalDate());
        cv.put("avatar", user.getAvatar());
        db.update(TABLE_NAME, cv, "ID=?", new String[] {user.getId()});
    }

    /**
     * 更新昵称 下同
     * @param id
     * @param col 列名
     * @param value 新的值
     * @param date
     * @return
     */
    public void updateInfo(String id, String col, String value, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col, value);
        cv.put("finalDate", date);
        try {
            db.update(TABLE_NAME, cv, "ID=?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * 删除用户
     * @param id
     */
    public void deleteUser(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{id});
    }
}
