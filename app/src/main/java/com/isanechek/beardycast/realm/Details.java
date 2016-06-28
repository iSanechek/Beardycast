package com.isanechek.beardycast.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by isanechek on 14.05.16.
 */
public class Details extends RealmObject {

    @PrimaryKey
    private String idUrlArticle;
    private String obj;

    public String getIdUrlArticle() {
        return idUrlArticle;
    }

    public void setIdUrlArticle(String idUrlArticle) {
        this.idUrlArticle = idUrlArticle;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }
}
