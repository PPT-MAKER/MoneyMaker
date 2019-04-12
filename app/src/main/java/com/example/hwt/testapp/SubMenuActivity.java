package com.example.hwt.testapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.hwt.testapp.detail.AlbumSecondFragment;
import com.example.hwt.testapp.spider.beans.AlbumBean;

import java.util.List;

public class SubMenuActivity extends AppCompatActivity {

    public static final String ALBUM_SUB = "subALbum";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                AlbumSecondFragment.newFragment((List<AlbumBean.AlbumSecondBean>) getIntent().getSerializableExtra(ALBUM_SUB))).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
