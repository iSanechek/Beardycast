package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;

import android.util.Log;
import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

import static com.isanechek.beardycast.utils.UrlUtil.getPageLoadedCount;
import static com.isanechek.beardycast.utils.UrlUtil.savePageLoadedCount;

/**
 * Created by isanechek on 03.07.16.
 */

public class Model {

    private static final Map<String, String> sections;
    static {
        sections = new HashMap<>();
        sections.put("home", "Home");
        sections.put("home", "Home");
        sections.put("home", "Home");
        sections.put("home", "Home");
        sections.put("home", "Home");
    }

    private static Model instance = null;
    private final Repository repository;
    private String selectedSection;

    public static synchronized Model getInstance() {
        if (instance == null) {
            Repository repo = new Repository();
            instance = new Model(repo);
        }
        return instance;
    }

    private Model(Repository repository) {
        this.repository = repository;
        this.selectedSection = Constants.HOME_LINK;
    }

    public Observable<Boolean> isNetworkUsed() {
        return repository.networkInUse().distinctUntilChanged();
    }

    public Observable<RealmResults<Article>> getSelectedArticleFeed() {
        return repository.getListArticle(selectedSection, false);
    }

    public void reloadArticleFeed() {
        repository.getListArticle(selectedSection, true);
    }

    public Observable<RealmResults<Article>> loadMoreArticleList() {
        String url = null;

        url = selectedSection + getPageLoadedCount(selectedSection);
        /*Пока так*/
        LogUtil.logD("Load More Article List", "Url -->> " + url);
        savePageLoadedCount(selectedSection);

        return repository.getListArticle(url, false);
    }

    public Observable<Article> getArticle(@NonNull String id) {
        return repository.getArticle(id).filter(article -> article.isValid());
    }

    public Observable<Podcast> getPodcast(String id) {
        return repository.getPodcast(id).filter(podcast -> podcast.isValid());
    }

    public void addPodcastUrl(String id, String url) {
        repository.updatePodcastDownloadUrl(id, url);
        Log.e("addPodcastUrl", "add: " + id + " " + url);
    }

    /**
     * Returns all sections available.
     *
     * @return A map of <key, title> pairs for all available sections.
     */
    public Map<String, String> getSections() {
        return sections;
    }

    public void selectSection(@NonNull String key) {
        selectedSection = key;
        repository.getListArticle(selectedSection, false);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        repository.close();
    }

    public @NonNull String getCurrentSectionKey() {
        return selectedSection;
    }

}
