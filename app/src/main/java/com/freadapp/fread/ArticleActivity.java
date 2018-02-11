package com.freadapp.fread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.SingleFragmentActivity;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by salaz on 2/11/2018.
 */

public class ArticleActivity extends SingleFragmentActivity {

    //this this is the activity that will be started I may have to capture the extra Intent URL from the browser and send it to the fragment through a bundle

    public static final String RECEIVED_URL = "received_url";

    private String mTitle = "Fread Mode";


    @Override
    protected Fragment createFragment() {

        //here is where the Article Fragment will be created
        Fragment articleFragment = ArticleFragment.newInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);

        //get intent that launched this activity
        Intent intent = getIntent();
        handleSendText(intent, articleFragment);

        return articleFragment;
}

    void handleSendText(Intent intent, Fragment fragment) {

        String url_received = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (url_received != null) {
            //store URL as a string in a bundle
            Bundle bundle = new Bundle();
            bundle.putString(RECEIVED_URL, url_received);
            fragment.setArguments(bundle);
        }

    }



}
