package com.freadapp.fread.tag_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.freadapp.fread.R;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagActivity extends AppCompatActivity {

    public static final String ARTICLE_KEY_ID_REPLY = "article_key_id_reply";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag_fragment_container);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddTagFragment addTagFragment = AddTagFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.add_tag_container, addTagFragment).commit();

    }
}
