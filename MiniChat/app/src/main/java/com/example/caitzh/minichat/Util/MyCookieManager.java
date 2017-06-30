package com.example.caitzh.minichat.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/21.
 */
public class MyCookieManager {
    private static CookieManager cookieManager = null;
    private static String userId = null;
    static SharedPreferences share = null;
    private static void createCookieManager() {
        Log.i("cookie:", "create manager");
        cookieManager = new java.net.CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }
    public static void getCookie(Context context, HttpURLConnection connection) {
        if (cookieManager == null) {
            createCookieManager();
        }

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                share = context.getSharedPreferences("MyCookie", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = share.edit();
                editor.putString("cookie", cookie);
                editor.commit();
                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }

    }

    public static void setCookie(HttpURLConnection connection) {
        if (cookieManager == null) {
            createCookieManager();
        }
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();

            if (cookies != null) {
                for (HttpCookie cookie : cookies) {
                    Log.i("cookie:", cookie.getName() + "=" + cookie.getValue());
                    connection.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
                }
            }
        }
    }

    public static boolean loadCookie(Context context) {
        if (cookieManager == null) {
            createCookieManager();
        }
        share = context.getSharedPreferences("MyCookie", Context.MODE_PRIVATE);
        String cookie = share.getString("cookie", "");
        if (cookie.equals("")) return false;
        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        return true;
    }

    public static void setUserId(String id) {
        userId = id;
        SharedPreferences.Editor editor = share.edit();
        editor.putString("userId", id);
        editor.commit();
    }

    public static String getUserId() {
        if (userId == null && share != null) {
            userId = share.getString("userId", "");
        }
        return userId;
    }

    public static void deleteCookie() {
        if (share != null) {
            SharedPreferences.Editor editor = share.edit();
            editor.clear();
            editor.commit();
            Log.v("TEST", "delete");
        }
    }
}
