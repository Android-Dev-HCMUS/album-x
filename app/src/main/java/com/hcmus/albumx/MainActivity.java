package com.hcmus.albumx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameFragment, AllPhotosLayout.newInstance("main_layout"))
                .commit();

        button = (ImageButton) findViewById(R.id.addBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_photo:
                    button.setVisibility(View.VISIBLE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameFragment, AllPhotosLayout.newInstance("main_layout"))
                            .commit();
                    break;
                case R.id.menu_album:
                    button.setVisibility(View.INVISIBLE);
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