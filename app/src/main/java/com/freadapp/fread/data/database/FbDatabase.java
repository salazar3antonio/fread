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
     * Create a new Tag in the User's tags database reference.
     * Creates a new Tag object in tags/[key]
     *
     * @param userTags Database reference of the User's Tags
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
     * Deletes entire Tag object from user/[uID]/tags
     *
     * @param context Application context for Toast notification
     * @param tag     Tag object to be removed
     * @param userTag Database reference to the User's Tag
     */
    public static void deleteTag(Context context, Tag tag, DatabaseReference userTag) {

        userTag.removeValue();
        Toast.makeText(context, tag.getTagName() + " deleted", Toast.LENGTH_SHORT).show();

    }

    /**
     * Removes the specified article object found at /articles/$articlekeyid in the database
     *
     * @param context      Application context
     * @param article      Article to be unsaved
     * @param userArticles Database Reference to the User's Articles
     */
    public static void unSaveArticle(Context context, Article article, DatabaseReference userArticles) {

        userArticles.child(article.getKeyId()).removeValue();

        Toast.makeText(context, "Article Unsaved.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves the specified Article. If Article is null, creates a new Article and push it to the database
     *
     * @param context      Application context
     * @param article      Article object to be saved
     * @param userArticles Database Reference to the User's Articles
     * @param url          URL received from Intent.EXTRA_TEXT
     * @param uid          User's UID
     */
    public static void saveArticle(Context context, Article article, DatabaseReference userArticles, String url, String uid) {

        //create a unique KeyID for the Article
        String key = userArticles.push().getKey();
        //store the keyid and userid into the Article object. This helps for retrieval later.
        article.setKeyId(key);
        article.setUid(uid);
        article.setUrl(url);
        article.setSaved(true);

        //a hash map to store the key (keyid) and value (article object) pair to be saved to the DB
        Map<String, Object> writeMap = new HashMap<>();
        writeMap.put(article.getKeyId(), article);
        //Save the specified article
        userArticles.updateChildren(writeMap);

        Toast.makeText(context, "Article Saved.", Toast.LENGTH_SHORT).show();

    }

    /**
     * Add Tag name to the specified Article in the Database.
     * Updates articles/[key]/articleTags
     *
     * @param article Database Reference to the specific Article
     * @param tag     Tag object
     */
    public static void addTagNameToArticle(final DatabaseReference article, final Tag tag) {

        // TODO: 4/5/2018 Check for duplicate Tag Name. User should not be able to add duplicate Tag Names

        article.addListenerForSingleValueEvent(new ValueEventListener() {
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
                article.updateChildren(writeMap);

                Log.i(TAG, "ADDED Tag Name >> " + tag.getTagName() + " << to Article KeyId >> " + specArticle.getKeyId() + " <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Remove the Tag Name from the specified Article.
     * Updates articles/[key]/articleTags
     *
     * @param article Database Reference to the specific Article
     * @param tag     Tag object
     */
    public static void removeTagNameFromArticle(final DatabaseReference article, final Tag tag) {

        article.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            articleTags.remove(i);

                            Map<String, Object> writeMap = new HashMap<>();
                            writeMap.put("articleTags", articleTags);
                            article.updateChildren(writeMap);
                            break;
                        }
                    }

                    Log.i(TAG, "REMOVED Tag Name >> " + tag.getTagName() + " << from Article KeyId >> " + specArticle.getKeyId() + " <<");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Add Article KeyId to the specified Tag in the Database.
     * Updates tags/[key]/taggedArticles
     *
     * @param tag          Database Reference to the specific Tag
     * @param articleKeyId KeyId of the Article object
     */
    public static void addArticleKeyIdToTag(final DatabaseReference tag, final String articleKeyId) {

        tag.addListenerForSingleValueEvent(new ValueEventListener() {
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
                tag.updateChildren(writeMap);

                Log.i(TAG, "ADDED Article KeyID >> " + articleKeyId + " << to Tag >> " + specTag.getTagName() + " <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Remove Article KeyId from the specified Tag
     * Updates tags/[key]/taggedArticles
     *
     * @param tag          Database Reference to the specific Tag
     * @param articleKeyId KeyId of the Article object
     */
    public static void removeArticleKeyIdFromTag(final DatabaseReference tag, final String articleKeyId) {

        tag.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            taggedArticles.remove(i);

                            Map<String, Object> writeMap = new HashMap<>();
                            writeMap.put("taggedArticles", taggedArticles);
                            tag.updateChildren(writeMap);
                            break;
                        }
                    }

                    Log.i(TAG, "REMOVED Article KeyID >> " + articleKeyId + " << from Tag >> " + specTag.getTagName() + " <<");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Updates the Tag name globally. All articles tagged with the oldTag will get updated with the newTag.
     * Updates articles/[keyId]/articleTags/[position] and updates tags/[keyId]/tagName/
     *
     * @param article Database Reference to the specific Article
     * @param oldTag  Old Tag object that is getting updated
     * @param newTag  New Tag object with new user entered tagName
     */
    public static void updateTagName(DatabaseReference tag, DatabaseReference article, Tag oldTag, Tag newTag) {

        List<Object> taggedArticles = oldTag.getTaggedArticles();

        if (taggedArticles != null) {

            for (Object articleKeyId : taggedArticles) {
                updateTagNameInArticle(article.child(articleKeyId.toString()), oldTag, newTag);
            }

//          Updates the Tag name inside of the Tag object.
            Map<String, Object> tagHash = new HashMap<>();
            tagHash.put("tagName", newTag.getTagName());
            tag.updateChildren(tagHash);

        }

    }

    /**
     * Updates the Tag name inside of the list of "articleTags" found in the Article object
     * Updates articles/[keyId]/articleTags/[position]
     *
     * @param article Database Reference to the specific Article
     * @param oldTag  Old Tag object that is getting updated
     * @param newTag  New Tag object with new user entered tagName
     */
    public static void updateTagNameInArticle(final DatabaseReference article, final Tag oldTag, final Tag newTag) {

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
                            tagHash.put(listPosition, newTag.getTagName());
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

