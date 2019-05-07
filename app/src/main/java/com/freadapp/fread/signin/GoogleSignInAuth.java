package com.freadapp.fread.signin;

import android.content.Context;
import android.content.Intent;

import com.freadapp.fread.helpers.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * This class creates the Google Sign In request options.
 */

public class GoogleSignInAuth {

    public static final String TAG = GoogleSignInAuth.class.getName();

    public static final int GOOGLE_SIGN_IN_REQ_CODE = 1;

    private Context mContext;

    public GoogleSignInAuth(Context context) {
        this.mContext = context;
    }

    public GoogleSignInClient getGoogleSignInClient() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.OAUTH_2_ID)
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(mContext, gso);
    }

    public GoogleSignInAccount onGoogleSignInResult(int requestCode, Intent data) throws ApiException {

        if (requestCode == GOOGLE_SIGN_IN_REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            return task.getResult(ApiException.class);
        }

        return null;

    }
}