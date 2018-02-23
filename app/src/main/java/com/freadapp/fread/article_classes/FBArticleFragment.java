package com.freadapp.fread.article_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.MainActivity;
import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.squareup.picasso.Picasso;

/**
 * Created by salaz on 2/22/2018.
 */

public class FBArticleFragment extends Fragment {

    public static final String TAG = FBArticleFragment.class.getName();

    private Article mArticle;
    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mArticleView;
    private TextView mPUBdateView;
    private ImageView mArticleImageView;


    private Button mSaveArticleButton;
    private Button mDeleteArticleButton;


    public FBArticleFragment() {
    }

    public static FBArticleFragment newInstance() {
        return new FBArticleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticle = new Article();

        if (getArguments() != null) {
            mArticle = getArguments().getParcelable(MainActivity.FB_ARTICLE);
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
        mSaveArticleButton = view.findViewById(R.id.write_to_db);
        mDeleteArticleButton = view.findViewById(R.id.delete_to_db);

        mTitleView.setText(mArticle.getTitle());
        mAuthorView.setText(mArticle.getAuthor());
        mArticleView.setText(mArticle.getArticle());
        mPUBdateView.setText(mArticle.getPublishDate());

        if (mArticle.getImage() == "") {
            Toast.makeText(getContext(), "no image link", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.with(getContext()).load(mArticle.getImage()).into(mArticleImageView);
        }

        return view;

    }
}
