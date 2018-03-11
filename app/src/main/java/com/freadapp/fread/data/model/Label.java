package com.freadapp.fread.data.model;

import java.util.List;

/**
 * Created by salaz on 3/4/2018.
 */

public class Label {

    private String keyid;
    private String label;
    private List<Object> labeledArticles;

    public Label() {
    }

    public Label(String keyid, String label, List<Object> labeledArticles) {
        this.keyid = keyid;
        this.label = label;
        this.labeledArticles = labeledArticles;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Object> getLabeledArticles() {
        return labeledArticles;
    }

    public void setLabeledArticles(List<Object> labeledArticles) {
        this.labeledArticles = labeledArticles;
    }
}
