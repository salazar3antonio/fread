package com.freadapp.fread.article_classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.freadapp.fread.MainActivity;
import com.freadapp.fread.R;
import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by salaz on 2/26/2018.
 */

public class ArticleActivity extends AppCompatActivity {

    public static final String TAG = ArticleActivity.class.getName();
    public static final String ARTICLE_BUNDLE = "article_bundle";
    public static final String ARTICLE_FRAGMENT_TAG = "article_fragment_tag";

    private Article mArticle;
    private String mURLreceived;
    private DatabaseReference mDBrefArticles;
    private FirebaseUser mUser;
    private boolean mArticleIsSaved = false;
    private Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //article extra from FB database
            mArticle = bundle.getParcelable(MainActivity.FB_ARTICLE);
            //URL extra from web browser
            mURLreceived = bundle.getString(Intent.EXTRA_TEXT);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDBrefArticles = firebaseDatabase.getReference().child("articles");

        if (findViewById(R.id.fragment_container) != null) {
            //placing in the loading screen for when quiz api is being called
            ArticleLoadingFragment articleLoadingFragmentLoadingFragment = ArticleLoadingFragment.newInstance();
            articleLoadingFragmentLoadingFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, articleLoadingFragmentLoadingFragment).commit();
        }

        //building up the Retrofit object to begin calling the API
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //passing in the ArticleAPI interface class into the retrofit Object
        ArticleAPI articleAPI = retrofit.create(ArticleAPI.class);

        if (mURLreceived != null) {
            //using the articleAPI object to call the GET method of the API. Passes in the URL received from the Intent.
            Call<Article> call = articleAPI.getArticle(mURLreceived, true);
            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {

                    if (response.isSuccessful()) {
                        //Assign mArticle to the Response body (JSON object).
                        //since our response is an Article object is must be stored as an Article Object.
                        mArticle = response.body();
                        //set URL to Article object
                        mArticle.setUrl(mURLreceived);
                        showArticleFragment();
                    } else {
                        Log.i(TAG, "API Response Failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //on failure, Toast error code
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "API Failed: " + t.getMessage());
                }
            });
        } else {
            //if no URL received from the intent (means activity was started from main activity)
            //then, show article and do not call the API
            showArticleFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //change save article menu item to be checked if article has been saved.
        if (mArticle != null && mArticle.isSaved()) {
            MenuItem saveArticleMenuItem = menu.findItem(R.id.save_article_menu_item);
            saveArticleMenuItem.setIcon(R.drawable.ic_check_circle_black_24dp);
            saveArticleMenuItem.setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_article_button:
            case R.id.save_article_menu_item:
                if (item.isChecked()) {
                    removeArticle(mArticle, mDBrefArticles);
                    item.setIcon(R.drawable.ic_add_box_black_24dp);
                    item.setChecked(false);
                } else {
                    saveArticle(mArticle, mUser.getUid(), mDBrefArticles);
                    item.setIcon(R.drawable.ic_check_circle_black_24dp);
                    item.setChecked(true);
                }
                return true;

            case R.id.article_overflow_menu:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Removes the article object found at /articles/$articlekeyid in the DB
     *
     * @param article  article object to remove
     * @param articles database reference of the articles
     */
    private void removeArticle(Article article, DatabaseReference articles) {
        //point the DB reference to /articles/$articlekeyid and remove the value
        articles.child(article.getKeyid()).removeValue();
        Toast.makeText(getApplicationContext(), "Article removed.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves an Article node and user id into the Articles database reference.
     * A keyid is created and stored in the Article object as well.
     *
     * @param article  article to be written to database
     * @param uid      user id of the user that is currently logged in
     * @param articles database reference of Articles
     */
    private void saveArticle(Article article, String uid, DatabaseReference articles) {

        //check if article is already saved
        //if mURLreceived is equal to the article's url or is not null then user has already saved the article
        //create a unique keyid for the Articlle
        String key = articles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyid(key);
        article.setUid(uid);
        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be save to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(key, article);
        //update the children of "articles" in the DB with the passed in Hash Map
        articles.updateChildren(writeMap);

        Toast.makeText(getApplicationContext(), "Article saved.", Toast.LENGTH_SHORT).show();

    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    private void showArticleFragment() {

        ArticleFragment articleFragment = ArticleFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, mArticle);
        articleFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, articleFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

}
