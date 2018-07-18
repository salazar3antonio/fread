package com.freadapp.fread.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FbDatabase;
import com.freadapp.fread.data.model.Tag;

public class NavTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final String TAG = NavTagViewHolder.class.getName();

    private Tag mTag;
    private TextView mNavTagName;
    private Context mContext;

    public NavTagViewHolder(View itemView) {
        super(itemView);

        mNavTagName = itemView.findViewById(R.id.nav_tag_textview);
        itemView.setOnClickListener(this);

    }

    public void bindToNavTag(Tag tag, Context context) {

        mTag = tag;
        mContext = context;
        mNavTagName.setText(mTag.getTagName());

    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, "Clicked " + mTag.getTagName(), Toast.LENGTH_SHORT).show();

    }
}
