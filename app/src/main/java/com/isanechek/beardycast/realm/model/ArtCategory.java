package com.isanechek.beardycast.realm.model;

import io.realm.RealmObject;

/**
 * Created by isanechek on 04.05.16.
 */
public class ArtCategory extends RealmObject {

    private String categoryName;
    private String categoryUrl;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
