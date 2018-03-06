package com.freadapp.fread.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the model for the Article object.
 * Artile Extraction API from Aylien.com.
 */

public class Article implements Parcelable {

    private String author;
    private String uid;
    private String image;
    private List<Object> tags = null;
    private String article;
    public String title;
    private String publishDate;
    private String url;
    private String keyid;
    private boolean saved = false;
    private List<Object> videos = null;
    private List<Object> feeds = null;


    public Article() {
    }

    public Article(String author, String uid, String image, List<Object> tags,
                   String article, String title, String publishDate, String url,
                   String keyid, boolean saved, List<Object> videos, List<Object> feeds) {
        this.author = author;
        this.uid = uid;
        this.image = image;
        this.tags = tags;
        this.article = article;
        this.title = title;
        this.publishDate = publishDate;
        this.url = url;
        this.keyid = keyid;
        this.saved = saved;
        this.videos = videos;
        this.feeds = feeds;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public List<Object> getVideos() {
        return videos;
    }

    public void setVideos(List<Object> videos) {
        this.videos = videos;
    }

    public List<Object> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Object> feeds) {
        this.feeds = feeds;
    }

    public Article(Parcel in) {

        author = in.readString();
        uid = in.readString();
        image = in.readString();
        tags = new ArrayList<>();
        in.readList(tags, null);
        article = in.readString();
        title = in.readString();
        publishDate = in.readString();
        url = in.readString();
        keyid = in.readString();
        saved = in.readByte() !=0;
        videos = new ArrayList<>();
        in.readList(videos, null);
        feeds = new ArrayList<>();
        in.readList(feeds, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(uid);
        parcel.writeString(image);
        parcel.writeList(tags);
        parcel.writeString(article);
        parcel.writeString(title);
        parcel.writeString(publishDate);
        parcel.writeString(url);
        parcel.writeString(keyid);
        parcel.writeByte((byte) (saved ? 1 : 0));
        parcel.writeList(videos);
        parcel.writeList(feeds);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
