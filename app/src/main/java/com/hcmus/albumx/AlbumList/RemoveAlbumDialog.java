package com.hcmus.albumx.AlbumList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class RemoveAlbumDialog extends AppCompatDialogFragment {
    RemoveAlbumDialogListener listener;
    int position;
    String name;

    public RemoveAlbumDialog(int position, String albumName) {
        this.position = position;
        this.name = albumName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Xóa \""+ name + "\"")
                .setMessage("Bạn có chắc chắn muốn xóa album \"" + name +"\" không? Các ảnh trong album sẽ không bị xóa")
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.deleteAlbum(position);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (RemoveAlbumDialogListener) getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement NewAlbumDialogListener");
        }
    }

    public interface RemoveAlbumDialogListener {
        void deleteAlbum(int position);
    }
}
