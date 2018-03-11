package com.freadapp.fread;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.freadapp.fread.article_classes.ArticleActivity;
import com.freadapp.fread.article_classes.ArticleFeedFragment;
import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.helpers.SingleFragmentActivity;
import com.freadapp.fread.signin_classes.SignInActivity;
import com.freadapp.fread.view_holder.ArticleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends SingleFragmentActivity {

    public static final String TAG = MainActivity.class.getName();

    @Override
    protected Fragment createFragment() {
        return ArticleFeedFragment.newInstance();
    }

}
