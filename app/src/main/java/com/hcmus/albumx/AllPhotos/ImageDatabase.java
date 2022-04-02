package com.hcmus.albumx.AllPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImageDatabase extends SQLiteOpenHelper {
    public Context context;
    public static final String DATABASE_NAME = "ImageDataManager";

    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "data";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_IMG = "img";

    public ImageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FIELD_NAME + " TEXT, " +
            FIELD_IMG + " BLOB " + ")";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Cursor getImages(String sql){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public boolean insertImageData(String name, byte[] img){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_NAME, name);
        contentValues.put(FIELD_IMG, img);

        long ins = database.insert(TABLE_NAME, null, contentValues);
        database.close();

        if(ins == -1) return false;
        else return true;
    }


}
