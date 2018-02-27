package com.freadapp.fread.helpers;

import android.util.Log;

import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by salaz on 2/26/2018.
 */

public class Retrofit {

    public static final String TAG = Retrofit.class.getName();

    private Article article;
    private retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
            .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
            .build();;
    private ArticleAPI articleAPI = retrofit.create(ArticleAPI.class);;

    public Retrofit() {

        //using the articleAPI object to call the GET method. Passed in URL.
        //will need to grab the URL from the user's web browser eventually. It's hard coded for now.

    }

    public void callArticleAPI(String url, boolean bestimage) {

        Call<Article> call = articleAPI.getArticle(url, true);
        call.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                //set the article response body to the article
                setArticle(response.body());

//                if (article.getImage() == "") {
//                    Toast.makeText(getContext(), "no image link", Toast.LENGTH_SHORT).show();
//                } else {
//                    Picasso.with(getContext()).load(article.getImage()).into(mArticleImageView);
//                }

            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                //on failure, Toast error code
                Log.w(TAG, "API Failed: " + t.getMessage());
            }
        });

    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}



