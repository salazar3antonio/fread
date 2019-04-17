package com.freadapp.fread.view_holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
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
    public void bindToTag(Tag tag, Article article) {

        mArticle = article;
        mTag = tag;
        mUserTagRef = FirebaseUtils.getUserTags();
        mUserArticleRef = FirebaseUtils.getUserArticles().child(mArticle.getKeyId());

        mTagName.setText(mTag.getTagName());

        setTagCheckBox();
        setTagOnCheckChanged();

    }

    private void setTagCheckBox() {

        mUserArticleRef.child(FirebaseUtils.FB_ARTICLE_TAGS).child(mTag.getKeyId()).addValueEventListener(new ValueEventListener() {
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
                    FirebaseUtils.addTagKeyToArticle(mUserArticleRef, mTag);
                    FirebaseUtils.addArticleKeyToTag(mUserTagRef, mArticle, mTag);
                } else {
                    FirebaseUtils.removeArticleKeyFromTag(mUserTagRef, mArticle, mTag);
                    FirebaseUtils.removeTagKeyFromArticle(mUserArticleRef, mTag);
                }

            }

        });

    }

}