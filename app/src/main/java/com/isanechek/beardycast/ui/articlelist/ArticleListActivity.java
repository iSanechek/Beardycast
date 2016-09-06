package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.annimon.stream.Stream;
import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.pref.PreferencesActivity;
import com.isanechek.beardycast.ui.articlelist.interfaces.ArticleListView;
import com.isanechek.beardycast.ui.details.DetailsArticleActivity;
import com.isanechek.beardycast.ui.mvp.MvpActivity;
import com.isanechek.beardycast.ui.widget.refreshview.PullToRefreshView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by isanechek on 04.09.16.
 */
public class ArticleListActivity extends MvpActivity<ArticleListPresenter> implements ArticleListView,
        ArticleListAdapter.OnArticleClickListener, ArticleListAdapter.OnLoadMoreCallback {
    private static final String TAG = "Article List Activity";
    private static final int LAYOUT = R.layout.main_activity;
    private static final int PROFILE_SETTING = 100000;
    private static final String STATE_POSITION_INDEX = "state_position_index";
    private static final String STATE_POSITION_OFFSET = "state_position_offset";

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private Toolbar toolbar;
    private PullToRefreshView refreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progress;
    private ArticleListAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initView(savedInstanceState);
        bindDrawer(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.loadData(Constants.HOME_LINK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
        if (recyclerView != null) {
            int index = layoutManager.findFirstVisibleItemPosition();
            View topView = recyclerView.getChildAt(0);
            int offset = topView != null ? topView.getTop() : 0;
            outState.putInt(STATE_POSITION_INDEX, index);
            outState.putInt(STATE_POSITION_OFFSET, offset);
        }
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void showError(int errorCode, @NonNull String errorMessage) {

    }

    @Override
    public void showProgress(boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void bindView(RealmResults<Article> articles) {
        adapter.setArticleList(getList(articles));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onArticleClicked(String articleId) {
        DetailsArticleActivity.startActivity(this, articleId);
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(ArticleListActivity.this, "Load More", Toast.LENGTH_SHORT).show();
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

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    protected ArticleListPresenter getPresenter() {
        return new ArticleListPresenter();
    }

    private void initView(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FF9800"));
        progress = (ProgressBar) findViewById(R.id.progressBar);
        refreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshStyle(this, 1);

        recyclerView = (RecyclerView) findViewById(R.id.article_list);
        layoutManager = new LinearLayoutManager(this);
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(STATE_POSITION_INDEX);
            int offset = savedInstanceState.getInt(STATE_POSITION_OFFSET);
            layoutManager.scrollToPositionWithOffset(index, offset);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ArticleListAdapter(this);
        adapter.bindRecyclerView(recyclerView);
        adapter.setOnCallbackListener(this, this);
        recyclerView.setAdapter(adapter);
    }

    private void bindDrawer(Bundle savedInstanceState) {
        final IProfile profile = new ProfileDrawerItem()
                .withName("iSanechek")
                .withIcon(R.drawable.holder1)
                .withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.holder1)
                .addProfiles(profile,
                        new ProfileSettingDrawerItem()
                                .withName("Add Account")
                                .withDescription("Add new Account")
                                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus_one)
                                        .actionBar()
                                        .paddingDp(5)
                                        .colorRes(R.color.material_drawer_primary_text))
                                .withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem()
                                .withName("Manage Account")
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(100001)
                ).withOnAccountHeaderListener((view, profile1, current) -> {
                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == PROFILE_SETTING) {
                        int count = 100 + headerResult.getProfiles().size() + 1;
                        IProfile newProfile = new ProfileDrawerItem()
                                .withNameShown(true)
                                .withName("Batman" + count)
                                .withEmail("batman" + count + "@gmail.com")
                                .withIcon(R.drawable.holder1)
                                .withIdentifier(count);
                        if (headerResult.getProfiles() != null) {
                            headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                        } else {
                            headerResult.addProfiles(newProfile);
                        }
                    }
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Beardycast")
                                .withIdentifier(1)
                                .withSetSelected(false),
                        new PrimaryDrawerItem()
                                .withName("TBBT")
                                .withIdentifier(2)
                                .withSetSelected(false),
                        new PrimaryDrawerItem()
                                .withName("Подкасты")
                                .withIdentifier(3)
                                .withSetSelected(false),
                        new PrimaryDrawerItem()
                                .withName("Info")
                                .withIdentifier(4)
                                .withSetSelected(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName("Settings")
                                .withIdentifier(5)
                                .withSetSelected(false)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        if (drawerItem.getIdentifier() == 1) {

                        } else if (drawerItem.getIdentifier() == 2) {

                        } else if (drawerItem.getIdentifier() == 3) {

                        } else if (drawerItem.getIdentifier() == 4) {

                        } else if (drawerItem.getIdentifier() == 5) {
                            showSettingActivity();
                        }
                    }
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    private void showSettingActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private RealmList<Article> getList(RealmResults<Article> articles) {
        RealmList<Article> list = new RealmList<>();
        list.clear();
        Stream.of(articles).forEach(list::add);
        return list;
    }

}
