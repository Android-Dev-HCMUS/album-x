package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.R;

import java.util.ArrayList;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {
    Context context;
    ArrayList<Bitmap> bitmapArrayList;

    public FullScreenImageAdapter(Context context, ArrayList<Bitmap> bitmapArrayList) {
        this.context = context;
        this.bitmapArrayList = bitmapArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.image_viewing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmapArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmapArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFull);
        }
    }
}
