package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.albumx.R;

import java.util.List;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {
    Context context;
    List<String> listImagePath;
    private OnItemClickListener listenerOnItemClick;

    public FullScreenImageAdapter(Context context, List<String> listImagePath, OnItemClickListener listenerOnItemClick) {
        this.context = context;
        this.listImagePath = listImagePath;
        this.listenerOnItemClick = listenerOnItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.image_viewing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;
        Glide.with(context).load(listImagePath.get(position)).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerOnItemClick.onItemClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImagePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFull);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position); }
}
