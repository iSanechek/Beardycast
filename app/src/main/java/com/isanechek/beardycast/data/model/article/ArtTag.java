package com.isanechek.beardycast.data.model.article;

import io.realm.RealmObject;

/**
 * Created by isanechek on 04.05.16.
 */
public class ArtTag extends RealmObject {

    private String tagName;
    private String tagUrl;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagUrl() {
        return tagUrl;
    }

    public void setTagUrl(String tagUrl) {
        this.tagUrl = tagUrl;
    }
}
