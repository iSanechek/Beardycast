package com.isanechek.beardycast.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by isanechek on 14.05.16.
 */
public class ArticleDetails extends RealmObject {
    @PrimaryKey
    private String id;

    private RealmList<Details> listObjs;

    public ArticleDetails(String id, RealmList<Details> listObjs) {
        this.id = id;
        this.listObjs = listObjs;
    }

    public ArticleDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Details> getListObjs() {
        return listObjs;
    }

    public void setListObjs(RealmList<Details> listObjs) {
        this.listObjs = listObjs;
    }
}
