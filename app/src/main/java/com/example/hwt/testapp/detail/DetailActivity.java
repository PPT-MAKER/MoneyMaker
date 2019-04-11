package com.example.hwt.testapp.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static final String ALBUM_BEAN = "albumbean";

    AlbumBean mAlbumBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlbumBean = (AlbumBean)getIntent().getSerializableExtra(ALBUM_BEAN);
        initFragments();
    }

    private void initFragments() {
        ArrayList<String> urls = new ArrayList<>();
        for (AlbumBean.AlbumSecondBean albumSecondBean : mAlbumBean.getSecondBeans()) {
            urls.add(albumSecondBean.getAlbumHref());
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                DetailFragment.newFragment(urls)).commit();
    }


}
