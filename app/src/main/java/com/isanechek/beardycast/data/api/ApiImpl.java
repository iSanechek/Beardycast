package com.isanechek.beardycast.data.api;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.parser.articles.Parser;
import com.isanechek.beardycast.data.parser.articles.model.list.ParserListModel;
import rx.Observable;

import java.util.List;

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
    public Observable<String> getDateLastPost() {
        return Observable.defer(() -> Observable.just(Parser.parseDateLastPost(Constants.HOME_LINK)));
    }


    @Override
    public Observable<String> getHtmlBody(String url) {
        return Observable.defer(() -> Observable.just(Parser.getDetailArticleContentBody(url)));
    }

    @Override
    public Observable<String> getPodcastUrl(String url) {
        return Observable.defer(() -> Observable.just(Parser.getMp3DownloadLink(url)));
    }
}
