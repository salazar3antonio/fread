package com.freadapp.fread.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.view_holders.TagViewHolder;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private LayoutInflater mInflater;
    private List<Object> mTags;
    private int mTagLayoutResource;

    public TagsAdapter(Context context, Article article, int layoutResource) {

        mTags = article.getArticleTags();
        mInflater = LayoutInflater.from(context);
        mTagLayoutResource = layoutResource;

    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tagView = mInflater.inflate(mTagLayoutResource, parent, false);
        return new TagViewHolder(tagView, R.id.tv_tag_small_name);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {

        if (mTags != null) {
            String tagName = mTags.get(position).toString();
            holder.bindToTag(tagName);
        }
    }

    @Override
    public int getItemCount() {
        if (mTags != null) {
            return mTags.size();
        } else {
            return 0;
        }
    }

}
