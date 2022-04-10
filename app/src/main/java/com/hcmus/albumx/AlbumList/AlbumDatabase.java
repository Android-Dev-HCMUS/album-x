package com.hcmus.albumx.AlbumList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
    }

    public class imageSet{
        public static final String TABLE_NAME = "Image_Album_Data";
        public static final String FIELD_ID = "id";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_PATH = "path";
        public static final String FIELD_ALBUM = "album";

        public static final int DEFAULT_TYPE = 0;
        public static final int USER_TYPE = 1;
    }

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

    public static final String CREATE_ALBUM_TABLE = "CREATE TABLE IF NOT EXISTS " + albumSet.TABLE_NAME + "(" +
            albumSet.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            albumSet.FIELD_NAME + " TEXT, " +
            albumSet.FIELD_TYPE + " INTEGER " + ")";

    public static final String CREATE_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + imageSet.TABLE_NAME + "(" +
            imageSet.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            imageSet.FIELD_NAME + " TEXT, " +
            imageSet.FIELD_PATH + " TEXT, " +
            imageSet.FIELD_ALBUM + " INTEGER " + ")";

    public static final String GENERATE_INITIAL_DATA_1 = "INSERT INTO " + albumSet.TABLE_NAME + "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('Recent', 0);";
    public static final String GENERATE_INITIAL_DATA_2 ="INSERT INTO " + albumSet.TABLE_NAME + "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('Favorite', 0);";
    public static final String GENERATE_INITIAL_DATA_3 ="INSERT INTO " + albumSet.TABLE_NAME + "("+albumSet.FIELD_NAME+", "+albumSet.FIELD_TYPE+") VALUES ('Editor', 0);";

    public Cursor getAlbums(String sql){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public int insertAlbumData(String name, int type){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(albumSet.FIELD_NAME, name);
        contentValues.put(albumSet.FIELD_TYPE, type);

        return (int) database.insert(albumSet.TABLE_NAME, null, contentValues);
    }
    public void updateAlbumData(int id, String name){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_NAME, name);

        String[] arg = {String.valueOf(id)};

        database.update(albumSet.TABLE_NAME, contentValues, albumSet.FIELD_ID + " = ? ", arg);
    }
    public void deleteAlbumData(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] arg = {String.valueOf(id)};

        database.delete(albumSet.TABLE_NAME, albumSet.FIELD_ID + " = ?", arg);
    }

    public void insertImageToAlbum(String name, String path, int albumRef){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(imageSet.FIELD_NAME, name);
        contentValues.put(imageSet.FIELD_PATH, path);
        contentValues.put(imageSet.FIELD_ALBUM, albumRef);

        database.insert(imageSet.TABLE_NAME, null, contentValues);
    }

    public ArrayList<String> getImagesOf(int albumID){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<String> paths = new ArrayList<>();

        String[] columns = {imageSet.FIELD_ID, imageSet.FIELD_NAME, imageSet.FIELD_PATH, imageSet.FIELD_ALBUM};
        String[] arg = {String.valueOf(albumID)};
        Cursor cursor = database.query(imageSet.TABLE_NAME, columns, imageSet.FIELD_ALBUM +" = ?", arg, null, null, null);
        while(cursor.moveToNext()){
            paths.add(cursor.getString(2));
        }

        return paths;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
