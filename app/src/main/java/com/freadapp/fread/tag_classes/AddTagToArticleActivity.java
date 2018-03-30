package com.freadapp.fread.tag_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.freadapp.fread.R;
import com.freadapp.fread.article_classes.ArticleDetailActivity;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagToArticleActivity extends AppCompatActivity {

    public static final String ARTICLE_TO_BE_TAGGED = "article_to_be_tagged";

    private String mArticleKeyID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag_fragment_container);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundleFromIntent = getIntent().getExtras();
        if (bundleFromIntent != null) {
            //get the ArticleKeyID from the intent that started this activity
            mArticleKeyID = bundleFromIntent.getString(ArticleDetailActivity.ARTICLE_KEY_ID);
        }

        AddTagToArticleFragment addTagToArticleFragment = AddTagToArticleFragment.newInstance();
        //save the bundle to the fragment so it can be passed to the AddTagToArticleFragment.
        Bundle bundleKeyID = new Bundle();
        bundleKeyID.putString(ARTICLE_TO_BE_TAGGED, mArticleKeyID);
        //send the ArticleKeyID to the fragment
        addTagToArticleFragment.setArguments(bundleKeyID);
        getSupportFragmentManager().beginTransaction().add(R.id.add_tag_container, addTagToArticleFragment).commit();

    }

}
