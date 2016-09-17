package com.isanechek.beardycast.data.model.article;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.Date;

/**
 * Created by isanechek on 04.05.16.
 */
@RealmClass
public class Article extends RealmObject {

    @PrimaryKey
    private String artLink;
    private String artTitle;
    private String artDescription;
    private String artImgLink;
    private String artTag;
    private boolean isNewArticle;
    private boolean isReadArticle;
    private boolean isSavedArticle;
    private boolean isPodcast;
    private Date artDatePost;
    private Date sortTimeStamp;
    private RealmList<ArtTag> tags;
    private RealmList<ArtCategory> category;
    private Podcast podcast;

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

    public String getArtImgLink() {
        return artImgLink;
    }

    public void setArtImgLink(String artImgLink) {
        this.artImgLink = artImgLink;
    }

    public String getArtTag() {
        return artTag;
    }

    public void setArtTag(String artTag) {
        this.artTag = artTag;
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

    public boolean isPodcast() {
        return isPodcast;
    }

    public void setPodcast(boolean podcast) {
        isPodcast = podcast;
    }

    public Date getArtDatePost() {
        return artDatePost;
    }

    public void setArtDatePost(Date artDatePost) {
        this.artDatePost = artDatePost;
    }

    public Date getSortTimeStamp() {
        return sortTimeStamp;
    }

    public void setSortTimeStamp(Date sortTimeStamp) {
        this.sortTimeStamp = sortTimeStamp;
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

    public Podcast getPodcast() {
        return podcast;
    }

    public void setPodcast(Podcast podcast) {
        this.podcast = podcast;
    }
}
