package com.freadapp.fread.article;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.tag.AddTagToArticleActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by salaz on 2/26/2018.
 * * This Activity will handle the Article coming from the User's main list of articles.
 */

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String TAG = ArticleDetailActivity.class.getName();
    public static final String ARTICLE_BUNDLE = "article_bundle";
    public static final String ARTICLE_FRAGMENT_TAG = "article_fragment_tag";
    public static final String ARTICLE_KEY_ID = "article_key_id";

    private Article mArticle = new Article();
    private DatabaseReference mUserArticle;
    private DatabaseReference mUserArticles;
    private FirebaseUser mUser;
    private String mUserUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_fragment_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        mUserArticles = FbDatabase.getUserArticles(mUserUid);

        //get the intent that started this activity and get the extras which includes the Article object
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            mArticle = bundle.getParcelable(ArticleFeedFragment.ARTICLE_MODEL);

            //store the ArticleKeyID into the Shared Preferences file
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.article_keyid_pref), mArticle.getKeyId());
            editor.apply();

        } else {
            //if bundle is null, retrieve the last saved keyID from SharedPreferences and set it to mArticle
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            String keyid = sharedPref.getString(getString(R.string.article_keyid_pref), "default");
            mArticle.setKeyId(keyid);
        }


        //DB reference to the specified Article. Defined by its Article KeyID
        mUserArticle = FbDatabase.getUserArticles(mUser.getUid()).child(mArticle.getKeyId());
        mUserArticle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if (article != null) {
                    showArticleFragment(article);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                    //unSaveArticle(mArticle, mUserArticle);
                    FbDatabase.unSaveArticle(getApplicationContext(), mArticle, mUserArticles);
                    item.setIcon(R.drawable.ic_save_outline_white);
                    item.setChecked(false);
                } else {
                    FbDatabase.saveArticle(getApplicationContext(), mArticle, mUserArticles, null, mUserUid);
                    item.setIcon(R.drawable.ic_save_white_24dp);
                    item.setChecked(true);
                }
                return true;

            case R.id.share_menu_item:
                return true;

            case R.id.add_tags_menu_item:
                //launch AddTag Activity and pass on article keyid
                Intent intent = new Intent(getApplicationContext(), AddTagToArticleActivity.class);
                intent.putExtra(ARTICLE_KEY_ID, mArticle.getKeyId());
                startActivity(intent);
                return true;

            case R.id.web_view_menu_item:
                FbDatabase.openArticleWebView(getParent(), mArticle.getUrl());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Removes the article object found at /articles/$articlekeyid in the DB
     *
     * @param specificArticle database reference of the article
     */
    private void unSaveArticle(Article article, DatabaseReference specificArticle) {

        specificArticle.removeValue();

        Toast.makeText(getApplicationContext(), "Article Unsaved.", Toast.LENGTH_SHORT).show();
    }

    private void saveArticle(Article article, DatabaseReference articles) {

        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyId(), article);
        //unSave the specified article
        articles.updateChildren(writeMap);

        Toast.makeText(getApplicationContext(), "Article saved.", Toast.LENGTH_SHORT).show();

    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    public void showArticleFragment(Article article) {

        ArticleDetailFragment articleDetailFragment = ArticleDetailFragment.newInstance();
        mArticle = article;

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, mArticle);
        articleDetailFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.article_container, articleDetailFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    public void openWebView() {

        Uri webpage = Uri.parse(mArticle.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);

    }

}
