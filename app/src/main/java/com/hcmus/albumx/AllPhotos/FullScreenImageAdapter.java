package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;


public class FullScreenImageAdapter extends PagerAdapter {
    Context context;
    List<String> imageArr;

    public FullScreenImageAdapter(Context context, List<String> imageArr) {
        this.context = context;
        this.imageArr = imageArr;
    }

    @Override
    public int getCount() {
        return imageArr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setImageResource(imageArr.get(position));
        container.addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
