package com.freadapp.fread.data.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FbDatabase {

    private static final String TAG = FbDatabase.class.getName();
    private static final String FB_USERS = "users";
    private static final String FB_ARTICLES = "articles";
    private static final String FB_TAGS = "tags";

    private static final FirebaseDatabase sFirebaseDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth sFirebaseAuth = FirebaseAuth.getInstance();
    private Article mArticle;
    private Tag mTag;

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
     * Create a new tag in the User's tags database reference.
     *
     * @param userTags Database reference of the User Tags
     * @param tagName  Name of tag the user entered
     */
    public static void createNewTag(DatabaseReference userTags, String tagName) {

        Tag tag = new Tag();
        String key = userTags.push().getKey();

        tag.setKeyid(key);
        tag.setTagName(tagName);

        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(key, tag);
        //update the children of "tags" in the DB with the passed in Hash Map
        userTags.updateChildren(writeMap);

    }

    /**
     * Removes the specified article object found at /articles/$articlekeyid in the database
     *
     * @param context      Application context
     * @param article      Article to be unsaved
     * @param userArticles Database Reference to the User's Articles
     */
    public static void unSaveArticle(Context context, Article article, DatabaseReference userArticles) {

        userArticles.child(article.getKeyid()).removeValue();

        Toast.makeText(context, "Article Unsaved.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves the specified Article. If Article is null, creates a new Article and pushes it to the database
     *
     * @param context      Application context
     * @param article      Article to be saved
     * @param userArticles Database Reference to the User's Articles
     * @param url          URL received from Intent.EXTRA_TEXT
     * @param uid          User's UID
     */
    public static void saveArticle(Context context, Article article, DatabaseReference userArticles, String url, String uid) {

        //create a unique KeyID for the Article
        String key = userArticles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyid(key);
        article.setUid(uid);
        article.setUrl(url);
        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyid(), article);
        //Save the specified article
        userArticles.updateChildren(writeMap);

        Toast.makeText(context, "Article Saved.", Toast.LENGTH_SHORT).show();

    }

    public static void addTagToArticle(final DatabaseReference articleRef, final Tag tag, final DatabaseReference userTags, final String articleKeyId) {

        // TODO: 4/5/2018 Check for duplicate Tag Name. User should not be able to add duplicate Tag Names

        articleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article specArticle = dataSnapshot.getValue(Article.class);

                List<Object> articleTags;

                //check to see if Article Tags are empty. If so create a new ArrayList and add the TagName
                if (specArticle.getArticleTags() != null) {
                    articleTags = specArticle.getArticleTags();
                    articleTags.add(tag.getTagName());
                } else {
                    articleTags = new ArrayList<>();
                    articleTags.add(tag.getTagName());
                }

                Map<String, Object> writeMap = new HashMap<>();
                writeMap.put("articleTags", articleTags);
                articleRef.updateChildren(writeMap);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userTags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tag specTag = dataSnapshot.getValue(Tag.class);

                List<Object> taggedArticles;

                if (specTag.getTaggedArticles() != null) {
                    taggedArticles = specTag.getTaggedArticles();
                    taggedArticles.add(articleKeyId);
                } else {
                    taggedArticles = new ArrayList<>();
                    taggedArticles.add(articleKeyId);
                }

                Map<String, Object> writeMap = new HashMap<>();
                writeMap.put("taggedArticles", taggedArticles);
                userTags.updateChildren(writeMap);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static void removeTagFromArticle(final DatabaseReference userArticle, final Tag tag, final DatabaseReference userTag, final String articleKeyId) {

        userArticle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Article specArticle = dataSnapshot.getValue(Article.class);
                List<Object> articleTags;

                //check to see if Article Tags are empty. If so create a new ArrayList and add the TagName
                if (specArticle.getArticleTags() != null) {

                    articleTags = specArticle.getArticleTags();
                    //loop through articleTags and search for a match of the TagName
                    for (int i = 0; i < articleTags.size(); i++) {
                        if (tag.getTagName().equals(articleTags.get(i).toString())) {

                            Log.i(TAG, articleTags.get(i).toString() + " was removed");
                            articleTags.remove(i);

                            Map<String, Object> writeMap = new HashMap<>();
                            writeMap.put("articleTags", articleTags);
                            userArticle.updateChildren(writeMap);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userTag.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Tag specTag = dataSnapshot.getValue(Tag.class);
                List<Object> taggedArticles;

                //check to see if Article Tags are empty. If so create a new ArrayList and add the TagName
                if (specTag.getTaggedArticles() != null) {

                    taggedArticles = specTag.getTaggedArticles();
                    //loop through taggedArticles and search for a match of the ArticleKeyID
                    for (int i = 0; i < taggedArticles.size(); i++) {
                        if (articleKeyId.equals(taggedArticles.get(i).toString())) {

                            Log.i(TAG, taggedArticles.get(i).toString() + " was removed");
                            taggedArticles.remove(i);

                            Map<String, Object> writeMap = new HashMap<>();
                            writeMap.put("taggedArticles", taggedArticles);
                            userTag.updateChildren(writeMap);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    public static void openArticleWebView(Activity activity, String url) {

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        activity.startActivity(intent);

    }

}

