package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hcmus.albumx.FavoriteList.FavoriteList;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AlbumList extends Fragment implements NewAlbumDialog.NewAlbumDialogListener {
    MainActivity main;
    Context context;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View album = inflater.inflate(R.layout.album_list_layout, null);

        ListView listView = album.findViewById(R.id.listView);

        AlbumListAdapter adapter = new AlbumListAdapter(context,
                R.layout.album_row, albumList,  icons);

        listView.setAdapter(adapter);

        // Code here
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                switch (position) {
                    case 0:
                        // All
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                albumList.remove(i);
                                icons.remove(i);
                                adapter.notifyDataSetChanged();
                            }
                        });

                builder.create().show();

                return true;
            }
        });
        
        Button addBtn = album.findViewById(R.id.add_album_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewAlbumDialog createNameDialog = new NewAlbumDialog();
                createNameDialog.setTargetFragment(AlbumList.this, 1);
                createNameDialog.show(getFragmentManager(), "create name dialog");

                adapter.notifyDataSetChanged();
            }
        });

        return album;
    }

    @Override
    public void applyName(String albumName) {
        albumList.add(albumName);
        icons.add(R.drawable.ic_photo);
    }
}
