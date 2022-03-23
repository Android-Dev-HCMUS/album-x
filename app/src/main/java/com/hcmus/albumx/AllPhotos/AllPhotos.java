package com.hcmus.albumx.AllPhotos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

public class AllPhotos extends Fragment {
    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    GridView gridView;

    public int[] imageArray = {
            R.drawable.stock_1, R.drawable.stock_2, R.drawable.stock_3, R.drawable.stock_4 ,
            R.drawable.stock_5, R.drawable.stock_6, R.drawable.stock_7, R.drawable.stock_8,
            R.drawable.stock_9, R.drawable.stock_10, R.drawable.stock_11, R.drawable.stock_12,
            R.drawable.stock_13, R.drawable.stock_14, R.drawable.stock_15, R.drawable.stock_16
    };

    public static AllPhotos newInstance(String strArg1) {
        AllPhotos fragment = new AllPhotos();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.all_photos_layout, null);

        selectBtn = (Button) view.findViewById(R.id.buttonSelect);
        subMenuBtn = (Button) view.findViewById(R.id.buttonSubMenu);
        gridView = (GridView) view.findViewById(R.id.gird_view);
        gridView.setAdapter(new GridImageAdapter(context, imageArray));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, ImageViewing.newInstance(i))
                        .addToBackStack(null)
                        .commit();

//                BottomNavigationView bottomNavigationView = main.findViewById(R.id.bottomNavigation);
//                bottomNavigationView.setVisibility(View.INVISIBLE);
//                ImageButton imageButton = main.findViewById(R.id.addBtn);
//                imageButton.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

}
