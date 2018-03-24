package com.freadapp.fread.tag_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.freadapp.fread.R;

/**
 * Created by salaz on 3/22/2018.
 */

public class AddTagFragment extends Fragment {

    private Button mAddTagButton;
    private EditText mTagNameEdit;

    public static AddTagFragment newInstance() {
        return new AddTagFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate teh add_tag_fragment. hold edit text and add tag button.
        View view = inflater.inflate(R.layout.add_tag_fragment, container, false);

        mAddTagButton = view.findViewById(R.id.add_tag_button);
        mTagNameEdit = view.findViewById(R.id.add_tag_edit_text);

        return view;
    }

    private void addTag() {

        //get tag name entered into edit text box and put to the articleTags property of the Article

    }
}
