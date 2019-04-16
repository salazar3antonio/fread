package com.freadapp.fread.data.api;

import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by salaz on 2/8/2018.
 */

public interface FetchArticleAPI {


    //These are the headers for the api. Holds app key and id.
    @Headers({
            "X-AYLIEN-TextAPI-Application-Key: " + Constants.AYLIEN_API_APP_KEY,
            "X-AYLIEN-TextAPI-Application-ID: " + Constants.AYLIEN_API_APP_ID,
            "accept-encoding: gzip, deflate"
    })

    //the GET method for extracting the text from a url. Must pass in a URL when called.
    @GET("extract")
    Call<Article> getArticle(@Query("url") String url, @Query("best_image") boolean best_image);

}
