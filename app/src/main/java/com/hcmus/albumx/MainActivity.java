package com.hcmus.albumx;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends FragmentActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameFragment, MainLayout.newInstance("main_layout"))
                .commit();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_photo:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameFragment, MainLayout.newInstance("main_layout"))
                            .commit();
                    break;
                case R.id.menu_album:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameFragment, AlbumList.newInstance("album"))
                            .commit();
                    break;
            }

            return true;
        });
    }
}