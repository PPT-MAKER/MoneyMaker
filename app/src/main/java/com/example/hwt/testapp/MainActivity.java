package com.example.hwt.testapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hwt.testapp.detail.DetailFragment;

public class MainActivity extends AppCompatActivity {
    private long lastBackPressTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, MainFragment.newFragment()).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MainFragment.newFragment()).commit();
                break;
            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DetailFragment.newFragment(this, null)).commit();
                break;
            case R.id.collection:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DetailFragment.newFragment(this, null)).commit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        long current = System.currentTimeMillis();
        if (current - lastBackPressTime < 1000) {
            finish();
        } else {
            lastBackPressTime = current;
            Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
        }
    }
}
