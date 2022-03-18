package com.hcmus.albumx;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AllPhotosLayout extends Fragment {
    MainActivity main;
    Context context;
    Button selectBtn, subMenuBtn;
    GridView gridView;

    public static AllPhotosLayout newInstance(String strArg1) {
        AllPhotosLayout fragment = new AllPhotosLayout();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.all_photos_layout, null);

        selectBtn = (Button) view.findViewById(R.id.buttonSelect);
        subMenuBtn = (Button) view.findViewById(R.id.buttonSubMenu);
        gridView = (GridView) view.findViewById(R.id.gird_view);
        gridView.setAdapter(new ImageAdapter(context));

        return view;
    }

}
