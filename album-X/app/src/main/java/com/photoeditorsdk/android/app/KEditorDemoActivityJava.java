package com.photoeditorsdk.android.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import ly.img.android.pesdk.ui.activity.PhotoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.serializer._3.IMGLYFileWriter;


public class KEditorDemoActivityJava extends Activity {


    public static int PESDK_RESULT = 1;
    public static int GALLERY_RESULT = 2;
    public static int CAMERA_RESULT = 3;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openGallery = findViewById(R.id.openGallery);
        Button openCamera = findViewById(R.id.openCamera);

        openGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSystemGalleryToSelectAnImage();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSystemCameraToTakeAnImage();
            }
        });
    }


    private void openSystemGalleryToSelectAnImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, GALLERY_RESULT);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, "No Gallery APP installed", Toast.LENGTH_LONG).show();
        }
    }

    private void openSystemCameraToTakeAnImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
//        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivityForResult(intent, CAMERA_RESULT);
    }


    private void openEditor(Uri inputImage) {
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new PhotoEditorBuilder(this).setSettingsList(settingsList).startActivityForResult(this, PESDK_RESULT);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Open Editor with some uri in this case with an image selected from the system gallery.
            Uri selectedImage = intent.getData();
            openEditor(selectedImage);

        } else if (resultCode == RESULT_OK && requestCode == CAMERA_RESULT) {
            Bundle extras = intent.getExtras();
            Bitmap bp = (Bitmap) extras.get("data");
            Uri capturedImage = getImageUri(this, bp);
            openEditor(capturedImage);

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
            // TODO: Do something with the source...
        }
    }
}
