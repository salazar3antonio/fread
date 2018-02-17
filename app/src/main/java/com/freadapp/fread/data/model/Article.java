package com.freadapp.fread.data.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    private UUID id;
    private Date date;

    //create a random UUID to track which article is which
    public Article() {
        this(UUID.randomUUID());
    }


    public Article(UUID id) {
        this.id = id;
        date = new Date();
    }

    public Article(String author, String image, List<Object> tags, String article, List<Object> videos, String title, String publishDate) {
        this.author = author;
        this.image = image;
        this.tags = tags;
        this.article = article;
        this.videos = videos;
        this.title = title;
        this.publishDate = publishDate;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
