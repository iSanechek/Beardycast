package com.isanechek.beardycast.ui.articlelist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.annimon.stream.Stream;
import com.isanechek.beardycast.App;
import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.pref.PreferencesActivity;
import com.isanechek.beardycast.rxbus.RxBus;
import com.isanechek.beardycast.rxbus.SizeNewArticleEvent;
import com.isanechek.beardycast.ui.articlelist.interfaces.ArticleListView;
import com.isanechek.beardycast.ui.details.DetailsArticleActivity;
import com.isanechek.beardycast.ui.mvp.MvpActivity;
import com.isanechek.beardycast.utils.RxUtil;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by isanechek on 04.09.16.
 */
public class ArticleListActivity extends MvpActivity<ArticleListPresenter> implements ArticleListView,
        ArticleListAdapter.OnArticleClickListener, ArticleListAdapter.OnLoadMoreCallback {
    private static final int LAYOUT = R.layout.main_activity;
    private static final int PROFILE_SETTING = 100000;
    private static final String STATE_POSITION_INDEX = "state_position_index";
    private static final String STATE_POSITION_OFFSET = "state_position_offset";

//    private PullToRefreshView refreshLayout;
    /*Не удалять!!! Надо будет потом.*/
//    private AccountHeader headerResult = null;
//    private Drawer result = null;

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ArticleListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View loadListProgress;
    private View loadMoreProgress;
    private View showErrorLayout;
    private ProgressBar networkUsedProgressBar;
    private Subscription busSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initView(savedInstanceState);
//        bindDrawer(savedInstanceState);

        Timber.tag("Article List Activity");
        Timber.d("onCreate");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.loadData(Constants.HOME_LINK);
        showToastSizeNewArticle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxUtil.unsubscribe(busSubscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState = result.saveInstanceState(outState);
//        outState = headerResult.saveInstanceState(outState);
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
//        if (result != null && result.isDrawerOpen()) {
//            result.closeDrawer();
//        } else {
//            super.onBackPressed();
//        }

        super.onBackPressed();
    }


    @Override
    public void showError(int errorCode, @NonNull String errorMessage) {
        showErrorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress(boolean visible) {
//        loadListProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
        networkUsedProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void bindView(RealmResults<Article> articles) {
        refreshLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.setArticleList(getList(articles));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void bindMoreView(RealmList<Article> articles) {
        adapter.getListArticle().add(null);
        adapter.notifyItemInserted(adapter.getListArticle().size() - 1);
        final int currentSize = adapter.getItemCount();

        adapter.getListArticle().remove(adapter.getListArticle().size() - 1);
        adapter.notifyItemRemoved(adapter.getListArticle().size());
        adapter.getListArticle().addAll(articles);
        adapter.notifyItemRangeInserted(currentSize, currentSize + 10);
        adapter.setLoaded();
    }

    @Override
    public void onArticleClicked(String articleId) {
        DetailsArticleActivity.startActivity(this, articleId);
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(ArticleListActivity.this, "Load More", Toast.LENGTH_SHORT).show();
        presenter.loadMore();
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
//        refreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
//        refreshLayout.setRefreshStyle(this, 1);

        loadListProgress = findViewById(R.id.article_progress_circle);
        loadMoreProgress = findViewById(R.id._artice_list_loading_more);
        showErrorLayout = findViewById(R.id.error_message);
        networkUsedProgressBar = (ProgressBar) findViewById(R.id.progress_network_used);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.article_swipe_refresh);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.accent));
        refreshLayout.setOnRefreshListener(() -> presenter.refresh());

        recyclerView = (RecyclerView) findViewById(R.id.article_list);
        recyclerView.setHasFixedSize(true);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

//    private void bindDrawer(Bundle savedInstanceState) {
//        final IProfile profile = new ProfileDrawerItem()
//                .withName("iSanechek")
//                .withIcon(R.drawable.holder1)
//                .withIdentifier(100);
//
//        headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withTranslucentStatusBar(true)
//                .withHeaderBackground(R.drawable.holder1)
//                .addProfiles(profile,
//                        new ProfileSettingDrawerItem()
//                                .withName("Add Account")
//                                .withDescription("Add new Account")
//                                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus_one)
//                                        .actionBar()
//                                        .paddingDp(5)
//                                        .colorRes(R.color.material_drawer_primary_text))
//                                .withIdentifier(PROFILE_SETTING),
//                        new ProfileSettingDrawerItem()
//                                .withName("Manage Account")
//                                .withIcon(GoogleMaterial.Icon.gmd_settings)
//                                .withIdentifier(100001)
//                ).withOnAccountHeaderListener((view, profile1, current) -> {
//                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == PROFILE_SETTING) {
//                        int count = 100 + headerResult.getProfiles().size() + 1;
//                        IProfile newProfile = new ProfileDrawerItem()
//                                .withNameShown(true)
//                                .withName("Batman" + count)
//                                .withEmail("batman" + count + "@gmail.com")
//                                .withIcon(R.drawable.holder1)
//                                .withIdentifier(count);
//                        if (headerResult.getProfiles() != null) {
//                            headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
//                        } else {
//                            headerResult.addProfiles(newProfile);
//                        }
//                    }
//                    return false;
//                })
//                .withSavedInstance(savedInstanceState)
//                .build();
//
//        result = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withHasStableIds(true)
//                .withAccountHeader(headerResult)
//                .addDrawerItems(
//                        new PrimaryDrawerItem()
//                                .withName("Beardycast")
//                                .withIdentifier(1)
//                                .withSetSelected(false),
//                        new PrimaryDrawerItem()
//                                .withName("TBBT")
//                                .withIdentifier(2)
//                                .withSetSelected(false),
//                        new PrimaryDrawerItem()
//                                .withName("Подкасты")
//                                .withIdentifier(3)
//                                .withSetSelected(false),
//                        new PrimaryDrawerItem()
//                                .withName("Info")
//                                .withIdentifier(4)
//                                .withSetSelected(false),
//                        new DividerDrawerItem(),
//                        new SecondaryDrawerItem()
//                                .withName("Settings")
//                                .withIdentifier(5)
//                                .withSetSelected(false)
//                )
//                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
//                    if (drawerItem != null) {
//                        if (drawerItem.getIdentifier() == 1) {
//
//                        } else if (drawerItem.getIdentifier() == 2) {
//
//                        } else if (drawerItem.getIdentifier() == 3) {
//
//                        } else if (drawerItem.getIdentifier() == 4) {
//
//                        } else if (drawerItem.getIdentifier() == 5) {
//                            showSettingActivity();
//                        }
//                    }
//                    return false;
//                })
//                .withSavedInstance(savedInstanceState)
//                .build();
//    }

    private void showToastSizeNewArticle() {
        RxUtil.unsubscribe(busSubscription);
        busSubscription = App.getInstance()
                .bus()
                .toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof SizeNewArticleEvent.Message) {
                        int size = ((SizeNewArticleEvent.Message)o).count;
                        if (size != 0) {
                            Toast.makeText(ArticleListActivity.this, "New Article", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ArticleListActivity.this, "No New Article", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
