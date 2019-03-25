package com.freadapp.fread.article;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.freadapp.fread.R;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.data.model.Article;
import com.google.firebase.database.DatabaseReference;

/**
 * This class shows an Alert Dialog if the user wants to delete an Article.
 */
public class DeleteArticleDialogFragment extends DialogFragment {

    public static final String DELETE_ARTICLE_DIALOG_FRAGMENT_TAG = "delete_article_dialog_fragment_tag";

    private Article mArticle;
    private DatabaseReference mUserArticles = FirebaseUtils.getUserArticles();

    public static DeleteArticleDialogFragment newInstance(Article article) {

        Bundle args = new Bundle();
        args.putParcelable(ArticleDetailActivity.ARTICLE_BUNDLE, article);
        DeleteArticleDialogFragment fragment = new DeleteArticleDialogFragment();
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle articleBundle = getArguments();

        if (articleBundle != null) {
            mArticle = articleBundle.getParcelable(ArticleDetailActivity.ARTICLE_BUNDLE);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getActivity());

        deleteAlert.setMessage(R.string.delete_article_dialog_title)
                .setPositiveButton(R.string.delete_article_positive_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUtils.deleteArticle(mArticle, mUserArticles);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(R.string.delete_article_negative_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return deleteAlert.create();

    }
}
