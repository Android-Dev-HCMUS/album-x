package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hcmus.albumx.R;

import java.io.File;
import java.util.ArrayList;


public class FullScreenImageAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> imageArr;

    public FullScreenImageAdapter(Context context, ArrayList<String> imageArr) {
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
        int padding = context.getResources().getDimensionPixelSize(
                R.dimen.default_padding_side);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        File imgFile = new  File(imageArr.get(position));
        Log.e("image", imgFile.getAbsolutePath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        container.addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
