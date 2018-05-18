package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private CheckBox mTagCheckBox;
    private String mArticleKeyID;
    private String mUserID;
    private DatabaseReference mUserArticleRef;
    private Context mContext;
    private Tag mTag;
    private List<Object> mArticleTags;

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
    public void bindToTag(Tag tag, Context context, String articleKeyID, String userID) {

        mArticleKeyID = articleKeyID;
        mUserID = userID;
        mContext = context;
        mTag = tag;
        mTagName.setText(mTag.getTagName());
        mUserArticleRef = FbDatabase.getUserArticles(mUserID).child(mArticleKeyID);
        setTagCheckBox(mTag);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (b) {

            if (mArticleTags == null) {
                //if no ArticleTags exists then add the Tag to the Article
                FbDatabase.addTagToArticle(mUserArticleRef, mTag);
                Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
            } else if (isTagInArticleTagsList(mTag, mArticleTags)) {
                //if the Tag is already in the list of ArticleTags, do not add the Tag
                Log.i(TAG, "TAG ->> " + mTag.getTagName() + " <<- is in ArticleTags");
            } else {
                // else if mArticleTags is not null and the Tag is not found in the ArticleTags list, then add the Tag to the Article
                FbDatabase.addTagToArticle(mUserArticleRef, mTag);
                Log.i(TAG, "ADDED TAG ->> " + mTag.getTagName() + " <<- to ArticleTags");
            }

        } else {
            //remove Tag from ArticleTags list
            FbDatabase.removeTagFromArticle(mUserArticleRef, mTag);
            Log.i(TAG, "REMOVED TAG ->> " + mTag.getTagName() + " <<- from ArticleTags");
        }

    }

    /**
     * Method to set the Tag checkbox as checked if the Tag is present in the ArticleTags list.
     * This is so User can know which Tags are presently associated with the specific Article
     *
     * @param tag Tag model to be checked
     */
    private void setTagCheckBox(final Tag tag) {

        mUserArticleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //store the snapshot into an Article object to check it's tags
                Article article = dataSnapshot.getValue(Article.class);

                //check to see if Article Tags are empty. If so create a new ArrayList and add the TagName
                if (article.getArticleTags() != null) {

                    mArticleTags = article.getArticleTags();

                    //loop through mArticleTags and search for a match of the TagName
                    for (int i = 0; i < mArticleTags.size(); i++) {
                        if (isTagInArticleTagsList(tag, mArticleTags)) {
                            //if Tag is in ArticleTags, check the checkbox.
                            mTagCheckBox.setChecked(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Method to check if the passed in Tag is in the ArticleTags list. Will return true if the Tag is found in the ArticleTags list.
     * Can be used to prevent duplicate tags.
     *
     * @param tag         Tag model to be checked
     * @param articleTags List of ArticleTags
     */
    private boolean isTagInArticleTagsList(Tag tag, List<Object> articleTags) {

        boolean tagInList = false;

        for (int i = 0; i < articleTags.size(); i++) {

            if (tag.getTagName().equals(articleTags.get(i).toString())) {
                tagInList = true;
                break;
            }
        }

        return tagInList;
    }


}
