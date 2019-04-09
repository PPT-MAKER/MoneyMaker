package com.example.hwt.testapp.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwt.testapp.Behavior.CollectionHelper;
import com.example.hwt.testapp.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer;

/**
 * Created by cb on 2019/4/9.
 */
public class DetailActivity extends AppCompatActivity {
    private static final String IMG_LIST = "imgList";

    private List<ImgTmp> imgTmpList;
    private VerticalViewPager viewPager;

    public static Intent newIntent(Context context, ArrayList<ImgTmp> imgTmps) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putParcelableArrayListExtra(IMG_LIST, imgTmps);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_detail);
        imgTmpList = getIntent().getParcelableArrayListExtra(IMG_LIST);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new Adapter(this, imgTmpList));
        viewPager.setPageTransformer(false, new DefaultTransformer());
    }

    private static class Adapter extends PagerAdapter {
        private List<ImgTmp> imgTmps;
        private Context context;
        private List<ViewHolder> viewHolders;

        public Adapter(Context context, @NonNull List<ImgTmp> imgTmps) {
            this.context = context;
            this.imgTmps = imgTmps;
            this.viewHolders = new LinkedList<>();
        }

        @Override
        public int getCount() {
            return imgTmps.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            ViewHolder viewHolder = (ViewHolder) o;
            return view == viewHolder.getContentView();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewHolder viewHolder;
            if (viewHolders.size() == 0) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View root = inflater.inflate(R.layout.view_detail_item, container, false);
                viewHolder = new ViewHolder(root);
            } else {
                viewHolder = viewHolders.remove(0);
            }
            viewHolder.bind(imgTmps.get(position));
            return viewHolder;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewHolder viewHolder = (ViewHolder) object;
            container.removeView(viewHolder.getContentView());
            viewHolders.add(viewHolder);
        }
    }

    private static class ViewHolder implements View.OnClickListener {
        private View root;
        private ImageView contentView;
        private ImageView collectBtn;
        private ImgTmp imgTmp;

        public ViewHolder(View view) {
            this.root = view;
            this.contentView = root.findViewById(R.id.content);
            this.collectBtn = root.findViewById(R.id.collect_btn);
            collectBtn.setOnClickListener(this);
        }

        public void bind(final ImgTmp img) {
            this.imgTmp = imgTmp;
            ImgGetterTmp.loadImg(new ImgGetterTmp.Callback() {
                @Override
                public void onLoaded(String url, Drawable drawable) {
                    if (url.equals(img.getImgUrl())) {
                        contentView.setImageDrawable(drawable);
                    }
                }
            });
            CollectionHelper.loadCollectionInfo(new CollectionHelper.Callback() {
                @Override
                public void onLoaded(String url, boolean isCollect) {
                    if (url.equals(img.getImgUrl())) {
                        collectBtn.setImageDrawable(root.getContext()
                                .getResources()
                                .getDrawable(isCollect ?
                                        R.drawable.collected :
                                        R.drawable.no_collect));
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (imgTmp != null) {
                CollectionHelper.collect(imgTmp.getImgUrl());
            }
        }

        public View getContentView() {
            return root;
        }
    }
}
