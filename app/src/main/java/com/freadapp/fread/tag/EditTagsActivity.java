package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;

public class EditTagsActivity extends AppCompatActivity {

    public static final String TAG = EditText.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_edit_container);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();

        EditTagsFragment editTagsFragment = EditTagsFragment.newInstance();

        boolean isUserSignedIn = FirebaseUtils.isFirebaseUserSignedIn();

        //if the fragment is already attached, don't recreate the fragment. Handles configuration changes.
        if (fragmentManager.getFragments().size() > 0) {
            Log.i(TAG, "Fragment already attached.");
        } else if (isUserSignedIn){
            fragmentManager.beginTransaction().add(R.id.fl_edit_tags_container, editTagsFragment).commit();
        } else {
            finish();
            Toast.makeText(this, "Sign in to edit tags.", Toast.LENGTH_SHORT).show();
        }

    }


}
