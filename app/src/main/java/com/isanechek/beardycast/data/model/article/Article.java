package com.isanechek.beardycast.data.model.article;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by isanechek on 04.05.16.
 */
@RealmClass
public class Article extends RealmObject {

    @PrimaryKey
    private String artLink;

    private String artTitle;
    private String artDescription;
    private Date artDatePost;
    private String artImgLink;

    private RealmList<ArtTag> tags;
    private RealmList<ArtCategory> category;

    private Date sortTimeStamp;

    private boolean isNewArticle;
    private boolean isReadArticle;
    private boolean isSavedArticle;
    private boolean isPodcast;
    private String podcastId;
    private String podName; // Beardycast\Beardycars\TBBT\T-Crowd Show

    public String getArtLink() {
        return artLink;
    }

    public void setArtLink(String artLink) {
        this.artLink = artLink;
    }

    public String getArtTitle() {
        return artTitle;
    }

    public void setArtTitle(String artTitle) {
        this.artTitle = artTitle;
    }

    public String getArtDescription() {
        return artDescription;
    }

    public void setArtDescription(String artDescription) {
        this.artDescription = artDescription;
    }

    public Date getArtDatePost() {
        return artDatePost;
    }

    public void setArtDatePost(Date artDatePost) {
        this.artDatePost = artDatePost;
    }

    public String getArtImgLink() {
        return artImgLink;
    }

    public void setArtImgLink(String artImgLink) {
        this.artImgLink = artImgLink;
    }

    public RealmList<ArtTag> getTags() {
        return tags;
    }

    public void setTags(RealmList<ArtTag> tags) {
        this.tags = tags;
    }

    public RealmList<ArtCategory> getCategory() {
        return category;
    }

    public void setCategory(RealmList<ArtCategory> category) {
        this.category = category;
    }

    public boolean isNewArticle() {
        return isNewArticle;
    }

    public void setNewArticle(boolean newArticle) {
        isNewArticle = newArticle;
    }

    public boolean isReadArticle() {
        return isReadArticle;
    }

    public void setReadArticle(boolean readArticle) {
        isReadArticle = readArticle;
    }

    public boolean isSavedArticle() {
        return isSavedArticle;
    }

    public void setSavedArticle(boolean savedArticle) {
        isSavedArticle = savedArticle;
    }

    public Date getSortTimeStamp() {
        return sortTimeStamp;
    }

    public void setSortTimeStamp(Date sortTimeStamp) {
        this.sortTimeStamp = sortTimeStamp;
    }

    public boolean isPodcast() {
        return isPodcast;
    }

    public void setPodcast(boolean podcast) {
        isPodcast = podcast;
    }

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }
}
