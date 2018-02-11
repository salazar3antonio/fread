package com.freadapp.fread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mArticleView;
    private TextView mPUBdateView;
    private ImageView mArticleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTitleView = this.findViewById(R.id.title_view);
        mAuthorView = this.findViewById(R.id.author_view);
        mArticleView = this.findViewById(R.id.article_view);
        mPUBdateView = this.findViewById(R.id.pubdate_view);
        mArticleImageView = this.findViewById(R.id.image_article_view);

        //Here is where the intent is handled. Web browser will start the intent, Fread then receives the Extra Text (URL)
        Intent intent = getIntent();
        //method to begin handling the intent extras
        handleSendText(intent);

    }

    void handleSendText(Intent intent) {
        String url_received = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (url_received != null) {

            //building up the Retrofit object to begin calling the API
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //passing in the ArticleAPI interface class into the retrofit Object
            ArticleAPI articleAPI = retrofit.create(ArticleAPI.class);

            //using the articleAPI object to call the GET method. Passed in URL.
            //will need to grab the URL from the user's web browser eventually. It's hard coded for now.
            Call<Article> call = articleAPI.getArticle(url_received, true);
            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    //create article that is an Article type and assign it to the Response body (JSON object).
                    //since our response is an Article object is must be stored as an Article Object.
                    Article article = response.body();

                    //set article views to hold received text from the response body
                    mTitleView.setText(article.getTitle());
                    mAuthorView.setText(article.getAuthor());
                    mArticleView.setText(article.getArticle());
                    mPUBdateView.setText(article.getPublishDate());

                    if (article.getImage() == "") {
                        Toast.makeText(getApplicationContext(), "no image link", Toast.LENGTH_SHORT).show();
                    } else {
                        Picasso.with(getApplicationContext()).load(article.getImage()).into(mArticleImageView);
                    }

                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //on failure, Toast error code
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "From Main Activity", Toast.LENGTH_SHORT).show();
        }

    }

}
