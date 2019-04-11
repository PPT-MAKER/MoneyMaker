package com.example.hwt.testapp.detail;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hwt.testapp.Behavior.CollectionHelper;
import com.example.hwt.testapp.R;
import com.example.hwt.testapp.spider.beans.PhotoBean;
import com.example.hwt.testapp.spider.service.SpiderService;

import java.io.IOException;
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

    private VerticalViewPager viewPager;
    private Bitmap bitmap;

    public static Fragment newFragment(String albumUrl) {
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
        String url = getArguments().getString(ALBUM);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, new DefaultTransformer());
        SpiderService.getPhoto(url)
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

    private void initSkin() {
        if (bitmap != null) {
            try {
                WallpaperManager manager = WallpaperManager.getInstance(getContext());
                manager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Adapter extends PagerAdapter {
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
            ViewHolder viewHolder = load(container, position);
            container.addView(viewHolder.getContentView());
            preload(container, position);
            return viewHolder;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewHolder viewHolder = (ViewHolder) object;
            container.removeView(viewHolder.getContentView());
            viewHolders.add(viewHolder);
        }

        private ViewHolder load(@NonNull ViewGroup container, int position) {
            ViewHolder viewHolder;
            if (viewHolders.size() == 0) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View root = inflater.inflate(R.layout.view_detail_item, container, false);
                viewHolder = new ViewHolder(root);
            } else {
                viewHolder = viewHolders.remove(0);
            }
            viewHolder.bind(imgs.get(position), position);
            return viewHolder;
        }

        private void preload(@NonNull ViewGroup container, int position) {
            if (position > 0) {
                load(container, position - 1);
            } else if (position < imgs.size() - 1) {
                load(container, position + 1);
            }
        }
    }

    private class ViewHolder extends GestureDetector.SimpleOnGestureListener implements View.OnClickListener, View.OnTouchListener {
        private View root;
        private ImageView contentView;
        private ImageView collectBtn;
        private PhotoBean photo;
        private boolean isCollected;
        private GestureDetector gestureDetector;

        public ViewHolder(View view) {
            this.root = view;
            this.contentView = root.findViewById(R.id.content);
            this.collectBtn = root.findViewById(R.id.collect_btn);
            collectBtn.setOnClickListener(this);
            gestureDetector = new GestureDetector(this);
            gestureDetector.setIsLongpressEnabled(false);
            root.setOnTouchListener(this);
        }

        public void bind(final PhotoBean photo, final int index) {
            bitmap = null;
            this.photo = photo;
            isCollected = CollectionHelper.isCollected(photo.getUrl());
            Glide.with(root.getContext()).load(photo.getUrl()).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    if(viewPager.getCurrentItem() == index)
                    bitmap = drawableToBitmap(glideDrawable);
                    return false;
                }
            }).into(contentView);
            collectBtn.setImageResource(getCollectDrawable(isCollected));
        }

        @Override
        public void onClick(View v) {
            collect();
        }

        private int getCollectDrawable(boolean isCollected) {
            return isCollected ?
                    R.drawable.collected :
                    R.drawable.no_collected;
        }

        public View getContentView() {
            return root;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            collect();
            return true;
        }

        private void collect() {
            if (photo.getUrl() != null) {
                isCollected = !isCollected;
                CollectionHelper.collect(photo.getUrl(), isCollected);
                collectBtn.setImageResource(getCollectDrawable(isCollected));
            }
        }

        private Bitmap drawableToBitmap (Drawable drawable) {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}
