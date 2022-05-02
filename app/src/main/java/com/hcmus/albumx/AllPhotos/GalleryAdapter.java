package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.albumx.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    protected PhotoListener photoListener;

    public static final int TYPE_DATE = 0;
    public static final int TYPE_IMAGE = 1;

    private List<ImageInfo> imageInfoArrayList;

    private List<ListItem> listItem;
    public void setData(List<ListItem> listItem){
        this.listItem = listItem;
        notifyDataSetChanged();
    }

    public GalleryAdapter(Context context, List<ImageInfo> imageInfoArrayList, PhotoListener photoListener) {
        this.context = context;
        this.imageInfoArrayList = imageInfoArrayList;
        this.photoListener = photoListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_DATE == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_photo_item_date, parent, false);
            return new HeaderViewHolder(view);
        } else if(TYPE_IMAGE == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new ImageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int pos =  position;
        ListItem item = listItem.get(position);
        if(item == null){
            return;
        }
        if(TYPE_DATE == holder.getItemViewType()){
            DateItem dateItem = (DateItem) item;
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.dateHeader.setText(dateItem.getDate());
        }
        else if(TYPE_IMAGE == holder.getItemViewType()){
            GroupImageItem groupImageItem = (GroupImageItem) item;
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            Glide.with(context)
                    .load(groupImageItem.getImageInfo().path)
                    .into(viewHolder.image);

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ListItem item = listItem.get(pos);
                    GroupImageItem image = (GroupImageItem) item;
                    if(item.getType() == ListItem.TYPE_IMAGE) {
                        viewHolder.bindImageShow(image.getImageInfo(), pos);
                    }
                    return false;
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListItem item = listItem.get(pos);
                    GroupImageItem image = (GroupImageItem) item;
                    List<ImageInfo> selectedImages = new ArrayList<>();
                    selectedImages = getSelectedImages();

                    if(item.getType() == ListItem.TYPE_IMAGE){
                        if(selectedImages.size() == 0){
                            photoListener.onPhotoClick(image.getImageInfo().path, pos);
                        }else {
                            viewHolder.bindImageShow(image.getImageInfo(), pos);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(listItem != null){
            return listItem.size();
        }
        return  0;
    }

    @Override
    public int getItemViewType(int position) {
        return listItem.get(position).getType();
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView dateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            dateHeader = itemView.findViewById(R.id.dateTextView);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSelected;
        RoundedImageView image;
        ConstraintLayout layoutImage;
        View viewBackground;

        public ImageViewHolder(@NonNull View itemView) {
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
            layoutImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });
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
