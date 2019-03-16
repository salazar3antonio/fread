package com.freadapp.fread.data.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.freadapp.fread.data.model.Article;
import com.freadapp.fread.data.model.Tag;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FbDatabase {

    private static final String TAG = FbDatabase.class.getName();
    private static final String FB_USERS = "users";
    private static final String FB_ARTICLES = "articles";
    private static final String FB_TAGS = "tags";
    public static final String FB_ARTICLES_TAGGED = "articlesTagged";
    public static final String FB_TAG_NAME = "tagName";

    private static final FirebaseDatabase sFirebaseDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth sFirebaseAuth = FirebaseAuth.getInstance();

    /**
     * Get a reference of the User's Articles from the Firebase Database
     *
     * @param userUid Firebase User's UID
     */
    public static DatabaseReference getUserArticles(String userUid) {
        return sFirebaseDatabase.getReference().child(FB_USERS).child(userUid).child(FB_ARTICLES);
    }

    /**
     * Get a reference of the User's Tags from the Firebase Database
     *
     * @param userUid Firebase User's UID
     */
    public static DatabaseReference getUserTags(String userUid) {
        return sFirebaseDatabase.getReference().child(FB_USERS).child(userUid).child(FB_TAGS);
    }

    /**
     * Authenticate the Firebase User. Returns Null if no user logged in.
     *
     * @param user Firebase User
     */
    public static FirebaseUser getAuthUser(FirebaseUser user) {
        user = sFirebaseAuth.getCurrentUser();
        if (user != null) {
            return user;
        } else {
            Log.e(TAG, "No user logged in");
            return null;
        }
    }

    /**
     * Creates a new Tag in the User's /tags database reference.
     *
     * @param userTags Database reference of the User's /tags
     * @param tagName  Name of the tag
     */
    public static void createNewTag(Context context, DatabaseReference userTags, String tagName) {

        Tag tag = new Tag();
        tag.setTagName(tagName);

        Map<String, Object> tagMap = new HashMap<>();
        tagMap.put(tag.getTagName(), tag);

        if (tagName.length() == 0) {
            Toast.makeText(context, "Enter tag name", Toast.LENGTH_SHORT).show();
        } else {
            userTags.updateChildren(tagMap);
            Toast.makeText(context, "Added " + tagName, Toast.LENGTH_SHORT).show();
        }

    }

    public static void addTagKeyToArticle(DatabaseReference articles, Tag tag) {

        //add the tag key (which is the the tagName) to "taggedArticles as a key/boolean pair
        Map<String, Object> map = new HashMap<>();
        map.put(tag.getTagName(), true);

        articles.child(FB_TAGS).updateChildren(map);

    }

    public static void removeTagKeyFromArticle(DatabaseReference articles, Tag tag) {

        articles.child(FB_TAGS).child(tag.getTagName()).removeValue();

    }

    public static void addArticleKeyToTag(DatabaseReference tags, Article article, Tag tag) {

        Map<String, Object> map = new HashMap<>();
        map.put(article.getKeyId(), true);

        tags.child(tag.getTagName()).child(FB_ARTICLES_TAGGED).updateChildren(map);

    }

    public static void removeArticleKeyFromTag(DatabaseReference tags, Article article, Tag tag) {

        tags.child(tag.getTagName()).child(FB_ARTICLES_TAGGED).child(article.getKeyId()).removeValue();

    }

    /**
     * Deletes the relationship between each Article that has this Tag
     * Deletes entire Tag object from user/$uid/tags/
     *
     * @param context  Context for Toast notification
     * @param tag      Tag to be removed
     * @param tags     Database reference to the specific tag
     * @param articles Database reference to the User's articles
     */
    public static void deleteTag(Context context, final Tag tag, DatabaseReference tags, final DatabaseReference articles) {

        tags.child(tag.getTagName()).child(FB_ARTICLES_TAGGED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String articleKeyId = child.getKey();
                    //remove the Tag from each related Article
                    articles.child(articleKeyId).child(FB_TAGS).child(tag.getTagName()).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //remove the Tag from /tags
        tags.child(tag.getTagName()).removeValue();

        Toast.makeText(context, tag.getTagName() + " deleted", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "REMOVED Tag >> " + tag + " from User Tags");
    }

    /**
     * Removes the specified article object found at /articles/[articlekeyid] in the database
     *
     * @param context      Application context
     * @param article      Article to be unsaved
     * @param userArticles Database Reference to the User's Articles
     */
    public static void deleteArticle(Context context, Article article, DatabaseReference userArticles) {

        userArticles.child(article.getKeyId()).removeValue();

        Toast.makeText(context, "Article Unsaved.", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "REMOVED Article >> " + article.getKeyId() + " from User Articles");

    }

    /**
     * Saves the specified Article. If Article is null, creates a new Article and push it to the database
     *
     * @param article      Article object to be saved
     * @param userArticles Database Reference to the User's Articles
     * @param url          URL received from Intent.EXTRA_TEXT
     * @param uid          User's UID
     */
    public static void saveArticle(Article article, DatabaseReference userArticles, String url, String uid) {

        //create a unique KeyID for the Article
        String key = userArticles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyId(key);
        article.setUid(uid);
        article.setUrl(url);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyId(), article);
        //Save the specified article
        userArticles.updateChildren(writeMap);

    }

    /**
     * Sets the key "saved" value of the Article object to true or false in the database
     *
     * @param context      Application context
     * @param article      Article object to set saved value to
     * @param userArticles Database Reference to the User's Articles
     * @param save         set saved value of Article object
     */
    public static void setSavedArticle(Context context, DatabaseReference userArticles, Article article, boolean save) {

        article.setSaved(save);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyId(), article);
        //Save the specified article
        userArticles.updateChildren(writeMap);

        if (save) {
            Toast.makeText(context, "Article Saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Article Unsaved.", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Edit the Tag Name globally.
     *
     * @param context       Context for Toast notification
     * @param tag           Tag object that is getting updated
     * @param updateTagName Updated new tag name
     */
    public static void editTagName(Context context, DatabaseReference tags, Tag tag, String updateTagName) {

        tags.child(tag.getTagName()).child(FbDatabase.FB_TAG_NAME).setValue(updateTagName);

        Toast.makeText(context, updateTagName + " updated", Toast.LENGTH_SHORT).show();

    }

    public static void openArticleWebView(Activity activity, String url) {

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        activity.startActivity(intent);

    }

}

