package com.example.hwt.testapp.detail;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.hwt.testapp.ListUtil;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;
import com.example.hwt.testapp.spider.service.SpiderService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.hwt.testapp.detail.DetailActivity.ALBUM_BEAN;

public class MainFragment extends Fragment {

    RecyclerView mListView;

    ViewAdapter mViewAdapter;

    List<AlbumBean> mAlbumBeans = new ArrayList<>();

    public static MainFragment newFragment() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (RecyclerView) view.findViewById(R.id.list_view);
        initData();
        return view;
    }

    private void initData() {
        mViewAdapter = new ViewAdapter(getContext(), R.layout.item_view, mAlbumBeans);
        mListView.setAdapter(mViewAdapter);
        mListView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                SpiderService.getAlbum()
                        .observeOn(Schedulers.io())
                        .doOnNext(new Consumer<List<AlbumBean>>() {
                            @Override
                            public void accept(List<AlbumBean> albumBeans) throws Exception {
                                if (!ListUtil.isEmpty(albumBeans)) {
                                    mAlbumBeans.addAll(albumBeans);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<AlbumBean>>() {
                    @Override
                    public void accept(List<AlbumBean> albumBeans) throws Exception {
                        mViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    static class ViewAdapter extends BaseQuickAdapter<AlbumBean, BaseViewHolder> {

        private Context mContext;

        public ViewAdapter(Context context, int layoutResId, @Nullable List<AlbumBean> data) {
            super(layoutResId, data);
            mContext = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, final AlbumBean item) {
            Glide.with(mContext)
                    .load(item.getCoverUrl())
                    .asBitmap()
                    .into((ImageView) helper.itemView.findViewById(R.id.coverImg));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(ALBUM_BEAN, item);
                    mContext.startActivity(intent);
                }
            });
        }

//        interface  ViewJumpHelper {
//            void jump(AlbumBean item);
//        }
    }
}
