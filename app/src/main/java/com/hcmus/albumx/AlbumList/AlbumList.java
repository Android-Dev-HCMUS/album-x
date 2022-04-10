package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;

public class AlbumList extends Fragment implements  RemoveAlbumDialog.RemoveAlbumDialogListener, SetNameAlbumDialog.NewAlbumDialogListener{
    public static String TAG = "Album List";

    MainActivity main;
    Context context;
    AlbumListAdapter adapter;
    ListView listView;

    AlbumDatabase db;

    private static ArrayList<AlbumInfo> albumList;

    public static AlbumList newInstance(String strArg1) {
        AlbumList fragment = new AlbumList();
        Bundle bundle = new Bundle();
        bundle.putString("arg2", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            db = AlbumDatabase.getInstance(context);

            if(albumList == null){
                albumList = new ArrayList<>();

                Cursor cursor = db.getAlbums("SELECT * FROM " + AlbumDatabase.albumSet.TABLE_NAME);
                while (cursor.moveToNext()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    int type = cursor.getInt(2);

                    if(name.equals("Recent")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_recent));
                    } else if (name.equals("Favorite")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
                    } else if (name.equals("Editor")){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_edit));
                    } else {
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_photo));
                    }
                }
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        if(v.getId() == R.id.listView && position != 0 && position != 1 && position != 2){
            MenuInflater inflater = main.getMenuInflater();
            inflater.inflate(R.menu.menu_list_album, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;

        switch (item.getItemId()){
            case R.id.delete_album:
                RemoveAlbumDialog removeAlbumDialog =
                        new RemoveAlbumDialog(position, albumList.get(position).name);
                removeAlbumDialog.setTargetFragment(AlbumList.this, 1);
                removeAlbumDialog.show(getFragmentManager(), "remove album dialog");
                return true;
            case R.id.modify_album_name:
                SetNameAlbumDialog createNameDialog =
                        new SetNameAlbumDialog(SetNameAlbumDialog.RENAME_ALBUM, position);
                createNameDialog.setTargetFragment(AlbumList.this, 1);
                createNameDialog.show(getFragmentManager(), "rename dialog");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View album = inflater.inflate(R.layout.album_list_layout, null);
        adapter = new AlbumListAdapter(context, R.layout.album_row, albumList);

        listView = album.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                main.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameFragment,
                                AlbumPhotos.newInstance(albumList.get(position).id, albumList.get(position).name),
                                AlbumPhotos.TAG)
                        .addToBackStack("AlbumPhotosUI")
                        .commit();
            }
        });

        Button addBtn = album.findViewById(R.id.add_album_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetNameAlbumDialog createNameDialog =
                        new SetNameAlbumDialog(SetNameAlbumDialog.SET_NAME_NEW_ALBUM);
                createNameDialog.setTargetFragment(AlbumList.this, 1);
                createNameDialog.show(getFragmentManager(), "create name dialog");
            }
        });

        return album;
    }

    @Override
    public void deleteAlbum(int position) {
        Log.e("position", String.valueOf(position));
        Log.e("position", String.valueOf(albumList.get(position).id));

        db.deleteAlbum(albumList.get(position).id);
        albumList.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void applyNameToNewAlbum(String albumName) {
        int id = db.insertAlbum(albumName, 1);

        albumList.add(new AlbumInfo(id, albumName, 1, R.drawable.ic_photo));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void renameAlbum(String albumName, int position) {
        albumList.get(position).name = albumName;
        db.updateAlbum(albumList.get(position).id, albumName);
        adapter.notifyDataSetChanged();
    }
}
