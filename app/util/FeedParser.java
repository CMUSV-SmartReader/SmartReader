package util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Article;
import models.Feed;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedParser {


    public static List<Article> parseFeed(Feed feed) throws Exception {
        URL url = new URL(feed.xmlUrl);
        HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
        httpcon.connect();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed syndFeed = input.build(new XmlReader(httpcon));
        List<?> entries = syndFeed.getEntries();
        Iterator<?> itEntries = entries.iterator();
        List<Article> articles = new ArrayList<Article>();
        while (itEntries.hasNext()) {
            SyndEntry entry = (SyndEntry) itEntries.next();
            Article newArticle = new Article(entry);
            articles.add(newArticle);
        }
        return articles;
    }
}
