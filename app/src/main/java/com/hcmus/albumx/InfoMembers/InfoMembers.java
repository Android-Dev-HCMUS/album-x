package com.hcmus.albumx.InfoMembers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AllPhotos.AllPhotos;
import com.hcmus.albumx.AllPhotos.GalleryAdapter;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;

import java.util.ArrayList;

public class InfoMembers extends Fragment {
    public static final String TAG = "InfoMembers";

    MainActivity main;
    Context context;

    ImageView imgInfo;

    public static InfoMembers newInstance() {
        return new InfoMembers();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            main = (MainActivity) getActivity();
            context = getContext();

        }catch (IllegalStateException ignored){
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.infomembers_layout, null);
        super.onViewCreated(view, savedInstanceState);

        imgInfo = (ImageView) view.findViewById(R.id.imageViewInfo);

        imgInfo.setImageResource(R.drawable.app_icon);

        return view;
    }


}
