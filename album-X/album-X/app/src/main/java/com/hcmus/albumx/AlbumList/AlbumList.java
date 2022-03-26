package com.hcmus.albumx.AlbumList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcmus.albumx.FavoriteList.FavoriteList;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

public class AlbumList extends Fragment {
    MainActivity main;
    Context context;

    private String[] albumList = {"All", "Recent", "Favorite", "Edited"};
    private final Integer[] icons = {R.drawable.ic_photo, R.drawable.ic_recent, R.drawable.ic_favorite,
            R.drawable.ic_text};

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
        View album = (View) inflater.inflate(R.layout.album_list_layout, null);

        ListView listView = (ListView) album.findViewById(R.id.listView);

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

        return album;
    }
}
