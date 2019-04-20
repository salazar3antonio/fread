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
import android.widget.Toast;

import com.freadapp.fread.article.ArticlesMainFragment;
import com.freadapp.fread.data.database.FirebaseUtils;
import com.freadapp.fread.dictionary.DictionaryMainFragment;
import com.freadapp.fread.signin.SignInFragment;
import com.freadapp.fread.tag.EditTagsActivity;
import com.freadapp.fread.tag.TagsMainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInSuccessListener {

    public static final String TAG = MainActivity.class.getName();

    public static final String MENU_ITEM_ID = "menu_item_id";

    private BottomNavigationView mBottomNavView;
    private Menu mBottomNaveMenu;
    private boolean mFirebaseUserSignedIn = FirebaseUtils.isFirebaseUserSignedIn();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mBottomNavView = findViewById(R.id.bottom_nav);
        mBottomNaveMenu = mBottomNavView.getMenu();

        if (savedInstanceState != null) {

            MenuItem menuItem = mBottomNaveMenu.findItem(savedInstanceState.getInt(MENU_ITEM_ID));
            //pass in last saved menu item id and create the fragment
            selectFragment(menuItem);

        } else {
            //initial loading of the first fragment
            selectFragment(mBottomNaveMenu.getItem(0));
        }

        mBottomNavView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectFragment(item);
                        return false;
                    }
                });

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
            case R.id.edit_tags_item:
                Intent editTagsIntent = new Intent(getApplicationContext(), EditTagsActivity.class);
                startActivity(editTagsIntent);
                return true;

            case R.id.sign_out_item:
                FirebaseAuth.getInstance().signOut();
                selectFragment(mBottomNaveMenu.getItem(0));
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(MENU_ITEM_ID, mBottomNavView.getSelectedItemId());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mFirebaseUserSignedIn) {
            selectFragment(mBottomNaveMenu.findItem(mBottomNavView.getSelectedItemId()));
        }

    }

    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof SignInFragment) {
            SignInFragment signInFragment = (SignInFragment) fragment;
            signInFragment.setOnSignInSuccessListener(this);
        }
    }

    private void selectFragment(MenuItem item) {

        boolean userLoggedIn = FirebaseUtils.isFirebaseUserSignedIn();

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.main_nav_articles:
                if (userLoggedIn) {
                    pushFragment(ArticlesMainFragment.newInstance());
                } else {
                    pushFragment(SignInFragment.newInstance());
                }
                break;
            case R.id.main_nav_tags:
                if (userLoggedIn) {
                    pushFragment(TagsMainFragment.newInstance());
                } else {
                    pushFragment(SignInFragment.newInstance());
                }
                break;
            case R.id.main_nav_dictionary:
                if (userLoggedIn) {
                    pushFragment(DictionaryMainFragment.newInstance());
                } else {
                    pushFragment(SignInFragment.newInstance());
                }
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

    @Override
    public void onSignInSuccess(boolean signInSuccess) {

        if (signInSuccess) {
            Toast.makeText(getApplicationContext(), "Sign In Success", Toast.LENGTH_SHORT).show();
            selectFragment(mBottomNaveMenu.getItem(0));
        } else {
            Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
        }

    }
}
