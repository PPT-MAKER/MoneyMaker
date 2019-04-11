package com.example.hwt.testapp.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.hwt.testapp.ListUtil;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;
import com.example.hwt.testapp.spider.beans.PhotoBean;
import com.example.hwt.testapp.spider.service.SpiderService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    public static final String LOAD_SECOND_ALBUM = "loadSecondAlbum";
    public static final String PHOTOS = "photos";

    private AlbumBean.AlbumSecondBean secondBean;

    private List<PhotoBean> photos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secondBean = (AlbumBean.AlbumSecondBean) getIntent().getSerializableExtra(LOAD_SECOND_ALBUM);
        photos = (List<PhotoBean>) getIntent().getSerializableExtra(PHOTOS);
        initFragments();
    }

    @SuppressLint("CheckResult")
    private void initFragments() {
        final ArrayList<String> urls = new ArrayList<>();
        if(secondBean != null){
            SpiderService.getPhoto(secondBean.getAlbumHref())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<PhotoBean>>() {
                        @Override
                        public void accept(List<PhotoBean> photoBeans) throws Exception {
                            if(!ListUtil.isEmpty(photoBeans)){
                                Iterator<PhotoBean> iterator = photoBeans.iterator();
                                while(iterator.hasNext()){
                                    PhotoBean next = iterator.next();
                                    urls.add(next.getUrl());
                                }
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                                        DetailFragment.newFragment(urls)).commit();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        }else if(photos != null){
            for (PhotoBean photo : photos) {
                urls.add(photo.getUrl());
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    DetailFragment.newFragment(urls)).commit();
        }

    }


}
