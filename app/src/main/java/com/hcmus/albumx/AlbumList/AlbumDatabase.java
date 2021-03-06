package com.hcmus.albumx.AlbumList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class AlbumDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DataManager";
    public static final int DATABASE_VERSION = 1;

    public class albumSet{
        public static final String TABLE_NAME = "Album_Data";
        public static final String FIELD_ID = "id";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_TYPE = "type";

        public static final int DEFAULT_TYPE = 0;
        public static final int USER_TYPE = 1;

        public static final String ALBUM_RECENT = "Recent";
        public static final String ALBUM_FAVORITE = "Favorite";
        public static final String ALBUM_EDITOR = "Editor";
        public static final String ALBUM_SECURE = "Secure Folder";
    }

    public class imageSet{
        public static final String TABLE_NAME = "Image_Album_Data";
        public static final String FIELD_ID = "id";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_PATH = "path";
        public static final String FIELD_REMOVE_PROPERTY = "is_remove";
        public static final String FIELD_CREATED_DATE = "created_at";
        public static final String FIELD_ALBUM = "album";
    }

    public static final String CREATE_ALBUM_TABLE = "CREATE TABLE IF NOT EXISTS " + albumSet.TABLE_NAME + "(" +
            albumSet.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            albumSet.FIELD_NAME + " TEXT, " +
            albumSet.FIELD_TYPE + " INTEGER " + ")";

    public static final String CREATE_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + imageSet.TABLE_NAME + "(" +
            imageSet.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            imageSet.FIELD_NAME + " TEXT, " +
            imageSet.FIELD_PATH + " TEXT, " +
            imageSet.FIELD_REMOVE_PROPERTY + " BIT, " +
            imageSet.FIELD_CREATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            imageSet.FIELD_ALBUM + " INTEGER " + ")";

    public static final String GENERATE_INITIAL_DATA_1 = "INSERT INTO " + albumSet.TABLE_NAME +
            "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('" + albumSet.ALBUM_RECENT + "', 0);";
    public static final String GENERATE_INITIAL_DATA_2 = "INSERT INTO " + albumSet.TABLE_NAME +
            "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('" + albumSet.ALBUM_FAVORITE + "', 0);";
    public static final String GENERATE_INITIAL_DATA_3 = "INSERT INTO " + albumSet.TABLE_NAME +
            "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('" + albumSet.ALBUM_EDITOR + "', 0);";
    public static final String GENERATE_INITIAL_DATA_4 = "INSERT INTO " + albumSet.TABLE_NAME +
            "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('" + albumSet.ALBUM_SECURE + "', 0);";

    private static AlbumDatabase instance;

    private AlbumDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AlbumDatabase getInstance(Context context) {
        if(instance == null){
            instance = new AlbumDatabase(context.getApplicationContext());
        }
        return instance;
    }

    public ArrayList<AlbumInfo> getAlbums(){
        ArrayList<AlbumInfo> albumInfoArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + AlbumDatabase.albumSet.TABLE_NAME, null);

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int type = cursor.getInt(2);

            if(name.equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_recent));
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_FAVORITE)){
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_EDITOR)){
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_edit));
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_SECURE)){
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_lock));
            } else {
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_photo));
            }
        }

        return albumInfoArrayList;
    }

    public ArrayList<AlbumInfo> getInfoAddAlbums() {
        ArrayList<AlbumInfo> albumInfoArrayList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + AlbumDatabase.albumSet.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int type = cursor.getInt(2);

            if(name.equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                ;
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_FAVORITE)){
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_EDITOR)){
                ;
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_SECURE)){
                ;
            } else {
                albumInfoArrayList.add(new AlbumInfo(id, name, type, R.drawable.ic_photo));
            }
        }
        return albumInfoArrayList;
    }

    public int insertAlbum(String name, int type){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(albumSet.FIELD_NAME, name);
        contentValues.put(albumSet.FIELD_TYPE, type);

        return (int) database.insert(albumSet.TABLE_NAME, null, contentValues);
    }
    public void updateAlbum(int id, String name){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_NAME, name);

        String[] arg = {String.valueOf(id)};

        database.update(albumSet.TABLE_NAME, contentValues, albumSet.FIELD_ID + " = ? ", arg);
    }
    public void deleteAlbum(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {String.valueOf(id)};

        database.delete(albumSet.TABLE_NAME, albumSet.FIELD_ID + " = ?", arg);
    }

    public void insertImageToAlbum(String name, String path, int albumRef){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_NAME, name);
        contentValues.put(imageSet.FIELD_PATH, path);
        contentValues.put(imageSet.FIELD_REMOVE_PROPERTY, 0);
        contentValues.put(imageSet.FIELD_CREATED_DATE, getDateTime());
        contentValues.put(imageSet.FIELD_ALBUM, albumRef);

        database.insert(imageSet.TABLE_NAME, null, contentValues);
    }

    public ArrayList<ImageInfo> getImagesOf(int albumID){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<>();

        String[] columns = {imageSet.FIELD_ID, imageSet.FIELD_NAME, imageSet.FIELD_PATH, imageSet.FIELD_CREATED_DATE};
        String[] arg = {String.valueOf(albumID)};
        Cursor cursor = database.query(imageSet.TABLE_NAME, columns,
                imageSet.FIELD_ALBUM +" = ? and " + imageSet.FIELD_REMOVE_PROPERTY + " = 0",
                arg,
                null, null, null);
        while(cursor.moveToNext()){
            imageInfoArrayList.add(new ImageInfo(cursor.getInt(0),cursor.getString(1),
                    cursor.getString(2),  cursor.getString(3), cursor.getString(3)));
        }
        return imageInfoArrayList;
    }

    public boolean isImageExistsInAlbum(String name, String path, int albumID){
        boolean isExists = false;
        SQLiteDatabase database = this.getReadableDatabase();

        String[] columns = {imageSet.FIELD_ID, imageSet.FIELD_NAME, imageSet.FIELD_PATH, imageSet.FIELD_ALBUM};
        String[] arg = {String.valueOf(albumID)};
        Cursor cursor = database.query(imageSet.TABLE_NAME, columns, imageSet.FIELD_ALBUM +" = ?", arg, null, null, null);
        while(cursor.moveToNext()){
            if(cursor.getString(1).equals(name) && cursor.getString(2).equals(path)){
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    public void recoverImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_REMOVE_PROPERTY, 0);

        String[] arg = {name, path};

        database.update(imageSet.TABLE_NAME, contentValues,
                imageSet.FIELD_NAME + " = ? and " + imageSet.FIELD_PATH + " = ? ", arg);
    }

    public void softDeleteImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_REMOVE_PROPERTY, 1);

        String[] arg = {name, path};

        database.update(imageSet.TABLE_NAME, contentValues,
                imageSet.FIELD_NAME + " = ? and " + imageSet.FIELD_PATH + " = ? ", arg);
    }

    public void moveImageToSecureFolder(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_REMOVE_PROPERTY, 2);

        String[] arg = {name, path};

        database.update(imageSet.TABLE_NAME, contentValues,
                imageSet.FIELD_NAME + " = ? and " + imageSet.FIELD_PATH + " = ? ", arg);
    }

    public void hardDeleteImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {name, path};

        database.delete(imageSet.TABLE_NAME,
                imageSet.FIELD_NAME + " = ? and " + imageSet.FIELD_PATH + " = ? ", arg);
    }

    private String getDateTime() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
