package com.freadapp.fread.data.model;

import java.util.Map;

/**
 * Created by salaz on 3/4/2018.
 */

public class Tag {

    private String tagName;
    private String keyId;
    private Map<String, Object> articlesTagged;

    public Tag() {
    }

    public Tag(String tagName, String keyId, Map<String, Object> articlesTagged) {
        this.tagName = tagName;
        this.keyId = keyId;
        this.articlesTagged = articlesTagged;
    }

    public String getTagName() {
        return tagName;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Map<String, Object> getArticlesTagged() {
        return articlesTagged;
    }

}
