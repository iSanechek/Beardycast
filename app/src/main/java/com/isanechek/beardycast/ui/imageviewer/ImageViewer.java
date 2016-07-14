package com.isanechek.beardycast.ui.imageviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.widget.PullBackLayout;

/**
 * Created by isanechek on 26.05.16.
 */
public class ImageViewer extends AppCompatActivity implements PullBackLayout.Callback {
    private static final int LAYOUT = R.layout.image_viewer_layout;

    private static final String TAG = "ImageViewer";

    public static Intent startActivity(Context context, String url) {
        Intent intent = new Intent(context, ImageViewer.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
        return intent;
    }

    private String url;

    private PullBackLayout backLayout;
    private ImageView pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");

        backLayout = (PullBackLayout) findViewById(R.id.pull_back);
        backLayout.setCallback(this);
        pic = (ImageView) findViewById(R.id.viewer_pic);

        url = getIntent().getExtras().getString("url");
        if (url == null) {
            // show err
            msg("URL -->> NULL");
        } else {
            msg("URL -->> " + url);
            initUI(url);
        }

    }

    private void initUI(String url) {
        Glide.with(ImageViewer.this).load(url).asBitmap().into(pic);
    }

    private void msg(String text) {
        Log.d(TAG, text);
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
}
