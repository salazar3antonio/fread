package com.freadapp.fread.article_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freadapp.fread.R;

/**
 * Created by salaz on 2/26/2018.
 */

public class ArticleLoadingFragment extends Fragment {

    public static ArticleLoadingFragment newInstance() {
        return new ArticleLoadingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_loading_fragment, container, false);
        return view;


    }
}
