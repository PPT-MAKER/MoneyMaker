package com.example.hwt.testapp.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
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
public class DetailFragment extends Fragment {
    private static final String IMG_LIST = "imgList";

    private List<ImgTmp> imgTmpList;
    private VerticalViewPager viewPager;

    public static Fragment newFragment(Context context, ArrayList<ImgTmp> imgTmps) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(IMG_LIST, imgTmps);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_img_detail, container, false);
        imgTmpList = getArguments().getParcelableArrayList(IMG_LIST);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(new Adapter(getContext(), imgTmpList));
        viewPager.setPageTransformer(false, new DefaultTransformer());
        return super.onCreateView(inflater, container, savedInstanceState);
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
        private boolean isCollected;

        public ViewHolder(View view) {
            this.root = view;
            this.contentView = root.findViewById(R.id.content);
            this.collectBtn = root.findViewById(R.id.collect_btn);
            collectBtn.setOnClickListener(this);
        }

        public void bind(final ImgTmp img) {
            this.imgTmp = img;
            isCollected = CollectionHelper.isCollected(img.getImgUrl());
            ImgGetterTmp.loadImg(new ImgGetterTmp.Callback() {
                @Override
                public void onLoaded(String url, Drawable drawable) {
                    if (url.equals(img.getImgUrl())) {
                        contentView.setImageDrawable(drawable);
                    }
                }
            });
            collectBtn.setImageDrawable(root.getContext()
                    .getResources()
                    .getDrawable(isCollected ?
                            R.drawable.collected :
                            R.drawable.no_collect));

        }

        @Override
        public void onClick(View v) {
            if (imgTmp != null) {
                CollectionHelper.collect(imgTmp.getImgUrl(), !isCollected);
            }
        }

        public View getContentView() {
            return root;
        }
    }
}
