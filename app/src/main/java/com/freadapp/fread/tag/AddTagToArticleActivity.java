package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.freadapp.fread.R;
import com.freadapp.fread.article.ArticleDetailActivity;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagToArticleActivity extends AppCompatActivity {

    public static final String ARTICLE_TO_BE_TAGGED = "article_to_be_tagged";
    private static final String TAG = "tag";

    private String mArticleKeyID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag_fragment_container);

        Toolbar toolbar = findViewById(R.id.add_tag_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        Bundle bundleFromIntent = getIntent().getExtras();
        if (bundleFromIntent != null) {
            //get the ArticleKeyID from the intent that started this activity
            mArticleKeyID = bundleFromIntent.getString(ArticleDetailActivity.ARTICLE_KEY_ID);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        AddTagToArticleFragment addTagToArticleFragment = AddTagToArticleFragment.newInstance();
        //save the bundle to the fragment so it can be passed to the AddTagToArticleFragment.
        Bundle bundleKeyID = new Bundle();
        bundleKeyID.putString(ARTICLE_TO_BE_TAGGED, mArticleKeyID);
        //send the ArticleKeyID to the fragment
        addTagToArticleFragment.setArguments(bundleKeyID);

        //if the fragment is already attached, don't recreate the fragment. Handles configuration changes.
        if (fragmentManager.getFragments().size() > 0) {
            Log.i(TAG, "Fragment already attached.");
        } else {
            fragmentManager.beginTransaction().add(R.id.add_tag_container, addTagToArticleFragment).commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_tag_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_tag_menu_item:
                return false;
            default:
                return super.onOptionsItemSelected(item);

        }


    }



}
