package com.freadapp.fread.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freadapp.fread.R;
import com.freadapp.fread.view_holders.ArticleTagViewHolder;

import java.util.List;

public class ArticleTagsAdapter extends RecyclerView.Adapter<ArticleTagViewHolder> {

    private LayoutInflater mInflater;
    private List<Object> mArticleTags;

    public ArticleTagsAdapter(Context context, List<Object> articleTags) {

        mArticleTags = articleTags;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public ArticleTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = mInflater.inflate(R.layout.article_tag_list_item, parent, false);
        return new ArticleTagViewHolder(tagView);
    }

    @Override
    public void onBindViewHolder(ArticleTagViewHolder holder, int position) {

        holder.bindTagName(mArticleTags, position);
    }

    @Override
    public int getItemCount() {
        return mArticleTags.size();
    }

}
