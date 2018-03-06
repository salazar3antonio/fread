package com.freadapp.fread.data.model;

import java.util.List;

/**
 * Created by salaz on 3/4/2018.
 */

public class Tag {

    private String keyid;
    private String utag;
    private List<Object> taggedArticles;

    public Tag() {
    }

    public Tag(String keyid,  String utag, List<Object> taggedArticles) {
        this.keyid = keyid;
        this.utag = utag;
        this.taggedArticles = taggedArticles;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getUtag() {
        return utag;
    }

    public void setUtag(String utag) {
        this.utag = utag;
    }

    public List<Object> getTaggedArticles() {
        return taggedArticles;
    }

    public void setTaggedArticles(List<Object> taggedArticles) {
        this.taggedArticles = taggedArticles;
    }
}
