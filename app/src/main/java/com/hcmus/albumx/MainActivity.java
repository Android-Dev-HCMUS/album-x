package com.hcmus.albumx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.AlbumList.AlbumList;
import com.hcmus.albumx.AllPhotos.AllPhotos;

public class MainActivity extends FragmentActivity {
    private static final int MY_READ_PERMISSION_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(AllPhotos.newInstance("main_layout"), "AllPhotos", "AllPhotosUI");

        ImageButton button = (ImageButton) findViewById(R.id.addBtn);

        //Check permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }

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
                case R.id.menu_recycleBin:
                    // Do something
                    break;
                case R.id.menu_info:
                    // Do something
                    break;
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment, String TAG, String name) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameFragment, fragment, TAG)
                .addToBackStack(name)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_READ_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}