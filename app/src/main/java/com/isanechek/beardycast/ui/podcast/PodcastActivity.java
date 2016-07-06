package com.isanechek.beardycast.ui.podcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.isanechek.beardycast.App;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.podcast.fragments.PodcastFragment;
import com.isanechek.beardycast.ui.podcast.fragments.PodcastInfoFragment;

import butterknife.ButterKnife;

/**
 * Created by isanechek on 11.05.16.
 */
public class PodcastActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.podcast_activity;
    private static final String TAG = "PodcastActivity";

    // Seekbar ratio reference
    public static int SEEKBAR_RATIO = 1000;

    public static Intent startPodcastActivity(Context context, Article article) {
        Intent intent = new Intent(context, PodcastActivity.class);
        intent.putExtra("podcast_id", article.getArtLink());
        context.startActivity(intent);
        return intent;
    }

    private Toolbar toolbar;
    int startW;
    int startH;
    ViewGroup rootScene;

    private PodcastPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        msg("HELLO FROM PODCAST ACTIVITY");
        App.getRefWAtcher(this);


        showInfoFragment(savedInstanceState);

//        testmethod();

//        String id = getIntent().getExtras().getString(Constants.MODEL_ID);
//        if (id == null) {
//            Toast.makeText(this, "Oopppsss", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            presenter = new PodcastPresenter(id, Model.getInstance(), this);
//            presenter.onCreate();
//            String title = getIntent().getExtras().getString(Constants.MODEL_TITLE);
//            assert title != null;
//            initView(title);
//            String img = getIntent().getExtras().getString(Constants.MODEL_IMG);
//            ImageView imageView = ButterKnife.findById(this, R.id.pic);
//            Glide.with(this).load(img).asBitmap().into(imageView);
//        }
    }

    private void testmethod() {
//        rootScene = (ViewGroup) findViewById(R.id.img_container);
//        View img = rootScene.findViewById(R.id.pic);
//
//        ViewTreeObserver observer = rootScene.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                helpTestMet();
//                rootScene.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
//
//
//        int backParams = getResources().getDimensionPixelOffset(R.dimen.item_width);
//        Button start = (Button) findViewById(R.id.start_tranz);
//        Button back = (Button) findViewById(R.id.back_tranz);
//
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TransitionManager.beginDelayedTransition(rootScene);
//                ViewGroup.LayoutParams params = img.getLayoutParams();
//                params.width = startW;
//                params.height = startH;
//                img.setLayoutParams(params);
//            }
//        });
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TransitionManager.beginDelayedTransition(rootScene);
//                ViewGroup.LayoutParams params = img.getLayoutParams();
//                params.width = backParams;
//                params.height = backParams;
//                img.setLayoutParams(params);
//            }
//        });
    }

    private void helpTestMet() {
        startW = rootScene.getWidth();
        startH = rootScene.getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_podcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.podcast_settings:
                // implements action
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(@NonNull String title) {
        // toolbar
        toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
    }

    private void showInfoFragment(Bundle bundle) {
        if (bundle == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PodcastInfoFragment())
                    .commit();
        }
    }

    public void showControlFragment() {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PodcastFragment()).commit();
    }


    /*Helper*/
    private void msg(String text) {
        Log.d(TAG, text);
    }
}
