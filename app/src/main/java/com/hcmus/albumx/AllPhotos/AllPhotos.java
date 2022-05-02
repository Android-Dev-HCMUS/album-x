package com.hcmus.albumx.AllPhotos;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AlbumList.AlbumDatabase;
import com.hcmus.albumx.BuildConfig;
import com.hcmus.albumx.EditedView.ImagesEditGallery;
import com.hcmus.albumx.CloudStorage.CloudStorage;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.hcmus.albumx.SecureFolder.SecureFolder;
import com.hcmus.albumx.SecureFolder.SecureFolderManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AllPhotos extends Fragment {
    public static String TAG = "ALl Photos";
    public static int ALBUM_ID = 0;

    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    ImageButton imageButton;

    RelativeLayout longClickBar;
    ImageButton addToAlbum;
    ImageButton shareMultipleImages;
    ImageButton deleteMultipleImages;
    ImageButton closeToolbar;
    SharedPreferences sp;

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;

    private static final int PICK_IMAGE_CODE = 1;
    ArrayList<ImageInfo> imageInfoArrayList;
    List<ListItem> listItems;
    LinkedHashMap<String, List<ImageInfo>> listImageGroupByDate;
    ImageDatabase myDB;

    List<Uri> listEditImages;

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
            handleEditImages();
            imageInfoArrayList = myDB.getAllImages();
//            imageInfoArrayList.forEach((imageInfo -> Log.e("AddImageInfo", imageInfo.getPath())));
            listItems = new ArrayList<>();
            listImageGroupByDate = new LinkedHashMap<>();

            prepareData();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contextView = (View) inflater.inflate(R.layout.all_photos_layout, null);
        super.onViewCreated(contextView, savedInstanceState);

        subMenuBtn = (Button) contextView.findViewById(R.id.buttonSubMenu);
        subMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.secure_folder:
                                openSecureFolder();
                                return true;
                            case R.id.change_theme_blue:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                sp = getContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("isNightMode", true);
                                editor.apply();
                                Toast.makeText(context, "Theme changed to black", Toast.LENGTH_SHORT).show();
                                reset();
                                return true;
                            case R.id.change_theme_light:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                sp = getContext().getSharedPreferences("MyPref", 0);
                                SharedPreferences.Editor editor2 = sp.edit();
                                editor2.putBoolean("isNightMode", false);
                                editor2.apply();
                                Toast.makeText(context, "Theme changed to white", Toast.LENGTH_SHORT).show();
                                reset();
                                return true;
                            default:
                                return false;
                        } //Switch
                    }

                    private void reset() {
                        Intent intent = new Intent (getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }); //setOnMenuItemClickListener
                popup.inflate(R.menu.menu_image_submenu);
                popup.show();
            }
        }); //subMenu onClickListener

        imageButton = (ImageButton) contextView.findViewById(R.id.addBtn);
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

        recyclerView = contextView.findViewById(R.id.recycleview_gallery_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        galleryAdapter = new GalleryAdapter(context, imageInfoArrayList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                int p = position;
                for(int i=0; i<imageInfoArrayList.size(); i++){
                    if(imageInfoArrayList.get(i).path.equals(imagePath)){
                        p = i;
                    }
                }
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewing.newInstance(imagePath,imageInfoArrayList,  p, AllPhotos.ALBUM_ID),
                                "ImageViewing")
                        .addToBackStack("ImageViewingUI")
                        .commit();

                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onLongClick(String imagePath, int position, boolean state) {
                return false;
            }

            @Override
            public void onImageAction(Boolean isSelected) { }
        });

        galleryAdapter.setData(listItems);

        // Multiple image toolbar
        longClickBar = (RelativeLayout) contextView.findViewById(R.id.longClickBar);
        selectBtn = (Button) contextView.findViewById(R.id.buttonSelect);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                longClickBar.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                // Show toolbar
                                longClickBar.setVisibility(View.VISIBLE);
                                galleryAdapter.setMultipleSelectState(true);

                                // Set view and its listeners
                                addToAlbum = (ImageButton) contextView.findViewById(R.id.addToAlbum);
                                // -> set listener

                                shareMultipleImages = (ImageButton) contextView.findViewById(R.id.shareMultipleImages);
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

                                deleteMultipleImages = (ImageButton) contextView.findViewById(R.id.deleteMultipleImages);
                                // -> set listener

                                closeToolbar = (ImageButton) contextView.findViewById(R.id.closeToolbar);
                                closeToolbar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        longClickBar.animate()
                                                .alpha(0f)
                                                .setDuration(500)
                                                .setListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animator) { }

                                                    @Override
                                                    public void onAnimationEnd(Animator animator) {
                                                        longClickBar.setVisibility(View.GONE);
                                                        galleryAdapter.setMultipleSelectState(false);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animator) { }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animator) { }
                                                });
                                        getFragmentManager().beginTransaction()
                                                .replace(R.id.frameFragment, new AllPhotos(), TAG)
                                                .commit();
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
        });

        recyclerView = contextView.findViewById(R.id.recycleview_gallery_images);
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

        return contextView;
    }

    public void openSecureFolder() {
        Intent intent = new Intent(getContext(), SecureFolderManager.class);
        getContext().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Pick multiple image
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        handleNewImagePick(data.getClipData().getItemAt(i).getUri());
                    }
                }
                else { //Pick one image
                    handleNewImagePick(data.getData());
                }
            }
        }

        galleryAdapter.notifyDataSetChanged();
    }

    public void handleEditImages(){
        listEditImages = ImagesEditGallery.listOfEditImages(context);
        if (listEditImages == null){
            return;
        }else{
            for (int i = 0; i < listEditImages.size(); i++) {
                handleEditImagePick(listEditImages.get(i));
            }

        }
    }

    private void handleEditImagePick(Uri contentUri){
        ImageInfo image = getInfoFromURI(contentUri);

        if (ImageDatabase.getInstance(context).isImageExistsInApplication(image.name)) {
            Log.d("pass", "1");
        }
        else {
            image.path = saveImageBitmap(contentUri, image.name);
            myDB.insertImage(image.name, image.path, image.createdDate);

            Cursor cursor = AlbumDatabase.getInstance(context).getAlbums();
            while (cursor.moveToNext()) {
                if (cursor.getString(1).equals(AlbumDatabase.albumSet.ALBUM_EDITOR)) {
                    AlbumDatabase.getInstance(context)
                            .insertImageToAlbum(image.name, image.path, cursor.getInt(0));
                    break;
                }
            }
        }
    }

    private void handleNewImagePick(Uri contentUri){
        ImageInfo image = getInfoFromURI(contentUri);

        if (ImageDatabase.getInstance(context).isImageExistsInApplication(image.name)) {
            Toast.makeText(context, "Image " + image.name + " is exists in gallery ! :)",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            image.path = saveImageBitmap(contentUri, image.name);
            myDB.insertImage(image.name, image.path, image.createdDate);

            Cursor cursor = AlbumDatabase.getInstance(context).getAlbums();
            while (cursor.moveToNext()) {
                if (cursor.getString(1).equals(AlbumDatabase.albumSet.ALBUM_RECENT)) {
                    AlbumDatabase.getInstance(context)
                            .insertImageToAlbum(image.name, image.path, cursor.getInt(0));
                    break;
                }
            }

            imageInfoArrayList.add(image);
        }

        prepareData();
    }

    private Bitmap createBitMapFromUri(Uri uri){
        Bitmap b = null;
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            b = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;
    }

    public String saveImageBitmap(Uri uri, String image_name) {
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
            createBitMapFromUri(uri).compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private ImageInfo getInfoFromURI(Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null,null,null,null);
        ImageInfo info = null;

        if(cursor != null){
            cursor.moveToFirst();
            @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));

            int lastModifiedIndex = cursor.getColumnIndex("last_modified");
            long last_mod = -1;
            String lastModified = null;
            if (!cursor.isNull(lastModifiedIndex)) {
                last_mod = cursor.getLong(lastModifiedIndex);
            }
            if (last_mod > -1) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                lastModified = simpleDateFormat.format(new Date(last_mod));
            }

            @SuppressLint("Range") String mimeType = cursor.getString(cursor.getColumnIndex("mime_type"));

            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            int byte_size = -1;
            String size = "";
            if (!cursor.isNull(sizeIndex)) {
                byte_size = cursor.getInt(sizeIndex);
            }
            if (byte_size > -1) {
                size = getFileSize((long) byte_size);
            }

            info = new ImageInfo(displayName, lastModified, mimeType, size);
            cursor.close();
        }

        return info;
    }

    public String getFileSize(long bytes) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unit = 0;
        for (int x = 0; x < 4; x++) {
            if (bytes > Math.pow(2, 10*x)) {
                unit = x;
            }
        }
        double result = bytes/Math.pow(2, 10*unit);
        return String.format(Locale.US, "%.2f", result) + units[unit];
    }

    private void prepareData(){
        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        listImageGroupByDate = new LinkedHashMap<>();
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

                return -(d1.compareTo(d2));
            }
        });

        for (ImageInfo item : imageInfoArrayList){
            if(item != null){
                try {
                    String d = formatterOut.format(Objects.requireNonNull(df.parse(item.createdDate)));

                    if(listImageGroupByDate.containsKey(d)){
                        Objects.requireNonNull(listImageGroupByDate.get(d)).add(item);
                    } else {
                        List<ImageInfo> list = new ArrayList<>();
                        list.add(item);
                        listImageGroupByDate.put(d, list);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        if(!listItems.isEmpty()){
            listItems.clear();
        }
        for(String date : listImageGroupByDate.keySet()){
            listItems.add(new DateItem(date));
            for(ImageInfo item : Objects.requireNonNull(listImageGroupByDate.get(date))){
                listItems.add(new GroupImageItem(item));
            }
        }
    }

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList){
        imageInfoArrayList = newList;
        listItems = new ArrayList<>();
        listImageGroupByDate = new LinkedHashMap<>();
        prepareData();
        galleryAdapter.setData(listItems);
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
