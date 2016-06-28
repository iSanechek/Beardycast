package com.isanechek.beardycast.data.parser.model.list;

/**
 * Created by isanechek on 04.05.16.
 */
public class ParserListTagModel {

    private String tagName;
    private String tagUrl;

    public ParserListTagModel(String tagName, String tagUrl) {
        this.tagName = tagName;
        this.tagUrl = tagUrl;
    }

    public ParserListTagModel() {
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagUrl() {
        return tagUrl;
    }
}
