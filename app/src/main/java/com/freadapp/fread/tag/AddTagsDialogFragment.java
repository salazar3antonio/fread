package com.freadapp.fread.tag;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.R;
import com.freadapp.fread.article.ArticleDetailActivity;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.AddTagDialogViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AddTagsDialogFragment extends DialogFragment {

    public static final String TAG = AddTagsDialogFragment.class.getName();

    public static final String ADD_TAGS_DIALOG_FRAGMENT_TAG = "add_tags_dialog_fragment_tag";

    private DatabaseReference mUserTags;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Query mAllTagQuery;
    private RecyclerView mRecyclerView;
    private ImageButton mCreateNewTagButton;
    private EditText mCreateNewTagEditText;
    private Article mArticle;


    public static AddTagsDialogFragment newInstance() {
        return new AddTagsDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserTags = FirebaseUtils.getUserTags();

        //get the Article supplied when the fragment was instantiated
        Bundle arguments = getArguments();
        if (arguments != null) {
            mArticle = arguments.getParcelable(ArticleDetailActivity.ARTICLE_BUNDLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tags_add_dialog_fragment, container, false);

        mCreateNewTagButton = view.findViewById(R.id.ib_create_new_tag);
        mCreateNewTagEditText = view.findViewById(R.id.et_create_new_tag);
        mRecyclerView = view.findViewById(R.id.add_tag_list_dialog_recycleView);

        mCreateNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new tag in the database once clicked
                FirebaseUtils.createNewTag(getContext(), mUserTags, mCreateNewTagEditText.getText().toString());
                //then clear the EditText field
                mCreateNewTagEditText.setText(null);
            }
        });

        setFirebaseAdapter();

        return view;
    }

    private void setFirebaseAdapter() {

        mAllTagQuery = mUserTags.orderByChild("tagName");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Tag, AddTagDialogViewHolder>(Tag.class, R.layout.tag_add_list_item,
                AddTagDialogViewHolder.class, mAllTagQuery) {

            @Override
            protected void populateViewHolder(AddTagDialogViewHolder viewHolder, Tag tag, int position) {

                //create bindToTag method
                viewHolder.bindToTag(tag, mArticle);

            }

        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

}
