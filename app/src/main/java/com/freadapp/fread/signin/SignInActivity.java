package com.freadapp.fread.signin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by salaz on 2/13/2018.
 */

public class SignInActivity extends AppCompatActivity {

    public static final String TAG = SignInActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_container);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showSignInFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Toast.makeText(getApplicationContext(), account.getEmail() + " is already signed in", Toast.LENGTH_SHORT).show();
            Log.d(TAG, account.getEmail() + " is already signed in");
        }

    }

    private void showSignInFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        SignInFragment signInFragment = SignInFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fl_sign_in_container, signInFragment).commit();

    }

}
