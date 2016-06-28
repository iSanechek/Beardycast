package com.isanechek.beardycast.data.parser.model.list;

/**
 * Created by isanechek on 04.05.16.
 */
public class ParserListCategoryModel {

    private String categoryName;
    private String categoryUrl;

    public ParserListCategoryModel(String categoryName, String categoryUrl) {
        this.categoryName = categoryName;
        this.categoryUrl = categoryUrl;
    }

    public ParserListCategoryModel() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }
}
