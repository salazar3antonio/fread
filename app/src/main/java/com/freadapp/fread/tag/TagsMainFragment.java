package com.freadapp.fread.tag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
    public static final String TAG_KEY_ID = "tag_keyId";
    public static final String TAG_DETAIL_FRAGMENT_TAG = "tag_detail_fragment_tag";


    private DatabaseReference mUserTagsRef;
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
            mUserTagsRef = FbDatabase.getUserTags(mUserUid);
        }

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mUser != null) {
            //inflate the view only if a user is signed in. else return null
            View view = inflater.inflate(R.layout.main_tags_fragment, container, false);
            mRecyclerView = view.findViewById(R.id.rv_tags_main_list);

            setMainTagsAdapter();

            return view;

        } else {
            return null;
        }

    }

    private void setMainTagsAdapter() {

        Query allTagsByName = mUserTagsRef.orderByChild(FbDatabase.FB_TAG_NAME);

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tag, TagViewHolder>(Tag.class, R.layout.tag_main_list_item,
                TagViewHolder.class, allTagsByName) {

            @Override
            public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getContext()).inflate(R.layout.tag_main_list_item, parent, false);

                return new TagViewHolder(getContext(), view, R.id.tv_tag_main_name);
            }

            @Override
            protected void populateViewHolder(TagViewHolder viewHolder, final Tag tag, int position) {

                viewHolder.bindToTag(tag);

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
