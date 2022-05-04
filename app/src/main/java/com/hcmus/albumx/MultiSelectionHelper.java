package com.hcmus.albumx;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.hcmus.albumx.AlbumList.AlbumDatabase;
import com.hcmus.albumx.AlbumList.AlbumInfo;
import com.hcmus.albumx.AlbumList.AlbumPhotos;
import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.RecycleBin.RecycleBinPhotos;
import com.hcmus.albumx.SecureFolder.SecureFolderPhotos;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MultiSelectionHelper {
    private MainActivity main;
    private Context context;

    public MultiSelectionHelper(MainActivity main, Context context) {
       this.main = main;
        this.context = context;
    }

    public void handleAddImagesToAlbum(ArrayList<ImageInfo> imageInfoArrayList) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            ArrayList<AlbumInfo> album;
            album = AlbumDatabase.getInstance(context).getInfoAddAlbums();

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            builderSingle.setIcon(R.drawable.ic_album);
            builderSingle.setTitle("Select One Album:");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

            for (AlbumInfo itemAlbum : album) {
                String name = itemAlbum.name;
                arrayAdapter.add(name);
            }

            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addImagesToAlbum(selectedImages, album.get(which));
                    dialog.dismiss();
                }
            });
            builderSingle.show();
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    private void addImagesToAlbum(List<ImageInfo> selectedImages, AlbumInfo toAlbum) {

        for (ImageInfo imageShow : selectedImages) {
            String path = imageShow.path;
            String name = imageShow.name;
            if (AlbumDatabase.getInstance(context)
                    .isImageExistsInAlbum(name,
                            path,
                            toAlbum.id)) {
                Toast.makeText(context, "Image exists in " + toAlbum.name, Toast.LENGTH_SHORT).show();
            } else {
                AlbumDatabase.getInstance(context)
                        .insertImageToAlbum(name, path, toAlbum.id);

                Toast.makeText(context, "Added to " + toAlbum.name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleDeleteImages(ArrayList<ImageInfo> imageInfoArrayList, int fromAlbum) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            if(fromAlbum == 0){
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_custom_dialog_remove_image_gallery);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);


                Button removeGallery = dialog.findViewById(R.id.remove_out_gallery);
                removeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeImagesFrom(imageInfoArrayList, selectedImages, true);

                        notifyDataSetChange(fromAlbum, imageInfoArrayList);

                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.layout_custom_dialog_remove_image_album);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

                Button removeAlbum = dialog.findViewById(R.id.remove_out_album);
                removeAlbum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeImagesFrom(imageInfoArrayList, selectedImages, false);

                        notifyDataSetChange(fromAlbum, imageInfoArrayList);

                        dialog.dismiss();
                    }
                });
                Button removeGallery = dialog.findViewById(R.id.remove_out_gallery);
                removeGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeImagesFrom(imageInfoArrayList, selectedImages, true);

                        notifyDataSetChange(fromAlbum, imageInfoArrayList);

                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    private void removeImagesFrom(ArrayList<ImageInfo> imageInfoArrayList, List<ImageInfo> selectedImages,
                                 boolean isRemoveOutOfGallery) {
        for (ImageInfo image : selectedImages) {
            if (isRemoveOutOfGallery) {
                ImageDatabase.getInstance(context)
                        .moveImageToRecycleBin(image.name, image.path);
                AlbumDatabase.getInstance(context)
                        .softDeleteImage(image.name, image.path);
            } else {
                AlbumDatabase.getInstance(context)
                        .hardDeleteImage(image.name, image.path);
            }

            imageInfoArrayList.remove(image);
        }
    }

    private void moveImagesToSecureFolder(ArrayList<ImageInfo> imageInfoArrayList, List<ImageInfo> selectedImages) {
        for (ImageInfo image : selectedImages) {
            ImageDatabase.getInstance(context)
                    .moveImageToSecureFolder(image.name, image.path);
            AlbumDatabase.getInstance(context)
                    .moveImageToSecureFolder(image.name, image.path);

            imageInfoArrayList.remove(image);
        }
    }

    public void handleMoveToSecureFolderImages(ArrayList<ImageInfo> imageInfoArrayList, int fromAlbum) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            if(fromAlbum != 3 && fromAlbum != -1){
                moveImagesToSecureFolder(imageInfoArrayList, selectedImages);
                notifyDataSetChange(fromAlbum, imageInfoArrayList);
            } else {
                Toast.makeText(context, "Cannot move to Secure Folder from this album!", Toast.LENGTH_SHORT).show();
            }
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    public void handleDeleteImagesForever(ArrayList<ImageInfo> imageInfoArrayList, int fromAlbum) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            Dialog dialog = new Dialog(context);
            dialog.getWindow().setContentView(R.layout.layout_custom_dialog_remove_image_recycle_bin);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

            Button removeGallery = dialog.findViewById(R.id.remove_out_gallery);
            removeGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ImageInfo image : selectedImages) {
                        ImageDatabase.getInstance(context).deleteImage(image.name, image.path);
                        deleteImageInStorage(image.name);
                        imageInfoArrayList.remove(image);
                    }

                    notifyDataSetChange(fromAlbum, imageInfoArrayList);

                    dialog.dismiss();
                }
            });
            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    public void deleteImageInStorage(String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root, "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, image_name);
        if (file.exists()) {
            file.delete();
        }
    }

    public void handleRestoreImages(ArrayList<ImageInfo> imageInfoArrayList, int fromAlbum) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            Dialog dialog = new Dialog(context);
            dialog.getWindow().setContentView(R.layout.layout_custom_dialog_restore_image_recycle_bin);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

            Button removeGallery = dialog.findViewById(R.id.restore_gallery);
            removeGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ImageInfo image : selectedImages) {
                        ImageDatabase.getInstance(context)
                                .recoverImageFromRecycleBin(image.name, image.path);
                        AlbumDatabase.getInstance(context)
                                .recoverImage(image.name, image.path);

                        imageInfoArrayList.remove(image);
                    }

                    notifyDataSetChange(fromAlbum, imageInfoArrayList);

                    dialog.dismiss();
                }
            });
            Button cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    public void handleShareImages(ArrayList<ImageInfo> imageInfoArrayList) {
        List<ImageInfo> selectedImages = getSelectedImages(imageInfoArrayList);

        if (!selectedImages.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_TEXT, "Sharing Images");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Album X share multi images");
            intent.setType("image/*");

            ArrayList<Uri> files = new ArrayList<>();

            for (ImageInfo imageShow : selectedImages) {
                String path = imageShow.path;
                Uri uri = getImageToShare(BitmapFactory.decodeFile(path), path, context);
                files.add(uri);
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

            context.startActivity(Intent.createChooser(intent, "Share Via"));
        } else {
            notificationWhenNothingIsSelect(context);
        }
    }

    private List<ImageInfo> getSelectedImages(ArrayList<ImageInfo> imageInfoArrayList) {
        List<ImageInfo> selectedImages = new ArrayList<>();
        for(ImageInfo imageShow: imageInfoArrayList){
            if(imageShow.getSelected()){
                selectedImages.add(imageShow);
            }
        }

        return selectedImages;
    }

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
    }

    private void notifyDataSetChange(int fromAlbum, ArrayList<ImageInfo> imageInfoArrayList) {
        if(fromAlbum == -1){
            RecycleBinPhotos fragment = (RecycleBinPhotos) main.getSupportFragmentManager()
                    .findFragmentByTag(RecycleBinPhotos.TAG);

            if (fragment != null) {
                fragment.notifyChangedListImage(imageInfoArrayList);
            }
        }
        else if(fromAlbum == 0){
            AllPhotos fragment = (AllPhotos) main.getSupportFragmentManager()
                    .findFragmentByTag(AllPhotos.TAG);

            if (fragment != null) {
                fragment.notifyChangedListImageOnDelete(imageInfoArrayList);
            }
        }
        else if(fromAlbum == 3){
            SecureFolderPhotos fragment = (SecureFolderPhotos) main.getSupportFragmentManager()
                    .findFragmentByTag(SecureFolderPhotos.TAG);

            if (fragment != null) {
                fragment.notifyChangedListImageOnDelete(imageInfoArrayList);
            }
        }
        else {
            AlbumPhotos fragment2 = (AlbumPhotos) main.getSupportFragmentManager()
                    .findFragmentByTag(AlbumPhotos.TAG);

            if (fragment2 != null) {
                fragment2.notifyChangedListImageOnDelete(imageInfoArrayList);
            }
        }
    }

    private void notificationWhenNothingIsSelect(Context context){
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
}
