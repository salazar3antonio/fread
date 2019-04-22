package com.freadapp.fread.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileMainFragment extends Fragment {

    private TextView mUserDisplayName;
    private TextView mUserEmailAddress;
    private Button mSignOut;
    private OnSignOutSuccessListener mSignOutSuccessCallback;

    public interface OnSignOutSuccessListener {
        void onSignOutSuccess(boolean signOutSuccess);
    }

    public static ProfileMainFragment newInstance() {

        Bundle args = new Bundle();

        ProfileMainFragment fragment = new ProfileMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_profile_fragment, container, false);

        mUserDisplayName = view.findViewById(R.id.tv_user_display_name);
        mUserEmailAddress = view.findViewById(R.id.tv_user_email_address);
        mSignOut = view.findViewById(R.id.bt_sign_out);

        if (FirebaseUtils.isFirebaseUserSignedIn()) {
            String userDisplayName = FirebaseUtils.getUserDisplayName();
            String userEmailAddress = FirebaseUtils.getUserEmailAddress();
            mUserDisplayName.setText(userDisplayName);
            mUserEmailAddress.setText(userEmailAddress);
        }

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Sign Out Success", Toast.LENGTH_SHORT).show();
                mSignOutSuccessCallback.onSignOutSuccess(true);
            }
        });

        return view;
    }

    public void setOnSignOutSuccessListener(OnSignOutSuccessListener callback) {
        this.mSignOutSuccessCallback = callback;
    }
}
