package com.isanechek.beardycast.testlab;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.parser.Parser;
import com.isanechek.beardycast.utils.ResUtils;
import com.isanechek.beardycast.utils.UrlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

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
    private static final String PATTERN_P = "(<p>[\\s\\S]*?</p>)";
    private static final String PATTERN_UL = "(<ul>[\\s\\S]*?</ul>)";
    private static final String P_T_T = "";

    private ArrayList<String> hell = new ArrayList<>();

    private int coubt = 0;
    private int sizeZ = 0;

    private ResUtils resUtils;

    private DisplayMetrics metrics;
    private LinearLayout container;
    private LinearLayout layout;
    ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        resUtils = new ResUtils();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coubt = 0;

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
            InputStream is = getAssets().open("obzor-moto-g4-plus.html");
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
            Elements elements = element.select("p, ul, blockquote, iframe, figure, ol, h1, h2, h3, h4");


            Elements testEl = element.children();

            Stream.of(elements)
                    .forEach(cache::add);
            return cache;
        }
        return null;
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

        hell.add("Hello"); // for item no is empty list
        LinearLayout.LayoutParams wwllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams mwllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams wwvglp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams mwvglp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (Element e : list) {


            String elementName = e.tagName();
            switch (elementName) {
                case "p":



                    if (blockquoteStatus) {

                        Log.i(TAG, "p QUOTE: " + e);
                        blockquoteStatus = false;
                    } else {
                        Log.i(TAG, "p: " + e);
                    }
//
//
//                    for (Element pElement : e.children()) {
//                        String pElementName = pElement.tagName();
//                        if (pElementName.equals("blockquote")) {
//                            for (Element element : pElement.children()) {
//                                String tagName = element.tagName();
//                                if (tagName.equals("ul")) {
//                                    createHell(element, true);
//                                } else {
//
//                                }
//                            }
//                        } else {
//                            if (isCheck(Parser.backString(e), IMG)) {
//                                LinearLayout ll = new LinearLayout(this);
//                                ll.setLayoutParams(mwllp);
//                                ll.setOrientation(LinearLayout.VERTICAL);
//                                ll.setPadding(1,1,0,1);
//                                ll.setBackgroundColor(Color.parseColor("#E0E0E0"));
//
//                                LinearLayout ll2 = new LinearLayout(this);
//                                ll.setLayoutParams(mwllp);
//                                ll.setOrientation(LinearLayout.VERTICAL);
//
//                                int height = dpToPix(150, metrics);
//                                ImageView iv = new ImageView(this);
//                                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//                                iv.setScaleType(ImageView.ScaleType.FIT_XY);
//                                String u = Parser.tryUrl(Parser.backString(e));
////                                Glide.with(this).load(u).asBitmap().placeholder(R.drawable.h1).into(iv);
//
//                                ll2.addView(iv);
//                                ll.addView(ll2);
//
//                                String imageDescription = e.select("img").attr("alt");
//                                if (imageDescription.length() != 0) {
//                                    LinearLayout dll = new LinearLayout(this);
//                                    dll.setLayoutParams(mwllp);
//                                    dll.setOrientation(LinearLayout.VERTICAL);
//                                    dll.setBackgroundColor(Color.parseColor("#EEEEEE"));
//
//                                    TextView textView = new TextView(this);
//                                    textView.setLayoutParams(wwvglp);
//                                    textView.setPadding(4,4,4,4);
//                                    textView.setTextSize(12f);
//                                    textView.setText(imageDescription);
//                                    dll.addView(textView);
//                                    ll.addView(dll);
//                                }
//
//                                container.addView(ll);
//                            } else {
//                                TextView textView = new TextView(this);
//                                textView.setLayoutParams(wwvglp);
//                                textView.setPadding(8,2,8,2);
//                                textView.setTextSize(16f);
//                                Spanned text = getText(e);
//                                textView.setText(text);
//                                container.addView(textView);
//                            }
//                        }
//                    }

                    break;
                case "ol":

                    Log.i(TAG, "ol: " + e);

//                    Spanned text = null;
//                    Elements olElements = e.children();
//                    if (olElements.size() != 0) {
//                        LinearLayout layout = new LinearLayout(this);
//                        layout.setLayoutParams(wwllp);
//                        layout.setOrientation(LinearLayout.HORIZONTAL);
//                        layout.setBackgroundColor(Color.parseColor("#E0E0E0"));
//                        for (Element element : olElements) {
//                            String olElementTagName = element.tagName();
//                            if (olElementTagName.equals("li")) {
//                                Spanned spanned = getText(element);
//                                TextView textView = new TextView(this);
//                                textView.setLayoutParams(wwvglp);
//                                textView.setPadding(4,4,4,4);
//                                textView.setTextSize(16f);
//                                textView.setText(spanned);
//                                layout.addView(textView);
//                            }
//                        }
//                        container.addView(layout);
//                    }

                    break;
                case "figure":


                    Log.i(TAG, "figure: " + e);
//                    String authors = e.getElementsByClass("line").last().text();
//                    if (authors != null) {
//                        LinearLayout layout = new LinearLayout(this);
//                        layout.setLayoutParams(wwllp);
//                        layout.setOrientation(LinearLayout.HORIZONTAL);
//                        layout.setPadding(8,8,8,8);
//                        layout.setBackgroundColor(Color.parseColor("#E0E0E0"));
//
//                        TextView textView = new TextView(this);
//                        textView.setLayoutParams(wwvglp);
//                        textView.setPadding(4,4,4,4);
//                        textView.setTextSize(16f);
//                        textView.setText(authors);
//
//                        container.addView(layout);
//
//                    }

                    break;
                case "ul":
                    if (blockquoteStatus) {
                        Log.i(TAG, "ul1: " + e);
                        blockquoteStatus = false;
                    } else {
                        Log.i(TAG, "ul: " + e);
                        for (Element element : e.children()) {

//                            if (isCheckList(element.))

                        }
                    }

                    break;
                case "blockquote":
                    blockquoteStatus = true;

//                    Elements elements = e.children();
//                    for (Element element : elements) {
//                        Log.d(TAG, "blockquote: " + element.tagName());
//                        Log.d(TAG, "blockquote1: " + element);
//                    }

                    break;
                case "h1":
                    Log.i(TAG, "h1: " + e);

                    break;
                case "h2":
                    Log.i(TAG, "h2: " + e);
                    break;
                case "h3":

                    Log.i(TAG, "h4: " + e);


                    break;
                case "h4":

                    Log.i(TAG, "h4: " + e);

                    break;
                case "iframe":

                    Log.i(TAG, "iframe: " + e);
//                    String url = e.attr("src");
//                    Log.e(TAG, UrlUtil.isImgurUrl(url) ? "isImgurUrl TRUE" : "isImgurUrl FALSE");
//                    Log.e(TAG, UrlUtil.isYouTubeUrl(url) ? "isYouTubeUrl TRUE" : "isYouTubeUrl FALSE");
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isCheckList(@NonNull String line) {
        Stream.of(hell).filter(value -> value.equals(line));
        return false;
    }

    private void msg1(String tagname, Element element) {
        Stream.of(element.children()).map(Element::toString).forEach(value -> Log.i(TAG, tagname + " " + value));
    }

    /*-----------------------------START----------------------------*/

    private void createBlockQuote(Element element) {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(8,8,8,8);
        ll.setBackgroundColor(Color.parseColor("#E0E0E0"));

        LinearLayout ll2 = new LinearLayout(this);
        ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll2.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setBackground(null);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_format_quote_white_24dp));
        imageView.setPadding(2,2,2,2);
        ll2.addView(imageView);
        ll.addView(ll2);

        LinearLayout ll3 = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);

        container.addView(ll);
    }


    private void createHell(Element element, boolean quote) {

        Elements rootElements = element.children();

        if (rootElements.size() != 0) {
            Log.i(TAG, "rootElements size: " + rootElements.size());
            for (Element e1 : rootElements) {
                String e1Name = e1.tagName();
                Log.i(TAG, "e1 tag name: " + e1Name);
                switch (e1Name) {
                    case "a":
                        Log.i(TAG, "e1: " + e1);
                        createATagTV(e1);
                        break;
                    case "li":
                        Elements e2 = e1.children();
                        Log.i(TAG, "e2 size: " + e2.size());
                        for (Element e3 : e2) {
                            String e3TagName = e3.tagName();
                            Log.i(TAG, "e3 tag name: " + e3.tagName());
                            switch (e3TagName) {
                                case "a":
                                    Log.i(TAG, "e3: " + e3);
                                    createATagTV(e3);
                                    break;
                                case "li":

                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "ul":
                        break;
                    default:
                        break;
                }
            }


        } else {

        }

    }

    private void createPtagTv(Element element) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(8,8,8,8);
        textView.setTextSize(16f);
        Spanned text = getText(element);
        textView.setText(text);
        container.addView(textView);
    }

    private void createATagTV(Element element) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(8,8,8,8);
        textView.setTextSize(16f);
        Spanned text = getText(element);
        textView.setText(text);
        container.addView(textView);
    }


    private Spanned getText(Element element) {
        return Html.fromHtml(element.toString());
    }

    private boolean isCheck(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(input);
        return matcher.matches();
    }
}
