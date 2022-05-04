package com.hcmus.albumx.AlbumList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hcmus.albumx.R;

public class SetNameAlbumDialog extends AppCompatDialogFragment {
    static int SET_NAME_NEW_ALBUM = 1;
    static int RENAME_ALBUM = 2;

    NewAlbumDialogListener listener;
    EditText albumName;
    int type;
    int position;

    public SetNameAlbumDialog(int type){
        this.type = type;
    }

    public SetNameAlbumDialog(int type, int position){
        this.type = type;
        this.position = position;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_name_album_dialog, null);

        albumName = view.findViewById(R.id.album_name);
        TextView header = view.findViewById(R.id.t1);
        if(type == SET_NAME_NEW_ALBUM){
            header.setText("New Album");
        }
        if(type == RENAME_ALBUM){
            header.setText("Rename Album");
        }

        builder.setView(view)
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(type == SET_NAME_NEW_ALBUM){
                            listener.applyNameToNewAlbum(albumName.getText().toString().trim());
                        }
                        if(type == RENAME_ALBUM){
                            listener.renameAlbum(albumName.getText().toString().trim(), position);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (NewAlbumDialogListener) getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement NewAlbumDialogListener");
        }

    }

    public interface NewAlbumDialogListener {
        void applyNameToNewAlbum(String albumName);
        void renameAlbum(String albumName, int position);
    }
}
