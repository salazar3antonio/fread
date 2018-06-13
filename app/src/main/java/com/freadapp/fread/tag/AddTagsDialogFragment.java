package com.freadapp.fread.tag;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.article.ArticleDetailActivity;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.AddTagDialogViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AddTagsDialogFragment extends DialogFragment {

    public static final String TAG = AddTagsDialogFragment.class.getName();

    public static final String ADD_TAGS_DIALOG_FRAGMENT_TAG = "add_tags_dialog_fragment_tag";

    private DatabaseReference mUserTags;
    private String mUserUid;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Query mAllTagQuery;
    private EditText mCreateNewTag;
    private RecyclerView mRecyclerView;
    private String mArticleKeyId;
    private ImageButton mAddNewTagButton;
    private EditText mCreateNewTagEditText;


    public static AddTagsDialogFragment newInstance() {
        return new AddTagsDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the current logged in user
        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        //get all of the user's tags
        mUserTags = FbDatabase.getUserTags(mUserUid);

        //get the ArticleKeyId supplied when the fragment was instantiated
        mArticleKeyId = getArguments().getString(ArticleDetailActivity.ARTICLE_KEY_ID);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_tags_dialog_fragment, container, false);

        mAddNewTagButton = view.findViewById(R.id.add_new_tag_button);
        mCreateNewTagEditText = view.findViewById(R.id.create_new_tag_edittext);
        mRecyclerView = view.findViewById(R.id.add_tag_list_dialog_recycleView);

        mAddNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTag();
            }
        });

        setFirebaseAdapter();

        return view;
    }

    private void setFirebaseAdapter() {

        mAllTagQuery = mUserTags.orderByChild("tagName").startAt("a");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Tag, AddTagDialogViewHolder>(Tag.class, R.layout.add_tags_dialog_list_item,
                AddTagDialogViewHolder.class, mAllTagQuery) {

            @Override
            protected void populateViewHolder(AddTagDialogViewHolder viewHolder, Tag model, int position) {

                //create bindToTag method
                viewHolder.bindToTag(getContext(), model, mUserUid, mArticleKeyId);

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public Tag getItem(int position) {
                return super.getItem(position);
            }

        };

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void createNewTag() {
        //get the text the user entered for the Tag Name
        String userEnteredTag = mCreateNewTagEditText.getText().toString().toLowerCase();
        if (userEnteredTag.length() == 0) {
            Toast.makeText(getContext(), "Enter tag name", Toast.LENGTH_SHORT).show();
        } else {
            //create a new tag with the passed in Tag Name
            FbDatabase.createNewTag(mUserTags, userEnteredTag);
            //clear out the EditText View
            Toast.makeText(getContext(), "Added " + userEnteredTag, Toast.LENGTH_SHORT).show();
            mCreateNewTagEditText.setText(null);
            Log.i(TAG, "NEW TAG ->> " + userEnteredTag + " <<- added to ArticleTags");

        }
    }

}
