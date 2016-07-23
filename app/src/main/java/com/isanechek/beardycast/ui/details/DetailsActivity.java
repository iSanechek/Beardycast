package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.pref.PreferencesActivity;
import com.isanechek.beardycast.ui.imageviewer.ImageViewer;
import com.isanechek.beardycast.ui.BaseActivity;
import com.isanechek.beardycast.ui.widget.BadgeDrawable;
import com.isanechek.beardycast.utils.Util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.isanechek.beardycast.utils.Util.dpToPix;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final int LAYOUT = R.layout.details_activity;
    private static final String TAG = "DetailsActivity";

    private static final String ARTICLE_ID = "article_id";
    private static final String IMG = "([^\\s]+(\\.(?i)(jpg|png|gif))$)";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private static final String TAG_P = "p";
    private static final String TAG_UL = "ul";
    private static final String TAG_OL = "ol";
    private static final String TAG_H1 = "h1";
    private static final String TAG_H2 = "h2";
    private static final String TAG_H3 = "h3";
    private static final String TAG_H4 = "h4";
    private static final String TAG_QUOTE = "blockquote";
    private static final String TAG_IFRAME = "iframe";
    private static final String TAG_FIGURE = "figure";

    private TextView mTitle;
    private String mTileText;
    private boolean mIsTheTitleVisible = false;
    private int coubt = 0;

    public static Intent startActivity(Context context, String link) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(ARTICLE_ID, link);
        context.startActivity(intent);
        return intent;
    }

    private ImageView imageView;
    private Toolbar toolbar;
    private MaterialProgressBar progressBar;
    private FloatingActionButton fab;
    private TextView articleTitle;
    private TextView articleDate;
    private HorizontalScrollView mTagsContainer;
    private HorizontalScrollView mCategoryContainer;
    private LinearLayout container;
    private LinearLayout mCategoryAndTagsLayout;

    private View mCoverView;
    private View mTitleView;
    private View mTimeView;
    private View mDurationView;
    private View mProgressView;
    private View mFabView;

    private DetailsPresenter presenter;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        String id = getIntent().getExtras().getString(ARTICLE_ID);
        if (id == null) {
            showErrorView(null);
            logD("id все плохо");
        } else {
            logD("id --> " + id);
            logD("Create Instance Presenter");
            presenter = new DetailsPresenter(this, Model.getInstance(), id);
        }
    }

    private void initView() {
        logD("Init View");
        imageView = (ImageView) findViewById(R.id.back_drop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            if (Util.isAndroid5Plus()) {
                finishAfterTransition();
            } else {
                finish();
            }
        });
        progressBar = (MaterialProgressBar) findViewById(R.id.details_progress);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        articleTitle = (TextView) findViewById(R.id.details_title);
        articleDate = (TextView) findViewById(R.id.details_date);
        mTagsContainer = (HorizontalScrollView) findViewById(R.id.list_tags_container_details);
        mCategoryContainer = (HorizontalScrollView) findViewById(R.id.list_category_container_details);
        container = (LinearLayout) findViewById(R.id.content_container);
        mTitle = (TextView) findViewById(R.id.main_textview_title);

    }

    @Override
    protected void onResume() {
        super.onResume();
        logD("onResume");
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logD("onPause");
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logD("onDestroy");
        presenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.list_setting:
                startSettingsActivity();
                break;
            case R.id.test:

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgress(boolean show) {
        logD(show ? "VISIBLE PROGRESS" : "INVISIBLE PROGRESS");
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void loadView(Article a) {
        logD("Load View");
        Glide.with(DetailsActivity.this).load(a.getArtImgLink()).crossFade(1000).into(imageView);
        toolbar.setTitle("");

        mTileText = a.getArtTitle();
        articleTitle.setText(mTileText);
        String postDate = Util.getDate(a.getArtDatePost());
        articleDate.setText(postDate);

        if (a.isPodcast()) {
            fab.setImageResource(a.isPodcast() ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_file_download_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_forum_white_24dp);
        }

        mCategoryAndTagsLayout = new LinearLayout(DetailsActivity.this);
        mCategoryAndTagsLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mCategoryAndTagsLayout.setOrientation(LinearLayout.HORIZONTAL);
        Stream.of(a.getCategory()).forEach(v -> {
            BadgeDrawable drawable = new BadgeDrawable.Builder()
                    .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                    .badgeColor(R.color.accent)
                    .text1(v.getCategoryName())
                    .build();

            SpannableString spannableString = new SpannableString(TextUtils.concat(drawable.toSpannable()));

            TextView tvt = new TextView(DetailsActivity.this);
            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvt.setText(spannableString);
            tvt.setTextSize(12f);
            mCategoryAndTagsLayout.addView(tvt);
        });
        mCategoryContainer.addView(mCategoryAndTagsLayout);

        Stream.of(a.getTags()).forEach(v -> {
            BadgeDrawable drawable = new BadgeDrawable.Builder()
                    .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                    .badgeColor(R.color.accent)
                    .text1(v.getTagName())
                    .build();

            SpannableString spannableString = new SpannableString(TextUtils.concat(drawable.toSpannable()));

            TextView tvt = new TextView(DetailsActivity.this);
            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvt.setText(spannableString);
            tvt.setTextSize(12f);
            mCategoryAndTagsLayout.addView(tvt);
        });
        mTagsContainer.addView(mCategoryAndTagsLayout);

    }

    public void createView(List<Element> list) {
        ViewGroup.LayoutParams layoutParamsM = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams layoutParamsW = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean blockquoteStatus = false;

        for (Element e : list) {
            String elementName = e.tagName();
            switch (elementName) {
                case TAG_P:
                    if (isCheck(Parser.backString(e), IMG)) {
                        int height = dpToPix(150, metrics);
                        ImageView iv = new ImageView(DetailsActivity.this);
                        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                        iv.setPadding(8,8,8,8);
                        String u = Parser.tryUrl(Parser.backString(e));
                        Glide.with(DetailsActivity.this)
                                .load(u)
                                .asBitmap()
                                .thumbnail(0.1f)
                                .placeholder(R.drawable.h1)
                                .error(R.drawable.holder1)
                                .centerCrop()
                                .into(iv);
                        container.addView(iv);
                    } else {
                        if (blockquoteStatus) {
                            LinearLayout blockquoteLL = new LinearLayout(DetailsActivity.this);
                            blockquoteLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            blockquoteLL.setOrientation(LinearLayout.HORIZONTAL);
                            ImageView ivi = new ImageView(DetailsActivity.this);
                            ivi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            ivi.setImageDrawable(getResources().getDrawable(R.drawable.ic_format_quote_white_24dp));
                            ivi.setBackground(null);
                            ivi.setPadding(8,8,8,8);
                            ivi.setBackgroundColor(Color.parseColor("#EEEEEE"));
                            blockquoteLL.addView(ivi);
                            TextView tvt = new TextView(DetailsActivity.this);
                            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvt.setPadding(8,8,8,8);
                            tvt.setTextSize(16f);
                            Spanned stvtText = getText(e);
                            tvt.setText(stvtText);
                            blockquoteLL.addView(tvt);
                            container.addView(blockquoteLL);
                            blockquoteStatus = false;
                        } else {
                            TextView tvt = new TextView(DetailsActivity.this);
                            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvt.setPadding(8,2,8,2);
                            tvt.setTextSize(16f);
                            Spanned tvtText = getText(e);
                            tvt.setText(tvtText);
                            container.addView(tvt);
                        }
                    }
                    break;
                case TAG_UL:
                    LinearLayout ll = new LinearLayout(DetailsActivity.this);
                    ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(8,0,8,0);
                    if (coubt == 0) {
                        coubt++;
                        Elements ee1 = e.children();
                        for (Element el2 : ee1) {
                            Elements e43 = el2.children();
                            for (Element d32 :e43) {
                                String tagName1 = d32.tagName();
                                if (tagName1.equals("a")) {
                                    TextView tv = new TextView(DetailsActivity.this);
                                    tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tv.setPadding(8,2,8,2);
                                    tv.setTextSize(16f);
                                    Spanned text = getText(d32);
                                    tv.setText(text);
                                    ll.addView(tv);
                                } else if (tagName1.equals("ul")) {
                                    for (Element f44 : d32.children()) {
                                        TextView tv1 = new TextView(DetailsActivity.this);
                                        tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tv1.setPadding(16,2,16,2);
                                        tv1.setTextSize(12f);
                                        Spanned text1 = getText(f44);
                                        tv1.setText(text1);
                                        ll.addView(tv1);
                                    }
                                }
                            }
                        }
                    } else {
                        TextView tv2 = new TextView(DetailsActivity.this);
                        tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv2.setPadding(8,2,8,2);
                        tv2.setTextSize(16f);
                        Spanned text2 = getText(e);
                        tv2.setText(text2);
                        ll.addView(tv2);
                    }
                    container.addView(ll);
                    break;
                case TAG_QUOTE:
                    blockquoteStatus = true;
                    break;
                case TAG_H1:
                    TextView textView2 = new TextView(DetailsActivity.this);
                    textView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView2.setPadding(8,8,8,8);
                    textView2.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView2.setText(getText(e));
                    container.addView(textView2);
                    break;
                case TAG_H2:
                    TextView textView3 = new TextView(DetailsActivity.this);
                    textView3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView3.setPadding(8,8,8,8);
                    textView3.setText(getText(e));
                    container.addView(textView3);
                    break;
                case TAG_H4:

                    break;
                case TAG_IFRAME:

                    break;
                default:
                    break;
            }
        }
    }

    private boolean isCheck(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(input);
        return matcher.matches();
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }
    private void startImageViewer(String imageUrl) {
        ImageViewer.startActivity(DetailsActivity.this, imageUrl);
    }
    private Spanned getText(Element element) {
        return Html.fromHtml(element.toString());
    }

    public void showErrorView(Throwable throwable) {
        Log.e(TAG, "showErrorView: ", throwable);
    }

    public void logD(String s) {
        Log.d(TAG, s);
    }

    public void logE(String s) {
        Log.e(TAG, s);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;


    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mTitle.setText(mTileText);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void helperTransition() {
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
    }

    private void onFabClick() {
        if (Util.isAndroid5Plus()) {
            onFabClickLLPlus();
        } else {
            onFabClickLLMinus();
        }
    }

    private void onFabClickLLPlus() {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                )
    }

    private void onFabClickLLMinus() {

    }
}
