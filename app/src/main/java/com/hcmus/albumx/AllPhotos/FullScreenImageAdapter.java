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

import java.util.ArrayList;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {
    Context context;
    ArrayList<String> imageArr;

    public FullScreenImageAdapter(Context context, ArrayList<String> imageArr) {
        this.context = context;
        this.imageArr = imageArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.image_viewing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image = imageArr.get(position);

        Glide.with(context).load(image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewFull);
        }
    }

//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        ImageView imageView = new ImageView(context);
//        int padding = context.getResources().getDimensionPixelSize(
//                 R.dimen.default_padding_side);
//        imageView.setPadding(padding, padding, padding, padding);
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//
//        File imgFile = new  File(imageArr.get(position));
//        Log.e("image", imgFile.getAbsolutePath());
//        if(imgFile.exists()){
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imageView.setImageBitmap(myBitmap);
//        }
//        container.addView(imageView, 0);
//
//        return imageView;
//    }

}
