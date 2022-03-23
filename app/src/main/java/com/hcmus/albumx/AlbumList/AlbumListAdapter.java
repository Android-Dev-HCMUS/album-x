package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hcmus.albumx.R;

import java.util.List;
import java.util.zip.Inflater;

public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private String[] albumList;
    private Integer[] icons;

    public AlbumListAdapter(Context context, int layout, String[] albumList, Integer[] icons){
        this.context = context;
        this.layout = layout;
        this.albumList = albumList;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return albumList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageViewRow);
            viewHolder.textView = (TextView) view.findViewById(R.id.textViewRow);

            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imageView.setImageResource(icons[i]);
        viewHolder.textView.setText(albumList[i]);

        return view;
    }
}
