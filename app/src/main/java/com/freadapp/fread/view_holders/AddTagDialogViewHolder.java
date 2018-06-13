package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.database.DatabaseReference;

public class AddTagDialogViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = AddTagDialogViewHolder.class.getName();

    private Tag mTag;
    private String mUserID;
    private DatabaseReference mUserTagRef;
    private DatabaseReference mUserArticleRef;
    private String mArticleKeyId;

    private Context mContext;
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
    public void bindToTag(Context context, final Tag tag, String userID, String articleKeyId) {

        mArticleKeyId = articleKeyId;
        mContext = context;
        mUserID = userID;
        mTag = tag;
        mTagName.setText(mTag.getTagName());
        mUserTagRef = FbDatabase.getUserTags(mUserID).child(mTag.getKeyid());
        mUserArticleRef = FbDatabase.getUserArticles(mUserID).child(mArticleKeyId);
//        mAddTagCheckBox.setChecked(false);

        if (mTag.getTaggedArticles() != null) {
            if (isArticleTagged(mTag, mArticleKeyId)) {
                mAddTagCheckBox.setChecked(true);
            } else {
                mAddTagCheckBox.setChecked(false);
            }
        } else {
            mAddTagCheckBox.setChecked(false);
        }

        mAddTagCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {

                    if (mTag.getTaggedArticles() == null) {
                        //if no ArticleTags exists then add the Tag to the Article
                        FbDatabase.addTagNameToArticle(mUserArticleRef, mTag);
                        FbDatabase.addArticleKeyIdToTag(mUserTagRef, mArticleKeyId);
                        Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
                    } else if (isArticleTagged(mTag, mArticleKeyId)) {
                        //if the ArticleKeyID is already in the list of TaggedArticles, do not add the ArticleKeyID
                        Log.i(TAG, "TAG ->> " + mTag.getTagName() + " <<- is in ArticleTags");
                    } else {
                        // else if mArticleTags is not null and the Tag is not found in the ArticleTags list, then add the Tag to the Article
                        FbDatabase.addTagNameToArticle(mUserArticleRef, mTag);
                        FbDatabase.addArticleKeyIdToTag(mUserTagRef, mArticleKeyId);
                        Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
                    }

                } else {
                    //remove Tag from Article and remove ArticleKeyID from Tag
                    FbDatabase.removeTagNameFromArticle(mUserArticleRef, mTag);
                    FbDatabase.removeArticleKeyIdFromTag(mUserTagRef, mArticleKeyId);
                }

            }


        });

    }

    /**
     * Method to check if the passed in ArticleKeyID is in the TaggedArticle list of the Tag model. Will return true if the ArticleKeyID is found in the ArticleTags list.
     * Can be used to prevent duplicates.
     *
     * @param tag          Tag model to be checked
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