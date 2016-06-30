package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.isanechek.beardycast.realm.Details;
import com.isanechek.beardycast.realm.Podcast;
import com.isanechek.beardycast.realm.model.Article;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by isanechek on 05.05.16.
 */
public class Repository implements Cloneable {

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
    public Observable<RealmResults<Article>> getAllArticles(@NonNull String url, boolean forceReload, boolean loadMore) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
            loader.loadData(url, realm, networkLoading, false, false, null, loadMore);
            lastNetworkRequest.put(url, System.currentTimeMillis());
        }

        RealmResults<Article> results = realm
                .where(Article.class)
                .findAllAsync();
        results = results.sort("artDatePost", Sort.DESCENDING);
        return results.asObservable()
                .filter(RealmResults<Article>::isLoaded);

    }

    @UiThread
    public Observable<Details> getArticle(@NonNull String url, boolean forceReload) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
            loader.loadData(url, realm, networkLoading, true, false, null, false);
            lastNetworkRequest.put(url, System.currentTimeMillis());
        }

        return realm.where(Details.class).equalTo("idUrlArticle", url).findFirstAsync().asObservable();
    }

    @UiThread
    public Observable<Details> getArticle(@NonNull String url, @NonNull String podName, boolean podcast, boolean forceReload, boolean loadMore) {
        /*Тут так, ибо нехер лесть в сеть когда не просят*/
        if (forceReload) {
            if (timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
                loader.testLoadData(url, realm, networkLoading, loadMore, podcast, podName);
                lastNetworkRequest.put(url, System.currentTimeMillis());
            }
        }
        return realm.where(Details.class).equalTo("idUrlArticle", url).findFirst().asObservable();
    }

    @UiThread
    public Observable<Podcast> getPodcast(@NonNull String url, boolean forceReload, String podName) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
            loader.loadData(url, realm, networkLoading, false, true, podName, false);
            lastNetworkRequest.put(url, System.currentTimeMillis());
        }

        return realm.where(Podcast.class).equalTo("idUrlArticle", url).findFirstAsync().asObservable();
    }


    private long timeSinceLastNetworkRequest(@NonNull String sectionKey) {
        Long lastRequest = lastNetworkRequest.get(sectionKey);
        if (lastRequest != null) {
            return TimeUnit.SECONDS.convert(System.currentTimeMillis() - lastRequest, TimeUnit.MILLISECONDS);
        } else {
            return Long.MAX_VALUE;
        }
    }

    @UiThread
    public Observable<Article> getArticle(@NonNull String id) {
        return realm.where(Article.class).equalTo("artLink", id).findFirstAsync().asObservable();
    }

    @UiThread
    public void firstStart() {

    }

    @UiThread
    public void close() {
        realm.close();
    }

}
