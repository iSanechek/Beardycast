package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.ui.imageviewer.ImageViewer;

import org.jsoup.nodes.Element;

import java.util.List;

import static com.isanechek.beardycast.utils.LogUtil.logD;
import static com.isanechek.beardycast.utils.Util.dpToPix;
import static com.isanechek.beardycast.utils.Util.isAndroid5Plus;

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

    private ImageView imageView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private LinearLayout linearLayout;

    private DetailsPresenter presenter;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        logD(TAG, "HELLO FROM " + TAG);
        initView();

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

    private void initView() {
        imageView = (ImageView) findViewById(R.id.back_drop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.details_progress);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        linearLayout.removeViewAt(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        linearLayout.removeViewAt(0);
    }

    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void loadView(Article a) {
        Glide.with(DetailsActivity.this).load(a.getArtImgLink()).crossFade(1000).into(imageView);
        toolbar.setTitle(a.getArtTitle());
        fab.setImageResource(a.isPodcast() ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_forum_white_24dp);
    }

    public void createView(List<Element> list) {
        createUI(list, false);
//        Stream.of(list).map(Element::toString).forEach(value -> logD(TAG, value));

    }

    private void createUI(List<Element> list, boolean state) {
        int height = dpToPix(150, metrics);
        linearLayout = (LinearLayout) findViewById(R.id.content_container);
        for (int i = 0; i < list.size(); i++) {
            Element e = list.get(i);
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            if (Parser.checkImg(Parser.backString(e))) {
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
                cardView.setOnClickListener(v -> ImageViewer.startActivity(DetailsActivity.this, Parser.backString(e)));
                layout.addView(cardView);
                String u = Parser.tryUrl(Parser.backString(e));
                Glide.with(this).load(u).centerCrop().placeholder(R.drawable.holder1).crossFade().into(imageView);
            } else {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextSize(16f);
                textView.setTextColor(Color.parseColor("#212121"));
                textView.setPadding(8, 0, 8, 0);
                textView.setText(Html.fromHtml(e.toString()));
                layout.addView(textView);
            }

            linearLayout.addView(layout);
        }
    }

    public void showErrorView(Throwable throwable) {
        Log.e(TAG, "showErrorView: ", throwable);
    }

}
