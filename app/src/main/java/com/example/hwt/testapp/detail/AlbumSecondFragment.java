package com.example.hwt.testapp.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.hwt.testapp.ListUtil;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import static com.example.hwt.testapp.detail.DetailActivity.LOAD_SECOND_ALBUM;

public class AlbumSecondFragment extends Fragment {

    RecyclerView mListView;

    ViewAdapter mViewAdapter;

    List<AlbumBean.AlbumSecondBean> mAlbumBeans = new ArrayList<>();

    private static final String DATA_KEY = "AlbumSecondFragmentData";

    public static Fragment newFragment(List<AlbumBean.AlbumSecondBean> data) {
        AlbumSecondFragment fragment = new AlbumSecondFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATA_KEY, (Serializable) data);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = view.findViewById(R.id.list_view);
        initData();
        return view;
    }

    private void initData() {
        mViewAdapter = new ViewAdapter(getContext(), R.layout.item_view, mAlbumBeans);
        mListView.setAdapter(mViewAdapter);
        mListView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        initDataSource();
    }

    private void initDataSource() {
        List<AlbumBean.AlbumSecondBean> albumSecondBeans = (List<AlbumBean.AlbumSecondBean>) getArguments().getSerializable(DATA_KEY);
        if (!ListUtil.isEmpty(albumSecondBeans)) {
            mAlbumBeans.clear();
            mAlbumBeans.addAll(albumSecondBeans);
            mViewAdapter.notifyDataSetChanged();
        }
    }

    static class ViewAdapter extends BaseQuickAdapter<AlbumBean.AlbumSecondBean, BaseViewHolder> {

        private Context mContext;

        public ViewAdapter(Context context, int layoutResId, @Nullable List<AlbumBean.AlbumSecondBean> data) {
            super(layoutResId, data);
            mContext = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, final AlbumBean.AlbumSecondBean item) {
            Glide.with(mContext)
                    .load(item.getCoverUrl())
                    .asBitmap()
                    .into((ImageView) helper.itemView.findViewById(R.id.coverImg));
            ((TextView) helper.itemView.findViewById(R.id.title)).setText(item.getName());
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(LOAD_SECOND_ALBUM, item);
                    mContext.startActivity(intent);
                }
            });
        }

//        interface  ViewJumpHelper {
//            void jump(AlbumBean item);
//        }
    }

}
