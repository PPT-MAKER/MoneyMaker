package com.example.hwt.testapp;

import android.app.Application;
import android.content.Context;

public class GApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        SPUtil.getInstance().init(sContext);
    }

    public static Context getContext() {
        return sContext;
    }
}
