package com.hcmus.albumx.AllPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hcmus.albumx.AlbumList.AlbumDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class ImageDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DataManager";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Image_Data";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_REMOVE_PROPERTY = "is_remove";
    public static final String FIELD_CREATE_DATE = "create_at";
    public static final String FIELD_REMOVE_DATE = "remove_at";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_NAME + " TEXT, " +
            FIELD_PATH + " TEXT, " +
            FIELD_REMOVE_PROPERTY + " BIT, " +
            FIELD_CREATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            FIELD_REMOVE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    private static ImageDatabase instance;

    private ImageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ImageDatabase getInstance(Context context) {
        if(instance == null){
            instance = new ImageDatabase(context.getApplicationContext());
        }
        return instance;
    }

    public ArrayList<ImageInfo> getAllImages(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<>();

        String[] columns = {ImageDatabase.FIELD_ID, ImageDatabase.FIELD_NAME, ImageDatabase.FIELD_PATH};
        Cursor cursor = database.query(ImageDatabase.TABLE_NAME, columns,
                ImageDatabase.FIELD_REMOVE_PROPERTY +" = 0", null,
                null, null, null);
        while(cursor.moveToNext()){
            imageInfoArrayList.add(new ImageInfo(cursor.getInt(0),cursor.getString(1),cursor.getString(2)));
        }
        return imageInfoArrayList;
    }

    public ArrayList<String> getImagesByPath(String path){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<String> paths = new ArrayList<>();

        String[] columns = {ImageDatabase.FIELD_ID, ImageDatabase.FIELD_NAME, ImageDatabase.FIELD_PATH};
        String[] arg = {path};
        Cursor cursor = database.query(ImageDatabase.TABLE_NAME, columns,
                ImageDatabase.FIELD_PATH +" = ? ", arg,
                null, null, null);
        while(cursor.moveToNext()){
            paths.add(cursor.getString(2));
        }

        return paths;
    }

    public ArrayList<ImageInfo> getImagesInRecycleBin(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<>();

        String[] columns = {ImageDatabase.FIELD_ID, ImageDatabase.FIELD_NAME, ImageDatabase.FIELD_PATH};
        Cursor cursor = database.query(ImageDatabase.TABLE_NAME, columns,
                ImageDatabase.FIELD_REMOVE_PROPERTY +" = 1", null,
                null, null, null);
        while(cursor.moveToNext()){
            imageInfoArrayList.add(new ImageInfo(cursor.getInt(0),cursor.getString(1),cursor.getString(2)));
        }
        return imageInfoArrayList;
    }

    public int insertImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME, name);
        contentValues.put(FIELD_PATH, path);
        contentValues.put(FIELD_REMOVE_PROPERTY, 0);

        return (int) database.insert(TABLE_NAME, null, contentValues);
    }

    public boolean isImageExistsInApplication(String name){
        boolean isExists = false;
        SQLiteDatabase database = this.getReadableDatabase();

        String[] columns = {FIELD_NAME, FIELD_PATH};
        String[] arg = {name};
        Cursor cursor = database.query(TABLE_NAME, columns, FIELD_NAME +" = ? ", arg, null, null, null);
        if(cursor.getCount() > 0){
            isExists = true;
        }
        return isExists;
    }

    public void moveImageToRecycleBin(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_REMOVE_PROPERTY, 1);
        contentValues.put(FIELD_REMOVE_DATE, getDateTime());

        String[] arg = {name, path};

        database.update(TABLE_NAME, contentValues,
                FIELD_NAME + " = ? and " + FIELD_PATH + " = ? ", arg);
    }

    public void recoverImageFromRecycleBin(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_REMOVE_PROPERTY, 0);

        String[] arg = {name, path};

        database.update(TABLE_NAME, contentValues,
                FIELD_NAME + " = ? and " + FIELD_PATH + " = ? ", arg);
    }

    public void deleteImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {name, path};

        database.delete(TABLE_NAME, FIELD_NAME + " = ? and " + FIELD_PATH + " = ? ", arg);
    }

    private String getDateTime() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(AlbumDatabase.CREATE_ALBUM_TABLE);
        sqLiteDatabase.execSQL(AlbumDatabase.GENERATE_INITIAL_DATA_1);
        sqLiteDatabase.execSQL(AlbumDatabase.GENERATE_INITIAL_DATA_2);
        sqLiteDatabase.execSQL(AlbumDatabase.GENERATE_INITIAL_DATA_3);
        sqLiteDatabase.execSQL(AlbumDatabase.CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }
}
