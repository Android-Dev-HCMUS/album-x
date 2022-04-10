package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AllPhotos.GalleryAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.List;

public class AlbumPhotos extends Fragment {
    public static String TAG = "Album Photos";

    MainActivity main;
    Context context;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    List<String> listImagePath;
    ImageDatabase myDB;

    int albumID;

    public static AlbumPhotos newInstance(int albumID, String albumName) {
        AlbumPhotos fragment = new AlbumPhotos();
        Bundle bundle = new Bundle();
        bundle.putInt("albumID", albumID);
        bundle.putString("albumName", albumName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            myDB = ImageDatabase.getInstance(context);

            if(getArguments() != null){
                albumID = getArguments().getInt("albumID");
            }
            Log.e("al", String.valueOf(albumID));
            listImagePath = AlbumDatabase.getInstance(context).getImagesOf(albumID);
        } catch (IllegalStateException ignored) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.album_photos_layout, null);
        super.onViewCreated(view, savedInstanceState);

        TextView albumName = view.findViewById(R.id.album_name);
        if(getArguments() != null){
            albumName.setText(getArguments().getString("albumName"));
        }

        recyclerView = view.findViewById(R.id.recyclerview_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        galleryAdapter = new GalleryAdapter(context, listImagePath, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewing.newInstance(imagePath, position, albumID),
                                ImageViewing.TAG)
                        .addToBackStack("ImageViewingUI")
                        .commit();
                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }
        });
        recyclerView.setAdapter(galleryAdapter);

        Button back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                onDetach();
            }
        });
        return view;
    }

    public void notifyChangedListImageOnDelete(List<String> newList){
        listImagePath.clear();
        listImagePath.addAll(newList);
        galleryAdapter.notifyDataSetChanged();
    }
}
