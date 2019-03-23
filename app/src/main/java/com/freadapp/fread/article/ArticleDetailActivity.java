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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freadapp.fread.R;
import com.freadapp.fread.data.api.FetchArticleAPI;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.helpers.LoadingFragment;
import com.freadapp.fread.tag.AddTagsDialogFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.freadapp.fread.article.ArticleDetailFragment.ARTICLE_DETAIL_FRAGMENT_TAG;
import static com.freadapp.fread.tag.AddTagsDialogFragment.ADD_TAGS_DIALOG_FRAGMENT_TAG;

/**
 * This class populates the details of the Article
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String TAG = ArticleDetailActivity.class.getName();

    public static final String ARTICLE = "fetched_article";
    public static final String ARTICLE_KEY_ID = "article_key_id";
    public static final String ARTICLE_BUNDLE = "article_bundle";


    private Article mArticle = new Article();
    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mUserArticles;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_fragment_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (findViewById(R.id.article_container) != null) {
            //placing in the loading screen for when quiz api is being called
            LoadingFragment loadingFragment = LoadingFragment.newInstance();
            loadingFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.article_container, loadingFragment).commit();
        }

        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        mUserArticles = FbDatabase.getUserArticles(mUserUid);

        Bundle intentExtras = getIntent().getExtras();

        if (intentExtras != null) {
            mArticle = intentExtras.getParcelable(ArticlesMainFragment.ARTICLE_MODEL);
            if (mArticle != null) {
                loadToolbarImage();
                showArticleFragment(mArticle);
            }
        }

        if (savedInstanceState != null) {
            //this handles config changes
            mArticle = savedInstanceState.getParcelable(ARTICLE);
            loadToolbarImage();
            showArticleFragment(mArticle);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.fetch_article_menu_item, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //change save article menu item to be checked if article has been saved.
        if (mArticle != null && mArticle.isSaved()) {
            MenuItem saveArticleMenuItem = menu.findItem(R.id.save_fetched_article_menu);
            saveArticleMenuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
            saveArticleMenuItem.setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_fetched_article_menu:
                if (item.isChecked()) {
                    FbDatabase.deleteArticle(getApplicationContext(), mArticle, mUserArticles);
                    item.setIcon(R.drawable.ic_bookmark_border_white_24dp);
                    item.setChecked(false);
                    mArticle.setSaved(false);
                } else {
                    FbDatabase.setSavedArticle(getApplicationContext(), mUserArticles, mArticle, true);
                    item.setIcon(R.drawable.ic_bookmark_white_24dp);
                    item.setChecked(true);
                    mArticle.setSaved(true);
                }
                return true;

            case R.id.add_tags_menu_item:

                if (mArticle.getKeyId() == null || !mArticle.isSaved()) {
                    //Set saved menu item to checked. Let's user know the Article has been saved.
                    MenuItem saveArticleMenuItem = mOptionsMenu.findItem(R.id.save_fetched_article_menu);
                    saveArticleMenuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
                    saveArticleMenuItem.setChecked(true);
                }

                //create a new dialog fragment that allows the user to add/create tags for the article
                AddTagsDialogFragment addTagsDialogFragment = AddTagsDialogFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString(ARTICLE_KEY_ID, mArticle.getKeyId());
                bundle.putParcelable(ARTICLE_BUNDLE, mArticle);
                addTagsDialogFragment.setArguments(bundle);

                addTagsDialogFragment.setTargetFragment(getSupportFragmentManager().findFragmentByTag(ARTICLE_DETAIL_FRAGMENT_TAG), 1);
                addTagsDialogFragment.show(getSupportFragmentManager(), ADD_TAGS_DIALOG_FRAGMENT_TAG);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    public void showArticleFragment(Article article) {

        ArticleDetailFragment articleDetailFragment = ArticleDetailFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, article);
        articleDetailFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.article_container, articleDetailFragment, ARTICLE_DETAIL_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mArticle != null) {
            //save Article object during config change
            outState.putParcelable(ARTICLE, mArticle);
        }
    }

    private void loadToolbarImage() {

        ImageView toolbarImage = findViewById(R.id.iv_article_toolbar);

        if (mArticle.getImage().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No image link", Toast.LENGTH_SHORT).show();
        } else {
            Glide.with(this).load(mArticle.getImage()).into(toolbarImage);
        }
    }

}
