package com.isanechek.beardycast.ui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.realm.model.ArtCategory;
import com.isanechek.beardycast.realm.model.ArtTag;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.utils.Util;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

import static com.isanechek.beardycast.utils.LogUtil.logD;
import static com.isanechek.beardycast.utils.Util.dpToPix;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.test_details_activity;
    private static final String TAG = "DetailsActivity";

    private static final String ARTICLE_ID = "article_id";
    public static Intent startActivity(Context context, Article article) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(ARTICLE_ID, article.getArtLink());
        context.startActivity(intent);
        return intent;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.back_drop)
    ImageView imageView;
    @BindView(R.id.details_container)
    NestedScrollView container;

    @BindColor(R.color.colorPrimary)
    int layoutColor;
    @BindColor(R.color.tagTextColor)
    int tagTextColor;
    @BindColor(R.color.secondary_text)
    int secondaryTextColor;
    @BindColor(R.color.divider)
    int dividerColor;

    private LinearLayout root;
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
            // Значит все плохо
            logD(TAG, "id все плохо");
        } else {
            logD(TAG, "id --> " + id);
            presenter = new DetailsPresenter(this, Model.getInstance(), id);
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

    public void testCreate(Article article) {
        loadView(article.getArtTitle(), article.getArtImgLink());
        createView(article);
    }
    public void loadView(String title, String imgUrl) {
        Glide.with(DetailsActivity.this).load(imgUrl).crossFade(1000).into(imageView);
        toolbar.setTitle(title);
    }

    public void createView(Article article) {
        /*Панель с тэгами и датой*/

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        root = new LinearLayout(this);
        root.setLayoutParams(layoutParams);
        root.setOrientation(LinearLayout.VERTICAL);

        CardView cardView = new CardView(this);
        cardView.setLayoutParams(layoutParams);
        root.addView(cardView);

        LinearLayout l1 = new LinearLayout(this);
        l1.setLayoutParams(layoutParams);
        l1.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(l1);

        int minHeight = dpToPix(100, metrics);
        LinearLayout dateAndCategoryContainer = new LinearLayout(this);
        dateAndCategoryContainer.setLayoutParams(layoutParams);
        dateAndCategoryContainer.setMinimumHeight(minHeight);
        dateAndCategoryContainer.setOrientation(LinearLayout.HORIZONTAL);
        l1.addView(dateAndCategoryContainer);

        LinearLayout categoryContainer = new LinearLayout(this);
        categoryContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1));
        categoryContainer.setOrientation(LinearLayout.VERTICAL);
        dateAndCategoryContainer.addView(categoryContainer);

        int p1 = dpToPix(8, metrics);
        int p2 = dpToPix(3, metrics);
        TagView tagView = new TagView(this);
        tagView.setLayoutParams(layoutParams);
        tagView.setLineMargin(p1);
        tagView.setTagMargin(p2);
        tagView.setTextPaddingLeft(p1);
        tagView.setTextPaddingTop(p1);
        tagView.setTextPaddingRight(p2);
        tagView.setTexPaddingBottom(p2);
        categoryContainer.addView(tagView);

        for (ArtCategory category : article.getCategory()) {
            Tag tag = new Tag(category.getCategoryName());
            tag.tagTextSize = 12f;
            tag.layoutColor = layoutColor;
            tag.tagTextColor = tagTextColor;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = tagTextColor;
            tagView.addTag(tag);
        }

        /*Date*/
        FrameLayout dateContainer = new FrameLayout(this);
        dateContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dateAndCategoryContainer.addView(dateContainer);

        String date = Util.getDate(article.getArtDatePost());
        TextView dateTv = new TextView(this);
        dateTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        dateTv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        dateTv.setPadding(8, 8, 8, 8);
        dateTv.setTextSize(12f);
        dateTv.setTextColor(secondaryTextColor);
        dateTv.setText(date);
        dateContainer.addView(dateTv);
        container.addView(root);

        int h = dpToPix(1, metrics);
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h));
        space.setBackgroundColor(dividerColor);
        root.addView(space);

    }
}
