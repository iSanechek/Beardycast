package com.isanechek.beardycast.data.api;

import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.rss.RssItemParser;

import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by isanechek on 03.05.16.
 */
public interface Api {
    /*List Article*/
    Observable<List<ParserListModel>> getArticleList(String url);
    /*Details Article*/
    Observable<List<Element>> getArticleDetails(String url);
    /*Last Date Update*/
    Observable<String> getDateLastPost();
    /*Episode List Feed*/
    Observable<ArrayList<RssItemParser>> getListPodcast(String url);
}
