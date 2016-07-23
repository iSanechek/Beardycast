package com.isanechek.beardycast.data;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.model.article.ArtCategory;
import com.isanechek.beardycast.data.model.article.ArtTag;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.model.list.ParserListCategoryModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListTagModel;
import com.isanechek.beardycast.utils.LogUtil;
import com.isanechek.beardycast.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static com.isanechek.beardycast.utils.Util.getPodName;

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
        msg("BOOM");
    }

    private void processAndAddData(List<ParserListModel> parserListModels) {

        msg("from parser size -->> " + parserListModels.size());
        RealmResults<Article> results = realm.where(Article.class).findAll();
        msg("from db size -->> " + results.size());
        if (results.size() == 0) {
            msg("DB NULL INSERT");
            insertData(parserListModels);
        }
        else {
            List<ParserListModel> list = checkNewItem(parserListModels, results);
            msg("DB NOT NULL " + ":IST SIZE -->> " + list.size());
            insertData(list);
        }
    }

    private void insertData(List<ParserListModel> parserListModels) {
        long start = System.currentTimeMillis();
        msg("insert data " + parserListModels.size());

        for (ParserListModel plm : parserListModels) {


            Article object = new Article();

            msg("article title -->> " + plm.getTitle());

            object.setArtTitle(plm.getTitle());
            object.setArtDescription(plm.getDescription());
            object.setArtDatePost(plm.getDatePost());
            object.setArtImgLink(plm.getImageUrl());
            object.setArtLink(plm.getLink());

            Date date = Util.getTimeStamp();
            object.setSortTimeStamp(date);

            object.setPodcast(plm.isPodcast());

            String podName = getPodName(plm.getTitle());
            msg("podcast name -->> " + podName);
            object.setPodName(podName);

            /*Insert category*/
            object.setCategory(new RealmList<>());
            for (ParserListCategoryModel model : plm.getCategory()) {
                ArtCategory category = new ArtCategory();
                category.setCategoryName(model.getCategoryName());
                category.setCategoryUrl(model.getCategoryUrl());
                object.getCategory().add(category);
                msg("category name -->> " + model.getCategoryName());
            }

            /*Insert Tags*/
            object.setTags(new RealmList<>());
            for (int i = 0; i < plm.getTags().size(); i++) {
                ParserListTagModel model = plm.getTags().get(i);
                ArtTag tag = new ArtTag();
                tag.setTagName(model.getTagName());
                tag.setTagUrl(model.getTagUrl());
                object.getTags().add(tag);
                msg("tags name -->> " + model.getTagName());
            }

            /*Пока пусть будет так*/
            object.setNewArticle(false);
            object.setReadArticle(false);
            object.setSavedArticle(false);

            realm.executeTransaction(r -> r.copyToRealmOrUpdate(object));
        }

        long finish = System.currentTimeMillis() - start;
        msg("Time Insert -->> " + finish);
    }

    private List<ParserListModel> checkNewItem(List<ParserListModel> list, RealmResults<Article> results) {
        msg("--------------CHECK NEW ARTICLE >>START<<-------------");
        msg("list size -->> " + list.size());
        msg("result size -->> " + results.size());
        List<ParserListModel> cache = new ArrayList<>();
        if (cache.isEmpty() || cache.size() != 0) {
            cache.clear();
            msg("cache clear");
        }
        for (int i = 0; i < list.size(); i++) {
            ParserListModel model = list.get(i);
            if (!check(model.getLink(), results)) {
                cache.add(model);
                msg("cache add -->> " + model.getTitle());
            }
        }

        msg("cache return size -->> " + cache.size());
        msg("--------------CHECK NEW ARTICLE >>END<<-------------");
        return cache;
    }

    private boolean check(String url, RealmResults<Article> results) {
        Stream.of(results).filter(value -> url.equalsIgnoreCase(value.getArtLink()));
        return false;
    }

    private void msg(String s) {
        LogUtil.logD(TAG, s);
    }
}
