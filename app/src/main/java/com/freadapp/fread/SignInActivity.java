package com.freadapp.fread;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.freadapp.fread.helpers.SingleFragmentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by salaz on 2/13/2018.
 */

public class SignInActivity extends SingleFragmentActivity {

    private FirebaseAuth mAuth;

    @Override
    protected Fragment createFragment() {

        mAuth = FirebaseAuth.getInstance();

        return new SignInFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Toast.makeText(getApplicationContext(), account.getEmail() + " is already signed in", Toast.LENGTH_SHORT).show();
        }

    }
}
