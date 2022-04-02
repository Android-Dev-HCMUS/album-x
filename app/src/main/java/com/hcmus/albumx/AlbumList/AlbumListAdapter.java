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
    private ArrayList<String> albumList;
    private ArrayList<Integer> icons;

    public AlbumListAdapter(Context context, int layout, ArrayList<String> albumList, ArrayList<Integer> icons){
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

        viewHolder.imageView.setImageResource(icons.get(i));
        viewHolder.textView.setText(albumList.get(i));

        return view;
    }
}
