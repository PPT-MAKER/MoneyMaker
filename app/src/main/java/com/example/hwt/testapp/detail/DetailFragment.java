package com.example.hwt.testapp.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hwt.testapp.Behavior.CollectionHelper;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.AlbumBean;
import com.example.hwt.testapp.spider.beans.PhotoBean;
import com.example.hwt.testapp.spider.service.SpiderService;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer;

/**
 * Created by cb on 2019/4/9.
 */
public class DetailFragment extends Fragment {
    private static final String ALBUM = "album";

    private AlbumBean album;
    private VerticalViewPager viewPager;

    public static Fragment newFragment(Context context, String albumUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM, albumUrl);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_img_detail, container, false);
        String url = getArguments().getParcelable(ALBUM);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, new DefaultTransformer());
        SpiderService.getPhoto(url, null)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<PhotoBean>>() {
            @Override
            public void accept(List<PhotoBean> photoBeans) throws Exception {
                viewPager.setAdapter(new Adapter(getContext(), photoBeans));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        return root;
    }

    private static class Adapter extends PagerAdapter {
        private List<PhotoBean> imgs;
        private Context context;
        private List<ViewHolder> viewHolders;

        public Adapter(Context context, @NonNull List<PhotoBean> imgs) {
            this.context = context;
            this.imgs = imgs;
            this.viewHolders = new LinkedList<>();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return imgs.size();
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
            viewHolder.bind(imgs.get(position));
            container.addView(viewHolder.getContentView());
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
        private PhotoBean photo;
        private boolean isCollected;

        public ViewHolder(View view) {
            this.root = view;
            this.contentView = root.findViewById(R.id.content);
            this.collectBtn = root.findViewById(R.id.collect_btn);
            collectBtn.setOnClickListener(this);
        }

        public void bind(final PhotoBean photo) {
            this.photo = photo;
            isCollected = CollectionHelper.isCollected(photo.getUrl());
            Glide.with(root.getContext()).load(photo.getUrl()).into(contentView);
            collectBtn.setImageDrawable(root.getContext()
                    .getResources()
                    .getDrawable(getCollectDrawable(isCollected)));

        }

        @Override
        public void onClick(View v) {
            if (photo.getUrl() != null) {
                isCollected = !isCollected;
                CollectionHelper.collect(photo.getUrl(), isCollected);
                collectBtn.setImageResource(getCollectDrawable(isCollected));
            }
        }

        private int getCollectDrawable(boolean isCollected) {
            return isCollected ?
                    R.drawable.collected :
                    R.drawable.no_collected;
        }

        public View getContentView() {
            return root;
        }
    }
}
