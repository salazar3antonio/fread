package com.freadapp.fread.tag_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.article_classes.ArticleDetailActivity;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holder.ArticleViewHolder;
import com.freadapp.fread.view_holder.TagViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagToArticleFragment extends Fragment {

    private Button mAddTagButton;
    private EditText mTagNameEdit;
    private String mArticleKeyID;
    private Tag mTag;
    private DatabaseReference mTagsDBref;
    private FirebaseUser mUser;
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

        mTag = new Tag();

        //get the current logged in user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        // DB reference of all Tags
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mTagsDBref = firebaseDatabase.getReference().child("users").child(mUser.getUid()).child("tags");


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
                //get the text the user inputed for the Tag Name
                String userEnteredTag = mTagNameEdit.getText().toString();
                createNewTag(userEnteredTag);
            }
        });

        setFirebaseAdapter();

        Toast.makeText(getContext(), "Tagging Article: " + mArticleKeyID, Toast.LENGTH_LONG).show();

        return view;
    }

    private void createNewTag(String tagName) {

        //create a unique keyid for a new Tag
        String key = mTagsDBref.push().getKey();
        //store the keyid and tagName into the Tag object.
        mTag.setKeyid(key);
        mTag.setTagName(tagName);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(key, mTag);
        //update the children of "tags" in the DB with the passed in Hash Map
        mTagsDBref.updateChildren(writeMap);

    }

    private void addTagToArticle() {

        //get a reference to the articles DB

    }

    private void setFirebaseAdapter() {

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Tag, TagViewHolder>(Tag.class, R.layout.tag_list_item,
                TagViewHolder.class, mTagsDBref) {


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

}
