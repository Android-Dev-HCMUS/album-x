package com.hcmus.albumx.AlbumList;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.albumx.AllPhotos.GalleryAdapter;
import com.hcmus.albumx.AllPhotos.GroupImageItem;
import com.hcmus.albumx.AllPhotos.ImageDatabase;
import com.hcmus.albumx.AllPhotos.ImageInfo;
import com.hcmus.albumx.AllPhotos.ListItem;
import com.hcmus.albumx.EditImage;
import com.hcmus.albumx.ImageViewing;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.hcmus.albumx.SecureFolder.SecureFolder;
import com.hcmus.albumx.SecureFolder.SecureFolderManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlbumPhotos extends Fragment {
    public static final String TAG = "Album Photos";

    private static final String ALBUM_ID_ARG = "albumID";
    private static final String ALBUM_NAME_ARG = "albumName";

    private MainActivity main;
    private Context context;

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;

    private ArrayList<ImageInfo> imageInfoArrayList = new ArrayList<>();
    List<ListItem> listItems;
    private ImageDatabase myDB;

    private int albumID;
    ImageButton cameraBtn;
    SharedPreferences sp;

    public static AlbumPhotos newInstance(int albumID, String albumName) {
        AlbumPhotos fragment = new AlbumPhotos();
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_ID_ARG, albumID);
        bundle.putString(ALBUM_NAME_ARG, albumName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            myDB = ImageDatabase.getInstance(context);

            if(getArguments() != null){
                albumID = getArguments().getInt(ALBUM_ID_ARG);
            }

            imageInfoArrayList = AlbumDatabase.getInstance(context).getImagesOf(albumID);

            listItems = new ArrayList<>();
            prepareData();
        } catch (IllegalStateException ignored) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.album_photos_layout, null);
        super.onViewCreated(view, savedInstanceState);

        TextView albumName = view.findViewById(R.id.album_name);

        if(getArguments() != null){
            albumName.setText(getArguments().getString(ALBUM_NAME_ARG));
        }
        if(getArguments().getString(ALBUM_NAME_ARG).trim().equals(AlbumDatabase.albumSet.ALBUM_EDITOR.trim()) ){
            cameraBtn = (ImageButton) view.findViewById(R.id.cameraBtn);
            cameraBtn.setVisibility(View.VISIBLE);
            cameraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditImage editImage = new EditImage(getActivity());

                    editImage.openSystemCameraToTakeAnImage();
                }
            });
        }

        Button back = (Button) view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.getSupportFragmentManager().popBackStack();
                onDetach();
            }
        });

        Button subMenu = (Button) view.findViewById(R.id.buttonSubMenu);
        subMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments().getString(ALBUM_NAME_ARG).equals("Secure Folder") && getArguments().getInt(ALBUM_ID_ARG) == 3) {
                    PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete_secure_folder:
                                    return true;
                                case R.id.change_PIN:
                                    // Đổi mã PIN cho thư mục secure folder
                                    changePIN();
                                    return true;
                                default:
                                    return false;
                            } //Switch
                        }
                    }); //setOnMenuItemClickListener
                    popup.inflate(R.menu.menu_secure_folder);
                    popup.show();
                }
            }
        });

        galleryAdapter = new GalleryAdapter(context, imageInfoArrayList, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String imagePath, int position) {
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout,
                                ImageViewing.newInstance(imagePath, imageInfoArrayList, position, albumID),
                                ImageViewing.TAG)
                        .addToBackStack("ImageViewingUI")
                        .commit();
                ((MainActivity)getActivity()).setBottomNavigationVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onLongClick(String imagePath, int position, boolean state) {
                return false;
            }

            @Override
            public void onImageAction(Boolean isSelected) {

            }
        });
        galleryAdapter.setData(listItems);

        recyclerView = view.findViewById(R.id.recyclerview_image);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (galleryAdapter.getItemViewType(position)){
                    case ListItem.TYPE_DATE:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(galleryAdapter);

        return view;
    }

    private void changePIN() {
        Intent intent = new Intent(getContext(), SecureFolder.class);
        activityResultLauncher2.launch(intent);
    }
    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String returnString = data.getStringExtra(Intent.EXTRA_TEXT);
                        //Lấy mã PIN đã lưu
                        sp = getContext().getSharedPreferences("MyPref", 0);
                        String storedPIN = sp.getString("PIN", null);
                        //Xử lý mã PIN
                        SharedPreferences.Editor ed;
                        if(md5(returnString).equals(storedPIN)) {
                            //changePIN
                            newPIN();
                        } else { Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show(); }
                    }
                }
            });

    private void newPIN() {
        Intent intent = new Intent(getContext(), SecureFolderManager.class);
        activityResultLauncher3.launch(intent);
    }
    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String returnString = data.getStringExtra(Intent.EXTRA_TEXT);
                        //Lấy mã PIN đã lưu
                        sp = getContext().getSharedPreferences("MyPref", 0);
                        String storedPIN = sp.getString("PIN", null);
                        //Xử lý mã PIN
                        SharedPreferences.Editor ed;
                        // changePIN
                        ed = sp.edit();
                        //Put hash password into shared preferences
                        ed.putString("PIN", md5(returnString));
                        ed.apply();
                        Toast.makeText(getContext(), "Change PIN successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void prepareData(){
        imageInfoArrayList.sort(new Comparator<ImageInfo>() {
            @Override
            public int compare(ImageInfo o1, ImageInfo o2) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = df.parse(o1.createdDate);
                    d2 = df.parse(o2.createdDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return d1.compareTo(d2);
            }
        });

        for(ImageInfo item : imageInfoArrayList){
            listItems.add(new GroupImageItem(item));
        }
    }

    public void notifyChangedListImageOnDelete(ArrayList<ImageInfo> newList){
        imageInfoArrayList = newList;
        listItems = new ArrayList<>();
        prepareData();
        galleryAdapter.setData(listItems);
    }
}
