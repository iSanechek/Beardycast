package com.isanechek.beardycast.realm.model.details;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by isanechek on 28.06.16.
 */
public class DetailsModel extends RealmObject {

    @PrimaryKey
    private String idUrlArticle;
    private RealmList<DetailsObject> objects;

    public String getIdUrlArticle() {
        return idUrlArticle;
    }

    public void setIdUrlArticle(String idUrlArticle) {
        this.idUrlArticle = idUrlArticle;
    }

    public RealmList<DetailsObject> getObjects() {
        return objects;
    }

    public void setObjects(RealmList<DetailsObject> objects) {
        this.objects = objects;
    }
}
