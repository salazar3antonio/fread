package com.freadapp.fread.article;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.tag.AddTagsDialogFragment;
import com.google.firebase.database.DatabaseReference;

import static com.freadapp.fread.article.ArticleDetailFragment.ARTICLE_DETAIL_FRAGMENT_TAG;
import static com.freadapp.fread.tag.AddTagsDialogFragment.ADD_TAGS_DIALOG_FRAGMENT_TAG;

/**
 * This class populates the details of the Article
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String TAG = ArticleDetailActivity.class.getName();

    public static final String ARTICLE_BUNDLE = "article_bundle";

    private Article mArticle;
    private DatabaseReference mUserArticles = FirebaseUtils.getUserArticles();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_fragment_container);

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            mArticle = intentExtras.getParcelable(ArticlesMainFragment.ARTICLE_MODEL);
        }

        if (savedInstanceState != null) {
            mArticle = savedInstanceState.getParcelable(ARTICLE_BUNDLE);
        }

        if (mArticle != null) {
            loadToolbarArticleImage(mArticle);
            showArticleDetailFragment(mArticle);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.article_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_tags_menu_item:
                showAddTagsDialogFragment(mArticle);
                return true;

            case R.id.delete_menu_item:
                FirebaseUtils.deleteArticle(getApplicationContext(), mArticle, mUserArticles);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mArticle != null) {
            //save Article object during config change
            outState.putParcelable(ARTICLE_BUNDLE, mArticle);
        }
    }

    /**
     * Creates new ArticleFragment, bundles the Article object and commits a FragmentTransaction to display the completed ArticleFragment
     */
    public void showArticleDetailFragment(Article article) {

        ArticleDetailFragment articleDetailFragment = ArticleDetailFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, article);
        articleDetailFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.article_container, articleDetailFragment, ARTICLE_DETAIL_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    /**
     * Creates new AddTagsDialogFragment, bundles the Article object and shows the dialog over the UI.
     */
    public void showAddTagsDialogFragment(Article article) {
        //create a new dialog fragment that allows the user to add/create tags for the article
        AddTagsDialogFragment addTagsDialogFragment = AddTagsDialogFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_BUNDLE, article);
        addTagsDialogFragment.setArguments(bundle);

        addTagsDialogFragment.show(getSupportFragmentManager(), ADD_TAGS_DIALOG_FRAGMENT_TAG);
    }

    public void loadToolbarArticleImage(Article article) {

        ImageView toolbarImageView = findViewById(R.id.iv_article_toolbar);

        if (article.getImage().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No image link", Toast.LENGTH_SHORT).show();
        } else {
            Glide.with(this).load(article.getImage()).into(toolbarImageView);
        }
    }

}
