package com.isanechek.beardycast.data;

import android.support.annotation.NonNull;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.model.details.DetailsModel;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmResults;
import rx.Observable;

import static com.isanechek.beardycast.utils.UrlUtil.getPageLoadedCount;

/**
 * Created by isanechek on 05.05.16.
 */
public class Model {

    private static final Map<String, String> sections;
    static {
        sections = new HashMap<>();
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

    public Observable<RealmResults<Article>> getSelectedArticleFeed() {
        return repository.getAllArticles(selectedSection, false, false);
    }

    public void reloadArticleFeed() {
        repository.getAllArticles(selectedSection, true, false);
    }

    public Observable<Article> getArticle(@NonNull String id) {
        return repository.getArticle(id);
    }

    public Observable<DetailsModel> getArticleDetails(String id) {
        return repository.getArticleDetails(id, false);
    }

    public Observable<Podcast> getPodcast(@NonNull String id, @NonNull String podcastName) {
        return repository.getPodcast(id, false, podcastName);
    }

    public Observable<Boolean> isNetworkUsed() {
        return repository.networkInUse().distinctUntilChanged();
    }

    public void firstStart() {
        repository.firstStart();
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
        repository.getAllArticles(selectedSection, false, false);
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
