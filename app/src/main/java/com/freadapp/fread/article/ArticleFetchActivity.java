package com.freadapp.fread.article;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.api.FetchArticleAPI;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.helpers.LoadingDialogFragment;
import com.freadapp.fread.helpers.NetworkUtils;
import com.freadapp.fread.signin.SignInFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.freadapp.fread.helpers.LoadingDialogFragment.*;

/**
 * This class wil handle the fetching of the Article information via Retrofit API
 * The user is able to save the Article to the Firebase Database and then add Tags via a SnackBar action.
 */

public class ArticleFetchActivity extends ArticleDetailActivity implements SignInFragment.OnSignInSuccessListener {

    public static final String TAG = ArticleFetchActivity.class.getName();

    private static final String ARTICLE_SAVED = "article_is_saved";

    private String mUrlIntentExtra;
    private Article mArticle;
    private DatabaseReference mUserArticles;
    private Snackbar mArticleSavedSnackBar;
    private boolean mArticleIsSaved;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.article_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        mArticleSavedSnackBar = Snackbar.make(findViewById(R.id.main_content), R.string.snackbar_article_saved, Snackbar.LENGTH_LONG);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState != null) {
            mArticle = savedInstanceState.getParcelable(ArticleDetailActivity.ARTICLE_BUNDLE);
            boolean articleSavedState = savedInstanceState.getBoolean(ARTICLE_SAVED);
            setArticleIsSaved(articleSavedState);
        }

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            //assign Extra Text from the Intent that the Web Browser started.
            mUrlIntentExtra = intentExtras.getString(Intent.EXTRA_TEXT);
        }

        if (mUser != null) {

            mUserArticles = FirebaseUtils.getUserArticles();

            if (mArticle == null) {
                showLoadingFragment(true, getSupportFragmentManager());
                fetchArticle();

            } else {
                showLoadingFragment(false, getSupportFragmentManager());
                loadToolbarArticleImage(mArticle);
                showArticleDetailFragment(mArticle);
                getSupportActionBar().show();
            }
        } else {
            Toast.makeText(this, "Must be signed in.", Toast.LENGTH_SHORT).show();
            showSignInFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.article_fetch_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isArticleIsSaved()) {
            MenuItem saveArticleMenuItem = menu.findItem(R.id.save_fetched_article_menu_item);
            saveArticleMenuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
            saveArticleMenuItem.setChecked(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_fetched_article_menu_item:
                if (item.isChecked()) {

                    FirebaseUtils.deleteArticle(mArticle, mUserArticles);

                    item.setIcon(R.drawable.ic_bookmark_border_white_24dp);
                    item.setChecked(false);
                    setArticleIsSaved(false);

                    if (mArticleSavedSnackBar.isShown()) {
                        mArticleSavedSnackBar.dismiss();
                    }

                } else {

                    FirebaseUtils.saveArticle(mArticle, mUserArticles, mUrlIntentExtra);

                    item.setIcon(R.drawable.ic_bookmark_white_24dp);
                    item.setChecked(true);
                    setArticleIsSaved(true);

                    mArticleSavedSnackBar.setAction(R.string.snackbar_add_tags, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAddTagsDialogFragment(mArticle);
                        }
                    });
                    mArticleSavedSnackBar.show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof SignInFragment) {
            SignInFragment signInFragment = (SignInFragment) fragment;
            signInFragment.setOnSignInSuccessListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mArticle != null) {
            //save Article object during config change
            outState.putParcelable(ArticleDetailActivity.ARTICLE_BUNDLE, mArticle);
            outState.putBoolean(ARTICLE_SAVED, mArticleIsSaved);
        }
    }

    private void showSignInFragment() {
        SignInFragment signInFragment = SignInFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_article_container, signInFragment).commit();
    }

    public boolean isArticleIsSaved() {
        return mArticleIsSaved;
    }

    public void setArticleIsSaved(boolean isSaved) {
        this.mArticleIsSaved = isSaved;
    }

    @Override
    public void onSignInSuccess(boolean signInSuccess) {
        if (signInSuccess) {
            showLoadingFragment(true, getSupportFragmentManager());
            fetchArticle();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUserArticles = FirebaseUtils.getUserArticles();
        }
    }

    private void fetchArticle() {

        if (NetworkUtils.isNetworkAvailableAndConnected(getApplicationContext())) {
            //building up the Retrofit object to begin calling the API
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //passing in the ArticleAPI interface class into the retrofit Object
            FetchArticleAPI fetchArticleAPI = retrofit.create(FetchArticleAPI.class);

            //using the articleAPI object to call the GET method of the API. Passes in the URL received from the Intent.
            Call<Article> call = fetchArticleAPI.getArticle(mUrlIntentExtra, true);
            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {

                    if (response.isSuccessful()) {
                        showLoadingFragment(false, getSupportFragmentManager());
                        //Assign mArticle to the API Response Body (JSON object).
                        mArticle = response.body();
                        loadToolbarArticleImage(mArticle);
                        showArticleDetailFragment(mArticle);
                        getSupportActionBar().show();

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
            finish();
            Toast.makeText(this, "Connect to the internet and try again.", Toast.LENGTH_LONG).show();
        }

    }
}
