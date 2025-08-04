package com.arjundabbe.jivanman.util;

import android.text.Html;
import android.util.Log;
import android.util.Xml;

import com.arjundabbe.jivanman.models.NewsArticle;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RSSFeedParser {

    public List<NewsArticle> parse(String feedUrl) {
        List<NewsArticle> newsList = new ArrayList<>();
        String TAG = "RSSFeedParser";

        try {
            URL url = new URL(feedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line
                        .replaceAll("&(?![a-zA-Z#]+;)", "&amp;")                  // Fix unescaped ampersands
                        .replaceAll("(?i)<\\s*/?\\s*t[^>]*>", "")                 // Remove <t> tags
                        .replaceAll("(?i)<\\s*/?\\s*table[^>]*>", "")             // Remove table tags
                        .replaceAll("(?i)<\\s*/?\\s*tr[^>]*>", "")
                        .replaceAll("(?i)<\\s*/?\\s*td[^>]*>", "")
                        .replaceAll("(?i)<\\s*/?\\s*div[^>]*>", "")
                        .replaceAll("(?i)<\\s*/?\\s*script[^>]*>", "")
                        .replaceAll("(?i)<\\s*/?\\s*style[^>]*>", "")
                        .replaceAll("(?i)<\\s*br\\s*/?>", " ")                    // Replace <br> with space
                        .replaceAll("(?i)<[^>]+>", "")                            // Remove remaining HTML tags
                        .replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E\\xA0-\\uFFFF]", "");  // Strip invalid unicode

                builder.append(line);
            }

            reader.close();
            connection.disconnect();

            String cleanedXml = builder.toString();

            // Add XML header if not present
            if (!cleanedXml.trim().startsWith("<?xml")) {
                cleanedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + cleanedXml;
            }

            InputStream cleanedStream = new ByteArrayInputStream(cleanedXml.getBytes(StandardCharsets.UTF_8));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new InputStreamReader(cleanedStream, StandardCharsets.UTF_8));

            int eventType = parser.getEventType();
            String title = "", description = "", pubDate = "", imageUrl = "";
            boolean insideItem = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
                            insideItem = true;
                            title = description = pubDate = imageUrl = "";
                        } else if (insideItem) {
                            switch (tagName.toLowerCase()) {
                                case "title":
                                    title = safeText(parser);
                                    break;
                                case "description":
                                    description = safeText(parser);
                                    imageUrl = extractImageFromDescription(description);
                                    break;
                                case "pubdate":
                                    pubDate = safeText(parser);
                                    break;
                                case "media:content":
                                case "enclosure":
                                    String urlAttr = parser.getAttributeValue(null, "url");
                                    if (urlAttr != null && !urlAttr.isEmpty()) {
                                        imageUrl = urlAttr;
                                    }
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
                            if (!title.isEmpty()) {
                                newsList.add(new NewsArticle(title, description, imageUrl, pubDate));
                                Log.d(TAG, "Parsed article: " + title);
                            }
                            insideItem = false;
                        }
                        break;
                }

                eventType = parser.next();
            }

            cleanedStream.close();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing RSS feed from: " + feedUrl, e);
        }

        return newsList;
    }

    private String safeText(XmlPullParser parser) {
        try {
            String raw = parser.nextText();
            return Html.fromHtml(raw, Html.FROM_HTML_MODE_LEGACY).toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String extractImageFromDescription(String description) {
        try {
            int imgStart = description.indexOf("<img");
            if (imgStart != -1) {
                int srcStart = description.indexOf("src=\"", imgStart);
                int srcEnd = description.indexOf("\"", srcStart + 5);
                if (srcStart != -1 && srcEnd != -1) {
                    return description.substring(srcStart + 5, srcEnd);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}
