//package com.isanechek.beardycast.testlab;
//
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.Toolbar;
//import android.text.Html;
//import android.text.Spanned;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.annimon.stream.Stream;
//import com.bumptech.glide.Glide;
//import com.isanechek.beardycast.R;
//import com.isanechek.beardycast.data.parser.Parser;
//import com.isanechek.beardycast.ui.imageviewer.ImageViewer;
//import com.isanechek.beardycast.utils.UrlUtil;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static com.isanechek.beardycast.utils.Util.dpToPix;
//
//public class ScrollingActivity extends AppCompatActivity {
//
//    private static final String TAG = "TEST ACTIVITY";
//    private static final String IMG = "([^\\s]+(\\.(?i)(jpg|png|gif))$)";
//    private static final String MP3 = "([^\\s]+(\\.(?i)(mp3))$)";
//    private static final String UL_TAG = "<ul>";
//    private static final String PATTER_ONE = "<div class=\"article-entry\"[^>]*?>[\\s\\S]*?<!-- toc -->([\\s\\S]*?)<!-- tocstop -->[\\s\\S]*?(<h1[\\s\\S]*?)<hr";
//    private static final String PATTERN_P = "(<p>[\\s\\S]*?</p>)";
//    private static final String PATTERN_UL = "(<ul>[\\s\\S]*?</ul>)";
//    private static final String P_T_T = "";
//
//
//    private int coubt = 0;
//    private int sizeZ = 0;
//
//    private DisplayMetrics metrics;
//    private LinearLayout container;
//    private LinearLayout layout;
//    ArrayList<View> views = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scrolling);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        coubt = 0;
//
//        metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        container = (LinearLayout) findViewById(R.id.content_container);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this::showToast);
//    }
//
//    private void showToast(View view) {
//        Snackbar.make(view, "Start Task", Snackbar.LENGTH_LONG).show();
//        startTask();
//        Log.d(TAG, "showToast: Start task");
//    }
//    private String fromJson() {
//        String json = null;
//
//        try {
//            InputStream is = getAssets().open("obzor-moto-g4-plus.html");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return json;
//    }
//
//    private List<Element> getList() {
//        List<Element> cache = new ArrayList<>();
//        List<String> cache1 = new ArrayList<>();
//
//        String linkOne = "http://beardycast.com/2016/07/11/Andrey_B/apple-maps-review/";
//        String linkTwo = "http://beardycast.com/2016/07/10/Anton_P/beardygram-1/";
////        String body = OkHelper.getBody(linkTwo);
//        String body = fromJson();
//        if (body != null) {
//
//            Document document = Jsoup.parse(body);
//            Element element = document.getElementsByClass("article-entry").first();
//            Elements elements = element.select("p, ul, blockquote, iframe, figure, ol, h1, h2, h3, h4");
//
//            Elements testEl = element.children();
//
//            Stream.of(testEl)
//                    .forEach(cache::add);
//            /*For test*/
////            Stream.of(elements)
////                    .map(Element::toString)
////                    .forEach(this::msg);
//
////            Pattern pattern = Pattern.compile(PATTER_ONE);
////            Matcher matcher = pattern.matcher(body);
//////            if (matcher.matches()) {
//////                Log.d(TAG, "Что то нашлось");
//////                String t = matcher.group();
//////                Log.d(TAG, "getList: " + t);
//////            } else {
//////                Log.e(TAG, "Ничего не нашлось");
//////            }
////
//////            while(matcher.find()) {
//////                Log.d(TAG, "getList: " + matcher.group());
//////            }
//////
//
//            return cache;
//        }
//        return null;
//    }
//
//    private void startTask() {
//        TaskHelper helper = new TaskHelper();
//        helper.execute();
//    }
//
//    private class TaskHelper extends AsyncTask<Void, Void, List<Element>> {
//
//        @Override
//        protected List<Element> doInBackground(Void... voids) {
//            return getList();
//        }
//
//        @Override
//        protected void onPostExecute(List<Element> elements) {
//            super.onPostExecute(elements);
//            if (elements.size() == 0) {
//                Log.e(TAG, "onPostExecute: NULL");
//            } else {
//                Log.d(TAG, "onPostExecute: " + elements.size());
//                testParsing(elements);
//            }
//        }
//    }
//
//    private void testParsing(List<Element> list) {
//        boolean blockquoteStatus = false;
//
//        for (Element e : list) {
//            String elementName = e.tagName();
//            switch (elementName) {
//                case "p":
//                    if (isCheck(Parser.backString(e), IMG)) {
//
//                        LinearLayout layout = new LinearLayout(ScrollingActivity.this);
//                        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        layout.setOrientation(LinearLayout.VERTICAL);
//                        layout.setPadding(1,1,0,1);
//                        layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//
//                        LinearLayout layout2 = new LinearLayout(ScrollingActivity.this);
//                        layout2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        layout2.setOrientation(LinearLayout.VERTICAL);
//
//
//                        int height = dpToPix(150, metrics);
//                        ImageView iv = new ImageView(ScrollingActivity.this);
//                        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
//                        String u = Parser.tryUrl(Parser.backString(e));
//                        Glide.with(ScrollingActivity.this)
//                                .load(u)
//                                .asBitmap()
//                                .thumbnail(0.1f)
//                                .placeholder(R.drawable.h1)
//                                .error(R.drawable.holder1)
//                                .centerCrop()
//                                .into(iv);
//
//                        layout2.addView(iv);
//                        layout.addView(layout2);
//
//                        String stroka = e.select("img").attr("alt");
//                        if (stroka.length() != 0) {
//                            int height1 = dpToPix(1, metrics);
//                            View view = new View(ScrollingActivity.this);
//                            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height1));
//                            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                            layout.addView(view);
//
//                            LinearLayout layout1 = new LinearLayout(ScrollingActivity.this);
//                            layout1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                            layout1.setOrientation(LinearLayout.VERTICAL);
//                            layout1.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//                            TextView textView = new TextView(ScrollingActivity.this);
//                            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                            textView.setTextSize(12f);
//                            textView.setPadding(8,8,8,8);
//                            textView.setText(stroka);
//                            layout1.addView(textView);
//                            layout.addView(layout1);
//                        }
//                        container.addView(layout);
//                    } else {
//                        if (blockquoteStatus) {
//                            LinearLayout blockquoteLL = new LinearLayout(ScrollingActivity.this);
//                            blockquoteLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT));
//                            blockquoteLL.setOrientation(LinearLayout.HORIZONTAL);
//
//                            ImageView ivi = new ImageView(ScrollingActivity.this);
//                            ivi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT));
//                            ivi.setImageDrawable(getResources().getDrawable(R.drawable.ic_format_quote_white_24dp));
//                            blockquoteLL.addView(ivi);
//
//                            TextView tvt = new TextView(ScrollingActivity.this);
//                            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT));
//                            tvt.setPadding(8,8,8,8);
//                            tvt.setTextSize(16f);
//                            Spanned stvtText = getText(e);
//                            tvt.setText(stvtText);
//                            blockquoteLL.addView(tvt);
//                            container.addView(blockquoteLL);
//                            blockquoteStatus = false;
//                        } else {
//                            TextView tvt = new TextView(ScrollingActivity.this);
//                            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT));
//                            tvt.setPadding(8,2,8,2);
//                            tvt.setTextSize(16f);
//                            Spanned tvtText = getText(e);
//                            tvt.setText(tvtText);
//                            container.addView(tvt);
//                        }
//                    }
//                    break;
//                case "ol":
//
//                    for (Element tt : e.children()) {
//                        TextView textView = new TextView(ScrollingActivity.this);
//                        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT));
//                        textView.setPadding(8,8,8,8);
//                        textView.setTextSize(16f);
//                        Spanned text = getText(e);
//                        textView.setText(text);
//                        textView.setTextColor(Color.CYAN);
//                        container.addView(textView);
//
//                    }
//
//                    break;
//                case "figure":
//                    String t = e.getElementsByClass("line").last().text();
//                    Log.w(TAG, "figure: " + t);
//
//                    break;
//                case "ul":
//                    createUlHell(e, false);
//
////                    LinearLayout ll = new LinearLayout(ScrollingActivity.this);
////                    ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
////                            ViewGroup.LayoutParams.WRAP_CONTENT));
////                    ll.setOrientation(LinearLayout.VERTICAL);
////                    ll.setPadding(8,0,8,0);
////                    if (coubt == 0) {
////                        coubt++;
////                        Elements ee = e.children();
////                        for (Element el2 : ee) {
////                            Elements e43 = el2.children();
////                            for (Element d32 :e43) {
////                                String tagName1 = d32.tagName();
////                                if (tagName1.equals("a")) {
////                                    TextView tv = new TextView(ScrollingActivity.this);
////                                    tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
////                                            ViewGroup.LayoutParams.WRAP_CONTENT));
////                                    tv.setPadding(8,2,8,2);
////                                    tv.setTextSize(16f);
////                                    Spanned text = getText(d32);
////                                    tv.setText(text);
////                                    ll.addView(tv);
////                                } else if (tagName1.equals("ul")) {
////                                    for (Element f44 : d32.children()) {
////                                        TextView tv1 = new TextView(ScrollingActivity.this);
////                                        tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
////                                                ViewGroup.LayoutParams.WRAP_CONTENT));
////                                        tv1.setPadding(16,2,16,2);
////                                        tv1.setTextSize(12f);
////                                        Spanned text1 = getText(f44);
////                                        tv1.setText(text1);
////                                        ll.addView(tv1);
////                                    }
////                                }
////                            }
////                        }
////                    } else {
////                        TextView tv2 = new TextView(ScrollingActivity.this);
////                        tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
////                        tv2.setPadding(8,2,8,2);
////                        tv2.setTextSize(16f);
////                        Spanned text2 = getText(e);
////                        tv2.setText(text2);
////                        ll.addView(tv2);
////                    }
////
////                    buildUiHelper(ll, false);
//                    break;
//                case "blockquote":
//                    for (Element element : e.children()) {
//                        String eName = element.tagName();
//                        if (eName.equals("ul")) {
//                            Log.e(TAG, "create hall: " + element);
//                            createUlHell(element, true);
//                        } else {
//                            Log.e(TAG, "not hell");
//                            blockquoteStatus = true;
//                        }
//                    }
//
//                    break;
//                case "h1":
//                    TextView textView2 = new TextView(ScrollingActivity.this);
//                    textView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT));
//                    textView2.setText(getText(e));
//                    textView2.setGravity(Gravity.CENTER_HORIZONTAL);
//                    buildUiHelper(textView2, false);
//                    break;
//                case "h2":
//                    TextView textView3 = new TextView(ScrollingActivity.this);
//                    textView3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT));
//                    textView3.setGravity(Gravity.CENTER_HORIZONTAL);
//                    textView3.setText(getText(e));
//                    buildUiHelper(textView3, false);
//                    break;
//                case "h3":
//
//                    TextView textView = new TextView(ScrollingActivity.this);
//                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    textView.setPadding(8,8,8,8);
//                    textView.setTextSize(16f);
//                    Spanned text = getText(e);
//                    textView.setText(text);
//                    buildUiHelper(textView, false);
//                    break;
//                case "h4":
//                    Log.d(TAG, "H4: " + e.toString());
//                    break;
//                case "iframe":
//                    String url = e.attr("src");
//                    Log.e(TAG, UrlUtil.isImgurUrl(url) ? "isImgurUrl TRUE" : "isImgurUrl FALSE");
//                    Log.e(TAG, UrlUtil.isYouTubeUrl(url) ? "isYouTubeUrl TRUE" : "isYouTubeUrl FALSE");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//    /*-----------------------------START----------------------------*/
//    private void createUlHell(Element element) {
//
//        Elements rootElements = element.children();
//        msg("rootElements", element);
//        if (rootElements.size() != 0) {
//            Log.i(TAG, "rootElements size: " + rootElements.size());
//            for (Element e1 : rootElements) {
//                String e1Name = e1.tagName();
//                Log.i(TAG, "e1 tag name: " + e1Name);
//                switch (e1Name) {
//                    case "a":
//                        Log.i(TAG, "e1: " + e1);
//                        createATagTV(e1);
//                        break;
//                    case "li":
//                        Elements e2 = e1.children();
//                        Log.i(TAG, "e2 size: " + e2.size());
//                        for (Element e3 : e2) {
//                            String e3TagName = e3.tagName();
//                            Log.i(TAG, "e3 tag name: " + e3.tagName());
//                            switch (e3TagName) {
//                                case "a":
//                                    Log.i(TAG, "e3: " + e3);
//                                    createATagTV(e3);
//                                    break;
//                                case "li":
//
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        break;
//                    case "ul":
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//
//        } else {
//            msge("rootElements");
//        }
//
//    }
//
//    private void createATagTV(Element element) {
//        TextView textView = new TextView(this);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        textView.setPadding(8,8,8,8);
//        textView.setTextSize(16f);
//        Spanned text = getText(element);
//        textView.setText(text);
//        container.addView(textView);
//    }
//
//    /*-----------------------------END----------------------------*/
//
//    /*HELL*/
//    private void createUlHell(Element element, boolean blockquote) {
//        ViewGroup.LayoutParams layoutParamsM = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        ViewGroup.LayoutParams layoutParamsW = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        LinearLayout layout = new LinearLayout(ScrollingActivity.this);
//        layout.setLayoutParams(layoutParamsM);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setPadding(1,1,1,1);
////
////        if (blockquote) {
////            ImageView imageView = new ImageView(ScrollingActivity.this);
////            imageView.setLayoutParams(layoutParamsW);
////            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_format_quote_white_24dp));
////            imageView.setBackground(null);
////            imageView.setPadding(8,8,8,8);
////            layout.addView(imageView);
////        }
//
//
//        Elements ee34 = element.children();  // children ul tag
//
//        for (Element element1 : ee34) {
//            msg("element1", element1);
//        }
//
//        if (ee34.size() != 0) {
//            for (Element dd1 : ee34) {
//                String dd1Name = dd1.tagName(); // li tag
//                msg("dd1", dd1);
//                if (dd1Name.equals("li")) {
//                    Elements ff34 = dd1.children(); // children li tag
//                    if (ff34.size() != 0) {
//                        for (Element ss3 : ff34) {
//                            String ss2Name = ss3.tagName();
//                            Log.i(TAG, "ss2Name5: " + ss2Name);
//                            switch (ss2Name) {
//                                case "a": {
//                                    Log.w(TAG, "one: " + ss3);
//                                    Spanned text = getText(ss3);
//                                    TextView textView = new TextView(ScrollingActivity.this);
//                                    textView.setLayoutParams(layoutParamsW);
//                                    textView.setPadding(8, 8, 8, 8);
//                                    textView.setTextSize(16f);
//                                    textView.setText(text);
//                                    layout.addView(textView);
//                                    break;
//                                }
//                                case "ul":
//                                    Elements gf45 = ss3.children();
//                                    if (gf45.size() > 0) {
//                                        for (Element g5 : gf45) {
//                                            String q5Name = g5.tagName();
//                                            Log.i(TAG, "q5Name: " + q5Name);
//                                            if (q5Name.equals("a")) {
//                                                Log.w(TAG, "two: " + g5);
//                                                Spanned text = getText(g5);
//                                                TextView textView = new TextView(ScrollingActivity.this);
//                                                textView.setLayoutParams(layoutParamsW);
//                                                textView.setPadding(12, 8, 8, 8);
//                                                textView.setTextSize(12f);
//                                                textView.setText(text);
//                                                layout.addView(textView);
//                                                Log.w(TAG, "g5: " + text);
//                                            } else if (q5Name.equals("ul")) {
//                                                Elements r5te = g5.children();
//                                                if (r5te.size() > 0) {
//                                                    for (Element el678 : r5te) {
//                                                        String rr0 = el678.tagName();
//                                                        if (rr0.equals("a")) {
//                                                            Spanned text = getText(el678);
//                                                            TextView textView = new TextView(ScrollingActivity.this);
//                                                            textView.setLayoutParams(layoutParamsW);
//                                                            textView.setPadding(16, 8, 8, 8);
//                                                            textView.setTextSize(12f);
//                                                            textView.setText(text);
//                                                            layout.addView(textView);
//                                                            Log.w(TAG, "el678: " + text);
//                                                        } else {
//                                                        /*Кто-то ебанулся со вложениями*/
//                                                        }
//                                                    }
//                                                }
//                                            } else if (q5Name.equals("li")) {
//
//                                                for (Element e95 : g5.children()) {
//                                                    String e95Name = e95.tagName();
//                                                    switch (e95Name) {
//                                                        case "a":
//                                                            Spanned text = getText(e95);
//                                                            TextView textView = new TextView(ScrollingActivity.this);
//                                                            textView.setLayoutParams(layoutParamsW);
//                                                            textView.setPadding(8, 8, 8, 8);
//                                                            textView.setTextSize(12f);
//                                                            textView.setText(text);
//                                                            layout.addView(textView);
//                                                            break;
//                                                        case "ul":
//                                                            for (Element e076 : e95.children()) {
//                                                                String e076Name = e076.tagName();
//                                                                switch (e076Name) {
//                                                                    case "li":
//                                                                        break;
//                                                                    case "a" :
//                                                                        break;
//                                                                    case "ul":
//                                                                        break;
//                                                                    default:
//                                                                        break;
//                                                                }
//                                                            }
//                                                            break;
//                                                        default:
//                                                            break;
//                                                    }
//                                                }
//
//                                                TextView textView = new TextView(ScrollingActivity.this);
//                                                textView.setLayoutParams(layoutParamsW);
//                                                textView.setPadding(8, 8, 8, 8);
//                                                textView.setTextSize(16f);
//                                                Spanned text = getText(g5);
//                                                textView.setText(text);
//                                                layout.addView(textView);
//                                            }
//                                        }
//                                    }
//                                    break;
//                                case "li": {
//
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        buildUiHelper(layout, false);
//
//
//    }
//
//    private void msge(String name) {
//        Log.e(TAG, name + " NULL");
//    }
//    private void msg(String name, Element element) {
//        for (Element e : element.children()) {
//            Log.w(TAG, name + " " + e);
//            Log.i(TAG, name + " " + e.tagName());
//        }
//    }
//
//    /*HELL END*/
//
//    /*Img Or Text*/
//    private void createTextView(Element element) {
//        if (isCheck(Parser.backString(element), IMG)) {
//            int height = dpToPix(150, metrics);
//            ImageView iv = new ImageView(ScrollingActivity.this);
//            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//            iv.setPadding(8,8,8,8);
//            String u = Parser.tryUrl(Parser.backString(element));
//            Glide.with(ScrollingActivity.this)
//                    .load(u)
//                    .asBitmap()
//                    .thumbnail(0.1f)
//                    .placeholder(R.drawable.h1)
//                    .error(R.drawable.holder1)
//                    .centerCrop()
//                    .into(iv);
//            Log.e(TAG, "Tag P text: " + element.toString());
//
//            container.addView(iv);
//
////                        View v = createImage(e);
////                        createTestImage(e);
////                        Log.e(TAG, "create Image");
////                        buildUiHelper(v, true);
//        } else {
//            TextView tvt = new TextView(ScrollingActivity.this);
//            tvt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            tvt.setPadding(8,2,8,2);
//            tvt.setTextSize(16f);
//            Spanned tvtText = getText(element);
//            tvt.setText(tvtText);
//            container.addView(tvt);
//        }
//    }
//    /*Img Or Text End*/
//
//
//    /*Block Quote*/
//    ViewGroup.LayoutParams layoutParamsM = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT);
//    ViewGroup.LayoutParams layoutParamsW = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT);
//
//    private TextView createBlockquoteTextView(Spanned text, float textSize, int leftadding) {
//        TextView textView = new TextView(ScrollingActivity.this);
//        textView.setLayoutParams(layoutParamsW);
//        textView.setPadding(leftadding,8,8,8);
//        textView.setTextSize(textSize);
//        textView.setText(text);
//        return textView;
//    }
//
//    private CardView createBlockquoteContainerBackground() {
//        CardView cardView = new CardView(ScrollingActivity.this);
//        cardView.setLayoutParams(layoutParamsM);
//        cardView.setCardBackgroundColor(Color.parseColor("#EEEEEE"));
//        cardView.setPadding(8,8,8,8);
//        return cardView;
//    }
//
//    private LinearLayout createBlockquoteContainer() {
//        LinearLayout layout = new LinearLayout(ScrollingActivity.this);
//        layout.setLayoutParams(layoutParamsM);
//        layout.setOrientation(LinearLayout.HORIZONTAL);
//        layout.setPadding(1,1,1,1);
//
//        ImageView imageView = new ImageView(ScrollingActivity.this);
//        imageView.setLayoutParams(layoutParamsW);
//        imageView.setPadding(8,8,8,8);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_format_quote_white_24dp));
//        imageView.setBackground(null);
//        layout.addView(imageView);
//        return layout;
//    }
//    /*Block Quote End*/
//
//    private void logD(String s) {
//        Log.d(TAG, s);
//    }
//
//    private String getStringMatched(Element element, String patternS) {
//        Pattern pattern = Pattern.compile(patternS);
//        Matcher matcher = pattern.matcher(element.toString());
//        if (matcher.matches()) {
//            return matcher.group();
//        }
//
//        return null;
//    }
//
//    private Spanned getText(Element element) {
//        return Html.fromHtml(element.toString());
//    }
//
//
//    private void buildUiHelper(View view, boolean card) {
//
//        if (!card) {
//            if (views.size() != 0) {
//                buildUi(views);
//                views.clear();
//            }
//            container.addView(view);
//        } else {
//            views.add(view);
//        }
//    }
//    private void buildUi(ArrayList<View> views) {
//        CardView cardView = new CardView(ScrollingActivity.this);
//        cardView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        cardView.setPadding(16,0,16,0);
//        LinearLayout layout = new LinearLayout(ScrollingActivity.this);
//        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setBackgroundColor(getResources().getColor(R.color.accent));
//        Stream.of(views).forEach(layout::addView);
//        cardView.addView(layout);
//        container.addView(cardView);
//    }
//
//    private View createImage(@NonNull Element element) {
//        int height = dpToPix(150, metrics);
//        CardView cardView = new CardView(ScrollingActivity.this);
//        cardView.setLayoutParams(new LinearLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, height));
//
//        ImageView imageView = new ImageView(ScrollingActivity.this);
//        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        cardView.addView(imageView);
//        cardView.setOnClickListener(v -> ImageViewer.startActivity(ScrollingActivity.this, Parser.backString(element)));
//        String u = Parser.tryUrl(Parser.backString(element));
////        Glide.with(this).load(u).centerCrop().placeholder(R.drawable.holder1).crossFade().into(imageView);
//        return cardView;
//    }
//
//    private void createTestImage(Element element) {
//        int height = dpToPix(150, metrics);
//
//        Elements obj = element.getAllElements();
//
//
//    }
//
//    private TextView createTextView(@NonNull Element element, boolean b) {
//        TextView textView = new TextView(ScrollingActivity.this);
//        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        textView.setTextSize(16f);
//        textView.setPadding(8,8,8,8);
//        textView.setTextColor(b ? Color.GREEN : Color.parseColor("#212121"));
//        textView.setText(Html.fromHtml(element.toString()));
//        return textView;
//    }
//
//    private boolean isCheck(String input, String pattern) {
//        Pattern p = Pattern.compile(pattern);
//        Matcher matcher = p.matcher(input);
//        return matcher.matches();
//    }
//}
