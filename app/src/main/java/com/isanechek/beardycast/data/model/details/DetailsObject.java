package com.isanechek.beardycast.data.model.details;

import io.realm.RealmObject;

/**
 * Created by isanechek on 28.06.16.
 */
public class DetailsObject extends RealmObject {

    private String object;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
