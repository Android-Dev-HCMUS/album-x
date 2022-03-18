package com.hcmus.albumx;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AlbumList extends Fragment {
    MainActivity main;
    Context context;

    private String[] albumList = {"All", "Recent", "Like", "Edited"};
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
        View album = (View) inflater.inflate(R.layout.album_list, null);

        ListView listView = (ListView) album.findViewById(R.id.listView);

        AlbumListAdapter adapter = new AlbumListAdapter(context,
                R.layout.album_row, albumList,  icons);

        listView.setAdapter(adapter);

        return album;
    }
}
