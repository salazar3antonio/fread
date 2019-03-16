package com.freadapp.fread.data.model;

import android.content.Context;
import android.widget.EditText;

import java.util.List;

/**
 * Created by salaz on 3/4/2018.
 */

public class Tag {

    private String tagName;

    public Tag() {
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
