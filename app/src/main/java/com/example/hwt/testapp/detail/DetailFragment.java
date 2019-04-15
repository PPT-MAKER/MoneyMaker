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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hwt.testapp.Behavior.CollectionHelper;
import com.example.hwt.testapp.Behavior.LoveAnimator;
import com.example.hwt.testapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer;

/**
 * Created by cb on 2019/4/9.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final String PHOTOS = "photots";

    private static final String COLLECT = "collect";

    private VerticalViewPager viewPager;
    private FloatingActionButton changeWallPapaerBtn;
    private Adapter adapter;
    private List<String> urls = new ArrayList<>();
    private Map<String, Bitmap> maps = new HashMap<>();

    public static Fragment newFragment(ArrayList<String> albumUrl) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(PHOTOS, albumUrl);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_img_detail, container, false);
        changeWallPapaerBtn = root.findViewById(R.id.change_wallpaper);
        changeWallPapaerBtn.setOnClickListener(this);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, new DefaultTransformer());
        adapter = new Adapter(getContext(), urls);
        viewPager.setAdapter(adapter);

        List<String> photos = getArguments().getStringArrayList(PHOTOS);
        if (photos != null) {
            urls.addAll(photos);
            adapter.notifyDataSetChanged();
        }
        return root;
    }

    private void initSkin() {
        int index = viewPager.getCurrentItem();
        if (index >= 0 && index < urls.size()) {
            Bitmap bitmap = maps.get(urls.get(viewPager.getCurrentItem()));
            if (bitmap != null) {
                try {
                    WallpaperManager manager = WallpaperManager.getInstance(getContext());
                    manager.setBitmap(bitmap);
                    Toast.makeText(getContext(), "壁纸切换成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        ViewPropertyAnimator animator = v.animate();
        animator.rotationBy(360).setDuration(500).start();
        initSkin();
    }

    private class Adapter extends PagerAdapter {
        private List<String> imgs;
        private Context context;
        private List<ViewHolder> viewHolders;

        public Adapter(Context context, @NonNull List<String> imgs) {
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

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            CollectionHelper.history(imgs.get(position));
        }
    }

    private class ViewHolder implements View.OnClickListener {
        private View root;
        private ImageView contentView;
        private ImageView collectBtn;
        private LoveAnimator mLoveAnimator;
        private String url;
        private boolean isCollected;
        private ProgressBar progressBar;

        public ViewHolder(View view) {
            this.root = view;
            this.contentView = root.findViewById(R.id.content);
            this.collectBtn = root.findViewById(R.id.collect_btn);
            this.progressBar = root.findViewById(R.id.progress);
            this.mLoveAnimator = root.findViewById(R.id.loveanimator);
            this.mLoveAnimator.setCollectAHelper(new LoveAnimator.CollectAHelper() {
                @Override
                public void collect() {
                    if (url != null) {
                        isCollected = true;
                        CollectionHelper.collect(url, isCollected);
                        collectBtn.setImageResource(getCollectDrawable(isCollected));
                    }
                }
            });
            collectBtn.setOnClickListener(this);
        }

        public void bind(final String url, final int position) {
            showProgress(true);
            this.url = url;
            isCollected = CollectionHelper.isCollected(url);
            Glide.with(root.getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    urls.remove(url);
                    adapter.notifyDataSetChanged();
                    showProgress(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    maps.put(s, drawableToBitmap(glideDrawable));
                    showProgress(false);
                    return false;
                }
            }).into(contentView);
            collectBtn.setImageDrawable(root.getContext()
                    .getResources()
                    .getDrawable(getCollectDrawable(isCollected)));

        }

        private void showProgress(boolean show) {
            if (null != progressBar) {
                if (show) {
                    progressBar.setVisibility(View.VISIBLE);
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
                    progressBar.startAnimation(fadeIn);
                } else {
                    Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    progressBar.startAnimation(fadeOut);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (url != null) {
                isCollected = !isCollected;
                CollectionHelper.collect(url, isCollected);
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

        private Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
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
