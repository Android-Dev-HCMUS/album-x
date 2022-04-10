package com.hcmus.albumx.FavoriteList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

public class FavoriteList extends Fragment {

    MainActivity mainActivity;
    Context context;
    GridView favoriteGridView;
    BottomNavigationView bottomNavigationView;
    ImageButton imageButton;

    public static FavoriteList newInstance(String strFavorite) {
        FavoriteList fragment = new FavoriteList();
        Bundle bundle = new Bundle();
        bundle.putString("favorite", strFavorite);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            mainActivity = (MainActivity) getActivity();
            bottomNavigationView = mainActivity.findViewById(R.id.bottomNavigation);
            imageButton = mainActivity.findViewById(R.id.addBtn);
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.album_photos_layout, null);

        //favoriteGridView = (GridView) view.findViewById(R.id.favorite_gird_view);
        //favoriteGridView.setAdapter(new FavoriteListAdapter(context, listFavoriteImg));
        favoriteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mainActivity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.frameContent, ImageViewing.newInstance(i, listFavoriteImg), "ImageViewing")
//                        .addToBackStack("ImageViewingUI")
//                        .commit();
//                hideNavAndButton();
            }
        });
        return view;
    }

    public void showNavAndButton(){
        bottomNavigationView.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
    }
    public void hideNavAndButton(){
        bottomNavigationView.setVisibility(View.INVISIBLE);
        imageButton.setVisibility(View.INVISIBLE);
    }

}
