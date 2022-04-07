package com.hcmus.albumx.AllPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public final class ImageDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ImageDataManager";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Image_Data";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_IMG = "img";

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
            FIELD_PATH + " TEXT, " +
            FIELD_IMG + " BLOB" + ")";

    public Cursor getImages(String sql){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void insertImageData(String name, String path, byte[] img){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME, name);
        contentValues.put(FIELD_PATH, path);
        contentValues.put(FIELD_IMG, img);

        database.insert(TABLE_NAME, null, contentValues);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }
}
