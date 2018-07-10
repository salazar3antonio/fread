package com.freadapp.fread.tag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.EditTagViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

public class EditTagsFragment extends Fragment {

    private DatabaseReference mUserTags;
    private DatabaseReference mUserArticleRef;
    private String mUserUid;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Query mAllTagQuery;
    private EditText mCreateNewTagEditText;
    private ImageButton mCreateNewTagButton;

    private RecyclerView mRecyclerView;

    public static EditTagsFragment newInstance() {
        return new EditTagsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the current logged in user
        mUser = FbDatabase.getAuthUser(mUser);
        mUserUid = mUser.getUid();
        //get all of the user's tags
        mUserTags = FbDatabase.getUserTags(mUserUid);
        mUserArticleRef = FbDatabase.getUserArticles(mUserUid);

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_tags_fragment, container, false);
        mCreateNewTagButton = view.findViewById(R.id.create_new_tag_button);
        mCreateNewTagEditText = view.findViewById(R.id.create_new_tag_edittext);
        mRecyclerView = view.findViewById(R.id.edit_tag_list_recycleView);

        mCreateNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FbDatabase.createNewTag(getContext(), mUserTags, mCreateNewTagEditText);
            }
        });

        setFirebaseAdapter();

        return view;
    }

    private void setFirebaseAdapter() {

        mAllTagQuery = mUserTags.orderByChild("tagName");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Tag, EditTagViewHolder>(Tag.class, R.layout.edit_tag_list_item,
                EditTagViewHolder.class, mAllTagQuery) {

            @Override
            protected void populateViewHolder(final EditTagViewHolder viewHolder, final Tag model, int position) {

                viewHolder.bindToTag(getContext(), model, mUserUid);

            }

        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }
}
