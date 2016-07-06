package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.annimon.stream.Stream;
import com.isanechek.beardycast.App;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.podcast.PodcastActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ListArtActivity extends AppCompatActivity {
    private static final String TAG = ListArtActivity.class.getSimpleName();
    private static final int LAYOUT = R.layout.main_activity;
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.article_list)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progress;

    private Unbinder unbinder;
    private ListTAdapter adapter;

    private ListArtPresenter presenter = new ListArtPresenter(this, ModelT.getInstance());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");
        unbinder = ButterKnife.bind(this);
        App.getRefWAtcher(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = null;

        initView();

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
        unbinder.unbind();
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

    public void configureToolbar(List<String> sections) {
        String[] sectionList = sections.toArray(new String[sections.size()]);
        final ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, sectionList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.titleSpinnerSectionSelected((String) adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView() {
        refreshLayout.setColorSchemeResources(ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            recyclerView.smoothScrollToPosition(0);
            presenter.refreshList();
        });
    }

    public void loadMore(RealmResults<Article> list) {

        adapter.getListArticle().add(null);
        adapter.notifyItemInserted(adapter.getListArticle().size() - 1);

        final int currentSize = adapter.getItemCount();
        adapter.getListArticle().remove(adapter.getListArticle().size() - 1);
        adapter.notifyItemRemoved(adapter.getListArticle().size());
        adapter.setLoaded();

        adapter.getListArticle().addAll(getList(list));
        adapter.notifyItemRangeInserted(currentSize, currentSize + 10);
    }

    public void showList(RealmResults<Article> list) {

        msg("Realm Result -->> " + list.size());

        adapter = new ListTAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.bindRecyclerView(recyclerView);
        adapter.setOnLoadMoreListener(() -> presenter.loadMore());
        recyclerView.setAdapter(adapter);
        adapter.setArticleList(getList(list));
        adapter.notifyDataSetChanged();

    }

    public void showNetworkLoading(Boolean networkInUse) {
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

    private void msg(String text) {
        Log.d(TAG, text);
    }

    public void showErrorMessage(Throwable throwable) {
        Log.e(TAG, "showErrorMessage: ", throwable);
    }
}
