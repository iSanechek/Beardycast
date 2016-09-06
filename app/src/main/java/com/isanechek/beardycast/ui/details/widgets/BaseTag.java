package com.isanechek.beardycast.ui.details.widgets;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

    protected int px1 = dpToPx(4), px2 = dpToPx(8), px3 = dpToPx(16), px4 = dpToPx(32), px5 = dpToPx(48);

    public TextView textView;

    protected float size() {
        return 16;
    }

    public BaseTag(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public TextView setHtmlText(String text) {
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(px2, 0, px2, 0);
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(params);
        new FormatTextTask(textView, text).execute();
        textView.setTextSize(size());
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

    public void setImage(String url, String description) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px2, 0, px2);
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(params);
        cardView.setElevation(2f);
        cardView.setCardBackgroundColor(ResUtils.getColor(getContext(), R.color.colorBackground));
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        llParams.setMargins(1, 1, 1, 1);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(llParams);
        ll.setOrientation(VERTICAL);
        ImageView imageView = new ImageView(getContext());
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop()
                .placeholder(ResUtils.getDrawable(getContext(), R.drawable.holder1))
                .error(ResUtils.getDrawable(getContext(), R.drawable.ic_error_loading_image_24dp))
                .into(imageView);
        ll.addView(imageView);
        if (description != null) {
            LinearLayout.LayoutParams tvParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(tvParams);
            tv.setPadding(4,2,4,2);
            tv.setTextColor(ResUtils.getColor(getContext(), R.color.secondary_text));
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
