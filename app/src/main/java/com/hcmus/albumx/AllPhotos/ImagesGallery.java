package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class ImagesGallery {

    public static ArrayList<String> listOfImages(Context context) {
        Uri uri;
        Cursor cursor;


        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //get folder name

        // column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("d", String.valueOf(absolutePathOfImage));
            listOfAllImages.add(absolutePathOfImage);

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        ArrayList<String> arrPath = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath.add(cursor.getString(dataColumnIndex));

        }

        cursor.close();

        return arrPath;
    }
}
// https://stackoverflow.com/questions/60097683/java-android-getcontentresolver-query-for-specific-camera-folder-to-get-cursor