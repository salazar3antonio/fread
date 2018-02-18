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
    private String title;
    private String publishDate;
    private UUID id;
    private Date saveDate;

    public Article() {
        //create new UUID and set it to the article id
        //this is a unique code that is meant to individualize each article object saved by the user
        UUID uuid = UUID.randomUUID();
        setId(uuid);
    }

    public Article(String author, String image, List<Object> tags, String article, String title, String publishDate) {
        this.author = author;
        this.image = image;
        this.tags = tags;
        this.article = article;
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

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date savedate) {
        this.saveDate = savedate;
    }
}
