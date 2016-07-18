package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.isanechek.beardycast.pref.PreferencesActivity;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class ListArtActivity extends AppCompatActivity {
    private static final String TAG = ListArtActivity.class.getSimpleName();
    private static final int LAYOUT = R.layout.main_activity;
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    private Toolbar toolbar;
    private Spinner spinner;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    ProgressBar progress;

    private ListTAdapter adapter;

    private ListArtPresenter presenter = new ListArtPresenter(this, ModelT.getInstance());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");
        App.getRefWAtcher(this);

        initView1();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = null;

        initView();


        presenter.onCreate();

    }

    private void initView1() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.article_list);
        progress = (ProgressBar) findViewById(R.id.progressBar);
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
                showSettingActivity();
                break;
            case R.id.test:

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
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.accent, R.color.colorPrimaryDark, R.color.accent);
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

    private void showSettingActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void msg(String text) {
        Log.d(TAG, text);
    }

    public void showErrorMessage(Throwable throwable) {
        Log.e(TAG, "showErrorMessage: ", throwable);
    }
}
