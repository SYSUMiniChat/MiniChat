package com.example.caitzh.minichat;

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
    private static void createCookieManager() {
        Log.i("cookie:", "create manager");
        cookieManager = new java.net.CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }
    public static void getCookie(HttpURLConnection connection) {
        if (cookieManager == null) {
            createCookieManager();
        }

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
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

    public static void setUserId(String id) {
        userId = id;
    }

    public static String getUserId() {
        return userId;
    }
}
