package com.isanechek.beardycast.ui.splash;

import android.util.Log;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.parser.model.list.ParserListCategoryModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListTagModel;
import com.isanechek.beardycast.pref.Preferences;
import com.isanechek.beardycast.realm.model.ArtCategory;
import com.isanechek.beardycast.realm.model.ArtTag;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.Presenter;
import com.isanechek.beardycast.utils.Util;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.isanechek.beardycast.utils.Util.getPodName;

/**
 * Created by isanechek on 30.05.16.
 */
public class SplashPresenter implements Presenter {

    private void msg(String s) {
        Log.d("SplashPresenter", s);
    }
    private void msge(String s) {
        Log.e("SplashPresenter", s);
    }

    private final SplashActivity view;
    private final Realm realm;
    private final ApiImpl api;

    private Subscription loaderSubscription;
    private Subscription dataSubscription;

    public SplashPresenter(SplashActivity view) {
        this.view = view;
        realm = Realm.getDefaultInstance();
        api = ApiImpl.getInstance();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        if (Preferences.MainPreferences.isFirstStart()) {
            msg("First Start");
//            loaderSubscription = model.isNetworkUsed()
//                    .subscribe(view::showProgress);

            // implements load data
            loadDate();
        }
    }

    @Override
    public void onPause() {
        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

    }

    @Override
    public void onDestroy() {
        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

        realm.close();
    }

    /**
     * Тут сработает при первом запуске, чтобы прогрузить первые 10 Article
     * и все подкасты (пока) с двух фидов.
     * Поэтому на слабом подключение может занять продолжительное время.
     */
    private void loadDate() {
        if (Preferences.MainPreferences.isFirstStart()) {
            api.getArticleList(Constants.HOME_LINK)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        for (ParserListModel model : list) {
                            Article article = new Article();
                            article.setArtTitle(model.getTitle());
                            article.setArtDescription(model.getDescription());
                            article.setArtDatePost(model.getDatePost());
                            article.setArtImgLink(model.getImageUrl());
                            article.setArtLink(model.getLink());

                            Date date = Util.getTimeStamp();
                            article.setSortTimeStamp(date);

                            article.setPodcast(model.isPodcast());

                            String podName = getPodName(model.getTitle());
                            msg("podcast name -->> " + podName);
                            article.setPodName(podName);

                            /*Insert category*/
                            article.setCategory(new RealmList<>());
                            for (ParserListCategoryModel m : model.getCategory()) {
                                ArtCategory category = new ArtCategory();
                                category.setCategoryName(m.getCategoryName());
                                category.setCategoryUrl(m.getCategoryUrl());
                                article.getCategory().add(category);
                                msg("category name -->> " + m.getCategoryName());
                            }

                            /*Insert Tags*/
                            article.setTags(new RealmList<>());
                            for (int i = 0; i < model.getTags().size(); i++) {
                                ParserListTagModel m = model.getTags().get(i);
                                ArtTag tag = new ArtTag();
                                tag.setTagName(m.getTagName());
                                tag.setTagUrl(m.getTagUrl());
                                article.getTags().add(tag);
                                msg("tags name -->> " + m.getTagName());
                            }

                            article.setNewArticle(false);
                            article.setReadArticle(false);
                            article.setSavedArticle(false);

                            realm.executeTransaction(r -> r.copyToRealmOrUpdate(article));
                        }
                    });
        }


        // mark first start is done
        Preferences.MainPreferences.markFirstStartDone();
    }


}
