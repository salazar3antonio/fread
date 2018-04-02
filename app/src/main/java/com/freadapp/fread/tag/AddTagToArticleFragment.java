package com.freadapp.fread.tag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.TagViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagToArticleFragment extends Fragment {

    private static final String TAG = AddTagToArticleActivity.class.getName();

    private Button mAddTagButton;
    private EditText mTagNameEdit;
    private String mArticleKeyID;
    private DatabaseReference mUserTags;
    private FirebaseUser mUser;
    private String mUserUid;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;

    public static AddTagToArticleFragment newInstance() {
        return new AddTagToArticleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            mArticleKeyID = savedInstanceState.getString(AddTagToArticleActivity.ARTICLE_TO_BE_TAGGED);
        }

        //get the current logged in user
        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        //get all of the user's tags
        mUserTags = FbDatabase.getUserTags(mUserUid);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate the add_tag_fragment. hold edit text and add tag button.
        View view = inflater.inflate(R.layout.add_tag_fragment, container, false);

        mAddTagButton = view.findViewById(R.id.add_tag_button);
        mTagNameEdit = view.findViewById(R.id.add_tag_edit_text);
        mRecyclerView = view.findViewById(R.id.tag_list_recycleView);

        mAddTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the text the user entered for the Tag Name
                String userEnteredTag = mTagNameEdit.getText().toString();
                //create a new tag with the passed in Tag Name
                FbDatabase.createNewTag(mUserTags, userEnteredTag);
            }
        });

        setFirebaseAdapter();

        Log.i(TAG, "Tagging Article: " + mArticleKeyID);

        return view;
    }


    private void setFirebaseAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Tag, TagViewHolder>(Tag.class, R.layout.tag_list_item,
                TagViewHolder.class, mUserTags) {


            @Override
            protected void populateViewHolder(TagViewHolder viewHolder, Tag model, int position) {

                Context context = getContext();
                viewHolder.bindToTag(model, context);

            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
