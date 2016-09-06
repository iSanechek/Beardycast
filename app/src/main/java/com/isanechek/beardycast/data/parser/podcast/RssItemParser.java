package com.isanechek.beardycast.data.parser.podcast;

import android.util.Log;

import com.isanechek.beardycast.data.network.OkHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RssItemParser {
    private String title = null;
    private Date pubDate = null;
    private String length = null;
    private String mp3url = null;
    private String duration = null;
    private String explicit = null;

    public RssItemParser(String title, Date pubDate, String length, String mp3url, String duration, String explicit) {
        this.title = title;
        this.pubDate = pubDate;
        this.length = length;
        this.mp3url = mp3url;
        this.duration = duration;
        this.explicit = explicit;
    }

    public String getTitle() {
        return title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getLength() {
        return length;
    }

    public String getMp3url() {
        return mp3url;
    }

    public String getDuration() {
        return duration;
    }

    public String getExplicit() {
        return explicit;
    }

    public static ArrayList<RssItemParser> getRssItems(String feedUrl) {

        InputStream is;
        ArrayList<RssItemParser> rssItemParsers = new ArrayList<>();

        try {
            is = OkHelper.getStream(feedUrl);
            DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document document = db.parse(is);
            Element element = document.getDocumentElement();

            NodeList nodeList = element.getElementsByTagName("item");

            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {

                    Element entry = (Element) nodeList.item(i);

                    Element _titleE = (Element) entry.getElementsByTagName(
                            "title").item(0);
                    Element _pubDateE = (Element) entry
                            .getElementsByTagName("pubDate").item(0);
                    Element _descriptionE = (Element) entry
                            .getElementsByTagName("enclosure").item(0);

                    String _title = _titleE.getFirstChild().getNodeValue();
                    String date = _pubDateE.getFirstChild().getNodeValue();

                    DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                    Date _pubDate = null;
                    try {
                        _pubDate = df.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        msg("Date parse error -->> " + e.getMessage());
                    }

                    String mp3url = _descriptionE.getAttribute("url");
                    String l = _descriptionE.getAttribute("length");
                    String length = Long.parseLong(l) / (1024 * 1024) + "mb";
                    String duration = getDuration(entry);
                    String explicit = getExplicit(entry);

                    msg("------------------------------Item----------------------------------------");
                    msg("title -->> " +_title);
                    msg("date -->> " + _pubDate);
                    msg("length -->> "+ Long.parseLong(l) / (1024 * 1024) + "mb");
                    msg("link2 -->> " + mp3url);
                    msg("duration -->> " + duration);
                    msg("explicit -->> " + explicit);
                    msg("------------------------------END-----------------------------------------");

                    RssItemParser rssItemParser = new RssItemParser(_title, _pubDate, length, mp3url, duration, explicit);

                    rssItemParsers.add(rssItemParser);
                }
            }
            if (is != null) {
                is.close();
            }
            return rssItemParsers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String[] podcastExplicitrTagNames = {"itunes:explicit"};
    private static String getExplicit(Element entry) {
        String expl = "";
        for (String explicit : podcastExplicitrTagNames) {
            try {
                NodeList d = entry.getElementsByTagName(explicit);
                if (d.equals(null) || d.getLength() < 1) {
                    continue;
                }
                expl = d.item(0).getChildNodes().item(0).getNodeValue();
                if (!expl.isEmpty()) break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return expl;
    }

    private static final String[] podcastDurationTagNames = {"itunes:duration"};
    private static String getDuration(Element entry) {
        String du = "";
        for (String duration : podcastDurationTagNames) {
            try {
                NodeList d = entry.getElementsByTagName(duration);
                if (d.equals(null) || d.getLength() < 1) {
                    continue;
                }
                du = d.item(0).getChildNodes().item(0).getNodeValue();
                if (!du.isEmpty()) break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return du;
    }

    private static void msg(String text) {
        Log.d("PARSER", text);
    }
}
