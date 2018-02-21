package com.freadapp.fread.main_user_ui;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Article;

import java.util.List;

/**
 * Created by salaz on 2/18/2018.
 */

public class MainUIFragment extends Fragment {

    //will need refernce to DB and logged in user. This is where we will get our data
    private Article mArticle;
    private ArticleAdapter mArticleAdapter;
    private RecyclerView mRecyclerView;
    private TextView mArticleTitle;

    private class ArticleHolder extends RecyclerView.ViewHolder {

        //constructor of the private inner class Articleholder
        public ArticleHolder(View item) {
            super(item);

            mArticleTitle = (TextView) item.findViewById(R.id.article_title_list_item);

        }

        //this method binds the data of the article to the views
        public void bindArticle(Article article) {
            mArticle = article;
            mArticleTitle.setText(mArticle.getTitle());

        }

    }


    private class ArticleAdapter extends RecyclerView.Adapter<ArticleHolder> {

        private List<Article> mArticles;

        public ArticleAdapter(List<Article> articles) {
            mArticles = articles;
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
        }

        @Override
        public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.article_list_item, parent, false);
            return new ArticleHolder(view);
        }

        @Override
        public void onBindViewHolder(ArticleHolder holder, int position) {
            Article article = mArticles.get(position);
            holder.bindArticle(article);
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }
    }
}
