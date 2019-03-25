package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.database.DatabaseReference;

public class EditTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final String TAG = EditTagViewHolder.class.getName();

    private Tag mTag;
    private String mNewTag;
    private String mUserID;
    private DatabaseReference mUserTagRef;
    private DatabaseReference mUserArticlesRef;


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
        mUserTagRef = FirebaseUtils.getUserTags();
        mUserArticlesRef = FirebaseUtils.getUserArticles();
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

                            mNewTag = (mEditTagName.getText().toString());

                            if (!mTag.getTagName().equals(mNewTag)) {
                                FirebaseUtils.editTagName(mContext, mUserTagRef, mTag, mNewTag);
                            }

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

                FirebaseUtils.deleteTag(mContext, mTag, mUserTagRef, mUserArticlesRef);

            }
        });

    }

    @Override
    public void onClick(View view) {

        //move focus to mEditTagName
        mEditTagName.requestFocus();
        //show keyboard
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTagName, InputMethodManager.SHOW_IMPLICIT);
        //set cursor to end of Tag Name
        mEditTagName.setSelection(mEditTagName.getText().length());

    }
}
