package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.albumx.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    protected PhotoListener photoListener;

    public static final int TYPE_DATE = 0;
    public static final int TYPE_IMAGE = 1;

    private List<ImageInfo> imageInfoArrayList;

    private boolean isMultipleSelectState = false;
    public void setMultipleSelectState(boolean state) {
        this.isMultipleSelectState = state;
        notifyDataSetChanged();
    }

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
            if(groupImageItem.getImageInfo().isSelected){
                viewHolder.imageSelected.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageSelected.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListItem item = listItem.get(pos);
                    GroupImageItem image = (GroupImageItem) item;

                    if(item.getType() == ListItem.TYPE_IMAGE){
                        if(!isMultipleSelectState){
                            photoListener.onPhotoClick(image.getImageInfo().path, pos);
                            // Remove all selected item
                        }else {
                            viewHolder.bindImageShow(listItem.get(pos));
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
        View viewBackground;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSelected = itemView.findViewById(R.id.imageSelected);
            image = itemView.findViewById(R.id.image);
            viewBackground = itemView.findViewById(R.id.viewBackground);
        }

        public void bindImageShow(ListItem item){
            GroupImageItem image = (GroupImageItem) item;

            if(image.getImageInfo().isSelected){
                viewBackground.setBackgroundResource(R.drawable.image_selected_background);
                imageSelected.setVisibility(View.GONE);
                image.getImageInfo().isSelected = false;
            } else {
                viewBackground.setBackgroundResource(R.drawable.image_background);
                imageSelected.setVisibility(View.VISIBLE);
                image.getImageInfo().isSelected = true;
            }
        }

    }
    public interface PhotoListener {
        void onPhotoClick(String imagePath, int position);
    }
}
