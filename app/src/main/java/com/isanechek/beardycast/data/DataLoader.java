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
import com.isanechek.beardycast.realm.model.details.DetailsModel;
import com.isanechek.beardycast.realm.model.details.DetailsObject;
import com.isanechek.beardycast.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void testLoadData(String url, Realm realm, BehaviorSubject<Boolean> networkInUse, boolean loadMore, boolean podcast, String podName) {

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

        msg("--------------INSERT ARTICLE >>START<<-------------");
        long start = System.currentTimeMillis();

        for (ParserListModel plm : list) {

            Article object = realm.createObject(Article.class);

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

            if (first) {
                object.setNewArticle(false);
                object.setReadArticle(false);
                msg("first TRUE");
                msg("new article FALSE");
                msg("read article FALSE");
            }
            else {
                msg("first FALSE");
                object.setNewArticle(true);
                object.setReadArticle(false);
                msg("new Article TRUE");
                msg("read article FALSE");
            }

            object.setSavedArticle(false);

            realm.copyToRealm(object);
        }
        long finish = System.currentTimeMillis() - start;
        msg("time insert article -->> " + finish);
        msg("--------------INSERT ARTICLE >>END<<-------------");
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
            if (check(model.getLink(), results)) {
                cache.add(model);
                msg("cache add -->> " + model.getTitle());
            }
        }
        msg("cache return size -->> " + cache.size());
        msg("--------------CHECK NEW ARTICLE >>END<<-------------");
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
        msg("--------------DETAILS ARTICLE >>START<<-------------");
        msg("url -->> " + url);
        msg(podcast ? "podcast TRUE" : "podcast FALSE");
        msg("podcast name -->> " + podName);
        networkInUse.onNext(true);
        api.getArticleDetails(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    networkInUse.onNext(false);
                    RealmResults<Details> results = realm.where(Details.class).findAll();
                    msg("results size -->> " + results.size());
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
        msg("--------------DETAILS ARTICLE >>END<<-------------");
    }

    private void parseRss(String name) {
        msg("--------------PARSE RSS >>START<<-------------");
        msg("name -->> " + name);
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

        msg("--------------PARSE RSS >>END<<-------------");
    }

    private void insertPodcast(ArrayList<RssItemParser> list, String id) {
        msg("--------------INSERT PODCAST >>START<<-------------");
        msg("rss items size -->> " + list.size());
        msg("podcast id -->> " + id);
        RealmResults<Podcast> results = realm.where(Podcast.class).findAllAsync();
        msg("results size -->> " + results.size());
        if (results.size() == 0) {
            insert(list, id);
            msg("first insert");
        } else {
            msg("update insert");
            List<RssItemParser> list1 = checkHeraks(list, results);
            msg("list1 size -->. " + list1.size());
            insert(list1, id);
        }
        msg("--------------INSERT PODCAST >>END<<-------------");
    }

    private void insert(List<RssItemParser> rssItemParsers, String id) {
        msg("--------------INSERT >>START<<-------------");
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
        msg("--------------INSERT >>END<<-------------");
    }

    private List<RssItemParser> checkHeraks(List<RssItemParser> parserList, RealmResults<Podcast> realmList) {
        msg("--------------CHECK HERAKS >>START<<-------------");
        msg("parse list size -->> " + parserList.size());
        msg("realm list size -->> " + realmList.size());
        List<RssItemParser> cache = new ArrayList<>();
        if (cache.isEmpty() || cache.size() != 0) {
            cache.clear();
            msg("cache clear");
        }
        for (int i = 0; i < parserList.size(); i++) {
            RssItemParser model = parserList.get(i);
            if (checkH(model.getMp3url(), realmList)) {
                cache.add(model);
                msg("cache add -->> " + model.getTitle());
            }
        }
        msg("cache return size -->> " + cache.size());
        msg("--------------CHECK HERAKS >>END<<-------------");
        return cache;
    }

    private List<?> testCheck(List<?> listNetwork, RealmResults<?> listDB) {
        List<?> cache = new ArrayList<>();
        if (cache.isEmpty() || cache.size() != 0) {
            cache.clear();
            msg("cache clear");
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

    private void testLoadDetails(String url, boolean podcast, String podName) {
        msg("--------------TEST LOAD DETAILS ARTICLE >>START<<-------------");
        msg("url -->> " + url);
        msg(podcast ? "podcast TRUE" : "podcast FALSE");
        msg("podcast name -->> " + podName);

        networkInUse.onNext(true);
        api.getArticleDetails(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    networkInUse.onNext(false);
                    insertDetails(list, podcast, podName, url);
                }, throwable -> {
                    networkInUse.onNext(false);
                    msg("Error load details -->> " + throwable.getMessage());
                });

        msg("--------------TEST LOAD DETAILS ARTICLE >>END<<-------------");
    }

    private void insertDetails(List<ParserModelArticle> list, boolean podcast, String  podName, String url) {
        msg("--------------INSERT DETAILS ARTICLE >>START<<-------------");

        DetailsModel details = new DetailsModel();
        details.setIdUrlArticle(url);
        msg("id art -->> " + url);
        details.setObjects(new RealmList<>());
        for (ParserModelArticle article : list) {
            DetailsObject object = new DetailsObject();
            object.setObject(article.getContentObj());
            details.getObjects().add(object);
        }

        realm.executeTransaction(r -> r.copyToRealmOrUpdate(details));


        if (podcast) {
            String link = null;
            if (podName.contains("BEARDYCARS")) {
                link = Constants.PODCAST_BEARDYCARS_RSS;
            } else if (podName.contains("BeardyCast")) {
                link = Constants.PODCAST_BEARDYCAST_RSS;
            }
            msg("link -->> " + link);

            networkInUse.onNext(true);
            api.getListPodcast(link)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(rssItemParsers -> {
                        networkInUse.onNext(false);
                        insertPodcastN(rssItemParsers, url);
                    }, error -> {
                        msg("podcast -->> " + error.getMessage());
                    });

        }
        msg("--------------INSERT DETAILS ARTICLE >>END<<-------------");
    }

    private void insertPodcastN(ArrayList<RssItemParser> list, String url) {


    }

    private void msg(String text) {
        Log.d(TAG, text);
    }

}
