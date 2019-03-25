package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.EditTagViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class EditTagsFragment extends Fragment {

    public static final String TAG = EditTagsFragment.class.getName();

    private DatabaseReference mUserTags;
    private String mUserUid;
    private FirebaseUser mUser;
    private EditText mCreateNewTagEditText;
    private ImageButton mCreateNewTagButton;
    private RecyclerView mRecyclerView;

    public static EditTagsFragment newInstance() {
        return new EditTagsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserTags = FirebaseUtils.getUserTags();

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mUser != null) {
            //inflate the view only if a user is signed in. else return null
            View view = inflater.inflate(R.layout.tags_edit_fragment, container, false);
            mCreateNewTagButton = view.findViewById(R.id.create_new_tag_button);
            mCreateNewTagEditText = view.findViewById(R.id.create_new_tag_edittext);
            mRecyclerView = view.findViewById(R.id.rv_tags_main_list);

            mCreateNewTagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //create a new tag in the database once clicked
                    FirebaseUtils.createNewTag(getContext(), mUserTags, mCreateNewTagEditText.getText().toString());
                    //then clear the EditText field
                    mCreateNewTagEditText.setText(null);
                }
            });

            setEditTagsAdapter();

            return view;

        } else {
            return null;
        }

    }

    private void setEditTagsAdapter() {

        Query allTagsByName = mUserTags.orderByChild(FirebaseUtils.FB_TAG_NAME);

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tag, EditTagViewHolder>(Tag.class, R.layout.tag_edit_list_item,
                EditTagViewHolder.class, allTagsByName) {

            @Override
            protected void populateViewHolder(final EditTagViewHolder viewHolder, final Tag tag, int position) {

                viewHolder.bindToTag(getContext(), tag, mUserUid);

            }

        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

}
