package com.example.hwt.testapp.detail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cb on 2019/4/9.
 */
public class ImgTmp implements Parcelable {
    protected ImgTmp(Parcel in) {
    }

    public static final Creator<ImgTmp> CREATOR = new Creator<ImgTmp>() {
        @Override
        public ImgTmp createFromParcel(Parcel in) {
            return new ImgTmp(in);
        }

        @Override
        public ImgTmp[] newArray(int size) {
            return new ImgTmp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public String getImgUrl() {
        return null;
    }
}
