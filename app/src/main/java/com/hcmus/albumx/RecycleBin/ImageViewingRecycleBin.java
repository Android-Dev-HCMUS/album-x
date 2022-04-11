package com.hcmus.albumx.RecycleBin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.albumx.AllPhotos.FullScreenImageAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;

public class ImageViewingRecycleBin extends Fragment {
    public static String TAG = "Image Viewing Recycle Bin";

    private MainActivity main;
    private Context context;

    private ViewPager2 viewPager;
    FullScreenImageAdapter adapter;

    private int pos = 0;

    private ArrayList<ImageInfo> imageInfoArrayList;
    ImageDatabase myDB;

    public static ImageViewing newInstance() {
        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();

            imageInfoArrayList = myDB.getImagesInRecycleBin();
        } catch (IllegalStateException ignored) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = (View) inflater.inflate(R.layout.image_viewing, container, false);

        adapter = new FullScreenImageAdapter(context, imageInfoArrayList);

        viewPager = view.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos, false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pos = position;
            }
        });

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);

                page.setScaleY(0.8f + v * 0.2f);
            }
        });
        viewPager.setPageTransformer(transformer);

        return view;
    }
}
