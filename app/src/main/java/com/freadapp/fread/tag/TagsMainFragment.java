package com.freadapp.fread.tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Tag;
import com.freadapp.fread.view_holders.TagViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TagsMainFragment extends Fragment {

    public static final String TAG = TagsMainFragment.class.getName();
    public static final String TAG_KEY_ID = "tag_keyId";
    public static final String TAG_DETAIL_FRAGMENT_TAG = "tag_detail_fragment_tag";
    public static final String TAG_MAIN_SCROLL_POSITION_KEY = "tag_main_scroll_position_key";


    private DatabaseReference mUserTags;
    private RecyclerView mRecyclerView;
    private TextView mEmptyTagsView;
    private GridLayoutManager mGridLayoutManager;
    private Query mAllTagsByName;
    private SharedPreferences mSharedPreferences;

    public static TagsMainFragment newInstance() {
        return new TagsMainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_tags_fragment, container, false);

        mEmptyTagsView = view.findViewById(R.id.tv_empty_tags);
        mRecyclerView = view.findViewById(R.id.rv_tags_main_list);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        if (FirebaseUtils.isFirebaseUserSignedIn()) {
            mUserTags = FirebaseUtils.getUserTags();
            mAllTagsByName = mUserTags.orderByChild(FirebaseUtils.FB_TAG_NAME);
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        int firstCompletelyVisibleItemPosition = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(TAG_MAIN_SCROLL_POSITION_KEY, firstCompletelyVisibleItemPosition);
        editor.apply();

    }

    @NonNull
    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Tag> options =
                new FirebaseRecyclerOptions.Builder<Tag>()
                        .setQuery(mAllTagsByName, Tag.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Tag, TagViewHolder>(options) {
            @Override
            public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TagViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tag_main_list_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull TagViewHolder holder, int position, @NonNull final Tag tag) {

                holder.bindToTag(getContext(), tag);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        commitTagDetailFragment(tag);

                    }
                });

            };

            @Override
            public void onDataChanged() {
                mEmptyTagsView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int scrollPosition = mSharedPreferences.getInt(TAG_MAIN_SCROLL_POSITION_KEY, 0);
                mGridLayoutManager.scrollToPosition(scrollPosition);
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    private void commitTagDetailFragment(Tag tag) {

        TagDetailFragment tagsMainFragment = TagDetailFragment.newInstance();

        Bundle tagBundle = new Bundle();
        tagBundle.putString(TAG_KEY_ID, tag.getKeyId());
        tagsMainFragment.setArguments(tagBundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content_frame, tagsMainFragment, TAG_DETAIL_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

}
