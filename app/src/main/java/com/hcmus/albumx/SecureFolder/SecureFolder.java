package com.hcmus.albumx.SecureFolder;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.hcmus.albumx.AlbumList.AlbumPhotos;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.jacknkiarie.signinui.models.SignInUI;

public class SecureFolder extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new SignInUI.Builder(this)
                .setSignInType(SignInUI.PIN_FORM)
                .setPinLength(6)
                .setTitle("Secure Folder")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SignInUI.REQUEST_CODE) {
            if (resultCode == SignInUI.RESULT_OK) {

                // check validation type user opted for - data?.getStringExtra(SignInUI.PARAM_SIGN_IN_TYPE)

                // email value - data?.getStringExtra(SignInUI.PARAM_EMAIL)
                // password value - data?.getStringExtra(SignInUI.PARAM_PASSWORD)
                // pin value - data?.getStringExtra(SignInUI.PARAM_PIN)

                // TODO the user provided correct values. Send request to validate credentials
                //Gửi mã pin và result ok về main activity
                String stringToPassBack = intent.getStringExtra(SignInUI.PARAM_PIN);
                Intent intent2 = new Intent();
                intent2.putExtra(Intent.EXTRA_TEXT, stringToPassBack);
                setResult(RESULT_OK, intent2);
                finish();
            } else {
                // the user has opted out of the sign in process. Close activity
                finish();
            }
        }
    }
}

