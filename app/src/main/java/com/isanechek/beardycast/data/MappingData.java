package com.isanechek.beardycast.data;

import com.annimon.stream.Stream;
import com.isanechek.beardycast.data.model.article.ArtCategory;
import com.isanechek.beardycast.data.model.article.ArtTag;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.articles.model.list.ParserListModel;
import com.isanechek.beardycast.utils.Util;

import java.util.Date;

import io.realm.RealmList;

/**
 * Created by isanechek on 11.08.16.
 */

public class MappingData {

    public static Article fromNetworkToDbModel(ParserListModel model) {
        Article article = new Article();
        article.setArtLink(model.getLink());
        article.setArtTitle(model.getTitle());
        article.setArtDescription(model.getDescription());
        article.setArtImgLink(model.getImageUrl());
        article.setNewArticle(false);
        article.setReadArticle(false);
        article.setSavedArticle(false);
        article.setArtDatePost(model.getDatePost());
        Date date = Util.getTimeStamp();
        article.setSortTimeStamp(date);
        article.setCategory(new RealmList<>());
        Stream.of(model.getCategory())
                .forEach(value -> {
                    ArtCategory category = new ArtCategory();
                    category.setCategoryName(value.getCategoryName());
                    category.setCategoryUrl(value.getCategoryUrl());
                    article.getCategory().add(category);
                });
        article.setTags(new RealmList<>());
        Stream.of(model.getTags())
                .forEach(value -> {
                    ArtTag tag = new ArtTag();
                    tag.setTagName(value.getTagName());
                    tag.setTagUrl(value.getTagUrl());
                    article.getTags().add(tag);
                });
        return article;
    }
}
