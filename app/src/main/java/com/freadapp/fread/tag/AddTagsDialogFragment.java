package com.freadapp.fread.tag;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.freadapp.fread.R;
import com.freadapp.fread.article.ArticleDetailActivity;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.AddTagDialogViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AddTagsDialogFragment extends DialogFragment {

    public static final String TAG = AddTagsDialogFragment.class.getName();

    public static final String ADD_TAGS_DIALOG_FRAGMENT_TAG = "add_tags_dialog_fragment_tag";
    public static final String ADD_TAGS_SCROLL_POSITION_KEY = "add_tags_scroll_position_key";


    private DatabaseReference mUserTags;
    private Query mAllTagQuery;
    private RecyclerView mRecyclerView;
    private ImageButton mCreateNewTagButton;
    private EditText mCreateNewTagEditText;
    private Article mArticle;
    private LinearLayoutManager mLinearLayoutManger;
    private SharedPreferences mSharedPreferences;


    public static AddTagsDialogFragment newInstance() {
        return new AddTagsDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserTags = FirebaseUtils.getUserTags();
        mAllTagQuery = mUserTags.orderByChild("tagName");

        //get the Article supplied when the fragment was instantiated
        Bundle arguments = getArguments();
        if (arguments != null) {
            mArticle = arguments.getParcelable(ArticleDetailActivity.ARTICLE_BUNDLE);
        }

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tags_add_dialog_fragment, container, false);

        mCreateNewTagButton = view.findViewById(R.id.ib_create_new_tag);
        mCreateNewTagEditText = view.findViewById(R.id.et_create_new_tag);
        mRecyclerView = view.findViewById(R.id.add_tag_list_dialog_recycleView);
        mLinearLayoutManger = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManger);

        mCreateNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new tag in the database once clicked
                FirebaseUtils.createNewTag(getContext(), mUserTags, mCreateNewTagEditText.getText().toString());
                //then clear the EditText field
                mCreateNewTagEditText.setText(null);
            }
        });

        attachRecyclerViewAdapter();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int firstCompletelyVisibleItemPosition = mLinearLayoutManger.findFirstCompletelyVisibleItemPosition();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ADD_TAGS_SCROLL_POSITION_KEY, firstCompletelyVisibleItemPosition);
        editor.apply();

    }

    @NonNull
    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Tag> options =
                new FirebaseRecyclerOptions.Builder<Tag>()
                        .setQuery(mAllTagQuery, Tag.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Tag, AddTagDialogViewHolder>(options) {
            @Override
            public AddTagDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new AddTagDialogViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tag_add_list_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull AddTagDialogViewHolder holder, int position, @NonNull final Tag tag) {

                holder.bindToTag(tag, mArticle);

            }

        };
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int scrollPosition = mSharedPreferences.getInt(ADD_TAGS_SCROLL_POSITION_KEY, 0);
                mLinearLayoutManger.scrollToPosition(scrollPosition);
            }
        });

        mRecyclerView.setAdapter(adapter);
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
