package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddTagDialogViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = AddTagDialogViewHolder.class.getName();

    private Tag mTag;
    private DatabaseReference mUserTagRef;
    private DatabaseReference mUserArticleRef;
    private Article mArticle;
    private TextView mTagName;
    private CheckBox mAddTagCheckBox;

    public AddTagDialogViewHolder(View itemView) {
        super(itemView);

        mTagName = itemView.findViewById(R.id.tag_name_dialog_textview);
        mAddTagCheckBox = itemView.findViewById(R.id.tag_dialog_checkbox);

    }

    /**
     * Binds the passed in Tag properties to the populated view.
     *
     * @param tag Tag model to be bound to the List Item Views
     */
    public void bindToTag(Tag tag, String userId, Article article) {

        mArticle = article;
        mTag = tag;
        mUserTagRef = FbDatabase.getUserTags(userId);
        mUserArticleRef = FbDatabase.getUserArticles(userId).child(mArticle.getKeyId());

        mTagName.setText(mTag.getTagName());

        setTagCheckBox();
        setTagOnCheckChanged();

    }

    private void setTagCheckBox() {

        mUserArticleRef.child("tags").child(mTag.getKeyid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value != null) {
                    mAddTagCheckBox.setChecked(true);
                } else {
                    mAddTagCheckBox.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setTagOnCheckChanged() {

        mAddTagCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    FbDatabase.addTagKeyToArticle(mUserArticleRef, mTag);
                    FbDatabase.addArticleKeyToTag(mUserTagRef, mArticle, mTag);
                } else {
                    FbDatabase.removeArticleKeyFromTag(mUserTagRef, mArticle, mTag);
                    FbDatabase.removeTagKeyFromArticle(mUserArticleRef, mTag);
                }

            }

        });

    }

}