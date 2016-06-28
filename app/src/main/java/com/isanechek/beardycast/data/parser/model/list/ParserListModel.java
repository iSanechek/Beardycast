package com.isanechek.beardycast.data.parser.model.list;

import java.util.Date;
import java.util.List;

/**
 * Created by isanechek on 26.04.16.
 */
public class ParserListModel {
    private String title;
    private String description;
    private Date datePost;
    private String link;
    private String imageUrl;
    private boolean isPodcast;

    private List<ParserListTagModel> tags;
    private List<ParserListCategoryModel> category;

    public ParserListModel(String title, String description, Date datePost, String link, String imageUrl, boolean isPodcast, List<ParserListTagModel> tags, List<ParserListCategoryModel> category) {
        this.title = title;
        this.description = description;
        this.datePost = datePost;
        this.link = link;
        this.imageUrl = imageUrl;
        this.isPodcast = isPodcast;
        this.tags = tags;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDatePost() {
        return datePost;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isPodcast() {
        return isPodcast;
    }

    public List<ParserListTagModel> getTags() {
        return tags;
    }

    public List<ParserListCategoryModel> getCategory() {
        return category;
    }
}
