package com.freadapp.fread.tag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.article.ArticleDetailActivity;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.view_holders.ArticleViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.freadapp.fread.tag.TagsMainFragment.TAG_KEY_ID;

public class TagDetailFragment extends Fragment {

    public static final String TAG = TagDetailFragment.class.getName();

    public static final String ARTICLE_MODEL = "article_model";

    private DatabaseReference mUserArticles = FirebaseUtils.getUserArticles();
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mArticleRecyclerView;
    private String mTagKeyId;


    public static TagDetailFragment newInstance() {
        return new TagDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTagKeyId = getArguments().getString(TAG_KEY_ID);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate fragment layout of article feed
        View view = inflater.inflate(R.layout.main_articles_fragment, container, false);

        mArticleRecyclerView = view.findViewById(R.id.rv_articles_main_list);

        setArticlesByTagAdapter();

        return view;
    }

    private void setArticlesByTagAdapter() {

        //query only Articles that contain the passed in TagKeyId
        Query query = mUserArticles.orderByChild(FirebaseUtils.FB_ARTICLE_TAGS + "/" + mTagKeyId).equalTo(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(Article.class, R.layout.article_list_item,
                ArticleViewHolder.class, query) {

            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, final Article article, final int position) {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch a new detailed article activity passing the article at the clicked position through an intent
                        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra(ARTICLE_MODEL, article);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToArticle(getContext(), article);

            }
        };

        mArticleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mArticleRecyclerView.setAdapter(mFirebaseAdapter);

    }

}
