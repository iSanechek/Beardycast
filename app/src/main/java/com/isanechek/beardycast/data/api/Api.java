package com.isanechek.beardycast.data.api;

import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;
import com.isanechek.beardycast.data.rss.RssItemParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by isanechek on 03.05.16.
 */
public interface Api {
    Observable<List<ParserListModel>> getArticleList(String url);
    Observable<List<ParserModelArticle>> getArticleDetails(String url);
    Observable<String> getDateLastPost();
    Observable<ArrayList<RssItemParser>> getListPodcast(String url);

    /*For test*/
    Observable<JSONObject> getArticleObject(String url);
}
