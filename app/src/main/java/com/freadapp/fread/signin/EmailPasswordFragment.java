package com.freadapp.fread.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class EmailPasswordFragment extends SignInFragment {

    public static final String TAG = EmailPasswordFragment.class.getName();

    private Button mSignInButton;
    private EditText mEmailText;
    private TextInputEditText mPasswordText;
    private int mSignInTypeCode;
    private TextView mForgotPasswordButton;

    public static EmailPasswordFragment newInstance() {

        Bundle args = new Bundle();

        EmailPasswordFragment fragment = new EmailPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mSignInTypeCode = bundle.getInt(SIGN_IN_TYPE_CODE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_in_with_email_fragment, container, false);

        mSignInButton = view.findViewById(R.id.bt_sign_in);
        mEmailText = view.findViewById(R.id.et_user_email);
        mPasswordText = view.findViewById(R.id.et_user_password);
        mForgotPasswordButton = view.findViewById(R.id.bt_forgot_password);

        if (mSignInTypeCode == 2) {
            mSignInButton.setText(R.string.sign_up_new_user);
            mForgotPasswordButton.setVisibility(View.GONE);
        }

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailText.getText().toString();
                String password = mPasswordText.getText().toString();

                if (password.length() < 6) {
                    Toast.makeText(getContext(), R.string.toast_password_length, Toast.LENGTH_LONG).show();
                } else {
                    switch (mSignInTypeCode) {
                        case 1:
                            firebaseAuthWithEmail(email, password);
                            break;
                        case 2:
                            createNewFirebaseUser(email, password);
                    }
                }
            }
        });

        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }

    public void firebaseAuthWithEmail(String email, String password) {

        if (!email.isEmpty() && !password.isEmpty()) {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mSignInSuccessCallback.onSignInSuccess(true);
                                FirebaseUtils.initFirebaseUser();
                                Log.d(TAG, "signInWithEmail:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                mSignInSuccessCallback.onSignInSuccess(false);
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), R.string.toast_enter_email_password, Toast.LENGTH_LONG).show();
        }
    }

    public void createNewFirebaseUser(String email, String password) {

        if (!email.isEmpty() || !password.isEmpty()) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUtils.initFirebaseUser();
                                mSignInSuccessCallback.onSignInSuccess(true);
                                Log.d(TAG, "signInWithEmail:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                mSignInSuccessCallback.onSignInSuccess(false);
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }




}