package com.isanechek.beardycast.data.model.podcast;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Episode extends RealmObject {

    @PrimaryKey
    private String idUrlArticle; // вермя в качестве id

    private String mEpisodeTitle;

    private String length; // From network
    private String duration; // From network
    private String podMp3Url; // From network
    private String localMp3Path;

    private int podTotalTime;
    private int podElapsedTime;

    private Date sortDate;

    private String explicit;
    private boolean isEpisodeListened;
    private boolean isEpisodeDownloaded;

    public String getmEpisodeTitle() {
        return mEpisodeTitle;
    }

    public void setmEpisodeTitle(String mEpisodeTitle) {
        this.mEpisodeTitle = mEpisodeTitle;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }

    public String getIdUrlArticle() {
        return idUrlArticle;
    }

    public void setIdUrlArticle(String idUrlArticle) {
        this.idUrlArticle = idUrlArticle;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPodMp3Url() {
        return podMp3Url;
    }

    public void setPodMp3Url(String podMp3Url) {
        this.podMp3Url = podMp3Url;
    }

    public String getLocalMp3Path() {
        return localMp3Path;
    }

    public void setLocalMp3Path(String localMp3Path) {
        this.localMp3Path = localMp3Path;
    }

    public int getPodTotalTime() {
        return podTotalTime;
    }

    public void setPodTotalTime(int podTotalTime) {
        this.podTotalTime = podTotalTime;
    }

    public int getPodElapsedTime() {
        return podElapsedTime;
    }

    public void setPodElapsedTime(int podElapsedTime) {
        this.podElapsedTime = podElapsedTime;
    }

    public String getExplicit() {
        return explicit;
    }

    public void setExplicit(String explicit) {
        this.explicit = explicit;
    }

    public boolean isEpisodeListened() {
        return isEpisodeListened;
    }

    public void setEpisodeListened(boolean episodeListened) {
        isEpisodeListened = episodeListened;
    }

    public boolean isEpisodeDownloaded() {
        return isEpisodeDownloaded;
    }

    public void setEpisodeDownloaded(boolean episodeDownloaded) {
        isEpisodeDownloaded = episodeDownloaded;
    }
}
