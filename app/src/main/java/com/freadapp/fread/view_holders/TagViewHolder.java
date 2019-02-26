package com.freadapp.fread.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.freadapp.fread.R;

public class TagViewHolder extends RecyclerView.ViewHolder {

    public TextView mTagNameTextView;

    public TagViewHolder(View itemView, int tagResourceId) {
        super(itemView);

        mTagNameTextView = itemView.findViewById(tagResourceId);

    }

}
