package com.hcmus.albumx.AlbumList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.hcmus.albumx.SecureFolder.SecureFolder;

import java.util.ArrayList;

public class AlbumList extends Fragment implements  RemoveAlbumDialog.RemoveAlbumDialogListener, SetNameAlbumDialog.NewAlbumDialogListener{
    public static String TAG = "Album List";

    MainActivity main;
    Context context;
    AlbumListAdapter adapter;
    ListView listView;

    AlbumDatabase db;

    private static ArrayList<AlbumInfo> albumList;

    public static AlbumList newInstance() {
        return new AlbumList();
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

                Cursor cursor = db.getAlbums();
                while (cursor.moveToNext()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    int type = cursor.getInt(2);

                    if(name.equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_recent));
                    } else if (name.equals(AlbumDatabase.albumSet.ALBUM_FAVORITE)){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
                    } else if (name.equals(AlbumDatabase.albumSet.ALBUM_EDITOR)){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_edit));
                    } else if (name.equals(AlbumDatabase.albumSet.ALBUM_SECURE)){
                        albumList.add(new AlbumInfo(id, name, type, R.drawable.ic_filter));
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
                if(position == 3) {
                    openSecureFolder();
                } else {
                    main.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameFragment,
                                    AlbumPhotos.newInstance(albumList.get(position).id, albumList.get(position).name),
                                    AlbumPhotos.TAG)
                            .addToBackStack("AlbumPhotosUI")
                            .commit();
                }

            }

            private void openSecureFolder() {
                Intent intent = new Intent(getContext(), SecureFolder.class);
                activityResultLauncher.launch(intent);
            }
            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();
                                String returnString = data.getStringExtra(Intent.EXTRA_TEXT);
                                //Xử lý mã PIN
                                if(returnString.equals("000000")) {
                                    main.getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameFragment,
                                                    AlbumPhotos.newInstance(3, "Secure Folder"),
                                                    AlbumPhotos.TAG)
                                            .addToBackStack("AlbumPhotosUI")
                                            .commit();
                                }
                                else {Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show();}
                            }
                        }
                    });
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

    public ArrayList<AlbumInfo> infoAddAlbums(){

        ArrayList<AlbumInfo> album;

        album = new ArrayList<>();

        Cursor cursor = db.getAlbums();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int type = cursor.getInt(2);

            if(name.equals(AlbumDatabase.albumSet.ALBUM_RECENT)){
                Log.d("pass", "pass");
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_FAVORITE)){
                album.add(new AlbumInfo(id, name, type, R.drawable.ic_favorite));
            } else if (name.equals(AlbumDatabase.albumSet.ALBUM_EDITOR)){
                Log.d("pass", "pass");
            } else {
                album.add(new AlbumInfo(id, name, type, R.drawable.ic_photo));
            }
        }
        return album;
    }
}
