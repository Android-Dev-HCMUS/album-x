package com.hcmus.albumx.AlbumList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class RemoveImageDialog extends AppCompatDialogFragment {
    RemoveAlbumDialog.RemoveAlbumDialogListener listener;
    int position;
    String path;

    public RemoveImageDialog(int position, String path) {
        this.position = position;
        this.path = path;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Xóa ảnh")
                .setMessage("Bạn muốn xóa khỏi album hay xóa khỏi thư viện?")
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
            listener = (RemoveAlbumDialog.RemoveAlbumDialogListener) getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement NewAlbumDialogListener");
        }
    }

    public interface RemoveAlbumDialogListener {
        void deleteAlbum(int position);
    }
}
