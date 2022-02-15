package com.hcmus.albumx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class GalleryAdapter extends  RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> listPhotos;
    protected PhotoListener photoListener;

    public GalleryAdapter(Context context, List<String> listPhotos, PhotoListener photoListener) {
        this.context = context;
        this.listPhotos = listPhotos;
        this.photoListener = photoListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.photo_list, parent, false) //photo_list laf gallery_item
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = listPhotos.get(position);

        //Glide.with(context).load(image).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoListener.onPhotoClick(image);
            }
        });



    }

    @Override
    public int getItemCount() {
        return listPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.listPhoto);  //image la listPhoto


        }
    }


    public interface PhotoListener {
        void onPhotoClick(String path);
    }
}
