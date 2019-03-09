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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FbDatabase {

    private static final String TAG = FbDatabase.class.getName();
    private static final String FB_USERS = "users";
    private static final String FB_ARTICLES = "articles";
    private static final String FB_TAGS = "tags";

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
     * Create a new Tag in the User's /tags database reference.
     *
     * @param userTags Database reference of the User's /tags
     * @param tagName  Name of the tag
     */
    public static void createNewTag(Context context, DatabaseReference userTags, String tagName) {

        Tag tag = new Tag(tagName);

        if (tagName.length() == 0) {
            Toast.makeText(context, "Enter tag name", Toast.LENGTH_SHORT).show();
        } else {
            userTags.child(tag.getTagName()).setValue(tag);
            Toast.makeText(context, "Added " + tagName, Toast.LENGTH_SHORT).show();
        }

    }

    public static void addTagKeyToArticle(DatabaseReference articles, Tag tag) {

        //add the tag key (which is the the tagName) to "taggedArticles as a key/boolean pair
        Map<String, Object> map = new HashMap<>();
        map.put(tag.getTagName(), true);

        articles.child("tags").updateChildren(map);

    }

    public static void removeTagKeyFromArticle(DatabaseReference articles, Tag tag) {

        articles.child("tags").child(tag.getTagName()).removeValue();

    }

    public static void addArticleKeyToTag(DatabaseReference tags, Article article, Tag tag) {

        Map<String, Object> map = new HashMap<>();
        map.put(article.getKeyId(), true);

        tags.child(tag.getTagName()).child("articlesTagged").updateChildren(map);

    }

    public static void removeArticleKeyFromTag(DatabaseReference tags, Article article, Tag tag) {

        tags.child(tag.getTagName()).child("articlesTagged").child(article.getKeyId()).removeValue();

    }

    /**
     * Deletes the relationship between each Article that has this Tag
     * Deletes entire Tag object from user/$uid/tags/
     *
     * @param context  Context for Toast notification
     * @param tag      Tag to be removed
     * @param userTag  Database reference to the specific tag
     * @param articles Database reference to the User's articles
     */
    public static void removeTag(Context context, final Tag tag, DatabaseReference userTag, final DatabaseReference articles) {

        userTag.child("articlesTagged").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String articleKeyId = child.getKey();
                    //remove the Tag from each related Article
                    articles.child(articleKeyId).child("tags").child(tag.getTagName()).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //remove the Tag from /tags/
        userTag.removeValue();

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
    public static void removeArticle(Context context, Article article, DatabaseReference userArticles) {

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
     * Updates the Tag name globally. All articles tagged with the oldTag will get updated with newTag.
     * Updates articles/[keyId]/articleTags/[position] and updates tags/[keyId]/tagName/
     *
     * @param article Database Reference to the specific Article
     * @param oldTag  Old Tag object that is getting updated
     * @param newTag  New Tag object with new user entered tagName
     */
    public static void updateTagName(DatabaseReference tag, DatabaseReference article, Tag oldTag, Tag newTag) {

        List<Object> taggedArticles = oldTag.getTaggedArticles();

        if (taggedArticles != null) {
            //if the Tag has Articles associated with the tag, loop through and update each Tag in each Article found.
            for (Object articleKeyId : taggedArticles) {
                updateTagNameInArticle(article.child(articleKeyId.toString()), oldTag, newTag);
            }
        }

//      Updates the Tag name inside of the Tag object.
        Map<String, Object> tagHash = new HashMap<>();
        tagHash.put("tagName", newTag.getTagName().toLowerCase());
        tag.updateChildren(tagHash);

    }

    /**
     * Updates the Tag name inside of the list of "articleTags" found in the Article object
     * Updates articles/[keyId]/articleTags/[position]
     *
     * @param article Database Reference to the specific Article
     * @param oldTag  Old Tag object that is getting updated
     * @param newTag  New Tag object with new user entered tagName
     */
    private static void updateTagNameInArticle(final DatabaseReference article, final Tag oldTag, final Tag newTag) {

        article.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Article specArticle = dataSnapshot.getValue(Article.class);
                List<Object> articleTags;

                //check to see if Article Tags are empty. If so create a new ArrayList and add the TagName
                if (specArticle.getArticleTags() != null) {

                    articleTags = specArticle.getArticleTags();

                    for (int i = 0; i < articleTags.size(); i++) {
                        if (oldTag.getTagName().equals(articleTags.get(i).toString())) {

                            String listPosition = String.valueOf(i);
                            Map<String, Object> tagHash = new HashMap<>();
                            tagHash.put(listPosition, newTag.getTagName().toLowerCase());
                            article.child("articleTags").updateChildren(tagHash);
                            break;
                        }
                    }

                    Log.i(TAG, "UPDATE Old Tag Name >> " + oldTag.getTagName() + " << to New Tag Name >> " + newTag.getTagName() + " <<");

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

