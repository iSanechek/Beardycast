package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.isanechek.beardycast.data.update.DataLoaderTest;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.details.DetailsModel;

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

    private DataLoaderTest loader;
    private Realm realm;
    private BehaviorSubject<Boolean> networkLoading = BehaviorSubject.create(false);
    private Map<String, Long> lastNetworkRequest = new HashMap<>();

    @UiThread
    public Repository() {
        loader = new DataLoaderTest();
        realm = Realm.getDefaultInstance();
    }

    @UiThread
    public Observable<Boolean> networkInUse() {
        return networkLoading.asObservable();
    }

    @UiThread
    public Observable<RealmResults<Article>> getAllArticles(@NonNull String url, boolean forceReload, boolean loadMore) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
//            loader.loadData(url, realm, networkLoading, false, false, null, loadMore);
            loader.loadData(url, false, realm, false, false, null, networkLoading);
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
    public Observable<DetailsModel> getArticleDetails(@NonNull String url, boolean forceReload) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
//            loader.loadData(url, realm, networkLoading, true, false, null, false);
            loader.loadData(url, false, realm, true, false, null, networkLoading);
            lastNetworkRequest.put(url, System.currentTimeMillis());
        }
        return realm.where(DetailsModel.class).equalTo("artLink", url).findFirstAsync().asObservable();
    }

//    @UiThread
//    public Observable<Details> getArticle(@NonNull String url, @NonNull String podName, boolean podcast, boolean forceReload, boolean loadMore) {
//        /*Тут так, ибо нехер лесть в сеть когда не просят*/
//        if (forceReload) {
//            if (timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
////                loader.testLoadData(url, realm, networkLoading, loadMore, podcast, podName);
//                loader.loadData(url, loadMore, realm, true, podcast, podName, networkLoading);
//                lastNetworkRequest.put(url, System.currentTimeMillis());
//            }
//        }
//        return realm.where(Details.class).equalTo("idUrlArticle", url).findFirst().asObservable();
//    }

    @UiThread
    public Observable<Podcast> getPodcast(@NonNull String url, boolean forceReload, String podName) {
        if (forceReload || timeSinceLastNetworkRequest(url) > MINIMUM_NETWORK_WAIT_SEC) {
//            loader.loadData(url, realm, networkLoading, false, true, podName, false);
            loader.loadData(url, false, realm, true, true, podName, networkLoading);
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
