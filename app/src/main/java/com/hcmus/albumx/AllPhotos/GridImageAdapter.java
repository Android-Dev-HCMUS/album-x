package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridImageAdapter extends BaseAdapter {
    Context context;
    int[] imageArray;

    public GridImageAdapter(Context context, int[] imageArray){
        this.context = context;
        this.imageArray = imageArray;
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
        ImageView imageView = (ImageView) view;

        if(imageView == null){
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
        }
        imageView.setImageResource(imageArray[i]);
        return imageView;
    }
}
