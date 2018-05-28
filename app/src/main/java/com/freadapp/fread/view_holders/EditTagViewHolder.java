package com.freadapp.fread.view_holders;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class EditTagViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = EditTagViewHolder.class.getName();

    private Tag mTag;
    private Tag mNewTag;
    private String mUserID;
    private DatabaseReference mUserTagRef;
    private DatabaseReference mUserArticleRef;


    private Context mContext;
    private EditText mEditTagName;
    private CheckBox mEditTagButton;
    private ImageButton mDeleteTagButton;

    public EditTagViewHolder(View itemView) {
        super(itemView);

        mEditTagName = itemView.findViewById(R.id.edit_tag_name);
        mEditTagButton = itemView.findViewById(R.id.edit_tag_button);
        mDeleteTagButton = itemView.findViewById(R.id.delete_tag_button);

    }

    /**
     * Binds the passed in Tag properties to the populated view.
     *
     * @param tag Tag model to be bound to the List Item Views
     */
    public void bindToTag(Context context, final Tag tag, String userID) {

        mContext = context;
        mUserID = userID;
        mTag = tag;
        mEditTagName.setText(mTag.getTagName());
        mUserTagRef = FbDatabase.getUserTags(mUserID);
        mUserArticleRef = FbDatabase.getUserArticles(mUserID);
        mEditTagButton.setChecked(false);

                // need to handle if Tags are empty
//        if (mTag.getTaggedArticles() != null) {
//            if (isArticleTagged(mTag, mArticleKeyID)) {
//            } else {
//            }
//        } else {
//        }

        mEditTagButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (b) {
                            //move focus to edit tag naem
                            mEditTagName.requestFocus();
                            //show soft input to let user edit name of tag
                            imm.showSoftInput(mEditTagName, InputMethodManager.SHOW_IMPLICIT);
                        } else {
                            //create new tag object to store the new user entered tag name
                            mNewTag = new Tag();
                            mNewTag.setTagName(mEditTagName.getText().toString());
                            //update Tag name in /tags/[keyId]/[tagName]
                            FbDatabase.updateTagName(mUserTagRef.child(mTag.getKeyid()), mUserArticleRef, mTag, mNewTag);
                        }

                    }
                });

        mEditTagName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    mEditTagButton.setChecked(true);
                } else {
                    mEditTagButton.setChecked(false);
                }

            }
        });


        mDeleteTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //loop through taggedArticles, get all article KeyIDs. then go through each article and remove the specified articleTag

                List<Object> taggedArticles = mTag.getTaggedArticles();

                if (taggedArticles != null) {

                    //loop through taggedArticles to get all Articles associated with the Tag
                    // then remove tagName from articleTags found at Database Reference articles/[keyid]/articleTags/[tagName]
                    // this is so when you delete a Tag, all Articles that have the Tag assigned to it get deleted as well.
                    for (Object articleKeyId : taggedArticles) {
                        FbDatabase.removeTagNameFromArticle(mUserArticleRef.child(articleKeyId.toString()), mTag);
                    }

                }

                //delete Tag object found at Database Reference users/[uid]/tags/[tag]
                FbDatabase.deleteTag(mContext, mTag, mUserTagRef.child(mTag.getKeyid()));

                Log.i(TAG, "onClick: Delete");

            }
        });

    }

}
