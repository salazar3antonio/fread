package com.freadapp.fread.article_classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.freadapp.fread.MainActivity;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.SingleFragmentActivity;

/**
 * Created by salaz on 2/11/2018.
 */

public class ArticleActivity extends SingleFragmentActivity {

    public static final String TAG = ArticleActivity.class.getName();
    public static final String RECEIVED_URL = "received_url";
    public static final String RECEIVED_ARTICLE = "received_article";

    private String mTitle = "fRead It";

    @Override
    protected Fragment createFragment() {

        //here is where the Article Fragments will be created
        Fragment articleFragment = ArticleFragment.newInstance();
        Fragment fbArticleFragment = FBArticleFragment.newInstance();

        //set action bar title. this will change eventually
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);

        //get intent that launched this activity
        Intent intent = getIntent();
        Article article = intent.getParcelableExtra(MainActivity.FB_ARTICLE);
        String url_received = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (url_received != null) {
            handleRecURL(intent, articleFragment);
            Log.i(TAG, "Launched from Web");
            return articleFragment;
        } if (article != null) {
            handleRecArticle(intent, fbArticleFragment);
            Log.i(TAG, "Launched from MainActivity");
            return fbArticleFragment;
        } else {
            return null;
        }

}

    void handleRecURL(Intent intent, Fragment fragment) {

        String url_received = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (url_received != null) {
            //store URL as a string in a bundle. ArticleFragment.class will pick this up
            Bundle bundle = new Bundle();
            bundle.putString(RECEIVED_URL, url_received);
            fragment.setArguments(bundle);
        } else {
            Log.i(TAG, "No URL received");
        }

    }

    void handleRecArticle(Intent intent, Fragment fragment){

        Article article = intent.getParcelableExtra(MainActivity.FB_ARTICLE);

        if (article != null) {
            Log.i(TAG, article.getTitle());
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.FB_ARTICLE, article);
            fragment.setArguments(bundle);
        } else {
            Log.i(TAG, "No article received");
        }

    }



}
