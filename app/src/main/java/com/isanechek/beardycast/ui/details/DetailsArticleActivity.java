package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.details.widgets.*;
import com.isanechek.beardycast.ui.imageviewer.ImageViewerActivity;
import com.isanechek.beardycast.ui.mvp.MvpActivity;
import com.isanechek.beardycast.ui.podcast.PodcastActivity;
import com.isanechek.beardycast.utils.Utils;
import timber.log.Timber;

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
    private TextView toolbar_date;
    private ImageView toolbar_img;
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
    private FrameLayout toolbarM;

    private DisplayMetrics metrics;
    private String id;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        Timber.tag(TAG);
        initWidgets();
        appBarLayout.addOnOffsetChangedListener(this);

        id = getIntent().getStringExtra(ARTICLE_ID);
        if (!TextUtils.isEmpty(id) || id != null) {
            presenter.loadData(id);
        } else {
            showError(3, "Article id null");
        }
    }

    private void initWidgets() {
        logD(TAG, "initWidgets");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_details_24dp);
//        toolbar.setNavigationOnClickListener(view -> {
//            if (Utils.isAndroid5Plus()) {
//                finishAfterTransition();
//            } else {
//                finish();
//            }
//        });

//        toolbar_progress = findViewById(R.id.toolbar_progress);

        toolbarM = (FrameLayout) findViewById(R.id.framelayout_title);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_date = (TextView) findViewById(R.id.toolbar_date);
//        toolbar_img = (ImageView) findViewById(R.id.toolbar_image);
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
                .load(article.getArtImgLink()).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);

        title = article.getArtTitle();
        toolbar_title.setText(title);
        articleTitle.setText(title);
        articleDate.setText(Utils.getDate(article.getArtDatePost()));


        if (article.isPodcast()) {
            presenter.getPodcastInfo(id);
            fab.setOnClickListener(v -> PodcastActivity.startPodcastActivity(DetailsArticleActivity.this, id));
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
//        toolbar_progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
                logE(TAG, "VISIBLE");
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_title, ALPHA_ANIMATIONS_DURATION, View.GONE);
                mIsTheTitleVisible = false;
                logE(TAG, "GONE");
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = false;
                logE(TAG, "VISIBLE1");
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION, View.GONE);
                mIsTheTitleContainerVisible = true;
                logE(TAG, "GONE1");
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

    public void parse(String html) {
        final Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            final String finalHtml = matcher.group(1);
            runOnUiThread(() -> {
                Document document = Document.parse(finalHtml);
                container.addView(recurseUi(document.getRoot()));
            });

        }
    }



    private final static Pattern p2 = Pattern.compile("^(b|i|u|del|s|strike|sub|sup|span|a|br)$");
//    private final static Pattern p2 = Pattern.compile("^(b|i|u|del|sub|sup|span|a|br)$");
    private final static Pattern youtubeId = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE);

    private BaseTag recurseUi(final Element element) {
//        BaseTag thisView = getViewByTag(element.tagName());

        BaseTag thisView = null;
        String elClassios = element.attr("class");
        if (elClassios != null && elClassios.contains("post-block")) {
            if (elClassios.contains("quote")) {
                thisView = new QuotePostBlock(this);
            } else if (elClassios.contains("code")) {
                thisView = new CodePostBlock(this);
                Element.fixSpace(element.getLast());
            } else if (elClassios.contains("spoil")) {
                thisView = new SpoilerPostBlock(this);
            } else {
                thisView = new PostBlock(this);
            }
            if (!element.get(0).htmlNoParent().trim().equals(""))
                ((PostBlock) thisView).setTitle(Html.fromHtml(element.get(0).htmlNoParent().trim()));
            else
                ((PostBlock) thisView).hideTitle();
            ((PostBlock) thisView).addBody(recurseUi(element.getLast()));
            return thisView;
        } else {
            thisView = getViewByTag(element.tagName());
        }

        if (element.tagName().equals("img")) {
            String imageDescription = null;
            if (element.attr("alt") != null) {
                imageDescription = element.attr("alt");
            }

            String tagImg = null;
            if (element.tagName().equals("img")) {
               String url = "http://beardycast.com/".concat(element.attr("src"));
                if (url.contains(".gif")) {
                    tagImg = "gif";
                }
                thisView.setImage(url, imageDescription, tagImg);
                String finalImageDescription = imageDescription;
                String finalTagImg = tagImg;
                thisView.setOnClickListener(v -> ImageViewerActivity.startActivity(DetailsArticleActivity.this, element.attr("src"), finalImageDescription, finalTagImg));
            }

            return thisView;
        }

        if (element.tagName().equals("iframe")) {
            if (element.attr("src").contains("www.youtube.com")) {
                String link = element.attr("src");
                Matcher matcher = youtubeId.matcher(link);
                if (matcher.matches()) {
                    String id = matcher.group(1);
                    thisView.setImage("http://img.youtube.com/vi/"+id+"/maxresdefault.jpg", null, "youtube");
                    thisView.setOnClickListener(v -> {
                        Timber.e("Click", link);
                        YoutubePlayerDialog dialog = YoutubePlayerDialog.getInstance(id);
                        dialog.show(getSupportFragmentManager(), "playerDialog");
                    });
                }
                return thisView;
            } else if (element.attr("src").contains("libsyn.com")) {
                presenter.getPodcastUrl(id, "http:" + element.attr("src"));
                logE(TAG, "podcast link: " + "http:" + element.attr("src"));
                return null;
            }
        }

        if (element.tagName().equals("h4")) {
//            presenter.updatePodcastUrl(id, element.getElements().get(1).attr("href"));
            return null;
        }

        if (element.tagName().equals("blockquote")) {
            thisView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        }

        if (element.tagName().equals("figure")) {
//            String text = element.getElements();
//            if (text != null) {
//                logE(TAG, "text: " + text);
//            } else {
//                logE(TAG, "text null");
//            }

            logE(TAG, "hz: " + element.toString());

        }

        String html = element.getText();

        boolean text = true;

        for (int i = 0; i < element.getElements().size(); i++) {
            Element child = element.get(i);
            BaseTag newView = null;
            if (p2.matcher(child.tagName()).matches()) {
                html = html.concat(child.html());
                text = true;
                continue;
            } else {
                newView = recurseUi(child);
                if (text) {
                    html = startBreakTag.matcher(html).replaceFirst("");
                    html = endBreakTag.matcher(html).replaceFirst("");
                    html = html.trim();
                    if (!html.isEmpty()) {
                        thisView.setHtmlText(html);
                        iTextViews++;
                        html = "";
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
            html = startBreakTag.matcher(html).replaceFirst("");
            html = endBreakTag.matcher(html).replaceFirst("");
            html = html.trim();
            html = html.concat(element.getAfterText());
            thisView.setHtmlText(html);
            iTextViews++;
            html = "";
        }
        return thisView;
    }

    private final static Pattern startBreakTag = Pattern.compile("^([ ]*|)<br>");
    private final static Pattern endBreakTag = Pattern.compile("<br>([ ]*|)$");
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
            case "p":
                return new PTag(this);
            default:
                return new BaseTag(this);
        }
    }

}
