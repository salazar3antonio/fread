package com.freadapp.fread.article;

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

import com.freadapp.fread.R;
import com.freadapp.fread.data.api.FetchArticleAPI;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.tag.AddTagToArticleActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.freadapp.fread.article.ArticleDetailActivity.ARTICLE_BUNDLE;
import static com.freadapp.fread.article.ArticleDetailActivity.ARTICLE_FRAGMENT_TAG;

/**
 * Created by salaz on 3/27/2018.
 * This Activity will handle the article coming from a web page. It will call the API and display the article.
 */

public class ArticleFetchActivity extends AppCompatActivity {

    public static final String TAG = ArticleFetchActivity.class.getName();

    private String mURLreceived;
    private Article mArticle;
    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUserArticles;

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

        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        mUserArticles = FbDatabase.getUserArticles(mUserUid);

        //building up the Retrofit object to begin calling the API
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //passing in the ArticleAPI interface class into the retrofit Object
        FetchArticleAPI fetchArticleAPI = retrofit.create(FetchArticleAPI.class);
        if (mURLreceived != null) {
            //using the articleAPI object to call the GET method of the API. Passes in the URL received from the Intent.
            Call<Article> call = fetchArticleAPI.getArticle(mURLreceived, true);
            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {

                    if (response.isSuccessful()) {
                        //Assign mArticle to the API Response Body (JSON object).
                        mArticle = response.body();
                        //show the Article in the Fragment
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
                    //unSaveArticle(mArticle, mArticlesDBref);
                    FbDatabase.unSaveArticle(getApplicationContext(), mArticle, mUserArticles);
                    item.setIcon(R.drawable.ic_save_outline_white);
                    item.setChecked(false);
                } else {
                    FbDatabase.saveArticle(getApplicationContext(), mArticle, mUserArticles, mURLreceived, mUser.getUid());
                    item.setIcon(R.drawable.ic_save_white_24dp);
                    item.setChecked(true);
                }
                return true;

            case R.id.share_menu_item:
                return true;

            case R.id.add_tags_menu_item:
                //launch AddTagActivity and pass the Article KeyID
                Intent intent = new Intent(getApplicationContext(), AddTagToArticleActivity.class);
                intent.putExtra(ArticleDetailActivity.ARTICLE_KEY_ID, mArticle.getKeyid());
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
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    public void showArticleFragment() {

        ArticleDetailFragment articleDetailFragment = ArticleDetailFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, mArticle);
        articleDetailFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.article_container, articleDetailFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

}
