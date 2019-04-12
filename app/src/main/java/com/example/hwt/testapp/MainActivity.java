package com.example.hwt.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hwt.testapp.Behavior.CollectionHelper;
import com.example.hwt.testapp.detail.DetailFragment;
import com.example.hwt.testapp.detail.MainFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RingProgressBar mRingProgressBar;

    private int state = 1;

    private long lastbackTime = 0L;

    private MainFragment mMainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRingProgressBar = (RingProgressBar) findViewById(R.id.progress_bar);
        if (mMainFragment == null) {
            mMainFragment = com.example.hwt.testapp.detail.MainFragment.newFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMainFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                state = 1;
                if (mMainFragment == null) {
                    mMainFragment = com.example.hwt.testapp.detail.MainFragment.newFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mMainFragment).commit();
                break;
            case R.id.history:
                state = 2;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DetailFragment.newFragment(new ArrayList<>(CollectionHelper.getHistoryItems()))).commit();
                break;
            case R.id.collection:
                state = 2;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DetailFragment.newFragment(new ArrayList<>(CollectionHelper.getCollectItems()))).commit();
                break;
            case R.id.clean:
                mRingProgressBar.setProgress(0);
                mRingProgressBar.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int progress = mRingProgressBar.getProgress() + new Random().nextInt(15) + 2;
                        if (progress > 100) {
                            Toast.makeText(MainActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
                            mRingProgressBar.setVisibility(View.GONE);
                            return;
                        }
                        mRingProgressBar.setProgress(progress);
                        mHandler.postDelayed(this, 500L);
                    }
                }, 500L);
                break;
            case R.id.search:
                Toast.makeText(MainActivity.this, "下个版本更新图片搜索功能，敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.good:
                Toast.makeText(this, "暂未上架，感谢您的支持🙏", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    Handler mHandler = new Handler();

    private void back() {
        if (System.currentTimeMillis() - lastbackTime < 2000L) {
            finish();
        } else {
            lastbackTime = System.currentTimeMillis();
            Toast.makeText(this, "再次返回可退出", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (state == 1) {
            back();
        } else {
            state = 1;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMainFragment).commit();
        }
    }
}
