package com.freadapp.fread.helpers;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.freadapp.fread.R;

/**
 * Created by salaz on 2/11/2018.
 * This abstract class overrides the oncreate() method of AppCompatActivity
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() {
        //this captures the id of the layout resource file. It is NOT the ID name of a view that lives inside of a layout resource file.
        return R.layout.article_fragment_container;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //find the fragment id (fragment_container) from the FragmentManager and commit a new Fragment object
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.article_container);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.article_container, fragment).commit();
        }
    }



}
