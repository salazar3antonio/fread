package com.freadapp.fread;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.freadapp.fread.article.ArticlesMainFragment;
import com.freadapp.fread.signin.SignInActivity;
import com.freadapp.fread.signin.SignInFragment;
import com.freadapp.fread.tag.EditTagsActivity;
import com.freadapp.fread.tag.TagsMainFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        setupNavigationView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu view on the toolbar
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in_item:
                Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(signInIntent);
                return true;
            case R.id.edit_tags_item:
                Intent editTagsIntent = new Intent(getApplicationContext(), EditTagsActivity.class);
                startActivity(editTagsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    private void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.main_nav_articles:
                pushFragment(ArticlesMainFragment.newInstance());
                break;
            case R.id.main_nav_tags:
                pushFragment(TagsMainFragment.newInstance());
                break;
            case R.id.main_nav_profile:
                pushFragment(SignInFragment.newInstance());
                break;
        }
    }

    private void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager != null) {

            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (ft != null) {
                ft.replace(R.id.main_content_frame, fragment);
                ft.commit();
            }
        }
    }

}
