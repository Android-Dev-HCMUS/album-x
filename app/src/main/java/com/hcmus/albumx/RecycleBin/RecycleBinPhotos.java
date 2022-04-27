package com.hcmus.albumx.RecycleBin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AllPhotos.GalleryAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;

public class RecycleBinPhotos extends Fragment {
    public static final String TAG = "RecycleBinPhotos";

    MainActivity main;
    Context context;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private ArrayList<ImageInfo> imageInfoArrayList;
    ImageDatabase myDB;

    public static RecycleBinPhotos newInstance(){
        return new RecycleBinPhotos();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            main = (MainActivity) getActivity();
            context = getContext();
            myDB = ImageDatabase.getInstance(context);
            imageInfoArrayList = myDB.getImagesInRecycleBin();
        }catch (IllegalStateException ignored){
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.recycle_bin_photos_layout, null);
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        galleryAdapter = new GalleryAdapter(context, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewingRecycleBin.newInstance(imagePath, position),
                                ImageViewingRecycleBin.TAG)
                        .addToBackStack("ImageViewingRecycleBinUI")
                        .commit();
                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onLongClick(String imagePath, int position) {
                return false;
            }

            @Override
            public void onImageAction(Boolean isSelected) {

            }
        });
        recyclerView.setAdapter(galleryAdapter);

        return view;
    }

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList){
        imageInfoArrayList.clear();
        imageInfoArrayList.addAll(newList);
        galleryAdapter.notifyDataSetChanged();
    }
}
