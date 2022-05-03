package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.hcmus.albumx.EditImage;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.hcmus.albumx.SelectMultiple;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlbumPhotos extends Fragment {
    public static final String TAG = "Album Photos";

    private static final String ALBUM_ID_ARG = "albumID";
    private static final String ALBUM_NAME_ARG = "albumName";

    private MainActivity main;
    private Context context;

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;

    private ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<>();
    List<ListItem> listItems;
    private ImageDatabase myDB;

    private int albumID;
    ImageButton cameraBtn;

    public static AlbumPhotos newInstance(int albumID, String albumName) {
        AlbumPhotos fragment = new AlbumPhotos();
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_ID_ARG, albumID);
        bundle.putString(ALBUM_NAME_ARG, albumName);
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
                albumID = getArguments().getInt(ALBUM_ID_ARG);
            }

            imageInfoArrayList = AlbumDatabase.getInstance(context).getImagesOf(albumID);

            listItems = new ArrayList<>();
            prepareData();
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
            albumName.setText(getArguments().getString(ALBUM_NAME_ARG));
        }
        if(getArguments().getString(ALBUM_NAME_ARG).trim().equals(AlbumDatabase.albumSet.ALBUM_EDITOR.trim()) ){
            cameraBtn = (ImageButton) view.findViewById(R.id.cameraBtn);
            cameraBtn.setVisibility(View.VISIBLE);
            cameraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditImage editImage = new EditImage(getActivity());

                    editImage.openSystemCameraToTakeAnImage();
                }
            });
        }

        Button back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                onDetach();
            }
        });

        galleryAdapter = new GalleryAdapter(context, imageInfoArrayList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewing.newInstance(imagePath, imageInfoArrayList, position, albumID),
                                ImageViewing.TAG)
                        .addToBackStack("ImageViewingUI")
                        .commit();
                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }
        });
        galleryAdapter.setData(listItems);

        SelectMultiple temp = new SelectMultiple();
        temp.selectMultiImages(view, context, galleryAdapter, imageInfoArrayList, 0);

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
