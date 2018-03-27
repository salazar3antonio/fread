package com.freadapp.fread.article_classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public static final String ARTICLE_KEY_ID = "article_key_id";

    private Article mArticle = new Article();
    private String mURLreceived;
    private String mArticleKeyID;
    private DatabaseReference mArticleDBref;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_fragment_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        //we don't need to pass an object anymore from ArticleFeedFragment. We only need the Article KeyId.
        //we then pass that id into the specified location and assign mArticle to the Article object found at that location



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            //assign Article KeyID extra from the Intent that started this activity
            mArticleKeyID = bundle.getString(ArticleFeedFragment.FB_ARTICLE_KEY_ID);
           //store the ArticleKeyID into the Shared Preferences file
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.article_keyid_pref), mArticleKeyID);
            editor.apply();

            //assign Extra Text from the Intent that the Web Browser started.
            mURLreceived = bundle.getString(Intent.EXTRA_TEXT);

        } else {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            mArticleKeyID = sharedPref.getString(getString(R.string.article_keyid_pref), "default");
        }

        //get FB Database and navigate to the specified article passed in from the intent.
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mArticleDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("articles").child(mArticleKeyID);
        //add listener to mArticleDB at the specified KeyID location
        mArticleDBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //assign the Article object found at the specified DB location
                mArticle = dataSnapshot.getValue(Article.class);
                showArticleFragment();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (findViewById(R.id.article_container) != null) {
            //placing in the loading screen for when quiz api is being called
            ArticleLoadingFragment articleLoadingFragmentLoadingFragment = ArticleLoadingFragment.newInstance();
            articleLoadingFragmentLoadingFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.article_container, articleLoadingFragmentLoadingFragment).commit();
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
                    removeArticle(mArticleDBref);
                    item.setIcon(R.drawable.ic_save_outline_white);
                    item.setChecked(false);
                } else {
                    saveArticle(mArticle, mUser.getUid(), mArticleDBref);
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
                intent.putExtra(ARTICLE_KEY_ID, mArticleKeyID);
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
    private void removeArticle(DatabaseReference articles) {
        //remove the specified article
        articles.removeValue();
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

        //create a unique keyid for the Article
        String key = articles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyid(key);
        article.setUid(uid);
        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
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
        fragmentTransaction.replace(R.id.article_container, articleFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    private void openWebView() {

        Uri webpage = Uri.parse(mArticle.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);

    }

}
