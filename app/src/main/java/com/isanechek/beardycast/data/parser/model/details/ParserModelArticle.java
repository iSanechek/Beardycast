package com.isanechek.beardycast.data.parser.model.details;

/**
 * Created by isanechek on 11.05.16.
 */
public class ParserModelArticle {
    private String contentObj;

    public ParserModelArticle(String contentObj) {
        this.contentObj = contentObj;
    }

    public ParserModelArticle() {
    }

    public String getContentObj() {
        return contentObj;
    }
}
