package com.example.hwt.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class CacheUtil {
    private static final String SP_NAME = "tk_cache";
    private SharedPreferences mSharedPreferences;
    private Gson gson = new Gson();

    public static String TAG = "CacheUtil";

    private static class StaticInner {
        private static CacheUtil spUtil = new CacheUtil();
    }

    public static CacheUtil getInstance() {
        return StaticInner.spUtil;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void cacheObject(String key, Object value) {
        mSharedPreferences.edit().putString(key, gson.toJson(value)).commit();
        Log.e(TAG, "cacheObject\n key="+ key + " value =" + value);
    }

    public synchronized Object getObject(String key, Type type) {
        String string = mSharedPreferences.getString(key, "");
        Log.e(TAG, "getObject from cache: " + string);
        return gson.fromJson(string, type);
    }
}
