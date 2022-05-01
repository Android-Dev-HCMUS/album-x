package com.hcmus.albumx.AllPhotos;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AlbumList.AlbumDatabase;
import com.hcmus.albumx.BuildConfig;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AllPhotos extends Fragment {
    public static String TAG = "ALl Photos";
    public static int ALBUM_ID = 0;

    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    ImageButton shareMultipleImages;
    ImageButton imageButton;
    RelativeLayout longClickBar;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private static final int PICK_IMAGE_CODE = 1;
    ArrayList<ImageInfo> imageInfoArrayList;
    ImageDatabase myDB;

    public static AllPhotos newInstance() {
        return new AllPhotos();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            myDB = ImageDatabase.getInstance(context);

            imageInfoArrayList = myDB.getAllImages();
//            imageInfoArrayList.forEach((imageInfo -> Log.e("AddImageInfo", imageInfo.getPath())));
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.all_photos_layout, null);
        super.onViewCreated(view, savedInstanceState);

        longClickBar = (RelativeLayout) view.findViewById(R.id.longClickBar);

        selectBtn = (Button) view.findViewById(R.id.buttonSelect);

        subMenuBtn = (Button) view.findViewById(R.id.buttonSubMenu);

        subMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.menu_change_theme:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                Toast.makeText(context, "Change theme", Toast.LENGTH_SHORT).show();
//                                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
//                                    context.setTheme(R.style.DarkTheme);
//                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                                    Toast.makeText(context, "Set dark", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    AppCompatDelegate.setDefault'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''NightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                                    context.setTheme(R.style.DarkTheme);
//                                    Toast.makeText(context, "Set light", Toast.LENGTH_SHORT).show();
//                                }
                                return true;
                            case R.id.secure_folder:
                                // Secure folder

                                return true;
                            default:
                                return false;
                        } //Switch
                    }
                }); //setOnMenuItemClickListener
                popup.inflate(R.menu.menu_image_submenu);
                popup.show();
            }
        }); //subMenu onClickListener

        imageButton = (ImageButton) view.findViewById(R.id.addBtn);
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

        galleryAdapter = new GalleryAdapter(context, imageInfoArrayList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewing.newInstance(imagePath, position, AllPhotos.ALBUM_ID),
                                "ImageViewing")
                        .addToBackStack("ImageViewingUI")
                        .commit();

                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onLongClick(String imagePath, int position, boolean state) {
                // Process handling here
                if (state) {
                    longClickBar.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                    longClickBar.setVisibility(View.VISIBLE);
                                    shareMultipleImages = (ImageButton) view.findViewById(R.id.shareMultipleImages);
                                    shareMultipleImages.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            List<ImageInfo> selectedImages = new ArrayList<>();

                                            selectedImages = getSelectedImages();
                                            //Create Img from bitmap and share with text

                                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                                            // adding text to share
                                            intent.putExtra(Intent.EXTRA_TEXT, "Sharing Images");

                                            // Add subject Here
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Album X share multi images");

                                            // setting type to image
                                            intent.setType("image/*");

                                            ArrayList<Uri> files = new ArrayList<Uri>();

                                            for(ImageInfo imageShow: selectedImages){
                                                String path = imageShow.path;
                                                Uri uri = getImageToShare(BitmapFactory.decodeFile(path), path);
                                                // putting uri of image to be shared
                                                files.add(uri);
                                            }
                                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                                            // calling startactivity() to share
                                            startActivity(Intent.createChooser(intent, "Share Via"));
                                        }
                                    });
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) { }

                                @Override
                                public void onAnimationCancel(Animator animator) { }

                                @Override
                                public void onAnimationRepeat(Animator animator) { }
                            });
                }
                else {
                    longClickBar.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) { }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    longClickBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) { }

                                @Override
                                public void onAnimationRepeat(Animator animator) { }
                            });
                }
                return true;
            }

            @Override
            public void onImageAction(Boolean isSelected) {

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
                    String imageName = path.substring(path.lastIndexOf("/") + 1);

                    if (ImageDatabase.getInstance(context).isImageExistsInApplication(imageName)) {
                        Toast.makeText(context, "Image " + imageName + " is exists in gallery :)",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String newImagePath = saveImageBitmap(BitmapFactory.decodeFile(path),
                                path.substring(path.lastIndexOf("/") + 1));  //tao bitmap tu uri
                        int id = myDB.insertImage(imageName, newImagePath);

                        Cursor cursor = AlbumDatabase.getInstance(context).getAlbums();
                        while (cursor.moveToNext()){
                            if(cursor.getString(1).equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                                AlbumDatabase.getInstance(context)
                                        .insertImageToAlbum(imageName, newImagePath, cursor.getInt(0));
                                break;
                            }
                        }
                        imageInfoArrayList.add(new ImageInfo(id, imageName, newImagePath, false));
                    }
                }
            } else {
                String path = getRealPathFromURI(data.getData());   //context
                String imageName = path.substring(path.lastIndexOf("/") + 1);

                if (ImageDatabase.getInstance(context).isImageExistsInApplication(imageName)) {
                    Toast.makeText(context, "Image " + imageName + " is exists in gallery ! :)",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String newImagePath = saveImageBitmap(BitmapFactory.decodeFile(path),  //decode từ uri
                            path.substring(path.lastIndexOf("/") + 1));
                    int id = myDB.insertImage(imageName, newImagePath);

                    Cursor cursor = AlbumDatabase.getInstance(context).getAlbums();
                    while (cursor.moveToNext()){
                        if(cursor.getString(1).equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                            AlbumDatabase.getInstance(context)
                                    .insertImageToAlbum(imageName, newImagePath, cursor.getInt(0));
                            break;
                        }
                    }
                    imageInfoArrayList.add(new ImageInfo(id, imageName, newImagePath, false));
                }
            }

            galleryAdapter.notifyDataSetChanged();
        }
    }

    public String saveImageBitmap(Bitmap image_bitmap, String image_name) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
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

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList){
        imageInfoArrayList.clear();
        imageInfoArrayList.addAll(newList);
        galleryAdapter.notifyDataSetChanged();
    }

    public List<ImageInfo> getSelectedImages() {
        List<ImageInfo> selectedImages = new ArrayList<>();
        for(ImageInfo imageShow: imageInfoArrayList){
            if(imageShow.isSelected){
                selectedImages.add(imageShow);
            }
        }

        return selectedImages;
    }


    // Retrieving the url to share
    private Uri getImageToShare(Bitmap bitmap, String path) {
        File imagefolder = new File(getActivity().getCacheDir() + path, "images");
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


}
