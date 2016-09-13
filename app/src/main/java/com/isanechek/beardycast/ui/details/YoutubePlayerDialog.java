package com.isanechek.beardycast.ui.details;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.isanechek.beardycast.R;
import com.pierfrancescosoffritti.youtubeplayer.AbstractYouTubeListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;
import timber.log.Timber;

/**
 * Created by isanechek on 12.09.16.
 */
public class YoutubePlayerDialog extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private String url;
    private YouTubePlayerView youTubePlayerView;
    private FullScreenManager fullScreenManager;

    public static YoutubePlayerDialog getInstance(String url) {
        YoutubePlayerDialog playerDialog = new YoutubePlayerDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        playerDialog.setArguments(bundle);
        return playerDialog;
    }

    public YoutubePlayerDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.tag("Youtube Player Dialog");
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.youtube_player, null);
        url = getArguments().getString("url");
        if (url != null) {
            fullScreenManager = new FullScreenManager(getActivity());
            youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtube_player_view);
            youTubePlayerView.initialize(new AbstractYouTubeListener() {

                @Override
                public void onReady() {
                    youTubePlayerView.loadVideo(url, 0);
                }

            }, true);
            youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                @Override
                public void onYouTubePlayerEnterFullScreen() {
//                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenManager.enterFullScreen();
                    youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_36dp), view1 -> youTubePlayerView.pauseVideo());
                }

                @Override
                public void onYouTubePlayerExitFullScreen() {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenManager.exitFullScreen();
                    youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_36dp), null);
                }
            });

            Button close_btn = (Button) view.findViewById(R.id.player_btn_close);
            close_btn.setOnClickListener(v -> mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
            Button copy_btn = (Button) view.findViewById(R.id.player_btn_copy);
            copy_btn.setOnClickListener(v -> {
                Toast.makeText(getActivity(), R.string.copy_past_toast, Toast.LENGTH_SHORT).show();
                copyPastYoutubeUrl("youtube.com/" + url);
            });
        } else {
            Timber.e("URL NULL");
        }

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayerView.release();
    }

    private void copyPastYoutubeUrl(String url) {

    }
}
