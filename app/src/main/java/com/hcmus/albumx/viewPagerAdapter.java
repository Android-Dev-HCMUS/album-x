package com.hcmus.albumx;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class viewPagerAdapter extends FragmentStatePagerAdapter {
    public viewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 1:
                return MainLayout.newInstance("main");
            case 0:
                return AlbumList.newInstance("album");
            default:
                return MainLayout.newInstance("main");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
