package com.hcmus.albumx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CloudStorage extends Activity {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;
    SignInButton googleSignInButton;
    Button logOutBtn;
    TextView personName, personEmail, personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_layout);

        logOutBtn = (Button) findViewById(R.id.btnLogOut);
        personName = (TextView) findViewById(R.id.personName);
        personEmail = (TextView) findViewById(R.id.personEmail);
        personId = (TextView) findViewById(R.id.personId);

        // Configure sign-in to request the user's ID, email, basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google sign in button
        googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        // GoogleSignInButton's event
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.googleSignInButton:
                        googleSignIn();
                        break;
                    default:
                        break;
                }
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnLogOut:
                        signOut();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing GoogleSignInAccount, if the user is already signed in
        // the GoogleSignInAccount will be non-null
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Hide the SignIn button
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The task returned from this call is always completed, no need to attach a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    // Google sign in
    private void googleSignIn() {
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    // Log out
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(CloudStorage.this);
                        updateUI(account);
                    }
                });
    }

    // Update UI
    private void updateUI(GoogleSignInAccount signedIn) {
        if (signedIn != null) {
            // Sign in successfully
            googleSignInButton.setVisibility(View.GONE);
            String personNameStr = signedIn.getDisplayName();
            String personEmailStr = signedIn.getEmail();
            String personIdStr = signedIn.getId();
//            Uri personPhoto = signedIn.getPhotoUrl();
            personName.setText("Person Name: " + personNameStr);
            personEmail.setText("Person Email: " + personEmailStr);
            personId.setText("Person ID: " + personIdStr);
            logOutBtn.setVisibility(View.VISIBLE);
        }
        else {
            // Sign in cancelled
            googleSignInButton.setVisibility(View.VISIBLE);
            personName.setText("Person Name: null");
            personEmail.setText("Person Email: null");
            personId.setText("Person ID: null");
            logOutBtn.setVisibility(View.GONE);
        }
    }

    // Handle sign in result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Sign in successfully, show authenticated UI
            updateUI(account);
        }
        catch(ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


}
