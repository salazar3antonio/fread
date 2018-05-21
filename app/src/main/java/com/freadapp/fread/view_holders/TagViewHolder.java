package com.freadapp.fread.view_holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.List;

public class TagViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    public static final String TAG = TagViewHolder.class.getName();

    private TextView mTagName;
    public CheckBox mTagCheckBox;
    private String mArticleKeyID;
    private String mUserID;
    private DatabaseReference mUserArticleRef;
    private DatabaseReference mUserTagsRef;
    private Tag mTag;

    public TagViewHolder(View itemView) {
        super(itemView);

        mTagName = itemView.findViewById(R.id.tag_name_textview);
        mTagCheckBox = itemView.findViewById(R.id.tag_checkbox);
        mTagCheckBox.setOnCheckedChangeListener(this);

    }

    /**
     * Binds the passed in Tag properties to the populated view.
     *
     * @param tag Tag model to be bound to the List Item Views
     */
    public void bindToTag(Tag tag, String articleKeyID, String userID) {

        mArticleKeyID = articleKeyID;
        mUserID = userID;
        mTag = tag;
        mTagName.setText(mTag.getTagName());
        mUserArticleRef = FbDatabase.getUserArticles(mUserID).child(mArticleKeyID);
        mUserTagsRef = FbDatabase.getUserTags(mUserID).child(mTag.getKeyid());

        if (mTag.getTaggedArticles() != null) {
                if (isArticleTagged(mTag, mArticleKeyID)) {
                    mTagCheckBox.setChecked(true);
                } else {
                    mTagCheckBox.setChecked(false);
                }
        } else {
            mTagCheckBox.setChecked(false);
        }




}

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (b) {

            if (mTag.getTaggedArticles() == null) {
                //if no ArticleTags exists then add the Tag to the Article
                FbDatabase.addTagToArticle(mUserArticleRef, mTag, mUserTagsRef, mArticleKeyID);
                Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
            } else if (isArticleTagged(mTag, mArticleKeyID)) {
                //if the ArticleKeyID is already in the list of TaggedArticles, do not add the ArticleKeyID
                Log.i(TAG, "TAG ->> " + mTag.getTagName() + " <<- is in ArticleTags");
            } else {
                // else if mArticleTags is not null and the Tag is not found in the ArticleTags list, then add the Tag to the Article
                FbDatabase.addTagToArticle(mUserArticleRef, mTag, mUserTagsRef, mArticleKeyID);
                Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
            }

        } else {
            //remove Tag from ArticleTags list and remove ArticleKeyID from TaggedArticles
            FbDatabase.removeTagFromArticle(mUserArticleRef, mTag, mUserTagsRef, mArticleKeyID);
            Log.i(TAG, "REMOVED TAG ->> " + mTag.getTagName() + " <<- from ArticleTags");
        }

    }


    /**
     * Method to check if the passed in ArticleKeyID is in the TaggedArticle list of the Tag model. Will return true if the ArticleKeyID is found in the ArticleTags list.
     * Can be used to prevent duplicates.
     *
     * @param tag         Tag model to be checked
     * @param articleKeyID KeyID of article
     */
    private boolean isArticleTagged(Tag tag, String articleKeyID) {

        boolean articleTagged = false;

        for (Object taggedArticle : tag.getTaggedArticles()) {
            if (articleKeyID.equals(taggedArticle)) {
                articleTagged = true;
                break;
            }
        }

        return articleTagged;
    }


}
