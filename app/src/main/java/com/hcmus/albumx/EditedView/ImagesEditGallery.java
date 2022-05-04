package com.hcmus.albumx.EditedView;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImagesEditGallery {

    public static ArrayList<Uri> listOfEditImages(Context context) {
        Cursor cursor;
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        try{
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);
        } catch (Exception e){
            return null;
        }
        //Total number of images
        int count = cursor.getCount();
        //Create an array to store uri
        ArrayList<Uri> arrUri = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(dataColumnIndex);
            if(path.contains("DCIM/img")){
                @SuppressLint("Range") Uri imageUri=
                        ContentUris
                                .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));

                arrUri.add(imageUri);
            }
        }
        cursor.close();
        return arrUri;
    }

}
// https://stackoverflow.com/questions/60097683/java-android-getcontentresolver-query-for-specific-camera-folder-to-get-cursor