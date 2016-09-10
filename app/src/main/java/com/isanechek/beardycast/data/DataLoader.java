package com.isanechek.beardycast.data;

import com.annimon.stream.Stream;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.articles.model.list.ParserListModel;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by isanechek on 03.07.16.
 */

public class DataLoader {
    private static final String TAG = "DataLoader";

    private ApiImpl api;
    private Realm realm;
    private BehaviorSubject<Boolean> networkInUse;

    public DataLoader() {
        api = ApiImpl.getInstance();
    }

    public void loadData(String url, Realm realm, BehaviorSubject<Boolean> networkLoading) {
        this.realm = realm;
        this.networkInUse = networkLoading;
        loadAndInsert(url);
    }

    private void loadAndInsert(String url) {
        networkInUse.onNext(true);
        api.getArticleList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processAndAddData, Observable::error);
        networkInUse.onNext(false);
    }

    private void processAndAddData(List<ParserListModel> parserListModels) {
        RealmResults<Article> results = realm.where(Article.class).findAll();
        if (results.size() == 0) {

            insertData(parserListModels);
        } else {
            List<ParserListModel> list = checkNewItem(parserListModels, results);
            if (list.size() != 0) {
                msg("new article: " + list.size());
                insertData(list);
            } else {
                msg("No New Article");
            }
        }
    }

    private void insertData(List<ParserListModel> parserListModels) {
        Stream.of(parserListModels).map(MappingData::fromNetworkToDbModel)
                .filter(value -> {
                    Podcast podcast = new Podcast();
                    podcast.setPodcastId(value.getArtLink());
                    realm.executeTransactionAsync(r -> r.copyToRealmOrUpdate(podcast), () -> Timber.d("Inserted: " + value.getArtTitle() + " podcast"), error -> Timber.e("Inserted Error: " + value.getArtTitle() + " podcast" + " " + error.toString()));
                    return true;
                })
                .forEach(value -> {
                    Timber.d("Insert Article: " + value.getArtTitle());
                    realm.executeTransactionAsync(r -> r.copyToRealmOrUpdate(value), () -> Timber.d("Inserted: " + value.getArtTitle()), error -> Timber.e("Inserted Error: " + value.getArtTitle() + " " + error.toString()));
                });


    }

    private List<ParserListModel> checkNewItem(List<ParserListModel> list, RealmResults<Article> results) {
        List<ParserListModel> cache = new ArrayList<>();
        cache.clear();
        Stream.of(list).filterNot(parserListModel -> Stream.of(results)
                        .anyMatch(article -> parserListModel.getLink().equalsIgnoreCase(article.getArtLink())))
                .forEach(cache::add);
        return cache;
    }

    private void msg(String s) {
        LogUtil.logE(TAG, s);
    }
}
