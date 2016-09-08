package com.isanechek.beardycast.ui.details.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.creativityapps.badgedimageviews.BadgedFourThreeImageView;
import com.creativityapps.badgedimageviews.BadgedImageView;
import com.isanechek.beardycast.App;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.utils.ResUtils;

/**
 * Created by radiationx on 03.09.16.
 */

public class BaseTag extends LinearLayout {

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    protected int px1 = dpToPx(4), px2 = dpToPx(8), px3 = dpToPx(16), px4 = dpToPx(32), px5 = dpToPx(48), px6 = dpToPx(150);

    public TextView textView;
    private LayoutInflater inflater;

    protected float size() {
        return 16;
    }

    public BaseTag(Context context) {
        super(context);
        setOrientation(VERTICAL);
        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TextView setHtmlText(String text) {
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(px2, 0, px2, 0);
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(params);
        new FormatTextTask(textView, text).execute();
        textView.setTextSize(size());
        textView.setTextIsSelectable(true);
        textView.setTextColor(ResUtils.getColor(getContext(), R.color.primary_text));
        addView(textView);
        return textView;
    }

    private class FormatTextTask extends AsyncTask<Void, Void, Void> {
        private TextView textView;
        private String text;
        private Spanned spanned;

        FormatTextTask(TextView textView, String text) {
            this.textView = textView;
            this.text = text;
        }

        protected Void doInBackground(Void... urls) {
            spanned = Html.fromHtml(text);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (textView != null){
                textView.setText(spanned);
                if(spanned.toString().isEmpty()){
                    textView.setVisibility(GONE);
                }
            }
        }
    }

    public void setImage(String url, String description, String tag) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px2, 0, px2);
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(params);
        cardView.setElevation(6f);
        cardView.setRadius(6f);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llParams.setMargins(px1, px1, 0, px1);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(llParams);
        ll.setOrientation(VERTICAL);
        BadgedFourThreeImageView imageView = (BadgedFourThreeImageView) inflater.inflate(R.layout.details_badged_image, null);
        imageView.setAdjustViewBounds(true);

        if (tag != null) {
            switch (tag) {
                case "youtube":
                    imageView.showBadge(true);
                    imageView.setBadgeText("YouTube");
                    imageView.setBadgeColor(Color.RED);
                    break;
                case "gif":
                    imageView.showBadge(true);
                    imageView.setBadgeText("Gif");
                    imageView.setBadgeColor(ContextCompat.getColor(getContext(), R.color.accent));
                    break;
                default:
                    imageView.showBadge(false);
                    break;
            }
        }


        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.holder1))
                .error(ContextCompat.getDrawable(getContext(), R.drawable.ic_error_loading_image_24dp))
                .into(imageView);
        ll.addView(imageView);
        if (description != null) {
            LinearLayout.LayoutParams tvParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(tvParams);
            tv.setPadding(8, 4, 8, 4);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
            tv.setTextSize(12f);
            new FormatTextTask(tv, description).execute();
            ll.addView(tv);
        }

        cardView.addView(ll);
        addView(cardView);
    }

    public TextView getTextView() {
        return textView;
    }

}
