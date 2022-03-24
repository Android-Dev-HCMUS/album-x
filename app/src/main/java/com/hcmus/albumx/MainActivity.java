package com.hcmus.albumx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.AlbumList.AlbumList;
import com.hcmus.albumx.AllPhotos.AllPhotos;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(AllPhotos.newInstance("main_layout"), "AllPhotos", "AllPhotosUI");

        ImageButton button = (ImageButton) findViewById(R.id.addBtn);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_album:
                    button.setVisibility(View.INVISIBLE);
                    loadFragment(AlbumList.newInstance("album"), "AlbumList", "AlbumListUI");
                    break;
                case R.id.menu_photo:
                default:
                    button.setVisibility(View.VISIBLE);
                    loadFragment(AllPhotos.newInstance("main_layout"), "AllPhotos", "AllPhotosUI");
                    break;
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment, String TAG, String name){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameFragment, fragment, TAG)
                .addToBackStack(name)
                .commit();
    }
}