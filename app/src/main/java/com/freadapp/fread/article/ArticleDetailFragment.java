package com.freadapp.fread.article;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;

import static com.freadapp.fread.tag.AddTagsDialogFragment.ADD_TAGS_DIALOG_FRAGMENT_TAG;

public class ArticleDetailFragment extends Fragment {

    public static final String TAG = ArticleDetailFragment.class.getName();

    public static final String ARTICLE_BUNDLE = "article_bundle";
    public static final String ARTICLE_DETAIL_FRAGMENT_TAG = "article_detail_fragment_tag";

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mArticleView;
    private TextView mPUBdateView;
    private Article mArticle;

    //public constructor
    public ArticleDetailFragment() {
    }

    //static method that returns a new ArticleFragment object
    public static ArticleDetailFragment newInstance() {
        return new ArticleDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mArticle = getArguments().getParcelable(ARTICLE_BUNDLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.article_detail_fragment, container, false);

        mTitleView = view.findViewById(R.id.title_view);
        mAuthorView = view.findViewById(R.id.author_view);
        mArticleView = view.findViewById(R.id.article_view);
        mPUBdateView = view.findViewById(R.id.pubdate_view);

        setTextViews();

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ArticleDetailActivity.ARTICLE_BUNDLE, mArticle);
    }

    private void setTextViews() {

        mTitleView.setText(mArticle.getTitle());
        mAuthorView.setText(mArticle.getAuthor());
        mArticleView.setText(mArticle.getArticle());
        mPUBdateView.setText(mArticle.getPublishDate());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment addTagsDialogFragment = getFragmentManager().findFragmentByTag(ADD_TAGS_DIALOG_FRAGMENT_TAG);

        //this attaches the AddTagsToArticleFragment upon a config change
        if (addTagsDialogFragment != null) {
            addTagsDialogFragment.setTargetFragment(this, 1);
        }

    }
}
