package com.hcmus.albumx.AllPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hcmus.albumx.AlbumList.AlbumDatabase;

import java.util.ArrayList;

public final class ImageDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DataManager";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Image_Data";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PATH = "path";

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

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_NAME + " TEXT, " +
            FIELD_PATH + " TEXT " + ")";

    public ArrayList<String> getAllImages(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<String> paths = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ImageDatabase.TABLE_NAME, null);
        while(cursor.moveToNext()){
            paths.add(cursor.getString(2));
        }
        return paths;
    }

    public ArrayList<String> getImagesByName(String name){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<String> paths = new ArrayList<>();

        String[] columns = {ImageDatabase.FIELD_ID, ImageDatabase.FIELD_NAME, ImageDatabase.FIELD_PATH};
        String[] arg = {name};
        Cursor cursor = database.query(ImageDatabase.TABLE_NAME, columns,
                ImageDatabase.FIELD_NAME +" = ?", arg,
                null, null, null);
        while(cursor.moveToNext()){
            paths.add(cursor.getString(2));
        }

        return paths;
    }

    public void insertImage(String name, String path){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME, name);
        contentValues.put(FIELD_PATH, path);

        database.insert(TABLE_NAME, null, contentValues);
    }
    public void deleteImage(String path){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {path};

        database.delete(TABLE_NAME, "path = ?", arg);
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
