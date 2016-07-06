package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by isanechek on 03.07.16.
 */

public class RepositoryT implements Cloneable {
    private static final String TAG = RepositoryT.class.getSimpleName();
    private static final long MINIMUM_NETWORK_WAIT_SEC = 120;

    private DataLoaderT loader;
    private Realm realm;
    private BehaviorSubject<Boolean> networkLoading = BehaviorSubject.create(false);
    private Map<String, Long> lastNetworkRequest = new HashMap<>();

    @UiThread
    public RepositoryT() {
        loader = new DataLoaderT();
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

        RealmResults<Article> results = realm.where(Article.class).findAll();
        results = results.sort("artDatePost", Sort.DESCENDING);
        LogUtil.logD("Repository ", "SIZE -->> " + results.size());
        return results.asObservable();
    }

    @UiThread
    public Observable<Article> getArticle(@NonNull String id) {
        return realm.where(Article.class).equalTo("artLink", id).findFirst().asObservable();
    }

    @UiThread
    public Observable<Podcast> getPodcast(@NonNull String id) {
        return realm.where(Podcast.class).equalTo("idUrlArticle", id).findFirst().asObservable();
    }

    @UiThread
    public Observable<RealmResults<Podcast>> getPodcastList(@NonNull String namePodcast, boolean forceReload) {
        if (forceReload) {
            /*Тут пока непонятно как лучше сделать*/
        }

        RealmResults<Podcast> results = realm.where(Podcast.class).findAll();
        results = results.sort("sortDate", Sort.DESCENDING);
        return results.asObservable();
    }

    @UiThread
    public void updatePodcastInfo(@NonNull String id,
                                                 @NonNull String idPodcast,
                                                 @NonNull String namePodcast) {
        realm.executeTransactionAsync(r -> {
            Article article = realm.where(Article.class).equalTo("artLink", id).findFirst();
            article.setPodcastId(idPodcast);
            article.setPodName(namePodcast);
            r.copyToRealmOrUpdate(article);
        }, error -> {
            LogUtil.logE(TAG, "Error Update Podcast Info -->> " + error.toString());
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
