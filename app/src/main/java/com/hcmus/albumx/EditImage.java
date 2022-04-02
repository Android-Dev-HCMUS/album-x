package com.hcmus.albumx;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.CameraPreviewBuilder;
import ly.img.android.pesdk.ui.activity.PhotoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;


public class EditImage extends Activity implements PermissionRequest.Response {

    // Important permission request for Android 6.0 and above, don't forget to add this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */
    }

    Activity activity;
    EditImage(Activity ac) {
        activity = ac;
    }

    public static int PESDK_RESULT = 1;
    public static int GALLERY_RESULT = 2;


    private SettingsList createPesdkSettingsList() {
        // Create a empty new SettingsList and apply the changes on this reference.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();
        // If you include our asset Packs and you use our UI you also need to add them to the UI Config,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add your own Assets.
        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );
        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );
        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );
        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );
        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );
        return settingsList;
    }

    private void openSystemGalleryToSelectAnImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {

            startActivityForResult(intent, GALLERY_RESULT);

        } catch (ActivityNotFoundException exception) {
            Toast.makeText(activity, "No Gallery APP installed", Toast.LENGTH_LONG).show();
        }
    }

    private void openSystemCameraToTakeAnImage() {
        PhotoEditorSettingsList settingsList = (PhotoEditorSettingsList) createPesdkSettingsList();

        new CameraPreviewBuilder(activity)
                .setSettingsList(settingsList)
                .startActivityForResult(activity, PESDK_RESULT, PermissionRequest.NEEDED_PREVIEW_PERMISSIONS_AND_FINE_LOCATION);

    }

    protected void openEditor(Uri inputImage) {
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        // Create folder "AlbumX" if doesn't exist
        /*
        final File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "AlbumX");

        if (!f.exists()) {
            f.mkdir();
        }
        */

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new PhotoEditorBuilder(activity).setSettingsList(settingsList).startActivityForResult(activity, PESDK_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Open Editor with some uri in this case with an image selected from the system gallery.
            Uri selectedImage = intent.getData();

            openEditor(selectedImage);

        } else if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);


            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());


            // TODO: Do something with the result image


            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            try {
                new IMGLYFileWriter(lastState).writeJson(new File(
                        getExternalFilesDir(null),
                        "serialisationReadyToReadWithPESDKFileReader.json"
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();

            Log.d("PESDK", "Source image is located here " + data.getSourceUri());

            // TODO: Do something with the source...
        }
    }
}
