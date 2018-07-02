package com.freadapp.fread.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.view_holders.ArticleTagViewHolder;

import java.util.List;

public class ArticleTagsAdapter extends RecyclerView.Adapter<ArticleTagViewHolder> {

    private LayoutInflater mInflater;
    private List<Object> mTags;

    public ArticleTagsAdapter(Context context, Article article) {

        mTags = article.getArticleTags();
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public ArticleTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = mInflater.inflate(R.layout.article_tag_list_item, parent, false);
        return new ArticleTagViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(ArticleTagViewHolder holder, int position) {

        if (mTags != null) {
            String tagName = mTags.get(position).toString();
            holder.mTagNameTextView.setText(tagName);
        }
    }

    @Override
    public int getItemCount() {
        if (mTags != null) {
            return mTags.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
