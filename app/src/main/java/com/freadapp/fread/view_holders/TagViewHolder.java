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
    }

    public void bindToTag(Context context, Tag tag) {

        mTagNameTextView.setText(tag.getTagName());

    }

    private String getFormattedString(Context context, int articlesTaggedSize) {

        return context.getString(R.string.number_of_articles, String.valueOf(articlesTaggedSize));

    }

}
