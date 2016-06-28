package com.isanechek.beardycast.data.parser;

import android.util.Log;

import com.isanechek.beardycast.Constants;
import com.isanechek.beardycast.data.network.OkHelper;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;
import com.isanechek.beardycast.data.parser.model.list.ParserListCategoryModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListModel;
import com.isanechek.beardycast.data.parser.model.list.ParserListTagModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by isanechek on 26.04.16.
 */
public class Parser {

    public static List<ParserListModel> parseList(String url) {
        List<ParserListModel> cache = new ArrayList<>();
        List<ParserListTagModel> listTags;
        List<ParserListCategoryModel> listCategory;

        String body = OkHelper.getBody(url);

        if (body != null) {
            Document document = Jsoup.parse(body);
            Elements rootElements = document.getElementsByClass("article-inner");
            for (Element element : rootElements) {
                listTags = new ArrayList<>();
                listCategory = new ArrayList<>();

                Element link = element.select("a").first();
                String articleUrl = tryUrl(link.attr("href"));
                String imageUrl = tryUrl(link.select("img[src]").attr("src"));
                String title = element.getElementsByClass("article-title").text();
                String date = element.getElementsByClass("article-date").text();
                String description = element.getElementsByClass("article-entry").select("p").first().text();

                /*Date*/
                DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                Date d1 = null;
                try {
                    d1 = df.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    msg("Date parse error -->> " + e.getMessage());
                }

                long time = System.currentTimeMillis();
                int hours = (int)((time/(1000*60*60)) % 24);
                int minutes = (int)((time/(1000*60)) % 60);
                int seconds = (int)((time/1000) % 60);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d1);
                calendar.add(Calendar.HOUR, hours);
                calendar.add(Calendar.MINUTE, minutes);
                calendar.add(Calendar.SECOND, seconds);

                Date resultDate = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                msg("Check date --> " + dateFormat.format(resultDate.getTime()));

                /*category*/
                Element category = element.getElementsByClass("article-category").first();
                Elements categoryElements = category.getElementsByClass("article-category-link");
                for (Element obj : categoryElements) {
                    String categoryName = obj.text();
                    String categoryUrl = tryUrl(obj.select("a").attr("href"));
                    listCategory.add(new ParserListCategoryModel(categoryName, categoryUrl));
                }

                /*Пока так*/
                boolean podcast = false;
                for (int i = 0; i < listCategory.size(); i++) {
                    ParserListCategoryModel model = listCategory.get(i);
                    if (checkPodcast(model.getCategoryName()))
                        podcast = true;
                }

                /*tags*/
                Element tags = element.getElementsByClass("article-tag-list").first();
                Elements tagElements = tags.getElementsByClass("article-tag-list-link");
                for (Element obj : tagElements) {
                    String tagName = obj.text();
                    String tagUrl = tryUrl(obj.select("a").attr("href"));
                    listTags.add(new ParserListTagModel(tagName, tagUrl));
                }

                cache.add(new ParserListModel(title, description, resultDate, articleUrl, imageUrl, podcast, listTags, listCategory));
            }
            return cache;
        }
        return null;
    }

    public static String parseDateLastPost(String url) {
        String body = OkHelper.getBody(url);
        if (body != null) {
            Document document = Jsoup.parse(body);
            Element element = document.getElementsByClass("article-inner").first();
            return element.getElementsByClass("article-title").text();
        }
        return null;
    }

    public static List<ParserModelArticle> getArticle(String url) {
        ArrayList<ParserModelArticle> cache = new ArrayList<>();

        String body = OkHelper.getBody(url);
        if (body != null) {
            Document document = Jsoup.parse(body);
            Element element = document.getElementsByClass("article-entry").first();
            Elements elements = element.select("p");
            for (Element obj : elements) {
                if (checkImg(backString(obj))) {
                    cache.add(new ParserModelArticle(tryUrl(backString(obj))));
                }
                else {
                    cache.add(new ParserModelArticle(obj.text()));
                }
            }
            return cache;
        }
        return null;
    }


    /*FOR TEST*/
    public static JSONObject getArticleJson(String url) {
        String body = OkHelper.getBody(url);
        JSONArray cache = new JSONArray();
        try {
            if (body != null) {
                Document document = Jsoup.parse(body);
                Element element = document.getElementsByClass("article-entry").first();
                Elements elements = element.select("p");
                for (Element obj : elements) {
                    JSONObject object = new JSONObject();
                    object.put("obj", obj.toString());
                    cache.put(object);
                    msg("Object -->> " + obj.toString());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(url, jsonObject);
                return jsonObject;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkImg(final String url) {
        Pattern pattern = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|bmp|gif))$)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches())
            return true;
        return false;
    }

    private static String backString(Element element) {
        if (element != null) {
            return element.select("img[src]").attr("src");
        }
        return null;
    }
    private static String tryUrl(String url) {
        return Constants.HOME_LINK + url;
    }

    private static boolean checkPodcast(String category) {
        Pattern pattern = Pattern.compile("Подкаст");
        Matcher matcher = pattern.matcher(category);
        if (matcher.matches())
            return true;
        return false;
    }

    private static void msg(String text) {
        Log.d("PARSER", text);
    }
}
