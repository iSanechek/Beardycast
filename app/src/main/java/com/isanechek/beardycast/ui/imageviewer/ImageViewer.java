package com.isanechek.beardycast.ui.imageviewer;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.widget.PullBackLayout;
import com.isanechek.beardycast.ui.widget.ShadowLayout;
import com.isanechek.beardycast.utils.BitmapUtils;

/**
 * Created by isanechek on 26.05.16.
 */
public class ImageViewer extends AppCompatActivity implements PullBackLayout.Callback {
    private static final int LAYOUT = R.layout.image_viewer_layout;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String PACKAGE_NAME = "com.isanechek.beardycast.ui.imageviewer";
    private static final int ANIM_DURATION = 500;
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
    private BitmapDrawable mBitmapDrawable;
    private FrameLayout mTopLevelLayout;
    private ShadowLayout mShadowLayout;
    private int mOriginalOrientation;
    private ColorMatrix colorizerMatrix = new ColorMatrix();
    ColorDrawable mBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO");

        backLayout = (PullBackLayout) findViewById(R.id.pull_back);
        backLayout.setCallback(this);
        mTopLevelLayout = (FrameLayout) findViewById(R.id.topLevelLayout);
        mShadowLayout = (ShadowLayout) findViewById(R.id.shadowLayout);
        pic = (ImageView) findViewById(R.id.viewer_pic);

//
//        Bundle bundle = getIntent().getExtras();
//        Bitmap bitmap = BitmapUtils.getBitmap(getResources(),
//                bundle.getInt(PACKAGE_NAME + ".resourceId"));
//        String description = bundle.getString(PACKAGE_NAME + ".description");
//        final int thumbnailTop = bundle.getInt(PACKAGE_NAME + ".top");
//        final int thumbnailLeft = bundle.getInt(PACKAGE_NAME + ".left");
//        final int thumbnailWidth = bundle.getInt(PACKAGE_NAME + ".width");
//        final int thumbnailHeight = bundle.getInt(PACKAGE_NAME + ".height");
//        mOriginalOrientation = bundle.getInt(PACKAGE_NAME + ".orientation");



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
