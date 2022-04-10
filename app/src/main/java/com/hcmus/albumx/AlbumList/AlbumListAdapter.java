package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hcmus.albumx.R;

import java.util.ArrayList;


public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<AlbumInfo> albumList;

    public AlbumListAdapter(Context context, int layout, ArrayList<AlbumInfo> albumList){
        this.context = context;
        this.layout = layout;
        this.albumList = albumList;
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

        viewHolder.imageView.setImageResource(albumList.get(i).icon);
        viewHolder.textView.setText(albumList.get(i).name);

        return view;
    }
}
