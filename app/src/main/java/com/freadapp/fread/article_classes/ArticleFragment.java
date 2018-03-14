package com.freadapp.fread.article_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.squareup.picasso.Picasso;

/**
 * Created by salaz on 2/11/2018.
 */

public class ArticleFragment extends Fragment {

    public static final String TAG = ArticleFragment.class.getName();

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mArticleView;
    private TextView mPUBdateView;
    private ImageView mArticleImageView;
    private Article mArticle;

    //todo tony save article state during rotation. need to prevent additional calls to the api.

    //public constructor
    public ArticleFragment() {
    }

    //static method that returns a new ArticleFragment object
    public static ArticleFragment newInstance() {
        return new ArticleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            mArticle = savedInstanceState.getParcelable(ArticleActivity.ARTICLE_BUNDLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.article_fragment, container, false);

        mTitleView = view.findViewById(R.id.title_view);
        mAuthorView = view.findViewById(R.id.author_view);
        mArticleView = view.findViewById(R.id.article_view);
        mPUBdateView = view.findViewById(R.id.pubdate_view);
        mArticleImageView = view.findViewById(R.id.image_article_view);

        setTextViews();

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ArticleActivity.ARTICLE_BUNDLE, mArticle);
    }

    private void setTextViews() {

        mTitleView.setText(mArticle.getTitle());
        mAuthorView.setText(mArticle.getAuthor());
        mArticleView.setText(mArticle.getArticle());
        mPUBdateView.setText(mArticle.getPublishDate());

        if (mArticle.getImage() == "") {
            Toast.makeText(getContext(), "no image link", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.with(getContext()).load(mArticle.getImage()).into(mArticleImageView);
        }


    }

}
