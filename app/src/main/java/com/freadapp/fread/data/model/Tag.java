package com.freadapp.fread.data.model;

import java.util.List;

/**
 * Created by salaz on 3/4/2018.
 */

public class Tag {

    private String keyid;
    private String tagName;
    private List<Object> taggedArticles = null;

    public Tag() {
    }

    public Tag(String keyid, String tagName, List<Object> taggedArticles) {
        this.keyid = keyid;
        this.tagName = tagName;
        this.taggedArticles = taggedArticles;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<Object> getTaggedArticles() {
        return taggedArticles;
    }

    public void setTaggedArticles(List<Object> taggedArticles) {
        this.taggedArticles = taggedArticles;
    }
}
