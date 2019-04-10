package com.example.hwt.testapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class SPUtil {
    private static final String SP_NAME = "tk_cache";
    private SharedPreferences mSharedPreferences;
    private Gson gson = new Gson();

    private static class StaticInner{
        private static SPUtil spUtil = new SPUtil();
    }

    public static SPUtil getInstance(){
        return StaticInner.spUtil;
    }

    public void init(Context context){
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void cacheObject(String key,Object value){
        mSharedPreferences.edit().putString(key,gson.toJson(value)).commit();
    }

    public synchronized Object getObject(String key, Type type){
        String string = mSharedPreferences.getString(key, "");
        return gson.fromJson(string,type);
    }
}
