package com.example.hwt.testapp.detail;

import android.graphics.drawable.Drawable;

/**
 * Created by cb on 2019/4/9.
 */
public class ImgGetterTmp {
    public static void loadImg(Callback callback) {

    }

    interface Callback {
        void onLoaded(String url, Drawable drawable);
    }

}
