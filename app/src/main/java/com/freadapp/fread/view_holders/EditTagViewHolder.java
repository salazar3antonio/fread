package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class EditTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        mEditTagButton = itemView.findViewById(R.id.edit_tag_checkbox);
        mDeleteTagButton = itemView.findViewById(R.id.delete_tag_imagebutton);
        itemView.setOnClickListener(this);

    }

    /**
     * Binds the passed in Tag properties to the populated view.
     *
     * @param tag Tag model to be bound to the List Item Views
     */
    public void bindToTag(final Context context, final Tag tag, String userID) {

        mContext = context;
        mUserID = userID;
        mTag = tag;
        mEditTagName.setText(mTag.getTagName());
        mUserTagRef = FbDatabase.getUserTags(mUserID);
        mUserArticleRef = FbDatabase.getUserArticles(mUserID);
        mEditTagButton.setChecked(false);

        mEditTagButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if (b) {
                            //move focus to mEditTagName
                            mEditTagName.requestFocus();
                            //set cursor to end of Tag Name
                            mEditTagName.setSelection(mEditTagName.getText().length());
                            //show soft input to let user edit name of tag
                        } else {
                            //create new Tag object to store the new Tag Name
                            mNewTag = new Tag();
                            mNewTag.setTagName(mEditTagName.getText().toString());
                            FbDatabase.updateTagName(mUserTagRef.child(mTag.getKeyid()), mUserArticleRef, mTag, mNewTag);

                            //force entered String to lowercase. All tags will be strictly lowercase.
                            String tag = mEditTagName.getText().toString();
                            mEditTagName.setText(tag.toLowerCase());

                            //reset Focus to top View
                            mEditTagName.clearFocus();
                        }

                    }
                });

        mEditTagName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    // this is so when you delete a Tag, all Tags associated with an Article get removed as well.
                    for (Object articleKeyId : taggedArticles) {
                        FbDatabase.removeTagNameFromArticle(mUserArticleRef.child(articleKeyId.toString()), mTag);
                    }
                }

                //delete Tag object found at Database Reference users/[uid]/tags/[tag]
                FbDatabase.removeTag(mContext, mTag, mUserTagRef.child(mTag.getKeyid()));

            }
        });

    }

    @Override
    public void onClick(View view) {

        //move focus to mEditTagName
        mEditTagName.requestFocus();
        //set cursor to end of Tag Name
        mEditTagName.setSelection(mEditTagName.getText().length());

    }
}
