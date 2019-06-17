package com.freadapp.fread.signin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button mResetPassword;
    private TextView mUserEmailInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);


        mResetPassword = findViewById(R.id.bt_reset_password);
        mUserEmailInput = findViewById(R.id.et_user_email);

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = mUserEmailInput.getText().toString();

                if (!emailAddress.isEmpty()) {
                    sendResetEmail(emailAddress);
                }

            }
        });
    }

    public void sendResetEmail(String emailAddress) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

}
