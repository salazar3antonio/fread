package com.freadapp.fread.article_classes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.tag_classes.AddTagToArticleActivity;
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

import static com.freadapp.fread.article_classes.ArticleDetailActivity.ARTICLE_BUNDLE;
import static com.freadapp.fread.article_classes.ArticleDetailActivity.ARTICLE_FRAGMENT_TAG;

/**
 * Created by salaz on 3/27/2018.
 * This Activity will handle the article coming from a web page. It will call the API and display the article.
 */

public class ArticleFetchActivity extends AppCompatActivity {

    public static final String TAG = ArticleFetchActivity.class.getName();

    private String mURLreceived;
    private Article mArticle;
    private DatabaseReference mArticlesDBref;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_fragment_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //assign Extra Text from the Intent that the Web Browser started.
            mURLreceived = bundle.getString(Intent.EXTRA_TEXT);

        }

        if (findViewById(R.id.article_container) != null) {
            //placing in the loading screen for when quiz api is being called
            ArticleLoadingFragment articleLoadingFragmentLoadingFragment = ArticleLoadingFragment.newInstance();
            articleLoadingFragmentLoadingFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.article_container, articleLoadingFragmentLoadingFragment).commit();
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        //get FB Database and navigate to the specified article passed in from the intent.
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mArticlesDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles");

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
                        //since our response is an Article is must be stored as an Article Object.
                        mArticle = response.body();
                        //save new article to the database
                        storeNewArticle(mArticle, mUser.getUid(), mArticlesDBref);
                        //show the article fragment in the container
                        showArticleFragment();
                    } else {
                        Log.e(TAG, "API Response Failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //on failure, Toast error code
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API Failed: " + t.getMessage());
                }
            });
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
            saveArticleMenuItem.setIcon(R.drawable.ic_save_white_24dp);
            saveArticleMenuItem.setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_article_menu_item:
                if (item.isChecked()) {
                    unSaveArticle(mArticle, mArticlesDBref);
                    item.setIcon(R.drawable.ic_save_outline_white);
                    item.setChecked(false);
                } else {
                    saveArticle(mArticle, mArticlesDBref);
                    item.setIcon(R.drawable.ic_save_white_24dp);
                    item.setChecked(true);
                }
                return true;

            case R.id.share_menu_item:
                return true;

            case R.id.add_tags_menu_item:
                //tagArticle(mTagsDB);
                //launch AddTag Activity and pass on article keyid
                Intent intent = new Intent(getApplicationContext(), AddTagToArticleActivity.class);
                //intent.putExtra(ARTICLE_KEY_ID, mArticle.getKeyid());
                startActivity(intent);
                return true;

            case R.id.web_view_menu_item:
                openWebView();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Removes the article object found at /articles/$articlekeyid in the DB
     *
     * @param articles database reference of the article
     */
    private void unSaveArticle(Article article, DatabaseReference articles) {

        article.setSaved(false);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyid(), article);
        //unSave the specified article
        articles.updateChildren(writeMap);

        Toast.makeText(getApplicationContext(), "Article Unsaved.", Toast.LENGTH_SHORT).show();
    }

    private void saveArticle(Article article, DatabaseReference articles) {

        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyid(), article);
        //unSave the specified article
        articles.updateChildren(writeMap);

        Toast.makeText(getApplicationContext(), "Article saved.", Toast.LENGTH_SHORT).show();

    }

    /**
     * Saves an Article node and user id into the Articles database reference.
     * A keyid is created and stored in the Article object as well.
     *
     * @param article  article to be written to database
     * @param uid      user id of the user that is currently logged in
     * @param articles database reference of Articles
     */
    private void storeNewArticle(Article article, String uid, DatabaseReference articles) {

        //create a unique keyid for the Article
        String key = articles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyid(key);
        article.setUid(uid);
        article.setUrl(mURLreceived);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(key, article);
        //update the children of "articles" in the DB with the passed in Hash Map
        articles.updateChildren(writeMap);

    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    public void showArticleFragment() {

        ArticleFragment articleFragment = ArticleFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, mArticle);
        articleFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.article_container, articleFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    public void openWebView() {

        Uri webpage = Uri.parse(mArticle.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {

        if (mArticle.isSaved()) {
            super.onDestroy();
        } else {
            //if user does not save the Article, remove it from the DB Ref
            mArticlesDBref.child(mArticle.getKeyid()).removeValue();
            super.onDestroy();
        }

    }
}
