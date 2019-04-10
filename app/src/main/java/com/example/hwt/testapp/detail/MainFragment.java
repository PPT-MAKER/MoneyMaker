package com.example.hwt.testapp.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwt.testapp.ListUtil;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;
import com.example.hwt.testapp.spider.service.SpiderService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends Fragment {

    RecyclerView mListView;

    List<AlbumBean> mAlbumBeans = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initData();
        return view;
    }

    private void initData() {

        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                SpiderService.getAlbum(null)
                        .observeOn(Schedulers.io())
                        .doOnNext(new Consumer<List<AlbumBean>>() {
                            @Override
                            public void accept(List<AlbumBean> albumBeans) throws Exception {
                                if (!ListUtil.isEmpty(albumBeans)) {
                                    mAlbumBeans.addAll(albumBeans);
                                }
                            }
                        }).subscribe(new Consumer<List<AlbumBean>>() {
                    @Override
                    public void accept(List<AlbumBean> albumBeans) throws Exception {

                    }
                });
            }
        });
    }

    private void notifyDataSetChanged() {

    }

}
