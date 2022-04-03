package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AllPhotos extends Fragment {
    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    BottomNavigationView bottomNavigationView;
    ImageButton imageButton;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    ImageDatabase myDB;
    ArrayList<Bitmap> bitmapList;

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
            myDB = ImageDatabase.getInstance(context);
            bitmapList = new ArrayList<>();
            if (main != null) {
                bottomNavigationView = main.findViewById(R.id.bottomNavigation);
                imageButton = main.findViewById(R.id.addBtn);
            }

            Cursor cursor = myDB.getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME);
            while (cursor.moveToNext()){
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                byte[] image = cursor.getBlob(3);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                bitmapList.add(bitmap);
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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent pickImage = new Intent();
//                pickImage.setType("image/*");
//                pickImage.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(pickImage, "Chọn ảnh"), PICK_IMAGE);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 1);
            }
        });

        recyclerView = view.findViewById(R.id.recycleview_gallery_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        galleryAdapter = new GalleryAdapter(context, bitmapList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(Bitmap bitmap, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent,
                                ImageViewing.newInstance(bitmap, position),
                                "ImageViewing")
                        .addToBackStack("ImageViewingUI")
                        .commit();

                hideNavAndButton();
               }
        });
        recyclerView.setAdapter(galleryAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){
            imageUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursors = getActivity().getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            cursors.moveToFirst();

            int columnIndex = cursors.getColumnIndex(filePathColumn[0]);
            String path = cursors.getString(columnIndex);

            try {
                String imageName = path.substring(path.lastIndexOf("/")+1);
                Cursor cursor = myDB.getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME +
                        " WHERE " + ImageDatabase.FIELD_NAME +" = '" + imageName +"';");

                if(cursor.moveToFirst()){
                    Toast.makeText(context, "Image is exists in gallery ! :)", Toast.LENGTH_SHORT).show();
                }else{
                    InputStream input = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);

                    myDB.insertImageData(imageName, path, byteArray.toByteArray());

                    bitmapList.add(bitmap);
                    galleryAdapter.notifyDataSetChanged();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
