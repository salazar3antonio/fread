package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.TagViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TagsMainFragment extends Fragment {

    public static final String TAG = TagsMainFragment.class.getName();

    private DatabaseReference mUserTags;
    private String mUserUid;
    private FirebaseUser mUser;
    private RecyclerView mRecyclerView;

    public static TagsMainFragment newInstance() {
        return new TagsMainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the current logged in user
        mUser = FbDatabase.getAuthUser(mUser);

        if (mUser != null) {
            // if a user is logged in get the user's ID
            mUserUid = mUser.getUid();
            //get all of the user's tags
            mUserTags = FbDatabase.getUserTags(mUserUid);
        }

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mUser != null) {
            //inflate the view only if a user is signed in. else return null
            View view = inflater.inflate(R.layout.tags_main_fragment, container, false);
            mRecyclerView = view.findViewById(R.id.rv_tag_list);

            setMainTagsAdapter();

            return view;

        } else {
            return null;
        }



    }

    private void setMainTagsAdapter() {

        Query allTagsByName = mUserTags.orderByChild("tagName");

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tag, TagViewHolder>(Tag.class, R.layout.tag_main_list_item,
                TagViewHolder.class, allTagsByName) {

            @Override
            public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_main_list_item, parent, false);
                return new TagViewHolder(view, R.id.tv_tag_main_name);
            }

            @Override
            protected void populateViewHolder(TagViewHolder viewHolder, Tag model, int position) {

                viewHolder.mTagNameTextView.setText(model.getTagName());


            }

        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }


}
