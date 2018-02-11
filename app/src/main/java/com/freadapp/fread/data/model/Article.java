package com.freadapp.fread.data.model;

import java.util.List;

/**
 * This is the model for the Article object. It has the properties that match the query parameters of the Article
 * Extraction API from Aylien.com.
 */

public class Article {

    private String author;
    private String image;
    private List<Object> tags = null;
    private String article;
    private List<Object> videos = null;
    private String title;
    private String publishDate;
    private List<Object> feeds = null;

    public Article(String author, String image, List<Object> tags, String article, List<Object> videos, String title, String publishDate, List<Object> feeds) {
        this.author = author;
        this.image = image;
        this.tags = tags;
        this.article = article;
        this.videos = videos;
        this.title = title;
        this.publishDate = publishDate;
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

    public List<Object> getVideos() {
        return videos;
    }

    public void setVideos(List<Object> videos) {
        this.videos = videos;
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

    public List<Object> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Object> feeds) {
        this.feeds = feeds;
    }

}
