package com.hcmus.albumx;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.AlbumList.AlbumList;
import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.CloudStorage.Authentication;
import com.hcmus.albumx.InfoMembers.InfoMembers;
import com.hcmus.albumx.RecycleBin.RecycleBinPhotos;

public class MainActivity extends FragmentActivity {
    private static final int MY_READ_PERMISSION_CODE = 101;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = this.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor ed;
        if(!sp.contains("isNightMode")){
            ed = sp.edit();
            //Set default as light theme
            ed.putBoolean("isNightMode", false);
            ed.commit();
        }
        boolean isNightMode = sp.getBoolean("isNightMode", true);
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.BlueGrayTheme);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.LightTheme);
        }
        setContentView(R.layout.activity_main);
        loadFragment(AllPhotos.newInstance(), "AllPhotos", "AllPhotosUI");
        //Check permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_album:
                    loadFragment(AlbumList.newInstance(), AlbumList.TAG, "AlbumListUI");

                    break;
                case R.id.menu_recycleBin:
                    loadFragment(RecycleBinPhotos.newInstance(), RecycleBinPhotos.TAG, "RecycleBinUI");
                    break;
                case R.id.menu_photo:
                default:
                    loadFragment(AllPhotos.newInstance(), AllPhotos.TAG, "AllPhotosUI");

                    break;
                case R.id.cloudStorage:
                    loadFragment(Authentication.newInstance(), Authentication.TAG, "AuthenticationUI");
                    break;

                case R.id.menu_info:
                    loadFragment(InfoMembers.newInstance(), InfoMembers.TAG, "InfoMembers");
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

        boolean checkWritePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;

        if (checkWritePermission) {
            Toast.makeText(this, "Write external storage permission granted", Toast.LENGTH_SHORT).show();
        } else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}