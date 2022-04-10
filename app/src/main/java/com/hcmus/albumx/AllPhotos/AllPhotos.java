package com.hcmus.albumx.AllPhotos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AllPhotos extends Fragment {
    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    BottomNavigationView bottomNavigationView;
    ImageButton imageButton;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private static final int PICK_IMAGE_CODE = 1;
    List<String> listImagePath;
    ImageDatabase myDB;

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
            listImagePath = new ArrayList<>();
            if (main != null) {
                bottomNavigationView = main.findViewById(R.id.bottomNavigation);
                imageButton = main.findViewById(R.id.addBtn);
            }

            Cursor cursor = myDB.getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME);
            while (cursor.moveToNext()){
                String path = cursor.getString(2);
                listImagePath.add(path);
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.photos_recyclerview, null);
        super.onViewCreated(view, savedInstanceState);

        selectBtn = (Button) view.findViewById(R.id.buttonSelect);
        subMenuBtn = (Button) view.findViewById(R.id.buttonSubMenu);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Choose images"), PICK_IMAGE_CODE);
            }
        });

        recyclerView = view.findViewById(R.id.recycleview_gallery_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        galleryAdapter = new GalleryAdapter(context, listImagePath, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent,
                                ImageViewing.newInstance(imagePath, position),
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

        if(requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {

            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    String path = getRealPathFromURI(data.getClipData().getItemAt(i).getUri());
                    Log.e("path clip", data.getClipData().getItemAt(i).getUri().toString());
                    String imageName = path.substring(path.lastIndexOf("/") + 1);
                    Cursor cursor = myDB.getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME +
                            " WHERE " + ImageDatabase.FIELD_NAME + " = '" + imageName + "';");

                    if (cursor.moveToFirst()) {
                        Toast.makeText(context, "Image " + imageName + " is exists in gallery ! :)", Toast.LENGTH_SHORT).show();
                    } else {
                        String newImagePath = saveImageBitmap(BitmapFactory.decodeFile(path), path.substring(path.lastIndexOf("/") + 1));
                        myDB.insertImageData(imageName, newImagePath);

                        listImagePath.add(newImagePath);
                    }
                }
            } else {
                Log.e("path", data.getData().toString());
                String path = getRealPathFromURI(data.getData());

                String imageName = path.substring(path.lastIndexOf("/") + 1);
                Cursor cursor = myDB.getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME +
                        " WHERE " + ImageDatabase.FIELD_NAME + " = '" + imageName + "';");

                if (cursor.moveToFirst()) {
                    Toast.makeText(context, "Image " + imageName + " is exists in gallery ! :)", Toast.LENGTH_SHORT).show();
                } else {
                    String newImagePath = saveImageBitmap(BitmapFactory.decodeFile(path), path.substring(path.lastIndexOf("/") + 1));
                    myDB.insertImageData(imageName, newImagePath);

                    listImagePath.add(newImagePath);
                }
            }

            galleryAdapter.notifyDataSetChanged();
        }
    }

    public boolean isStoragePermissionGranted() {
        boolean checkWritePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (checkWritePermission) {
            return  true;
        } else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    public String saveImageBitmap(Bitmap image_bitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        if (isStoragePermissionGranted()) { // check or ask permission
            File myDir = new File(root, "/saved_images");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File file = new File(myDir, image_name);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile(); // if file already exists will do nothing
                FileOutputStream out = new FileOutputStream(file);
                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getActivity().getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();

        return filePath;
    }

    public void notifyChangedListImageOnDelete(List<String> newList){
        listImagePath.clear();
        listImagePath.addAll(newList);
        galleryAdapter.notifyDataSetChanged();
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
