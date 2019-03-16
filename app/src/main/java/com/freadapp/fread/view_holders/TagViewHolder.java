package com.freadapp.fread.view_holders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.tag.TagDetailFragment;

import static com.freadapp.fread.tag.TagsMainFragment.TAG_BUNDLE;
import static com.freadapp.fread.tag.TagsMainFragment.TAG_DETAIL_FRAGMENT_TAG;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TextView mTagNameTextView;
    private TextView mNumberOfArticles;
    private Context mContext;

    public TagViewHolder(Context context, View itemView, int tagResourceId) {
        super(itemView);

        mContext = context;
        mTagNameTextView = itemView.findViewById(tagResourceId);
        mNumberOfArticles = itemView.findViewById(R.id.tv_articles_number);

    }

    public void bindToTag(Tag tag) {


        mTagNameTextView.setText(tag.getTagName());

        if (tag.getArticlesTagged() != null) {
            String numArticlesFormatted = mContext.getString(R.string.number_of_articles, String.valueOf(tag.getArticlesTagged().size()));
            mNumberOfArticles.setText(numArticlesFormatted);
        } else {
            mNumberOfArticles.setText("0");
        }


    }

}
