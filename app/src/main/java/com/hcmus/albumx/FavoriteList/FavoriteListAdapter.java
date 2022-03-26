package com.hcmus.albumx.FavoriteList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class FavoriteListAdapter extends BaseAdapter {
    private Context context;
    private int[] listFavoriteImg;

    public FavoriteListAdapter(Context context, int[] listFavoriteImg) {
        this.context = context;
        this.listFavoriteImg = listFavoriteImg;
    }

    @Override
    public int getCount() {
        return listFavoriteImg.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;
        if (imageView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
        }
        imageView.setImageResource(listFavoriteImg[position]);
        return imageView;
    }

}
