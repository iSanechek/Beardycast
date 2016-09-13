package com.isanechek.beardycast.ui.imageviewer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.parser.articles.Parser;
import com.isanechek.beardycast.ui.widget.PullBackLayout;
import timber.log.Timber;

/**
 * Created by isanechek on 26.05.16.
 */
public class ImageViewerActivity extends AppCompatActivity implements PullBackLayout.Callback {
    private static final int LAYOUT = R.layout.image_viewer_layout;
    private static final String TAG = "ImageViewerActivity";

    public static Intent startActivity(Context context, String url, String description, String tagImg) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("description", description);
        intent.putExtra("tagImg", tagImg);
        context.startActivity(intent);
        return intent;
    }

    private String url;
    private String description;
    private String tagImg;

    private PullBackLayout backLayout;
    private ImageView pic;
    private TextView descTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        Timber.tag("Image Viewer");

        backLayout = (PullBackLayout) findViewById(R.id.pull_back);
        backLayout.setCallback(this);
        pic = (ImageView) findViewById(R.id.viewer_pic);
        descTextView = (TextView) findViewById(R.id.description);

        url = Parser.tryUrl(getIntent().getExtras().getString("url"));
        if (url != null) {
            initUI(url);
        } else {
            Timber.e("Url Null");
        }
        description = getIntent().getExtras().getString("description");
        if (description != null) {
            new FormatTextTask().execute();
        }
        tagImg = getIntent().getExtras().getString("tagImg");

    }

    private void initUI(String link) {
//        AndroidNetworking.get(url)
//                .setTag("imageviewer")
//                .

        Glide.with(ImageViewerActivity.this)
                .load(link)
                .placeholder(R.drawable.holder1)
                .into(pic);
    }

    @Override
    public void onPullStart() {

    }

    @Override
    public void onPull(@PullBackLayout.Direction int direction, float progress) {

    }

    @Override
    public void onPullCancel(@PullBackLayout.Direction int direction) {

    }

    @Override
    public void onPullComplete(@PullBackLayout.Direction int direction) {
        finish();
    }


    private class FormatTextTask extends AsyncTask<Void, Void, Void> {
        private Spanned spanned;

        FormatTextTask() {
        }

        protected Void doInBackground(Void... urls) {
            spanned = Html.fromHtml(description);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (descTextView != null){
                descTextView.setText(spanned);
                if(spanned.toString().isEmpty()){
                    descTextView.setVisibility(View.GONE);
                }
            }
        }
    }
}
