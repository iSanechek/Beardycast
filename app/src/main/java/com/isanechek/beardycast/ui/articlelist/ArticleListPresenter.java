package com.isanechek.beardycast.ui.articlelist;

import android.util.Log;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.MappingData;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.article.Podcast;
import com.isanechek.beardycast.data.parser.articles.model.list.ParserListModel;
import com.isanechek.beardycast.ui.articlelist.interfaces.ArticleListView;
import com.isanechek.beardycast.ui.mvp.MvpPresenter;
import com.isanechek.beardycast.utils.RxUtil;
import io.realm.Realm;
import io.realm.RealmList;
import org.xml.sax.ErrorHandler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import static com.isanechek.beardycast.utils.UrlUtil.getPageLoadedCount;
import static com.isanechek.beardycast.utils.UrlUtil.savePageLoadedCount;

/**
 * Created by isanechek on 04.09.16.
 */
public class ArticleListPresenter extends MvpPresenter<ArticleListView> {
    private static final String TAG = "Article List Presenter";

    private Model model;
    private ApiImpl api;
    private Realm realm;
    private Subscription subscription;
    private Subscription isNetworkUsed;
    private Subscription loadMoreS;
    public ArticleListPresenter() {
        model = Model.getInstance();
        api = ApiImpl.getInstance();
        realm = Realm.getDefaultInstance();
        Timber.tag(TAG);
    }

    public void loadData(String url) {
        checkViewAttached();

        RxUtil.unsubscribe(isNetworkUsed);
        isNetworkUsed = model.isNetworkUsed()
                .subscribe(aBoolean -> getMvpView().showProgress(aBoolean));

        RxUtil.unsubscribe(subscription);
        subscription = model.getSelectedArticleFeed()
                .subscribe(articles -> getMvpView().bindView(articles),
                        throwable -> getMvpView().showError(0, throwable.toString()));
    }

    public void refresh() {
        model.reloadArticleFeed();
    }

    public void loadMore() {
//        model.loadMoreArticleList();
        RealmList<Article> cache = new RealmList<>();
        if (cache.size() != 0) {
            cache.clear();
        }
        checkViewAttached();
        RxUtil.unsubscribe(loadMoreS);
        String url = getPageLoadedCount("home");
        msg("url " + url);
        loadMoreS = api.getArticleList(url)
                .onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    msg("return error: " + throwable.toString());
                    return new ArrayList<>();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(parserListModels -> {
                    msg("parser sie: " + parserListModels.size());
                    Stream.of(parserListModels)
                            .map(MappingData::fromNetworkToDbModel)
                            .filter(value -> {
//                                realm.executeTransactionAsync(realm1 -> {
//                                    Podcast podcast = new Podcast();
//                                    podcast.setPodcastId(value.getArtLink());
//                                    value.setPodcast(podcast);
//                                    Timber.d("Podcast Object: " + podcast.getPodcastId());
//                                });
                                return true;
                            }).forEach(value -> {
                        cache.add(value);

//                        realm.executeTransactionAsync(r -> r.copyToRealmOrUpdate(value));
                    });
                    msg("cache size: " + cache.size());
                    if (cache.size() != 0) {
                        getMvpView().bindMoreView(cache);
                    }
                    savePageLoadedCount("home");
                }, Timber::e);

    }

    private void msg(String msg) {
        Log.e(TAG, "msg: " + msg);
    }
}