package com.hcmus.albumx;


import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.AllPhotos.FullScreenImageAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImagesGallery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import androidx.core.content.FileProvider;


public class ImageViewing extends Fragment {
    MainActivity main;
    Context context;

    Button back, more, edit, like, share;
    ViewPager2 viewPager;


    public static int GALLERY_RESULT = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    ArrayList<String> imageArray = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    int position = 0;

    public static ImageViewing newInstance(Bitmap bitmap, int pos) {

    public static ImageViewing newInstance(String path, int pos, List<String> imageArray) {

        ImageViewing fragment = new ImageViewing();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bitmap", bitmap);
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
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            imageArray.add(path);
            byte[] image = cursor.getBlob(3);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            bitmapArrayList.add(bitmap);
        }


        viewPager = view.findViewById(R.id.imageViewPager);
        viewPager.setAdapter(new FullScreenImageAdapter(context, bitmapArrayList));
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

        back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                onDetach();

                //notify();

            }

        });
        ImagesGallery.listOfImages(context);

        }); // back onClickListener


        edit = (Button) view.findViewById(R.id.buttonEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String path = imageArray.get(viewPager.getCurrentItem());
                Log.d("pathne", path);
//                Uri uri = getImageContentUri(context, path);

                String path = finalImageArray.get(viewPager.getCurrentItem());


                Uri selectedUri = Uri.fromFile(new File(path));
                EditImage editImage = new EditImage(getActivity());
                editImage.openEditor(selectedUri);
            }
        }); //edit onClickListener

        share = (Button) view.findViewById(R.id.buttonShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create Img from bitmap and share with text
                shareImageandText(bitmapArrayList.get(position));
            }
        });


                String path = finalImageArray.get(viewPager.getCurrentItem());

                //Create bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                //Create Img from bitmap and share with text
                shareImageandText(bitmap);
            }
        }); //share onClickListener
        more = (Button) view.findViewById(R.id.buttonMore);


        more = (Button) view.findViewById(R.id.buttonMore);
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
                                    wallpaperManager.setBitmap(bitmapArrayList.get(position));
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
        View view = fragment.getView();
        if (fragment != null) {
            fragment.showNavAndButton();
            fragment.refresh(view);
        }

    }

    public static Uri getImageContentUri2(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = context.getContentResolver();
                    Uri picCollection = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    ContentValues picDetail = new ContentValues();
                    picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
                    picDetail.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    picDetail.put(MediaStore.Images.Media.RELATIVE_PATH,"DCIM/" + UUID.randomUUID().toString());
                    picDetail.put(MediaStore.Images.Media.IS_PENDING,1);
                    Uri finaluri = resolver.insert(picCollection, picDetail);
                    picDetail.clear();
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(picCollection, picDetail, null, null);
                    return finaluri;
                }else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

            } else {
                return null;
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    }   //onDetach


}