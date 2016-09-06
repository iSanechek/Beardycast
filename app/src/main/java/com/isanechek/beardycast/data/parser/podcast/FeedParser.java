package com.isanechek.beardycast.data.parser.podcast;

import com.annimon.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 02.07.16.
 */

public class FeedParser {
    private static final String TAG = FeedParser.class.getSimpleName();

    public FeedParser() {
    }

    /**/
    public Observable getArticleDetails(final String url) {
        return Async.start(() -> parseArticleDetails(url), Schedulers.computation());
    }

    private List<String> parseArticleDetails(String json) {
        final List<String> cache = new ArrayList<>();
        Elements elements = null;
        if (json != null) {
            Document document = Jsoup.parse(json);
            Element element = document.getElementsByClass("article-entry").first();
            elements = element.select("p");

        }

        Stream.of(elements)
                .map(Element::toString)
                .forEach(cache::add);

        /*For Test*/
        Stream.of(cache)
                .forEach(value -> logD("Test", value));
        return cache;
    }
}
