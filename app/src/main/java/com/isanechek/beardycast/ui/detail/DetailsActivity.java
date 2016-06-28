package com.isanechek.beardycast.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;
import com.isanechek.beardycast.realm.model.ArtCategory;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.imageviewer.ImageViewer;
import com.isanechek.beardycast.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.isanechek.beardycast.utils.Util.dpToPix;
import static com.isanechek.beardycast.utils.Util.isAndroid5Plus;

public class DetailsActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.details_activity;
    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static Intent startDetailsIntent(Context context, Article article) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(Constants.MODEL_ID, article.getArtLink());
        context.startActivity(intent);
        return intent;
    }

    private DetailsPresenter presenter;
    private DisplayMetrics metrics;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.details_progress)
    MaterialProgressBar progress;
//    @BindView(R.id.title_details)
//    TextView title_tv;
    @BindView(R.id.date_details)
    TextView date_tv;
//    @BindView(R.id.error_details_view)
//    CardView error_view;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.details_category)
    TagView tagView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);
        msg("HELLO FROM DETAILS ACTIVITY");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CoordinatorLayout layout = ButterKnife.findById(this, R.id.coordinatorLayout);
        String id = getIntent().getExtras().getString(Constants.MODEL_ID);
        if (id == null) {
            Snackbar.make(layout, "Oopppsssss", Snackbar.LENGTH_LONG)
                    .show();
        }
        else {
            presenter = new DetailsPresenter(this, Model.getInstance(), id);
            presenter.onCreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
        msg("Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
        msg("Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        msg("GOOD BAY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.article_menu:
                // implements action
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadView(Article article) {
        ImageView imageView = ButterKnife.findById(this, R.id.back_drop);
        Glide.with(this).load(article.getArtImgLink()).placeholder(R.drawable.h1).into(imageView);

        showCategory(article.getCategory());

        // Тут категории будут
        String title = "";
        for (ArtCategory category : article.getCategory()) {
            title += category.getCategoryName() + "\t";
        }
        msg("text -->> " + title);

//        Stream.of(article.getCategory())
//                .map(new Function<ArtCategory, Object>() {
//                    @Override
//                    public Object apply(ArtCategory value) {
//                        return value.getCategoryName() + "\t";
//                    }
//                }).collect(Collectors.joining(","));
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));

//        title_tv.setText(article.getArtTitle());
    }

    public void showErrorView() {
//        error_view.setVisibility(View.VISIBLE);
    }

    public void hideErrorView() {
//        error_view.setVisibility(View.GONE);
    }

    public void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void hideProgress() {
//        progress.setVisibility(View.INVISIBLE);
    }

    private void showCategory(RealmList<ArtCategory> list) {
        for (int i = 0; i < list.size(); i++) {
            ArtCategory category = list.get(i);
            Tag t1 = new Tag(category.getCategoryName());

            t1.tagTextSize = 12f;
            t1.layoutColor =  Color.parseColor("#ffe100");
            t1.tagTextColor = Color.parseColor("#FFFFFF");
            t1.layoutBorderSize = 1f;
            t1.layoutBorderColor = Color.parseColor("#ffe100");

            tagView.addTag(t1);
        }
    }

    public void createUI(List<ParserModelArticle> list) {
        int height = dpToPix(150, metrics);
        LinearLayout linearLayout = ButterKnife.findById(this, R.id.content_container);
        for (int i = 0; i < list.size(); i++) {
            ParserModelArticle d = list.get(i);
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            if (Parser.checkImg(d.getContentObj())) {
                CardView cardView = new CardView(this);
                cardView.setLayoutParams(new LinearLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, height));
                cardView.setPadding(0, 8, 0, 8);

                if (isAndroid5Plus()) {
                    cardView.setElevation(2f);
                }

                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                cardView.addView(imageView);
                cardView.setOnClickListener(v -> ImageViewer.startActivity(DetailsActivity.this, d.getContentObj()));
                layout.addView(cardView);
                Glide.with(this).load(d.getContentObj()).centerCrop().placeholder(R.drawable.holder1).crossFade().into(imageView);
            } else {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextSize(16f);
                textView.setTextColor(Color.parseColor("#212121"));
                textView.setPadding(8, 0, 8, 0);
                textView.setText(d.getContentObj());
                layout.addView(textView);
            }

            linearLayout.addView(layout);
        }

//        hideProgress();

    }

    private void msg(String text) {
        LogUtil.logD(TAG, text);
    }
}
