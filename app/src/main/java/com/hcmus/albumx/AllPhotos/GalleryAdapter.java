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

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    protected PhotoListener photoListener;

    private List<String> listImagePath;

    public GalleryAdapter(Context context, List<String> listImagePath, PhotoListener photoListener) {
        this.context = context;
        this.listImagePath = listImagePath;
        this.photoListener = photoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos =  position;
        Glide.with(context).load(listImagePath.get(pos)).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoListener.onPhotoClick(listImagePath.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImagePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }
    public interface PhotoListener {

        void onPhotoClick(String imagePath, int position);
    }
}
