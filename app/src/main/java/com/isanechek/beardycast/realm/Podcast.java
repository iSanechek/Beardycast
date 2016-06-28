package com.isanechek.beardycast.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Podcast extends RealmObject {

    @PrimaryKey
    private String idUrlArticle; // вермя в качестве id

    private String length; // From network
    private String duration; // From network
    private String podMp3Url; // From network
    private String localMp3Path;

    private long podTotalTime;
    private long podElapsedTime;

    private String explicit;
    private boolean isEpisodeListened;
    private boolean isEpisodeDownloaded;

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

    public long getPodTotalTime() {
        return podTotalTime;
    }

    public void setPodTotalTime(long podTotalTime) {
        this.podTotalTime = podTotalTime;
    }

    public long getPodElapsedTime() {
        return podElapsedTime;
    }

    public void setPodElapsedTime(long podElapsedTime) {
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
