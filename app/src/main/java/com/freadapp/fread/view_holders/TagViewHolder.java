package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    private TextView mTagName;
    private CheckBox mTagCheckBox;
    private String mArticleKeyID;
    private Article mArticle;
    private String mUserID;
    private Context mContext;
    private Tag mTag;

    public TagViewHolder(View itemView) {
        super(itemView);

        mTagName = itemView.findViewById(R.id.tag_name_textview);
        mTagCheckBox = itemView.findViewById(R.id.tag_checkbox);

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
        mTagCheckBox.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        DatabaseReference article = FbDatabase.getUserArticles(mUserID).child(mArticleKeyID);

        article.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article article1 = dataSnapshot.getValue(Article.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (b) {

            FbDatabase.addTagToArticle(article, mTag);
            Toast.makeText(mContext, "Tag Added", Toast.LENGTH_SHORT).show();
        } else {
            //remove tag from Article tags
            FbDatabase.removeTagFromArticle(article, mTag);
            Toast.makeText(mContext, "Tag Removed", Toast.LENGTH_SHORT).show();
        }

    }

    public List<Object> getArticleTags(Article article) {
        mArticle = article;
        return mArticle.getArticleTags();
    }



}
