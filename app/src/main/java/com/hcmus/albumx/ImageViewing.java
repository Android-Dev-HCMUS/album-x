package com.hcmus.albumx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.AllPhotos.FullScreenImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewing extends Fragment {
    MainActivity main;
    Context context;

    public ImageViewing() {

    }

    public static ImageViewing newInstance(String path) {
        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
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
        String path = null;

        if (bundle != null) {
            path = bundle.getString("path");
        }


        File imgFile = new  File(path);

        //set Img

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) view.findViewById(R.id.imageView);

            myImage.setImageBitmap(myBitmap);

        }
//        ViewPager viewPager = view.findViewById(R.id.imageViewPager);
//        viewPager.setAdapter(new FullScreenImageAdapter(context, imageArray));

        Button back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                AllPhotos fragment = (AllPhotos) main.getSupportFragmentManager().findFragmentByTag("AllPhotos");
                if (fragment != null) {
                    fragment.showNavAndButton();
                }
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        AllPhotos fragment = (AllPhotos) main.getSupportFragmentManager().findFragmentByTag("AllPhotos");
        if (fragment != null) {
            fragment.showNavAndButton();
        }
    }
}