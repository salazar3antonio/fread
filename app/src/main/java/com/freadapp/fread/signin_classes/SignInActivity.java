package com.freadapp.fread.signin_classes;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.freadapp.fread.article_classes.ArticleActivity;
import com.freadapp.fread.helpers.SingleFragmentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by salaz on 2/13/2018.
 */

public class SignInActivity extends SingleFragmentActivity {

    public static final String TAG = ArticleActivity.class.getName();

    @Override
    protected Fragment createFragment() {

        return new SignInFragment();

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
}
