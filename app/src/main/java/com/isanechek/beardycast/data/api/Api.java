package com.isanechek.beardycast.data.api;

import com.isanechek.beardycast.data.parser.articles.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.podcast.RssItemParser;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isanechek on 03.05.16.
 */
public interface Api {
    /*List Article*/
    Observable<List<ParserListModel>> getArticleList(String url);
    /*Last Date Update*/
    Observable<String> getDateLastPost();
    /*Episode List Feed*/
    Observable<ArrayList<RssItemParser>> getListPodcast(String url);
    /*Details Article*/
    Observable<String> getHtmlBody(String url);
    /*Podcast Url*/
    Observable<String> getPodcastUrl(String url);
}
