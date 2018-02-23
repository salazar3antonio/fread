package com.freadapp.fread.article_classes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.R;
import com.freadapp.fread.data.api.ArticleAPI;
import com.freadapp.fread.data.model.Article;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by salaz on 2/11/2018.
 */

public class ArticleFragment extends Fragment {

    public static final String TAG = ArticleFragment.class.getName();

    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mArticleView;
    private TextView mPUBdateView;
    private ImageView mArticleImageView;
    private String mUrlReceived;
    private Article mArticle;
    private FirebaseUser mUser;
    private DatabaseReference mDBRef_Article;

    private Button mSaveArticleButton;
    private Button mDeleteArticleButton;

    private DatabaseReference mDatabase;

    //public constructor
    public ArticleFragment() {
    }

    //static method that returns a new ArticleFragment object
    public static ArticleFragment newInstance() {
        return new ArticleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create new Article object.
        mArticle = new Article();
        //get auth instance and assign it to the Firebase user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        //reference of the Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //reference to user-articles/<uid-generated-by-fb>
        mDBRef_Article = mDatabase.child("articles").push();

        if (getArguments() != null) {
            mUrlReceived = getArguments().getString(ArticleActivity.RECEIVED_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.article_fragment, container, false);

        mTitleView = view.findViewById(R.id.title_view);
        mAuthorView = view.findViewById(R.id.author_view);
        mArticleView = view.findViewById(R.id.article_view);
        mPUBdateView = view.findViewById(R.id.pubdate_view);
        mArticleImageView = view.findViewById(R.id.image_article_view);
        mSaveArticleButton = view.findViewById(R.id.write_to_db);
        mDeleteArticleButton = view.findViewById(R.id.delete_to_db);

        //building up the Retrofit object to begin calling the API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.AYLIEN_API_ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //passing in the ArticleAPI interface class into the retrofit Object
        ArticleAPI articleAPI = retrofit.create(ArticleAPI.class);

        //using the articleAPI object to call the GET method. Passed in URL.
        //will need to grab the URL from the user's web browser eventually. It's hard coded for now.
        Call<Article> call = articleAPI.getArticle(mUrlReceived, true);
        call.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                //Assign mArticle to the Response body (JSON object).
                //since our response is an Article object is must be stored as an Article Object.
                mArticle = response.body();

                //set article views to hold received text from the response body
                mTitleView.setText(mArticle.getTitle());
                mAuthorView.setText(mArticle.getAuthor());
                mArticleView.setText(mArticle.getArticle());
                mPUBdateView.setText(mArticle.getPublishDate());

                if (mArticle.getImage() == "") {
                    Toast.makeText(getContext(), "no image link", Toast.LENGTH_SHORT).show();
                } else {
                    Picasso.with(getContext()).load(mArticle.getImage()).into(mArticleImageView);
                }

            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                //on failure, Toast error code
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "API Failed: " + t.getMessage());
            }
        });

        mSaveArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArticle.setUid(mUser.getUid());
                writeArticleToDB(mArticle);
                Toast.makeText(getContext(), "Saved Article to " + mUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });

        mDeleteArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArticleToDB();
                Toast.makeText(getContext(), "Removed Article to " + mUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    //this method deletes the article that the user has saved
    private void deleteArticleToDB() {
        mDBRef_Article.removeValue();
    }

    //this method writes the article object to the user that is logged in
    private void writeArticleToDB(Article article) {
        mDBRef_Article.setValue(article);
    }

}
