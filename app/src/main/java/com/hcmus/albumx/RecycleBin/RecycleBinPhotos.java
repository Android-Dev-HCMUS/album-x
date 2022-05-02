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
import com.hcmus.albumx.AllPhotos.GroupImageItem;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.AllPhotos.ListItem;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecycleBinPhotos extends Fragment {
    public static final String TAG = "RecycleBinPhotos";

    MainActivity main;
    Context context;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private ArrayList<ImageInfo> imageInfoArrayList;
    List<ListItem> listItems;
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
            listItems = new ArrayList<>();
            prepareData();
        }catch (IllegalStateException ignored){
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.recycle_bin_photos_layout, null);
        super.onViewCreated(view, savedInstanceState);

        galleryAdapter = new GalleryAdapter(context, imageInfoArrayList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewingRecycleBin.newInstance(imagePath, imageInfoArrayList, position),
                                ImageViewingRecycleBin.TAG)
                        .addToBackStack("ImageViewingRecycleBinUI")
                        .commit();
                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }
        });
        galleryAdapter.setData(listItems);

        recyclerView = view.findViewById(R.id.recyclerview_image);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (galleryAdapter.getItemViewType(position)){
                    case ListItem.TYPE_DATE:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(galleryAdapter);

        return view;
    }

    private void prepareData(){
        imageInfoArrayList.sort(new Comparator<ImageInfo>() {
            @Override
            public int compare(ImageInfo o1, ImageInfo o2) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = df.parse(o1.createdDate);
                    d2 = df.parse(o2.createdDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return d1.compareTo(d2);
            }
        });

        for(ImageInfo item : imageInfoArrayList){
            listItems.add(new GroupImageItem(item));
        }
    }

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList){
        imageInfoArrayList = newList;
        listItems = new ArrayList<>();
        prepareData();
        galleryAdapter.setData(listItems);
    }
}
