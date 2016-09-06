package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.details.widgets.*;
import com.isanechek.beardycast.ui.mvp.MvpActivity;
import com.isanechek.beardycast.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.isanechek.beardycast.utils.LogUtil.logD;
import static com.isanechek.beardycast.utils.LogUtil.logE;

/**
 * Created by isanechek on 10.08.16.
 */

public class DetailsArticleActivity extends MvpActivity<DetailsArticlePresenter>
        implements DetailsArticleView, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = DetailsArticleActivity.class.getSimpleName();
    private static final int LAYOUT = R.layout.details_activity;
    private static final String ARTICLE_ID = "article.id";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    public static Intent startActivity(Context context, @NonNull String id) {
        Intent intent = new Intent(context, DetailsArticleActivity.class);
        intent.putExtra(ARTICLE_ID, id);
        context.startActivity(intent);
        return intent;
    }

    private int iViews = 0;
    private int iTextViews = 0;
    int iterations = 0;

    private Toolbar toolbar;
    private View toolbar_progress;
    private TextView toolbar_title;
    private ImageView imageView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private LinearLayout titleContainer;
    private TextView articleTitle;
    private TextView articleDate;
    private LinearLayout container;
    private HorizontalScrollView mCategoryContainer;
    private HorizontalScrollView mTagsContainer;
    private LinearLayout mCategoryAndTagsLayout;
    private String title;


    private DisplayMetrics metrics;
    private String id;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        logD(TAG, "onCreate");

        initWidgets();

        appBarLayout.addOnOffsetChangedListener(this);

        String id = getIntent().getStringExtra(ARTICLE_ID);
        if (!TextUtils.isEmpty(id) || id != null) {
            presenter.loadData(id);
        } else {
            showError(3, "Article id null");
        }
    }

    private void initWidgets() {
        logD(TAG, "initWidgets");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(view -> {
            if (Util.isAndroid5Plus()) {
                finishAfterTransition();
            } else {
                finish();
            }
        });

        toolbar_progress = (View) findViewById(R.id.toolbar_progress);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        imageView = (ImageView) findViewById(R.id.back_drop);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        titleContainer = (LinearLayout) findViewById(R.id.title_container);
        articleTitle = (TextView) findViewById(R.id.details_title);
        articleDate = (TextView) findViewById(R.id.details_date);
        container = (LinearLayout) findViewById(R.id.content_container);
        mCategoryContainer = (HorizontalScrollView) findViewById(R.id.list_category_container_details);
        mTagsContainer = (HorizontalScrollView) findViewById(R.id.list_tags_container_details);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logD(TAG, "onDestroy");
    }

    @NonNull
    @Override
    protected DetailsArticlePresenter getPresenter() {
        return new DetailsArticlePresenter();
    }

    @Override
    public void bindView(Article article) {
        logD(TAG, "bindView");
        logD(TAG, "Article: " + article.getArtTitle());

        Glide.with(this)
                .load(article.getArtImgLink())
                .asBitmap().into(imageView);

        title = article.getArtTitle();
        toolbar_title.setText(title);
        articleTitle.setText(title);
        articleDate.setText(Util.getDate(article.getArtDatePost()));



        if (article.isPodcast()) {
            fab.setImageResource(article.isPodcast() ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_file_download_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_forum_white_24dp);
        }
    }

    @Override
    public void bindContentView(String body) {
        logD(TAG, "bindContentView");

        parse(body);
    }

    @Override
    public void showError(int errorCode, @NonNull String errorMessage) {
        switch (errorCode) {
            case 0:
                logD(TAG, "showError: " + errorMessage);
                break;
            case 1:
                logD(TAG, "showContentError: " + errorMessage);
                break;
            case 3:
                logD(TAG, errorMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void showProgress(boolean visible) {
        logD(TAG, visible ? "showProgress" : "hideProgress");
        toolbar_progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if(!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                toolbar_title.setText(title);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private Pattern pattern = Pattern.compile("(<div class=\"article-entry\"[^>]*?>[\\s\\S]*?</div>)[^<]*?<footer");
    float coef = 1;

    public void parse(String html) {
        runOnUiThread(() -> getSupportActionBar().setTitle("loaded"));

        final Matcher matcher = pattern.matcher(html);
        Log.d("kek", "check 3");
        if (matcher.find()) {
            final String finalHtml = matcher.group(1);

            runOnUiThread(() -> {
                final long time = System.currentTimeMillis();
                Document document = Document.parse(finalHtml);
                container.addView(recurseUi(document.getRoot()));
                getSupportActionBar().setTitle("ui " + Math.floor((System.currentTimeMillis() - time) * coef));
            });

        }
    }



    private final static Pattern p2 = Pattern.compile("^(b|i|u|del|sub|sup|span|a|br)$");
    private Matcher matcher;

    private BaseTag recurseUi(final Element element) {
        BaseTag thisView = getViewByTag(element.tagName());
        if (element.tagName().equals("img")) {
            String imageDescription = null;
            if (element.attr("alt") != null) {
                imageDescription = element.attr("alt");
            }

            thisView.setImage("http://beardycast.com/".concat(element.attr("src")), imageDescription);
            thisView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailsArticleActivity.this, element.attr("src"), Toast.LENGTH_SHORT).show();
                }
            });

            return thisView;
        }

        String html = element.getText();

        boolean text = true;

        for (int i = 0; i < element.getElements().size(); i++) {
            Element child = element.get(i);
            BaseTag newView = null;
            if (!text && child.tagName().equals("br"))
                continue;
            matcher = p2.matcher(child.tagName());
            boolean res1 = true, res2 = true;
            for (Element temp : child.getElements()) {
                if (temp.tagName().equals("a")) {
                    for (Element imgs : temp.getElements()) {
                        if (imgs.tagName().equals("img")) {
                            res1 = false;
                        }
                    }
                } else if (!temp.tagName().equals("br")) {
                    res2 = false;
                }
            }
            if (/*res1 && res2 && */matcher.matches()) {
                html = html.concat(child.tagName().equals("p") ? child.htmlNoParent() : child.html());
                text = true;
                continue;

            } else {
                newView = recurseUi(child);
                if (text) {
                    html = html.trim();
                    if (!html.isEmpty()) {
                        thisView.setHtmlText(html);
                        iTextViews++;
                    }
                }

                html = "";
                text = false;
            }
            if (newView != null)
                thisView.addView(newView);

            iterations++;
        }
        html = html.trim();
        if (!html.isEmpty()) {
            html = html.concat(element.getAfterText());
            thisView.setHtmlText(html);
            iTextViews++;
        }
        return thisView;
    }

    private BaseTag getViewByTag(String tag) {
        switch (tag) {
            case "h1":
                return new H1Tag(this);
            case "h2":
                return new H2Tag(this);
            case "ul":
                return new UlTag(this);
            case "li":
                return new LiTag(this);
            default:
                return new BaseTag(this);
        }
    }

}