package com.hcmus.albumx;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    Context main;

    public int[] imageArray = {
            R.drawable.stock_1, R.drawable.stock_2, R.drawable.stock_3, R.drawable.stock_4 ,
            R.drawable.stock_5, R.drawable.stock_6, R.drawable.stock_7, R.drawable.stock_8,
            R.drawable.stock_9, R.drawable.stock_10, R.drawable.stock_11, R.drawable.stock_12,
            R.drawable.stock_13, R.drawable.stock_14, R.drawable.stock_15, R.drawable.stock_16
    };

    public ImageAdapter(Context context){
        this.main = context;
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public Object getItem(int i) {
        return imageArray[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView imageView = new ImageView(main);
        imageView.setImageResource(imageArray[i]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(350, 350));

        return imageView;
    }
}
