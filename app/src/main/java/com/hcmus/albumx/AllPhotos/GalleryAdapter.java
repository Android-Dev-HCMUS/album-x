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

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    protected PhotoListener photoListener;

    private List<Bitmap> bitmapList;

    public GalleryAdapter(Context context, List<Bitmap> bitmapList, PhotoListener photoListener) {
        this.context = context;
        this.bitmapList = bitmapList;
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
        holder.image.setImageBitmap(bitmapList.get(pos));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoListener.onPhotoClick(bitmapList.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }
    public interface PhotoListener {

        void onPhotoClick(Bitmap bitmap, int position);
    }
}
