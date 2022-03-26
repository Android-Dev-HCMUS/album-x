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
import android.widget.Button;

import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

public class ImageViewing extends Fragment {
    MainActivity main;
    Context context;

    public ImageViewing() {

    }

    public static ImageViewing newInstance(int imagePosition, int[] imageArray) {
        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();
        bundle.putInt("positionArray", imagePosition);
        bundle.putIntArray("imageArray", imageArray);
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
        int[] imageArray = null;

        if (bundle != null) {
            position = bundle.getInt("positionArray");
            imageArray = bundle.getIntArray("imageArray");
        }

        ViewPager viewPager = view.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(new FullScreenImageAdapter(context, imageArray));
        viewPager.setCurrentItem(position);

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