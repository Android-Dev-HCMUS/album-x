package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

public class ImageViewing extends Fragment {
    MainActivity main;
    Context context;

    public int[] imageArray = {
            R.drawable.stock_1, R.drawable.stock_2, R.drawable.stock_3, R.drawable.stock_4 ,
            R.drawable.stock_5, R.drawable.stock_6, R.drawable.stock_7, R.drawable.stock_8,
            R.drawable.stock_9, R.drawable.stock_10, R.drawable.stock_11, R.drawable.stock_12,
            R.drawable.stock_13, R.drawable.stock_14, R.drawable.stock_15, R.drawable.stock_16
    };

    public ImageViewing() {

    }

    public static ImageViewing newInstance(int imagePosition) {
        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();
        bundle.putInt("position", imagePosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.image_viewing, null);

        Bundle bundle = getArguments();
        int position = 0;
        if (bundle != null) {
            position = bundle.getInt("position");
        }

        ViewPager viewPager = view.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(new FullScreenImageAdapter(context, imageArray));
        viewPager.setCurrentItem(position);

        return view;
    }
}