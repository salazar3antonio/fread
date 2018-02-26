package com.freadapp.fread.article_classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.freadapp.fread.MainActivity;
import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.SingleFragmentActivity;

/**
 * Created by salaz on 2/11/2018.
 */

public class ArticleActivity extends SingleFragmentActivity {

    public static final String TAG = ArticleActivity.class.getName();
    public static final String RECEIVED_URL = "received_url";

    //        todo tony Move article save button to toolbar

    @Override
    protected Fragment createFragment() {

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);

        //here is where the Article Fragments will be created
        Fragment articleFragment = ArticleFragment.newInstance();
        Fragment fbArticleFragment = FBArticleFragment.newInstance();

        //get intent that launched this activity
        Intent intent = getIntent();
        Article article = intent.getParcelableExtra(MainActivity.FB_ARTICLE);
        String url_received = intent.getStringExtra(Intent.EXTRA_TEXT);


        if (url_received != null) {
            handleReceivedURL(intent, articleFragment);
            Log.i(TAG, "Launched from Web");
            return articleFragment;
        }
        if (article != null) {
            handleReceivedArticle(intent, fbArticleFragment);
            Log.i(TAG, "Launched from MainActivity");
            return fbArticleFragment;
        } else {
            return null;
        }

    }

    /**
     * Handles the URL string received from the Aylien article extraction API
     *
     * @param intent   intent that started this activity
     * @param fragment fragment that will be inflated
     */
    void handleReceivedURL(Intent intent, Fragment fragment) {

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

    /**
     * Handles the Article object received by the Firebase Database
     *
     * @param intent   intent that started this activity
     * @param fragment fragement that will be inflated
     */
    void handleReceivedArticle(Intent intent, Fragment fragment) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_article_button:
                return true;

            case R.id.article_overflow_menu:
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }


    }
}
