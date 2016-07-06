package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.details_t_activity;
    private static final String TAG = "DetailsActivity";

    private static final String ARTICLE_ID = "article_id";

    public static Intent startActivity(Context context, String link) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(ARTICLE_ID, link);
        context.startActivity(intent);
        return intent;
    }

    @BindView(R.id.coordinatorLayout)
    NestedScrollView container;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.back_drop)
    ImageView imageView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.details_progress)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindColor(R.color.colorPrimary)
    int layoutColor;
    @BindColor(R.color.tagTextColor)
    int tagTextColor;
    @BindColor(R.color.secondary_text)
    int secondaryTextColor;
    @BindColor(R.color.divider)
    int dividerColor;

    private DetailsPresenter presenter;
    private Unbinder unbinder;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        unbinder = ButterKnife.bind(this);
        logD(TAG, "HELLO FROM " + TAG);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        String id = getIntent().getExtras().getString(ARTICLE_ID);
        if (id == null) {
            showErrorView(null);
            logD(TAG, "id все плохо");
        } else {
            logD(TAG, "id --> " + id);
            presenter = new DetailsPresenter(this, ModelT.getInstance(), id);
        }
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

    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void loadView(Article a) {
        Glide.with(DetailsActivity.this).load(a.getArtImgLink()).crossFade(1000).into(imageView);
        toolbar.setTitle(a.getArtTitle());

        fab.setImageResource(a.isPodcast() ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_forum_white_24dp);

    }

    public void createView(List<String> list) {

        Stream.of(list).forEach(value -> logD(TAG, value));

    }

    public void showErrorView(Throwable throwable) {
        Log.e(TAG, "showErrorView: ", throwable);
    }

}
