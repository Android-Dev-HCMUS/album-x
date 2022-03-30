package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.List;

public class AllPhotos extends Fragment {
    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    GridView gridView;
    BottomNavigationView bottomNavigationView;
    ImageButton imageButton;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    TextView gallery_number;

    public int[] imageArray = {
            R.drawable.stock_1, R.drawable.stock_2, R.drawable.stock_3, R.drawable.stock_4 ,
            R.drawable.stock_5, R.drawable.stock_6, R.drawable.stock_7, R.drawable.stock_8,
            R.drawable.stock_9, R.drawable.stock_10, R.drawable.stock_11, R.drawable.stock_12,
            R.drawable.stock_13, R.drawable.stock_14, R.drawable.stock_15, R.drawable.stock_16
    };

    public static AllPhotos newInstance(String strArg1) {
        AllPhotos fragment = new AllPhotos();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            if (main != null) {
                bottomNavigationView = main.findViewById(R.id.bottomNavigation);
                imageButton = main.findViewById(R.id.addBtn);
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.all_photos_recyclerview, null);
        super.onViewCreated(view, savedInstanceState);

        selectBtn = (Button) view.findViewById(R.id.buttonSelect);
        subMenuBtn = (Button) view.findViewById(R.id.buttonSubMenu);

        recyclerView = view.findViewById(R.id.recycleview_gallery_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        images = ImagesGallery.listOfImages(context);
        galleryAdapter = new GalleryAdapter(context, images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, ImageViewing.newInstance(path, position, images), "ImageViewing")
                        .addToBackStack("ImageViewingUI")
                        .commit();

                hideNavAndButton();
                //DO Sth with photo
                Toast.makeText(main, "" +path, Toast.LENGTH_SHORT).show();  // đoạn này khi click vào 1 ảnh
            }
        });
        recyclerView.setAdapter(galleryAdapter);

//        gridView = (GridView) view.findViewById(R.id.grid_view);
//        gridView.setAdapter(new GridImageAdapter(context, imageArray));
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                main.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.frameContent, ImageViewing.newInstance(i, imageArray), "ImageViewing")
//                        .addToBackStack("ImageViewingUI")
//                        .commit();
//
//                hideNavAndButton();
//            }
//        });

        return view;
    }

    public void showNavAndButton(){
        bottomNavigationView.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
    }
    public void hideNavAndButton(){
        bottomNavigationView.setVisibility(View.INVISIBLE);
        imageButton.setVisibility(View.INVISIBLE);
    }

}
