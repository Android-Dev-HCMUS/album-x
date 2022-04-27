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

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    protected PhotoListener photoListener;

    public static final int TYPE_DATE = 0;
    public static final int TYPE_IMAGE = 1;

    private List<ListItem> listItem;
    public void setData(List<ListItem> listItem){
        this.listItem = listItem;
        notifyDataSetChanged();
    }

    public GalleryAdapter(Context context, PhotoListener photoListener) {
        this.context = context;
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
            return new ViewHolder(view);
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
        } else if(TYPE_IMAGE == holder.getItemViewType()){
            GroupImageItem groupImageItem = (GroupImageItem) item;
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(context).load(groupImageItem.getImageInfo().path).into(viewHolder.image);

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //holder.bindImageShow(imageInfoArrayList.get(pos));
//                photoListener.onLongClick(imageInfoArrayList.get(pos).path, pos);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //photoListener.onPhotoClick(imageInfoArrayList.get(pos).path, pos);
            }
        });
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

//    public List<ImageInfo> getSelectedImages() {
//        List<ImageInfo> selectedImages = new ArrayList<>();
//        for(ImageInfo imageShow: imageInfoArrayList){
//            if(imageShow.isSelected){
//                selectedImages.add(imageShow);
//            }
//        }
//
//        return selectedImages;
//    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView dateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            dateHeader = itemView.findViewById(R.id.dateTextView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.draweeView);

        }

//        void  bindImageShow(final ImageInfo imageShow){
//            if(imageShow.isSelected){
//                viewBackground.setBackgroundResource(R.drawable.image_selected_background);
//                imageSelected.setVisibility(View.VISIBLE);
//            } else {
//                viewBackground.setBackgroundResource(R.drawable.image_background);
//                imageSelected.setVisibility(View.GONE);
//            }
//
//            layoutImage.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    if(imageShow.isSelected){
//                        viewBackground.setBackgroundResource(R.drawable.image_background);
//                        imageSelected.setVisibility(View.GONE);
//                        imageShow.isSelected = false;
//                        if(getSelectedImages().size() == 0){
//                            photoListener.onImageAction(false);
//                        }
//
//                    } else {
//                        viewBackground.setBackgroundResource(R.drawable.image_selected_background);
//                        imageSelected.setVisibility(View.VISIBLE);
//                        imageShow.isSelected =true;
//                        photoListener.onImageAction(true);
//                    }
//                    return true;
//                }
//            });
//        }

    }
    public interface PhotoListener {

        void onPhotoClick(String imagePath, int position);
        boolean onLongClick(String imagePath, int position);
        void onImageAction(Boolean isSelected);
    }
}
