package com.isanechek.beardycast.testlab;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.ui.imageviewer.ImageViewer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.isanechek.beardycast.utils.Util.dpToPix;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = "TEST ACTIVITY";
    private static final String IMG = "([^\\s]+(\\.(?i)(jpg|png|gif))$)";
    private static final String MP3 = "([^\\s]+(\\.(?i)(mp3))$)";
    private static final String UL_TAG = "<ul>";
    private static final String PATTER_ONE = "<div class=\"article-entry\"[^>]*?>[\\s\\S]*?<!-- toc -->([\\s\\S]*?)<!-- tocstop -->[\\s\\S]*?(<h1[\\s\\S]*?)<hr";

    private DisplayMetrics metrics;
    private LinearLayout container;
    private LinearLayout layout;
    ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        container = (LinearLayout) findViewById(R.id.content_container);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::showToast);
    }

    private void showToast(View view) {
        Snackbar.make(view, "Start Task", Snackbar.LENGTH_LONG).show();
        startTask();
        Log.d(TAG, "showToast: Start task");
    }
    private String fromJson() {
        String json = null;

        try {
            InputStream is = getAssets().open("article.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private List<Element> getList() {
        List<Element> cache = new ArrayList<>();
        List<String> cache1 = new ArrayList<>();

        String linkOne = "http://beardycast.com/2016/07/11/Andrey_B/apple-maps-review/";
        String linkTwo = "http://beardycast.com/2016/07/10/Anton_P/beardygram-1/";
//        String body = OkHelper.getBody(linkTwo);
        String body = fromJson();
        if (body != null) {

            Document document = Jsoup.parse(body);
            Element element = document.getElementsByClass("article-entry").first();
            Elements elements = element.select("p, ul, blockquote, h1, h2, h4");
            Stream.of(elements)
                    .forEach(cache::add);
            /*For test*/
//            Stream.of(elements)
//                    .map(Element::toString)
//                    .forEach(this::msg);

//            Pattern pattern = Pattern.compile(PATTER_ONE);
//            Matcher matcher = pattern.matcher(body);
////            if (matcher.matches()) {
////                Log.d(TAG, "Что то нашлось");
////                String t = matcher.group();
////                Log.d(TAG, "getList: " + t);
////            } else {
////                Log.e(TAG, "Ничего не нашлось");
////            }
//
////            while(matcher.find()) {
////                Log.d(TAG, "getList: " + matcher.group());
////            }
////

            return cache;
        }
        return null;
    }

    private void msg(String s) {
        Log.d(TAG, "msg: " + s);
    }

    private void startTask() {
        TaskHelper helper = new TaskHelper();
        helper.execute();
    }

    private class TaskHelper extends AsyncTask<Void, Void, List<Element>> {

        @Override
        protected List<Element> doInBackground(Void... voids) {
            return getList();
        }

        @Override
        protected void onPostExecute(List<Element> elements) {
            super.onPostExecute(elements);
            if (elements.size() == 0) {
                Log.e(TAG, "onPostExecute: NULL");
            } else {
                Log.d(TAG, "onPostExecute: " + elements.size());
                testParsing(elements);
            }
        }
    }

    private void testParsing(List<Element> list) {
        boolean blockquoteStatus = false;

        for (Element e : list) {
            String elementName = e.tagName();
            switch (elementName) {
                case "p":
                    if (isCheck(Parser.backString(e), IMG)) {
                        View v = createImage(e);
                        createTestImage(e);
                        buildUiHelper(v, true);
                    } else {
                        if (blockquoteStatus) {
                            TextView t = createTextView(e, true);
                            buildUiHelper(t, true);
                            blockquoteStatus = false;
                        } else {
                            TextView textView = createTextView(e, false);
                            buildUiHelper(textView, true);
                        }
                    }
                    break;
                case "ul":
                    Elements ee = e.children();

                    LinearLayout linearLayout = new LinearLayout(ScrollingActivity.this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    for (Element element : ee) {

                        Elements elements = element.select("a, li");

                        for (Element element1 : elements) {
                            String element1Name = element1.tagName();
                            if (element1Name.equals("a")) {
                                TextView textView = new TextView(ScrollingActivity.this);
                                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                textView.setPadding(8,8,8,8);
                                textView.setText(getText(element1));
                                linearLayout.addView(textView);
                            }
                        }
                    }
                    buildUiHelper(linearLayout, false);
                    break;
                case "blockquote":
                    blockquoteStatus = true;
                    break;
                case "h1":
                    TextView textView2 = new TextView(ScrollingActivity.this);
                    textView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView2.setText(getText(e));
                    textView2.setGravity(Gravity.CENTER_HORIZONTAL);
                    buildUiHelper(textView2, false);
                    break;
                case "h2":
                    TextView textView3 = new TextView(ScrollingActivity.this);
                    textView3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView3.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView3.setText(getText(e));
                    buildUiHelper(textView3, false);
                    break;
                case "h4":

                    break;
                default:
                    break;
            }
        }
    }

    private Spanned getText(Element element) {
        return Html.fromHtml(element.toString());
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
        CardView cardView = new CardView(ScrollingActivity.this);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        cardView.setPadding(16,0,16,0);
        LinearLayout layout = new LinearLayout(ScrollingActivity.this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(getResources().getColor(R.color.accent));
        Stream.of(views).forEach(layout::addView);
        cardView.addView(layout);
        container.addView(cardView);
    }

    private View createImage(@NonNull Element element) {
        int height = dpToPix(150, metrics);
        CardView cardView = new CardView(ScrollingActivity.this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, height));

        ImageView imageView = new ImageView(ScrollingActivity.this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        cardView.addView(imageView);
        cardView.setOnClickListener(v -> ImageViewer.startActivity(ScrollingActivity.this, Parser.backString(element)));
        String u = Parser.tryUrl(Parser.backString(element));
//        Glide.with(this).load(u).centerCrop().placeholder(R.drawable.holder1).crossFade().into(imageView);
        return cardView;
    }

    private void createTestImage(Element element) {
        int height = dpToPix(150, metrics);

        Elements obj = element.getAllElements();


    }

    private TextView createTextView(@NonNull Element element, boolean b) {
        TextView textView = new TextView(ScrollingActivity.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(16f);
        textView.setPadding(8,8,8,8);
        textView.setTextColor(b ? Color.GREEN : Color.parseColor("#212121"));
        textView.setText(Html.fromHtml(element.toString()));
        return textView;
    }

    private boolean isCheck(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(input);
        return matcher.matches();
    }
}
