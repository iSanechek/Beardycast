package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by isanechek on 03.07.16.
 */

public class Repository implements Cloneable {
    private static final String TAG = Repository.class.getSimpleName();
    private static final long MINIMUM_NETWORK_WAIT_SEC = 120;

    private DataLoader loader;
    private Realm realm;
    private BehaviorSubject<Boolean> networkLoading = BehaviorSubject.create(false);
    private Map<String, Long> lastNetworkRequest = new HashMap<>();

    @UiThread
    public Repository() {
        loader = new DataLoader();
        realm = Realm.getDefaultInstance();
    }

    @UiThread
    public Observable<Boolean> networkInUse() {
        return networkLoading.asObservable();
    }

    @UiThread
    public Observable<RealmResults<Article>> getListArticle(@NonNull String url, boolean forceReload) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
            loader.loadData(url, realm, networkLoading);
            lastNetworkRequest.put(url, System.currentTimeMillis());
        }

        RealmResults<Article> results = realm.where(Article.class).findAllAsync();
        results = results.sort("artDatePost", Sort.DESCENDING);
        return results.asObservable();
    }

    @UiThread
    public Observable<Article> getArticle(@NonNull String id) {
        return realm.where(Article.class).equalTo("artLink", id).findFirstAsync().asObservable();
    }

    @UiThread
    public Observable<Podcast> getPodcast(String id) {
        return realm.where(Podcast.class).equalTo("podcastId", id).findFirstAsync().asObservable();
    }

    @UiThread
    public void updatePodcastDownloadUrl(String objectId, String podcastDownloadUrl) {
        Podcast podcast = realm.where(Podcast.class).equalTo("podcastId", objectId).findFirst();
        realm.beginTransaction();
        podcast.setPodcastUrl(podcastDownloadUrl);
        realm.commitTransaction();
    }

//    @UiThread
//    public void updatePodcastListinedDone(String objectId) {
//        realm.executeTransaction(r -> {
//            Podcast podcast = realm.where(Podcast.class).equalTo("podcastId", objectId).findFirst();
//            podcast.setPodcastListened(true);
//            r.copyToRealmOrUpdate(podcast);
//        });
//    }
//
//    @UiThread
//    public void updatePodcastElapsedTime(String objectId, long time) {
//        realm.executeTransaction(r -> {
//            Podcast podcast = realm.where(Podcast.class).equalTo("podcastId", objectId).findFirst();
//            podcast.setPodcastElapsedTime(time);
//            r.copyToRealmOrUpdate(podcast);
//        });
//    }
//
//    @UiThread
//    public void updatePodcastTotalTime(String objectId, long time) {
//        realm.executeTransaction(r -> {
//            Podcast podcast = realm.where(Podcast.class).equalTo("podcastId", objectId).findFirst();
//            podcast.setPodcastTotalTime(time);
//            r.copyToRealmOrUpdate(podcast);
//        });
//    }
//
//    @UiThread
//    public void updatePodcastDownloadState(String objectId) {
//        realm.executeTransaction(r -> {
//            Podcast podcast = realm.where(Podcast.class).equalTo("podcastId", objectId).findFirst();
//            podcast.setPodcastDownloaded(true);
//            r.copyToRealmOrUpdate(podcast);
//        });
//    }

    @UiThread
    public void updateArticleRead(String objectId) {
        realm.executeTransaction(r -> {
            Article article = realm.where(Article.class).equalTo("artLink", objectId).findFirst();
            article.setReadArticle(true);
            r.copyToRealmOrUpdate(article);
        });
    }

    @UiThread
    public void updateArticleNew(String objectId) {
        realm.executeTransaction(r -> {
            Article article = realm.where(Article.class).equalTo("artLink", objectId).findFirst();
            article.setNewArticle(false);
            r.copyToRealmOrUpdate(article);
        });
    }

    @UiThread
    public void close() {
        if (!realm.isClosed())
            realm.close();
    }

    private long timeSinceLastNetworkRequest(@NonNull String sectionKey) {
        Long lastRequest = lastNetworkRequest.get(sectionKey);
        if (lastRequest != null) {
            return TimeUnit.SECONDS.convert(System.currentTimeMillis() - lastRequest, TimeUnit.MILLISECONDS);
        } else {
            return Long.MAX_VALUE;
        }
    }
}
