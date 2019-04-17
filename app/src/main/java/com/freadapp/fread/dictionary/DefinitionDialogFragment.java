package com.freadapp.fread.dictionary;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Word;

public class DefinitionDialogFragment extends DialogFragment {

    public static final String TAG = DefinitionDialogFragment.class.getName();

    public static final String DEFINITION_DIALOG_FRAGMENT_TAG = "definition_dialog_fragment_tag";
    public static final String WORD_BUNDLE_ARGS = "word_bundle_args";

    private TextView mWordName;
    private TextView mLexicalCategory;
    private TextView mDefinition;
    private Word mWord;

    public static DefinitionDialogFragment newInstance(Word word) {

        Bundle args = new Bundle();
        args.putParcelable(WORD_BUNDLE_ARGS, word);
        DefinitionDialogFragment fragment = new DefinitionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mWord = bundle.getParcelable(WORD_BUNDLE_ARGS);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.definition_dialog_fragment, container, false);

        mWordName = view.findViewById(R.id.tv_word_name);
        mLexicalCategory = view.findViewById(R.id.tv_lexical_category);
        mDefinition = view.findViewById(R.id.tv_definition);

        mWordName.setText(mWord.getWord());
        mLexicalCategory.setText(mWord.getLexicalCategories().get(0));
        mDefinition.setText(mWord.getDefinitions().get(0));

        return view;

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
