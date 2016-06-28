package com.isanechek.beardycast.data;

import android.util.Log;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;
import com.isanechek.beardycast.data.parser.model.list.ParserListCategoryModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListTagModel;
import com.isanechek.beardycast.data.rss.RssItemParser;
import com.isanechek.beardycast.realm.Details;
import com.isanechek.beardycast.realm.Podcast;
import com.isanechek.beardycast.realm.model.ArtCategory;
import com.isanechek.beardycast.realm.model.ArtTag;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static com.isanechek.beardycast.utils.UrlUtil.getPageLoadedCount;
import static com.isanechek.beardycast.utils.UrlUtil.savePageLoadedCount;

/**
 * Created by isanechek on 04.05.16.
 */
public class DataLoader {
    private static final String TAG = "DataLoader";

    private ApiImpl api;
    private Realm realm;
    private BehaviorSubject<Boolean> networkInUse;

    public DataLoader() {
        api = ApiImpl.getInstance();
    }

    public void loadData(String url, Realm realm, BehaviorSubject<Boolean> networkInUse, boolean details, boolean podcast, String pN, boolean loadMore) {
        this.realm = realm;
        this.networkInUse = networkInUse;
        msg("-----------------LOAD DATA >>START<<----------------------");
        if (!details) {
            loadArticle(url, loadMore);
            msg("load article");
            msg("url -->> " + url);
            msg(loadMore ? "load more TRUE" : "load more FALSE");
        } else {
            loadDetails(url, podcast, pN);
            msg("load details");
            msg("url -->> " + url);
            msg(podcast ? "Podcast TRUE" : "podcast FALSE");
            msg("podcast name -->> " + pN);
        }
        msg("------------------LOAD DATA >>END<<-----------------------");
    }

    private void loadArticle(String url, boolean loadMore) {
        msg("--------------LOAD ARTICLE >>START<<-------------");
        msg(loadMore ? "load more TRUE" : "load more FALSE");
        if (loadMore) {
            url = url + getPageLoadedCount();
        }
        msg("url -->> " + url);
        networkInUse.onNext(true);
        api.getArticleList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(parserListModels -> {
                    processAndAddData(parserListModels);
                    networkInUse.onNext(false);
                }, throwable -> {
                    networkInUse.onNext(false);
                });
        msg("--------------LOAD ARTICLE >>END<<-------------");
    }

    private void processAndAddData(final List<ParserListModel> list) {
        msg("--------------PROCESS AND ADD DATE >>START<<-------------");
        if (list.isEmpty()) return;

        msg("list size -->> " + list.size());

        savePageLoadedCount();

        realm.executeTransaction(realm1 -> {
            RealmResults<Article> models = realm1.where(Article.class).findAll();
            if (models.size() == 0) {
                insertDate(list, true);
                msg("insert");
                msg("models size");
            }
            else {
                insertDate(checkNewItem(list, models), false);
            }
        });

        msg("--------------PROCESS AND ADD DATE >>END<<-------------");

    }

    private void insertDate(List<ParserListModel> list, boolean first) {
        if (list.isEmpty() || list.size() == 0) {
            return;
        }

        long start = System.currentTimeMillis();

        for (ParserListModel plm : list) {

            Article object = realm.createObject(Article.class);

            object.setArtTitle(plm.getTitle());
            object.setArtDescription(plm.getDescription());
            object.setArtDatePost(plm.getDatePost());
            object.setArtImgLink(plm.getImageUrl());
            object.setArtLink(plm.getLink());

            Date date = Util.getTimeStamp();
            object.setSortTimeStamp(date);

            object.setPodcast(plm.isPodcast());

            String podName = getPodName(plm.getTitle());
            object.setPodName(podName);

//            if (plm.getTitle().contains("BeardyCast")) {
//                object.setPodName("BeardyCast");
//                msg("podName -->> BeardyCast");
//            } else if (plm.getTitle().contains("BEARDYCARS")) {
//                object.setPodName("BEARDYCARS");
//                msg("podName -->> BEARDYCARS");
//            } else if (plm.getTitle().contains("Theory")) {
//                object.setPodName("Theory");
//                msg("podName -->> Theory");
//            } else if (plm.getTitle().contains("Crowd")) {
//                object.setPodName("Crowd");
//                msg("podName --> Crowd");
//            }

            /**
             * Для таких умных как я!
             * Почему так уебищно дальше!?!
             * Потому что читай внимательно!
             * Но лентяем скажу - потому что модели разные.
             * Спасибо за понимание!
             *
             * ps. С любовью ваш А.Тайнюк(aka iSanechek).
             * Мир! Труд! Май!
             */
            /*Insert category*/
            object.setCategory(new RealmList<>());
            for (ParserListCategoryModel model : plm.getCategory()) {
                ArtCategory category = new ArtCategory();
                category.setCategoryName(model.getCategoryName());
                category.setCategoryUrl(model.getCategoryUrl());
                object.getCategory().add(category);
            }

            /*Insert Tags*/
            object.setTags(new RealmList<>());
            for (int i = 0; i < plm.getTags().size(); i++) {
                ParserListTagModel model = plm.getTags().get(i);
                ArtTag tag = new ArtTag();
                tag.setTagName(model.getTagName());
                tag.setTagUrl(model.getTagUrl());
                object.getTags().add(tag);
            }

            if (first) {
                object.setNewArticle(false);
                object.setReadArticle(false);
            }
            else {
                object.setNewArticle(true);
                object.setReadArticle(false);
            }

            object.setSavedArticle(false);

            realm.copyToRealm(object);
        }
        long finish = System.currentTimeMillis() - start;
        msg("time insert article -->> " + finish);
    }

    private List<ParserListModel> checkNewItem(List<ParserListModel> list, RealmResults<Article> results) {
        List<ParserListModel> cache = new ArrayList<>();
        if (cache.isEmpty() || cache.size() != 0) {
            cache.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            ParserListModel model = list.get(i);
            if (check(model.getLink(), results)) {
                cache.add(model);
            }
        }
        return cache;
    }

    private boolean check(String url, RealmResults<Article> results) {
        for (Article article : results) {
            if (url.equalsIgnoreCase(article.getArtLink()))
                return false;
        }
        return true;
    }

    /*---------------------------------DETAILS--------------------------------------*/

    private void loadDetails(String url, boolean podcast, String podName) {
        networkInUse.onNext(true);
        api.getArticleDetails(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    networkInUse.onNext(false);
                    RealmResults<Details> results = realm.where(Details.class).findAll();
                    if (results.size() != list.size()) {
                        for (ParserModelArticle article : list) {
                            Details details = new Details();
                            details.setIdUrlArticle(url);
                            details.setObj(article.getContentObj());
                            realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(details));
                        }
                    }
                }, throwable -> {
                    networkInUse.onNext(false);
                });

        if (podcast) {
            parseRss(podName);
        }
    }

    private void parseRss(String name) {
        // Если первая загрузка, то грузим весь rss
        // Иначе первые N kb
        String url = null;
        if (name.contains("BEARDYCARS")) {
            url = Constants.PODCAST_BEARDYCARS_RSS;
        } else if (name.contains("BeardyCast")) {
            url = Constants.PODCAST_BEARDYCAST_RSS;
        }

        msg("url -->> " + url);

        networkInUse.onNext(true);
        api.getListPodcast(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(rssItemParsers -> {
                    insertPodcast(rssItemParsers, name);
                    networkInUse.onNext(false);
                }, throwable -> {
                    networkInUse.onNext(false);
                });
    }

    private void insertPodcast(ArrayList<RssItemParser> list, String id) {

        RealmResults<Podcast> results = realm.where(Podcast.class).findAllAsync();
        if (results.size() == 0) {
            insert(list, id);
        } else {
            List<RssItemParser> list1 = checkHeraks(list, results);
            insert(list1, id);
        }
    }

    private void insert(List<RssItemParser> rssItemParsers, String id) {
        if (rssItemParsers.isEmpty()) return;

        long start = System.currentTimeMillis();
        for (RssItemParser rss : rssItemParsers) {

            Podcast p = new Podcast();

            p.setIdUrlArticle(id);
            p.setLength(rss.getLength());
            p.setDuration(rss.getDuration());
            p.setPodMp3Url(rss.getMp3url());
            p.setPodTotalTime(100);
            p.setPodElapsedTime(0);
            p.setExplicit(rss.getExplicit());
            p.setEpisodeListened(false);
            p.setEpisodeDownloaded(false);

            realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(p));

        }

        long finish = System.currentTimeMillis() - start;
        msg("time insert podcast -->> " + finish);


    }

    private List<RssItemParser> checkHeraks(List<RssItemParser> parserList, RealmResults<Podcast> realmList) {
        List<RssItemParser> cache = new ArrayList<>();
        if (cache.isEmpty() || cache.size() != 0) {
            cache.clear();
        }
        for (int i = 0; i < parserList.size(); i++) {
            RssItemParser model = parserList.get(i);
            if (checkH(model.getMp3url(), realmList)) {
                cache.add(model);
            }
        }
        return cache;
    }

    private boolean checkH(String url, RealmResults<Podcast> results) {
        for (Podcast podcast : results) {
            if (url.equalsIgnoreCase(podcast.getPodMp3Url()))
                return false;
        }
        return true;
    }


    private String getPodName(String name) {
        if (name.contains("BeardyCast")) {
            msg("podName -->> BeardyCast");
            return ("BeardyCast");
            } else if (name.contains("BEARDYCARS")) {
            msg("podName -->> BEARDYCARS");
            return ("BEARDYCARS");
            } else if (name.contains("Theory")) {
            msg("podName -->> Theory");
            return ("Theory");
            } else if (name.contains("Crowd")) {
            msg("podName --> Crowd");
            return ("Crowd");
            }
        return "";
    }

    /*FIRST START*/

    private void msg(String text) {
        Log.d(TAG, text);
    }

}
