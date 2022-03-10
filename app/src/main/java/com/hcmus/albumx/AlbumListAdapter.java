package com.hcmus.albumx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.zip.Inflater;

public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<String> albumList;
    private Integer[] icons;

    public AlbumListAdapter(Context context, int layout, List<String> albumList, Integer[] icons){
        this.context = context;
        this.layout = layout;
        this.albumList = albumList;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int i) {
        return albumList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewRow);
        TextView textView = (TextView) view.findViewById(R.id.textViewRow);

        imageView.setImageResource(icons[i]);
        textView.setText(albumList.get(i));

        return view;
    }
}
