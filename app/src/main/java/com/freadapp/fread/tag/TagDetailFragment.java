package com.freadapp.fread.tag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    public static final String TAG_DETAIL_SCROLL_POSITION_KEY = "tag_detail_scroll_position_key";

    private DatabaseReference mUserArticles;
    private RecyclerView mArticleRecyclerView;
    private String mTagKeyId;
    private LinearLayoutManager mLinearLayoutManger;
    private Query mArticlesWithTags;
    private SharedPreferences mSharedPreferences;

    public static TagDetailFragment newInstance() {
        return new TagDetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTagKeyId = getArguments().getString(TAG_KEY_ID);
        }

        mUserArticles = FirebaseUtils.getUserArticles();
        mArticlesWithTags = mUserArticles.orderByChild(FirebaseUtils.FB_ARTICLE_TAGS + "/" + mTagKeyId).equalTo(true);
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate fragment layout of article feed
        View view = inflater.inflate(R.layout.main_articles_fragment, container, false);

        mArticleRecyclerView = view.findViewById(R.id.rv_articles_main_list);
        mLinearLayoutManger = new LinearLayoutManager(getContext());
        mArticleRecyclerView.setLayoutManager(mLinearLayoutManger);

        attachRecyclerViewAdapter();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int firstCompletelyVisibleItemPosition = mLinearLayoutManger.findFirstCompletelyVisibleItemPosition();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(TAG_DETAIL_SCROLL_POSITION_KEY, firstCompletelyVisibleItemPosition);
        editor.apply();

    }

    @NonNull
    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Article> options =
                new FirebaseRecyclerOptions.Builder<Article>()
                        .setQuery(mArticlesWithTags, Article.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(options) {
            @Override
            public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_list_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ArticleViewHolder holder, int position, @NonNull final Article article) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //launch a new detailed article activity passing the article at the clicked position through an intent
                        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                        intent.putExtra(ARTICLE_MODEL, article);
                        startActivity(intent);
                    }
                });

                holder.bindToArticle(getContext(), article);

            };

        };
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int scrollPosition = mSharedPreferences.getInt(TAG_DETAIL_SCROLL_POSITION_KEY, 0);
                mLinearLayoutManger.scrollToPosition(scrollPosition);
            }
        });

        mArticleRecyclerView.setAdapter(adapter);
    }

}
