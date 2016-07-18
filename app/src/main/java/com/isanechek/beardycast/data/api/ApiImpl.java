package com.isanechek.beardycast.data.api;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.rss.RssItemParser;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class ApiImpl implements Api {

    private static ApiImpl instance;

    public static ApiImpl getInstance() {
        if (instance == null) {
            instance = new ApiImpl();
        }
        return instance;
    }

    @Override
    public Observable<List<ParserListModel>> getArticleList(final String url) {
        return Observable.defer(() -> Observable.just(Parser.parseList(url)));
    }

    @Override
    public Observable<List<Element>> getArticleDetails(final String url) {
        return Observable.defer(() -> Observable.just(Parser.getArticle(url)));
    }


    @Override
    public Observable<String> getDateLastPost() {
        return Observable.defer(() -> Observable.just(Parser.parseDateLastPost(Constants.HOME_LINK)));
    }

    @Override
    public Observable<ArrayList<RssItemParser>> getListPodcast(final String url) {
        return Observable.defer(() -> Observable.just(RssItemParser.getRssItems(url)));
    }
}
