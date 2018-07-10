package com.freadapp.fread;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static java.lang.Math.toIntExact;

import com.freadapp.fread.article.ArticleFeedFragment;
import com.freadapp.fread.signin.SignInFragment;
import com.freadapp.fread.tag.EditTagsFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setTitle(R.string.articles_menu_text);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.articles_menu_text)
                .withIcon(R.drawable.ic_chrome_reader_mode_white_24dp);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.edit_tags_menu_text)
                .withIcon(R.drawable.ic_edit_black_24dp);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.settings_menu_text)
                .withIcon(R.drawable.ic_settings_black_24dp);

        DividerDrawerItem dividerDrawerItem = new DividerDrawerItem();

        final Drawer mainDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1, item2, dividerDrawerItem, item3
                )
                .build();

        mainDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                //cast long to int for switch statement
                int identifier = (int) (long) drawerItem.getIdentifier();
                switch (identifier) {
                    case 1:
                        ArticleFeedFragment articleFeedFragment = ArticleFeedFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content_framelayout, articleFeedFragment).commit();
                        actionbar.setTitle(R.string.articles_menu_text);
                        mainDrawer.closeDrawer();
                        return true;
                    case 2:
                        EditTagsFragment editTagsFragment = EditTagsFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content_framelayout, editTagsFragment).commit();
                        actionbar.setTitle(R.string.edit_tags_menu_text);
                        mainDrawer.closeDrawer();
                        return true;
                    case 3:
                        SignInFragment signInFragment = SignInFragment.newInstance();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content_framelayout, signInFragment).commit();
                        actionbar.setTitle(R.string.sign_in_menu_text);
                        mainDrawer.closeDrawer();
                        return true;
                }
                return true;
            }
        });

        ArticleFeedFragment articleFeedFragment = ArticleFeedFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_framelayout, articleFeedFragment).commit();

    }

}
