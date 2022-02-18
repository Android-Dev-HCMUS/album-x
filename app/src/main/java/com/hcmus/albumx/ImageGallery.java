package com.hcmus.albumx;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class ImageGallery {
    //not done lost 2 lines
    public static ArrayList<Media> loadImage(Context context, boolean isAscending) {
        ArrayList<Media> listOfAllImages = new ArrayList<>();
        // Query image information only when user clicked the details options in pop-up menu
        String location = "";
        String displayName = "";
        String size = "";
        String dateAdded = "";
        String dateModified = "";
        String width = "";
        String height = "";
        String duration = "";

        // Select absolute path, display name, size (Byte), date added, date modified, resolution (including width and height)
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.MediaColumns.DURATION
        };
        String orderBy = MediaStore.MediaColumns.DATE_TAKEN + (isAscending ? " ASC" : " DESC");
        Cursor cursor = null;
        try {
            Uri uri = uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri,
                    projection,
                    null,
                    null,
                    orderBy);

            while(cursor.moveToNext()) {
                location = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
                dateModified = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));
                height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));
                Media image = new Image(displayName, location, size, dateAdded, dateModified, width, height, duration, (byte) 0);

                listOfAllImages.add(image);
            }
        } catch (Exception e) {
            Log.d("Get image error", e.getMessage());
        }
        return listOfAllImages;
    }
}
