package com.hcmus.albumx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(AllPhotos.newInstance("main_layout"), "AllPhotos", "AllPhotosUI");

        //Check permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_album:
                    loadFragment(AlbumList.newInstance("album"), AlbumList.TAG, "AlbumListUI");
                    break;
                case R.id.menu_photo:
                default:
                    loadFragment(AllPhotos.newInstance("main_layout"), AllPhotos.TAG, "AllPhotosUI");
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
                .commit();
    }

    public void setBottomNavigationVisibility(int visibility) {
        bottomNavigationView.setVisibility(visibility);
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