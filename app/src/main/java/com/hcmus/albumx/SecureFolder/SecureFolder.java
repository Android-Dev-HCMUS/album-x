package com.hcmus.albumx.SecureFolder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.hcmus.albumx.AlbumList.AlbumPhotos;
import com.hcmus.albumx.MainActivity;
import com.hcmus.albumx.R;
import com.jacknkiarie.signinui.models.SignInUI;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureFolder extends AppCompatActivity {
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_folder_manager);

        sp = this.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor ed;
        if(!sp.contains("password")){
            if(!sp.contains("isSettingPassword")) {
                Toast.makeText(this, "Setup PIN", Toast.LENGTH_SHORT).show();
                newPassword();
            } else {
                if(sp.getBoolean("isSettingPassword", false)) {
                    new SignInUI.Builder(this)
                            .setSignInType(SignInUI.PIN_FORM)
                            .setPinLength(6)
                            .setTitle("Secure Folder")
                            .build();
                }
            }
        } else {
            Toast.makeText(this, "Enter PIN to unlock", Toast.LENGTH_SHORT).show();
            new SignInUI.Builder(this)
                    .setSignInType(SignInUI.PIN_FORM)
                    .setPinLength(6)
                    .setTitle("Secure Folder")
                    .build();
        }
    }

    private void newPassword() {
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putBoolean("isSettingPassword", true);
        ed.apply();
        Intent intent = new Intent(this, SecureFolder.class);
        activityResultLauncher.launch(intent);
    }
    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String returnString = data.getStringExtra(Intent.EXTRA_TEXT);
                        //Xử lý mã PIN
                        sp = getApplicationContext().getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor ed;
                        if(!sp.contains("password")){
                            ed = sp.edit();
                            //Put hash password into shared preferences
                            ed.putString("password", md5(returnString));
                            ed.commit();
                            finish();
                        }
                    } else {
                        SharedPreferences.Editor ed;
                        ed = sp.edit();
                        ed.putBoolean("isSettingPassword", false);
                        ed.apply();
                        finish();
                    }
                }
            });

    public String md5(String s) {
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

