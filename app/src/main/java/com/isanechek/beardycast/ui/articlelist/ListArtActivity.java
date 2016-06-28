package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.isanechek.beardycast.App;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.podcast.PodcastActivity;

import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.RealmResults;

public class ListArtActivity extends AppCompatActivity {
    private static final String TAG = ListArtActivity.class.getSimpleName();
    private static final int LAYOUT = R.layout.main_activity;
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    RealmRecyclerView recyclerView;

    private Toolbar toolbar;

    ListArtPresenter presenter = new ListArtPresenter(this, Model.getInstance());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");
//        ButterKnife.bind(this);

        App.getRefWAtcher(this);

        toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        initRecyclerView();

        presenter.onCreate();

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
            case R.id.list_setting:
                // implements action
                break;
            case R.id.test:
                startActivity(new Intent(this, PodcastActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        recyclerView = ButterKnife.findById(this, R.id.realm_recycler_view);
        recyclerView.setOnRefreshListener(() -> presenter.refreshList());
        recyclerView.setOnLoadMoreListener(o -> {
            msg("LOAD MORE");
            presenter.loadMore();
        });
    }

    public void showLoadMore() {
        recyclerView.enableShowLoadMore();
    }

    public void hideLoadMore() {
        recyclerView.disableShowLoadMore();
    }

    public void showList(RealmResults<Article> list) {
        ListAdapter listAdapter = new ListAdapter(this, list, true, true);
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    public void hideRefreshing() {
        recyclerView.setRefreshing(false);
    }

    public void showNetworkLoading(Boolean networkInUse) {
//        progress.setVisibility(networkInUse ? View.VISIBLE : View.INVISIBLE);
    }

    private void msg(String text) {
        Log.d(TAG, text);
    }
}
