package com.freadapp.fread.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freadapp.fread.R;

/**
 * Created by salaz on 2/26/2018.
 */

public class LoadingDialogFragment extends DialogFragment {

    public static final String LOADING_DIALOG_FRAGMENT_TAG = "loading_dialog_fragment_tag";

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void showLoadingFragment(boolean showIt, FragmentManager fragmentManager) {
        if (showIt) {
            LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.newInstance();
            loadingDialogFragment.show(fragmentManager, LoadingDialogFragment.LOADING_DIALOG_FRAGMENT_TAG);
        } else {
            Fragment findLoadingFragment = fragmentManager.findFragmentByTag(LoadingDialogFragment.LOADING_DIALOG_FRAGMENT_TAG);
            DialogFragment dialog = (DialogFragment) findLoadingFragment;
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }


}
