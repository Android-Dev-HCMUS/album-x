package com.hcmus.albumx;


import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.hcmus.albumx.AlbumList.AlbumDatabase;
import com.hcmus.albumx.AlbumList.AlbumInfo;
import com.hcmus.albumx.AlbumList.AlbumList;
import com.hcmus.albumx.AllPhotos.DateItem;
import com.hcmus.albumx.AllPhotos.GalleryAdapter;
import com.hcmus.albumx.AllPhotos.GroupImageItem;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.AllPhotos.ListItem;

import java.io.File;
import java.io.FileOutputStream;
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

public class SelectMultiple extends Fragment{

    RelativeLayout longClickBar;
    Button selectBtn;
    ImageButton addToAlbum;
    ImageButton shareMultipleImages;
    ImageButton deleteMultipleImages;
    ImageButton closeToolbar;


    public void selectMultiImages (View contextView, Context context, GalleryAdapter galleryAdapter, ArrayList<ImageInfo> imageInfoArrayList, Integer type){
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
                                addToAlbum.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        List<ImageInfo> finalSelectedImages = new ArrayList<>();

                                        finalSelectedImages = getSelectedImages(imageInfoArrayList);
                                        if(finalSelectedImages.size() != 0){
                                            ArrayList<AlbumInfo> album;
                                            album = AlbumList.newInstance().infoAddAlbums(context);

                                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                                            builderSingle.setIcon(R.drawable.ic_album);
                                            builderSingle.setTitle("Select One Album:");

                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

                                            for(AlbumInfo itemAlbum: album){
                                                String name = itemAlbum.name;
                                                arrayAdapter.add(name);
                                            }

                                            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            List<ImageInfo> selectedImages = finalSelectedImages;
                                            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String strName = arrayAdapter.getItem(which);
                                                    handleAddImagesToAlbum(strName, album, which, context, imageInfoArrayList, selectedImages);
                                                    dialog.dismiss();
                                                    turnOffMultiSelectionMode(galleryAdapter, imageInfoArrayList);
                                                }
                                            });
                                            builderSingle.show();
                                        } else {
                                            notificationWhenNothingIsSelect(context);
                                        }
                                    }
                                });
                                // -> set listener

                                shareMultipleImages = (ImageButton) contextView.findViewById(R.id.shareMultipleImages);
                                shareMultipleImages.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        List<ImageInfo> finalSelectedImages = new ArrayList<>();

                                        finalSelectedImages = getSelectedImages(imageInfoArrayList);

                                        if(finalSelectedImages.size() != 0){
                                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                                            // adding text to share
                                            intent.putExtra(Intent.EXTRA_TEXT, "Sharing Images");

                                            // Add subject Here
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Album X share multi images");

                                            // setting type to image
                                            intent.setType("image/*");

                                            ArrayList<Uri> files = new ArrayList<Uri>();

                                            for(ImageInfo imageShow: finalSelectedImages){
                                                String path = imageShow.path;
                                                Uri uri = getImageToShare(BitmapFactory.decodeFile(path), path, context);
                                                // putting uri of image to be shared
                                                files.add(uri);
                                            }
                                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                                            // calling startactivity() to share
                                            context.startActivity(Intent.createChooser(intent, "Share Via"));
                                            turnOffMultiSelectionMode(galleryAdapter, imageInfoArrayList);
                                        } else {
                                            notificationWhenNothingIsSelect(context);
                                        }
                                    }
                                });

                                deleteMultipleImages = (ImageButton) contextView.findViewById(R.id.deleteMultipleImages);
                                deleteMultipleImages.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        List<ImageInfo> finalSelectedImages = new ArrayList<>();

                                        finalSelectedImages = getSelectedImages(imageInfoArrayList);
                                        if (finalSelectedImages.size() != 0) {
                                            Dialog dialog = new Dialog(context);
                                            if(type == 1){
                                                dialog.setContentView(R.layout.layout_custom_dialog_remove_image_gallery);
                                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

                                                Button removeGalleryBtn = dialog.findViewById(R.id.remove_out_gallery);
                                                List<ImageInfo> selectedImages = finalSelectedImages;
                                                removeGallery(removeGalleryBtn,finalSelectedImages,imageInfoArrayList, galleryAdapter, dialog);

                                                Button cancel = dialog.findViewById(R.id.cancel);
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            } else {
                                                dialog.setContentView(R.layout.layout_custom_dialog_remove_image_album);
                                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

                                                Button removeAlbum = dialog.findViewById(R.id.remove_out_album);
                                                removeAlbum.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        //removeImageFrom(false);
                                                        dialog.dismiss();
                                                    }
                                                });

                                                Button removeGalleryBtn = dialog.findViewById(R.id.remove_out_gallery);
                                                List<ImageInfo> selectedImages = finalSelectedImages;
                                                removeGallery(removeGalleryBtn,finalSelectedImages,imageInfoArrayList, galleryAdapter, dialog);

                                                Button cancel = dialog.findViewById(R.id.cancel);
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                            dialog.show();
                                        } else {
                                            notificationWhenNothingIsSelect(context);
                                        }
                                    }
                                });
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
                                                        turnOffMultiSelectionMode(galleryAdapter, imageInfoArrayList);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animator) { }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animator) { }
                                                });
//                                        getFragmentManager().beginTransaction()
//                                                .replace(R.id.frameFragment, new AllPhotos(), TAG)
//                                                .commit();
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
    }

    private void removeGallery(Button removeGallery, List<ImageInfo> selectedImages, ArrayList<ImageInfo> imageInfoArrayList, GalleryAdapter galleryAdapter, Dialog dialog){

        removeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ImageInfo imageShow : selectedImages) {
                    String path = imageShow.path;
                    String name = imageShow.name;
                    ImageDatabase.getInstance(getContext())
                            .moveImageToRecycleBin(name, path);
                    AlbumDatabase.getInstance(getContext())
                            .softDeleteImage(name, path);

                    imageInfoArrayList.removeIf(
                            image -> image.path.equals(imageShow.path) && image.name.equals(imageShow.name));
                }
                notifyChangedListImageOnDelete(imageInfoArrayList, galleryAdapter);

                dialog.dismiss();
                turnOffMultiSelectionMode(galleryAdapter, imageInfoArrayList);
            }
        });
    }
    public  void handleAddImagesToAlbum(String albumName,  ArrayList<AlbumInfo> album, int pos, Context context, ArrayList<ImageInfo> imageInfoArrayList, List<ImageInfo> selectedImages){

        for(ImageInfo imageShow: selectedImages){
            String path = imageShow.path;
            String name = imageShow.name;
            if(AlbumDatabase.getInstance(context)
                    .isImageExistsInAlbum(name,
                            path,
                            album.get(pos).id)){
                Toast.makeText(context, "Image exists in " + albumName, Toast.LENGTH_SHORT).show();
            } else{
                AlbumDatabase.getInstance(context)
                        .insertImageToAlbum(name, path, album.get(pos).id);

                Toast.makeText(context, "Added to " + albumName, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public List<ImageInfo> getSelectedImages(ArrayList<ImageInfo> imageInfoArrayList) {
        List<ImageInfo> selectedImages = new ArrayList<>();
        for(ImageInfo imageShow: imageInfoArrayList){
            if(imageShow.getSelected()){
                selectedImages.add(imageShow);
            }
        }

        return selectedImages;
    }


    // Retrieving the url to share
    private Uri getImageToShare(Bitmap bitmap, String path, Context context) {
        File imagefolder = new File(context.getCacheDir() + path, "images");
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


    public void turnOffMultiSelectionMode(GalleryAdapter galleryAdapter, ArrayList<ImageInfo> imageInfoArrayList){
        longClickBar.setVisibility(View.GONE);
        galleryAdapter.setMultipleSelectState(false);
        for(ImageInfo imageShow: imageInfoArrayList){
            if(imageShow.getSelected()){
                imageShow.setSelected(false);
            }
        }
    }

    public void notificationWhenNothingIsSelect(Context context){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Please select one image !");


        builderSingle.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList, GalleryAdapter galleryAdapter){
        List<ListItem> listItems = new ArrayList<>();
        prepareData(newList,  listItems);
        galleryAdapter.setData(listItems);
    }

    private void prepareData(ArrayList<ImageInfo> imageInfoArrayList, List<ListItem> listItems){
        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        LinkedHashMap<String, List<ImageInfo>> listImageGroupByDate = new LinkedHashMap<>();
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


    //Con xoa album
//    private void removeImageFrom(boolean isRemoveOutOfGallery){
//        if(isRemoveOutOfGallery){
//            ImageDatabase.getInstance(getContext())
//                    .moveImageToRecycleBin(imageInfoArrayList.get(pos).name,imageInfoArrayList.get(pos).path);
//            AlbumDatabase.getInstance(getContext())
//                    .softDeleteImage(imageInfoArrayList.get(pos).name,imageInfoArrayList.get(pos).path);
//        } else{
//            AlbumDatabase.getInstance(getContext())
//                    .hardDeleteImage(imageInfoArrayList.get(pos).name,imageInfoArrayList.get(pos).path);
//        }
//
//        imageInfoArrayList.remove(pos);
//
//        if(imageInfoArrayList.size() > 0){
//            adapter.notifyDataSetChanged();
//
//        } else {
//            main.getSupportFragmentManager().popBackStack();
//            onDetach();
//        }
//    }
}
