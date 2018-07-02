package com.freadapp.fread.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;

import java.util.List;

public class ArticleTagViewHolder extends RecyclerView.ViewHolder {

    public TextView mTagNameTextView;

    public ArticleTagViewHolder(View itemView) {
        super(itemView);

        mTagNameTextView = itemView.findViewById(R.id.article_tag_name);

    }

}
