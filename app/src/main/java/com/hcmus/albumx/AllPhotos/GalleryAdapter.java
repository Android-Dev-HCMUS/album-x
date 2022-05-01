package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.albumx.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    protected PhotoListener photoListener;

    private List<ImageInfo> imageInfoArrayList;

    public GalleryAdapter(Context context, List<ImageInfo> imageInfoArrayList, PhotoListener photoListener) {
        this.context = context;
        this.imageInfoArrayList = imageInfoArrayList;
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
        Glide.with(context).load(imageInfoArrayList.get(pos).path).into(holder.image);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.bindImageShow(imageInfoArrayList.get(pos), pos);
//                photoListener.onLongClick(imageInfoArrayList.get(pos).path, pos);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoListener.onPhotoClick(imageInfoArrayList.get(pos).path, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageInfoArrayList.size();
    }

    public List<ImageInfo> getSelectedImages() {
        List<ImageInfo> selectedImages = new ArrayList<>();
        for(ImageInfo imageShow: imageInfoArrayList){
            if(imageShow.isSelected){
                selectedImages.add(imageShow);
            }
        }

        return selectedImages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSelected;
        RoundedImageView image;
        ConstraintLayout layoutImage;
        View viewBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageSelected = itemView.findViewById(R.id.imageSelected);
            image = itemView.findViewById(R.id.image);
            layoutImage = itemView.findViewById(R.id.layoutImage);
            viewBackground = itemView.findViewById(R.id.viewBackground);
        }

        void  bindImageShow(final ImageInfo imageShow, int pos){
            if(imageShow.isSelected){
                viewBackground.setBackgroundResource(R.drawable.image_selected_background);
                imageSelected.setVisibility(View.VISIBLE);
            } else {
                viewBackground.setBackgroundResource(R.drawable.image_background);
                imageSelected.setVisibility(View.GONE);
            }

            layoutImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(imageShow.isSelected){
                        viewBackground.setBackgroundResource(R.drawable.image_background);
                        imageSelected.setVisibility(View.GONE);
                        imageShow.isSelected = false;
                        if(getSelectedImages().size() == 0){
                            photoListener.onLongClick(imageShow.path, pos, false);
                            photoListener.onImageAction(false);
                        }

                    } else {
                        viewBackground.setBackgroundResource(R.drawable.image_selected_background);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageShow.isSelected =true;
                        photoListener.onLongClick(imageShow.path, pos, true);
                        photoListener.onImageAction(true);
                    }
                    return true;
                }
            });
        }

    }
    public interface PhotoListener {

        void onPhotoClick(String imagePath, int position);
        boolean onLongClick(String imagePath, int position, boolean state);
        void onImageAction(Boolean isSelected);
    }
}
