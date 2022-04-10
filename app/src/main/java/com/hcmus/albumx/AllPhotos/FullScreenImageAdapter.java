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

    public FullScreenImageAdapter(Context context, List<String> listImagePath) {
        this.context = context;
        this.listImagePath = listImagePath;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.image_viewing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(listImagePath.get(position)).into(holder.imageView);
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
}
