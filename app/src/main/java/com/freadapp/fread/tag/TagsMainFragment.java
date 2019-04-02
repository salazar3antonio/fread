package com.freadapp.fread.tag;

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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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


    private DatabaseReference mUserTags;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;

    public static TagsMainFragment newInstance() {
        return new TagsMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_tags_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.rv_tags_main_list);

        if (FirebaseUtils.isFirebaseUserSignedIn()) {
            mUserTags = FirebaseUtils.getUserTags();
            setMainTagsAdapter();
        } else {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    private void setMainTagsAdapter() {

        Query allTagsByName = mUserTags.orderByChild(FirebaseUtils.FB_TAG_NAME);

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tag, TagViewHolder>(Tag.class, R.layout.tag_main_list_item,
                TagViewHolder.class, allTagsByName) {

            @Override
            protected void populateViewHolder(TagViewHolder viewHolder, final Tag tag, int position) {

                viewHolder.bindToTag(getContext(), tag);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        commitTagDetailFragment(tag);

                    }
                });

            }

        };

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
        }
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
