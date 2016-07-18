package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.pref.PreferencesActivity;
import com.isanechek.beardycast.ui.imageviewer.ImageViewer;
import com.isanechek.beardycast.utils.UrlUtil;
import com.isanechek.beardycast.utils.Util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.kaede.tagview.TagView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.isanechek.beardycast.utils.Util.dpToPix;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.details_t_activity;
    private static final String TAG = "DetailsActivity";

    private static final String ARTICLE_ID = "article_id";
    private static final String IMG = "([^\\s]+(\\.(?i)(jpg|png|bmp|gif))$)";

    private static final String TAG_P = "p";
    private static final String TAG_UL = "ul";
    private static final String TAG_H1 = "h1";
    private static final String TAG_H2 = "h2";
    private static final String TAG_QUOTE = "blockquote";

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
    private TagView tagView;
    private LinearLayout container;

    private int indexCreate = 0;
    private ArrayList<View> views = new ArrayList<>();

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
            presenter = new DetailsPresenter(this, ModelT.getInstance(), id);
        }
    }

    private void initView() {
        logD("Init View");
        imageView = (ImageView) findViewById(R.id.back_drop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (MaterialProgressBar) findViewById(R.id.details_progress);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        articleTitle = (TextView) findViewById(R.id.details_title);
        articleDate = (TextView) findViewById(R.id.details_date);
        tagView = (TagView) findViewById(R.id.details_tags_container);
        container = (LinearLayout) findViewById(R.id.content_container);

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
                startsettingsActivity();
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

        articleTitle.setText(a.getArtTitle());
        String postDate = Util.getDate(a.getArtDatePost());
        articleDate.setText(postDate);

        if (a.isPodcast()) {
            fab.setImageResource(a.isPodcast() ? R.drawable.ic_play_arrow_black_24dp : R.drawable.ic_file_download_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_forum_white_24dp);
        }

    }

    public void createView(List<Element> list) {
        boolean blockquoteStatus = false;
        for (Element e : list) {
            String elementName = e.tagName();
            switch (elementName) {
                case TAG_P:
                    String imageUrl = UrlUtil.getImageUrlFromElement(e);
                    if (isCheck(imageUrl, IMG)) {
                        View v = createImage(imageUrl);
                        buildUiHelper(v, true);
                    } else {
                        if (blockquoteStatus) {
                            View view = createTextView(e, true);
                            buildUiHelper(view, true);
                            blockquoteStatus = false;
                        } else {
                            View view = createTextView(e, false);
                            buildUiHelper(view, true);
                        }
                    }
                    break;
                case TAG_UL:
                    Elements ee = e.children();
                    LinearLayout linearLayout = new LinearLayout(DetailsActivity.this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    for (Element element : ee) {
                        Elements elements = element.select("a, li");
                        for (Element element1 : elements) {
                            String element1Name = element1.tagName();
                            if (element1Name.equals("a")) {
                                TextView textView = new TextView(DetailsActivity.this);
                                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                textView.setPadding(8,4,8,4);
                                textView.setText(getText(element1));
                                linearLayout.addView(textView);
                            }
                        }
                    }
                    buildUiHelper(linearLayout, false);
                    break;
                case TAG_QUOTE:
                    blockquoteStatus = true;
                    break;
                case TAG_H1:
                    View mH1View = createTextView(e);
                    buildUiHelper(mH1View, false);
                    break;
                case TAG_H2:
                    View mH2View = createTextView(e);
                    buildUiHelper(mH2View, false);
                    break;
                default:
                    break;
            }
        }
    }

    private void buildUiHelper(View view, boolean card) {
        if (!card) {
            if (views.size() != 0) {
                buildUi(views);
                views.clear();
            }
            container.addView(view);
        } else {
            views.add(view);
        }
    }
    private void buildUi(ArrayList<View> views) {
        CardView cardView = new CardView(DetailsActivity.this);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        cardView.setPadding(16,0,16,0);
        LinearLayout layout = new LinearLayout(DetailsActivity.this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#FAFAFA"));
        Stream.of(views).forEach(layout::addView);
        cardView.addView(layout);
        container.addView(cardView);
    }


    private View createImage(@NonNull String imageUrl) {
        int height = dpToPix(150, metrics);
        ImageView imageView = new ImageView(DetailsActivity.this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        imageView.setOnClickListener(view -> startImageViewer(imageUrl));
        Glide.with(DetailsActivity.this).load(imageUrl).centerCrop().placeholder(R.drawable.holder1).into(imageView);
        return imageView;
    }

    private View createTextView(@NonNull Element element) {
        TextView textView = new TextView(DetailsActivity.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(16f);
        textView.setTextColor(Color.parseColor("#212121"));
        Spanned text = getText(element);
        textView.setText(text);
        return textView;
    }

    private View createTextView(@NonNull Element element, boolean b) {
        TextView textView = new TextView(DetailsActivity.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(16f);
        textView.setPadding(8,8,8,8);
        textView.setTextColor(Color.parseColor("#212121"));
        textView.setText(Html.fromHtml(element.toString()));
        return textView;
    }

    private boolean isCheck(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(input);
        return matcher.matches();
    }

    private void startsettingsActivity() {
        startActivity(new Intent(DetailsActivity.this, PreferencesActivity.class));
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

}
