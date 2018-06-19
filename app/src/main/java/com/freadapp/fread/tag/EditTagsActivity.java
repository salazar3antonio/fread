package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.freadapp.fread.R;

public class EditTagsActivity extends AppCompatActivity {

    public static final String TAG = EditText.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tags_fragment_container);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();

        EditTagsFragment editTagsFragment = EditTagsFragment.newInstance();

        //if the fragment is already attached, don't recreate the fragment. Handles configuration changes.
        if (fragmentManager.getFragments().size() > 0) {
            Log.i(TAG, "Fragment already attached.");
        } else {
            fragmentManager.beginTransaction().add(R.id.edit_tags_container, editTagsFragment).commit();
        }

    }


}
