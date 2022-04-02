package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.os.Bundle;
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

import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.FavoriteList.FavoriteList;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AlbumList extends Fragment implements  RemoveAlbumDialog.RemoveAlbumDialogListener, SetNameAlbumDialog.NewAlbumDialogListener{
    MainActivity main;
    Context context;
    AlbumListAdapter adapter;
    ListView listView;

    private ArrayList<String> albumList = new ArrayList<String>(Arrays.asList("All", "Recent", "Favorite", "Edited"));
    private ArrayList<Integer> icons = new ArrayList<Integer>(Arrays.asList(R.drawable.ic_photo, R.drawable.ic_recent, R.drawable.ic_favorite,
            R.drawable.ic_text));

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

        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.listView){
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
                        new RemoveAlbumDialog(position, (String) listView.getItemAtPosition(position));
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
        adapter = new AlbumListAdapter(context,
                R.layout.album_row, albumList,  icons);

        listView = album.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                switch (position) {
                    case 0:
                        main.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameFragment, AllPhotos.newInstance("all photo"))
                                .commit();
                        break;
                    case 1:
                        // Recent
                        break;
                    case 2:
                        // Favorite
                        main.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameFragment, FavoriteList.newInstance("favorite"))
                                .commit();
                        break;
                    case 3:
                        // Edited
                        break;
                    default:
                        break;
                }
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
        albumList.remove(position);
        icons.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void applyNameToNewAlbum(String albumName) {
        albumList.add(albumName);
        icons.add(R.drawable.ic_photo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void renameAlbum(String albumName, int position) {
        albumList.set(position, albumName);
        adapter.notifyDataSetChanged();
    }
}
