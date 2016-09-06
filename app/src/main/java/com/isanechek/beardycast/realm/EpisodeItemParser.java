package com.isanechek.beardycast.realm;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by isanechek on 30.05.16.
 */
public class EpisodeItemParser extends RealmObject {

    private String title;
    private Date pubDate;
    private String length;
    private String mp3url;
    private String duration;
    private String explicit;

    public EpisodeItemParser(String title, Date pubDate, String length, String mp3url, String duration, String explicit) {
        this.title = title;
        this.pubDate = pubDate;
        this.length = length;
        this.mp3url = mp3url;
        this.duration = duration;
        this.explicit = explicit;
    }

    public EpisodeItemParser() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMp3url() {
        return mp3url;
    }

    public void setMp3url(String mp3url) {
        this.mp3url = mp3url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExplicit() {
        return explicit;
    }

    public void setExplicit(String explicit) {
        this.explicit = explicit;
    }
}
