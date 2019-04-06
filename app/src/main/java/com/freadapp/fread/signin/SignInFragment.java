package com.freadapp.fread.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by salaz on 2/13/2018.
 */

public class SignInFragment extends Fragment {

    public static final String TAG = SignInFragment.class.getName();
    public static final int GOOGLE_SIGIN_REQ_CODE = 1;

    private Button mSignOutButton;
    private SignInButton mGoogleSignInButton;
    private LoginButton mFacebookSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private FirebaseAuth mFirebaseAuth;
    private OnSignInSuccessListener mSignInSuccessCallback;


    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    public void setOnSignInSuccessListener(OnSignInSuccessListener callback) {
        this.mSignInSuccessCallback = callback;
    }

    public interface OnSignInSuccessListener {
        void onSignInSuccess(boolean signInSuccess);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.OAUTH_2_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        mSignOutButton = view.findViewById(R.id.signout_button);
        mGoogleSignInButton = view.findViewById(R.id.google_signin_button);
        mSignOutButton = view.findViewById(R.id.signout_button);
        mFacebookSignInButton = view.findViewById(R.id.facebook_signin_button);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        mFacebookSignInButton.setReadPermissions("email", "public_profile");
        mFacebookSignInButton.setFragment(this);
        mFacebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutFirebaseUser();
            }
        });

        return view;
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGIN_REQ_CODE);
    }

    private void signOutFirebaseUser() {

        FirebaseAuth.getInstance().signOut();

        toastSignOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGIN_REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

                Log.d(TAG, "Google sign in success");

            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mSignInSuccessCallback.onSignInSuccess(true);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Firebase user authentication success");
                            toastLoginSuccess();

                        } else {

                            // if sign in fails, log and snackbar failure
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            toastLoginFailure();
                        }
                    }
                });

    }

    private void firebaseAuthWithFacebook(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mSignInSuccessCallback.onSignInSuccess(true);
                            Log.d(TAG, "signInWithCredential:success");
                            toastLoginSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            toastLoginFailure();
                        }
                    }
                });
    }

    public void toastLoginSuccess() {
        Toast.makeText(getContext(), "Sign In Success", Toast.LENGTH_SHORT).show();
    }

    public void toastLoginFailure() {
        Toast.makeText(getContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
    }

    public void toastSignOut() {
        Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
    }

}
