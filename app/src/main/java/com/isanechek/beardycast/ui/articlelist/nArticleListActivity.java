package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.annimon.stream.Stream;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.pref.PreferencesActivity;
import com.isanechek.beardycast.ui.details.DetailsArticleActivity;
import com.isanechek.beardycast.ui.widget.refreshview.PullToRefreshView;
import io.realm.RealmList;
import io.realm.RealmResults;

public class nArticleListActivity extends AppCompatActivity implements ArticleListAdapter.OnArticleClickListener {
    private static final String TAG = nArticleListActivity.class.getSimpleName();
    private static final int LAYOUT = R.layout.main_activity;
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    private PullToRefreshView refreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progress;
    private ArticleListAdapter adapter;

    private nArticleListPresenter presenter = new nArticleListPresenter(this, Model.getInstance());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");
        initToolbar();

        progress = (ProgressBar) findViewById(R.id.progressBar);
        refreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshStyle(this, 1);
        recyclerView = (RecyclerView) findViewById(R.id.article_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleListAdapter(this);
        adapter.bindRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            presenter.refreshList();
        });

//        adapter.setOnCallbackListener(() -> {
//            presenter.loadMore();
//            Log.e(TAG, "Load More");
//        });

        presenter.onCreate();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FF9800"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable(RECYCLER_VIEW_STATE, recyclerView.m);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.list_search:
                showSettingActivity();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void loadMore(RealmResults<Article> list) {
        Log.e(TAG, "loadMore: " + list.size());

        adapter.getListArticle().add(null);
        adapter.notifyItemInserted(adapter.getListArticle().size() - 1);
        final int currentSize = adapter.getItemCount();
        adapter.getListArticle().remove(adapter.getListArticle().size() - 1);
        adapter.notifyItemRemoved(adapter.getListArticle().size());
        adapter.setLoaded();
        adapter.getListArticle().addAll(getList(list));
        adapter.notifyItemRangeInserted(currentSize, currentSize + 10);
    }

    void showList(RealmResults<Article> list) {
        msg("Realm Result -->> " + list.size());
        adapter.setArticleList(getList(list));
        adapter.notifyDataSetChanged();

    }

    void showNetworkLoading(Boolean networkInUse) {
        progress.setVisibility(networkInUse ? View.VISIBLE : View.INVISIBLE);
        if (!networkInUse) {
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private RealmList<Article> getList(RealmResults<Article> articles) {
        RealmList<Article> list = new RealmList<>();
        list.clear();
        Stream.of(articles).forEach(list::add);
        return list;
    }

    private void showSettingActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void msg(String text) {
        Log.d(TAG, text);
    }

    void showErrorMessage(Throwable throwable) {
        Log.e(TAG, "showErrorMessage: ", throwable);
    }

    @Override
    public void onArticleClicked(String articleId) {
        DetailsArticleActivity.startActivity(this, articleId);
    }
}
