package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Tag;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TextView mTagNameTextView;
    private TextView mNumberOfArticles;

    public TagViewHolder(View itemView) {
        super(itemView);

        mTagNameTextView = itemView.findViewById(R.id.tv_tag_main_name);
        mNumberOfArticles = itemView.findViewById(R.id.tv_articles_number);

    }

    public void bindToTag(Context context, Tag tag) {

        mTagNameTextView.setText(tag.getTagName());

        if (tag.getArticlesTagged() != null) {
            int articlesTaggedSize = tag.getArticlesTagged().size();
            String formattedString = getFormattedString(context, articlesTaggedSize);
            mNumberOfArticles.setText(formattedString);
        } else {
            mNumberOfArticles.setText(R.string.empty_number_of_articles);
        }

    }

    private String getFormattedString(Context context, int articlesTaggedSize) {

        return context.getString(R.string.number_of_articles, String.valueOf(articlesTaggedSize));

    }

}
