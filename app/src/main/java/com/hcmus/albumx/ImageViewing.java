package com.hcmus.albumx;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.hcmus.albumx.AlbumList.AlbumDatabase;
import com.hcmus.albumx.AlbumList.AlbumInfo;
import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.AllPhotos.FullScreenImageAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImagesGallery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ImageViewing extends Fragment {
    private MainActivity main;
    private Context context;

    private ViewPager2 viewPager;


    public static int GALLERY_RESULT = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private ArrayList<String> imageArray = new ArrayList<>();
    private int position = 0;

    ArrayList<AlbumInfo> albumList;

    public static ImageViewing newInstance(String imagePath, int pos) {
        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", imagePath);
        bundle.putInt("position", pos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();

            if(albumList == null){
                albumList = new ArrayList<>();

                Cursor cursor = AlbumDatabase.getInstance(context)
                        .getAlbums("SELECT * FROM " + AlbumDatabase.albumSet.TABLE_NAME);
                while (cursor.moveToNext()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    int type = cursor.getInt(2);

                    if(name.equals("Recent")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_recent));
                    } else if (name.equals("Favorite")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
                    } else if (name.equals("Editor")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_edit));
                    } else {
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_photo));
                    }
                }
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.image_viewing, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }

        Cursor cursor = ImageDatabase.getInstance(context).getImages("SELECT * FROM " + ImageDatabase.TABLE_NAME);
        while (cursor.moveToNext()){
            String path = cursor.getString(2);
            imageArray.add(path);
        }

        FullScreenImageAdapter adapter = new FullScreenImageAdapter(context, imageArray);

        viewPager = view.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, false);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);

                page.setScaleY(0.8f + v * 0.2f);
            }
        });
        viewPager.setPageTransformer(transformer);

        Button back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                onDetach();
                //notify();
            }
        });
        ImagesGallery.listOfImages(context);

        Button like = (Button) view.findViewById(R.id.buttonLike);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumDatabase.getInstance(context)
                        .insertImageToAlbum("", imageArray.get(position), albumList.get(1).id);

                Toast.makeText(context, "Added to Favorite", Toast.LENGTH_SHORT).show();
            }
        });

        Button edit = (Button) view.findViewById(R.id.buttonEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = imageArray.get(position);
                Log.d("pathne", path);
//                Uri uri = getImageContentUri(context, path);

                Uri selectedUri = Uri.fromFile(new File(path));
                EditImage editImage = new EditImage(getActivity());
                editImage.openEditor(selectedUri);
            }
        }); //edit onClickListener

        Button share = (Button) view.findViewById(R.id.buttonShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Img from bitmap and share with text
                shareImageandText(BitmapFactory.decodeFile(imageArray.get(position)));
            }
        });

        Button delete = (Button) view.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDatabase db = ImageDatabase.getInstance(getContext());
                db.deleteImageData(imageArray.get(position));
                imageArray.remove(position);

                if(position == imageArray.size()){
                    position--;
                } else{
                    position++;
                }

                if(imageArray.size() > 0){
                    adapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(position, false);
                } else {
                    main.getSupportFragmentManager().popBackStack();
                    onDetach();
                }
            }
        });

        Button more = (Button) view.findViewById(R.id.buttonMore);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.menu_wallpaper:
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(v.getContext());
                                try {
                                    viewPager.getDrawableState();
                                    // set the wallpaper by calling the setResource function
                                    wallpaperManager.setBitmap(BitmapFactory.decodeFile(imageArray.get(position)));
                                    Toast.makeText(context, "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    // here the errors can be logged instead of printStackTrace
                                    e.printStackTrace();
                                }
                                return true;
                            default:
                                return false;
                        } //Switch
                    }
                }); //setOnMenuItemClickListener
                popup.inflate(R.menu.menu_image_more);
                popup.show();
            }
        }); //more onClickListener

        return view;
    }   //View
    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/*");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }   //ShareImageandText

    // Retrieving the url to share
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getActivity().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.jpeg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);

        } catch (Exception e) {
            Log.d("err", e.getMessage());
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }   //getImageToShare

    @Override
    public void onDetach() {
        super.onDetach();
        AllPhotos fragment = (AllPhotos) main.getSupportFragmentManager()
                .findFragmentByTag("AllPhotos");

        if (fragment != null) {
            fragment.showNavAndButton();
            fragment.notifyChangedListImageOnDelete(imageArray);
        }

    }
}