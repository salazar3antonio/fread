package com.freadapp.fread.tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.EditTagViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class EditTagsFragment extends Fragment {

    public static final String TAG = EditTagsFragment.class.getName();
    public static final String EDIT_TAGS_SCROLL_POSITION_KEY = "edit_tags_scroll_position_key";

    private DatabaseReference mUserTags;
    private EditText mCreateNewTagEditText;
    private ImageButton mCreateNewTagButton;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManger;
    private Query mAllTagsByName;
    private SharedPreferences mSharedPreferences;

    public static EditTagsFragment newInstance() {
        return new EditTagsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserTags = FirebaseUtils.getUserTags();
        mAllTagsByName = mUserTags.orderByChild(FirebaseUtils.FB_TAG_NAME);
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tags_edit_fragment, container, false);
        mCreateNewTagButton = view.findViewById(R.id.ib_create_new_tag);
        mCreateNewTagEditText = view.findViewById(R.id.et_create_new_tag);
        mRecyclerView = view.findViewById(R.id.rv_tags_main_list);
        mLinearLayoutManger = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManger);

        mCreateNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTagName = mCreateNewTagEditText.getText().toString();
                //create a new tag in the database once clicked
                FirebaseUtils.createNewTag(getContext(), mUserTags, newTagName);
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
        editor.putInt(EDIT_TAGS_SCROLL_POSITION_KEY, firstCompletelyVisibleItemPosition);
        editor.apply();

    }

    @NonNull
    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Tag> options =
                new FirebaseRecyclerOptions.Builder<Tag>()
                        .setQuery(mAllTagsByName, Tag.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Tag, EditTagViewHolder>(options) {
            @Override
            public EditTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new EditTagViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tag_edit_list_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull EditTagViewHolder holder, int position, @NonNull final Tag tag) {

                holder.bindToTag(getContext(), tag);

            };

        };
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int scrollPosition = mSharedPreferences.getInt(EDIT_TAGS_SCROLL_POSITION_KEY, 0);
                mLinearLayoutManger.scrollToPosition(scrollPosition);
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

}
