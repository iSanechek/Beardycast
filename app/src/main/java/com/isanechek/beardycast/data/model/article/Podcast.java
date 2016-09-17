package com.isanechek.beardycast.data.model.article;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by isanechek on 09.09.16.
 */
public class Podcast extends RealmObject {

    @PrimaryKey
    private String podcastId; // тут ссылка на article
    private String podcastUrl;
    private String podcastLocalUrl;
    private String podcastTitle;
    private long podcastElapsedTime;
    private long podcastTotalTime;
    private boolean isPodcastListened;
    private boolean isPodcastDownloaded;

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public String getPodcastLocalUrl() {
        return podcastLocalUrl;
    }

    public void setPodcastLocalUrl(String podcastLocalUrl) {
        this.podcastLocalUrl = podcastLocalUrl;
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public void setPodcastTitle(String podcastTitle) {
        this.podcastTitle = podcastTitle;
    }

    public long getPodcastElapsedTime() {
        return podcastElapsedTime;
    }

    public void setPodcastElapsedTime(long podcastElapsedTime) {
        this.podcastElapsedTime = podcastElapsedTime;
    }

    public long getPodcastTotalTime() {
        return podcastTotalTime;
    }

    public void setPodcastTotalTime(long podcastTotalTime) {
        this.podcastTotalTime = podcastTotalTime;
    }

    public boolean isPodcastListened() {
        return isPodcastListened;
    }

    public void setPodcastListened(boolean podcastListened) {
        isPodcastListened = podcastListened;
    }

    public boolean isPodcastDownloaded() {
        return isPodcastDownloaded;
    }

    public void setPodcastDownloaded(boolean podcastDownloaded) {
        isPodcastDownloaded = podcastDownloaded;
    }
}
