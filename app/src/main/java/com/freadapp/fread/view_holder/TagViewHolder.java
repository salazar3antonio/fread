package com.freadapp.fread.view_holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Tag;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TextView mTagName;
    private CheckBox mTagCheckBox;

    public TagViewHolder(View itemView) {
        super(itemView);

        mTagName = itemView.findViewById(R.id.tag_name_textview);
        mTagCheckBox = itemView.findViewById(R.id.tag_checkbox);

    }
    /**
     * Binds the passed in Tag properties to the populated view.
     * @param tag Tag model to be bound to the List Item Views
     */
    public void bindToTag(Tag tag, Context context) {

        mTagName.setText(tag.getTagName());

    }



}
