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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by salaz on 2/26/2018.
 */

public class NewArticleActivity extends AppCompatActivity {

    public static final String TAG = NewArticleActivity.class.getName();
    public static final String ARTICLE_BUNDLE = "article_bundle";
    public static final String ARTICLE_FRAGMENT_TAG = "article_fragment_tag";

    private Article mArticle;
    private String mURLreceived;
    private DatabaseReference mDBRef_Article;

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mDBRef_Article = databaseReference.child("articles").push();

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
            //using the articleAPI object to call the GET method. Passed in URL.
            Call<Article> call = articleAPI.getArticle(mURLreceived, true);
            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    //Assign mArticle to the Response body (JSON object).
                    //since our response is an Article object is must be stored as an Article Object.
                    mArticle = response.body();
                    showCompletedArticleFragment();
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //on failure, Toast error code
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "API Failed: " + t.getMessage());
                }
            });
        } else {
            showCompletedArticleFragment();
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
                ArticleFragment.writeArticleToDB(mArticle, mDBRef_Article);
                Toast.makeText(getApplicationContext(), mDBRef_Article.getKey() + " was saved.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.article_overflow_menu:
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    private void showCompletedArticleFragment() {

        ArticleFragment articleFragment = ArticleFragment.newInstance();

        Bundle articleBundle = new Bundle();
        articleBundle.putParcelable(ARTICLE_BUNDLE, mArticle);
        articleFragment.setArguments(articleBundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, articleFragment, ARTICLE_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

}
