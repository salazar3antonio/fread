package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddTagDialogViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = AddTagDialogViewHolder.class.getName();

    private Tag mTag;
    private String mUserID;
    private DatabaseReference mUserTagRef;
    private DatabaseReference mUserArticleRef;
    private String mArticleKeyId;
    private Article mArticle;

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
    public void bindToTag(Context context, final Tag tag, String userID, String articleKeyId, Article article) {

        mArticleKeyId = articleKeyId;
        mArticle = article;
        mContext = context;
        mUserID = userID;
        mTag = tag;
        mTagName.setText(mTag.getTagName());
        mUserTagRef = FbDatabase.getUserTags(mUserID);
        mUserArticleRef = FbDatabase.getUserArticles(mUserID).child(mArticleKeyId);

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