package com.freadapp.fread.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.helpers.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.freadapp.fread.signin.GoogleSignInAuth.GOOGLE_SIGN_IN_REQ_CODE;

/**
 * Created by salaz on 2/13/2018.
 */

public class SignInFragment extends Fragment {

    public static final String TAG = SignInFragment.class.getName();

    public static final String SIGN_IN_TYPE_CODE = "sign_in_type_code";

    private Button mCreateAccountButton;
    private Button mSignInWithEmailButton;
    private SignInButton mGoogleSignInButton;
    private LoginButton mFacebookSignInButton;
    private FragmentManager mFragmentManager;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    public FirebaseAuth mFirebaseAuth;
    public OnSignInSuccessListener mSignInSuccessCallback;
    private GoogleSignInAuth mGoogleSignInAuth;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    public interface OnSignInSuccessListener {
        void onSignInSuccess(boolean signInSuccess);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFragmentManager = getActivity().getSupportFragmentManager();
        mGoogleSignInAuth = new GoogleSignInAuth(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        mCreateAccountButton = view.findViewById(R.id.bt_create_new_account);
        mSignInWithEmailButton = view.findViewById(R.id.bt_sign_in_with_email);
        mGoogleSignInButton = view.findViewById(R.id.bt_google_sign_in);
        mFacebookSignInButton = view.findViewById(R.id.bt_facebook_sign_in);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkAvailableAndConnected(getContext())) {
                    Intent signInIntent = mGoogleSignInAuth.getGoogleSignInClient().getSignInIntent();
                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQ_CODE);
                } else {
                    Toast.makeText(getContext(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                }

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

        mSignInWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isNetworkAvailableAndConnected(getContext())) {

                    commitEmailPasswordFragment(1, mFragmentManager);

                } else {
                    Toast.makeText(getContext(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isNetworkAvailableAndConnected(getContext())) {

                    commitEmailPasswordFragment(2, mFragmentManager);

                } else {
                    Toast.makeText(getContext(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        try {
            GoogleSignInAccount googleSignInAccount = mGoogleSignInAuth.onGoogleSignInResult(requestCode, data);
            firebaseAuthWithGoogle(googleSignInAccount);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
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
                            FirebaseUtils.initFirebaseUser();
                            Log.d(TAG, "Firebase user authentication success");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
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
                            FirebaseUtils.initFirebaseUser();
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void setOnSignInSuccessListener(OnSignInSuccessListener callback) {
        this.mSignInSuccessCallback = callback;
    }

    private void commitEmailPasswordFragment(int signInTypeCode, FragmentManager fragmentManager) {

        EmailPasswordFragment emailPasswordFragment = EmailPasswordFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putInt(SIGN_IN_TYPE_CODE, signInTypeCode);
        emailPasswordFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(getId(), emailPasswordFragment).commit();
    }

}
