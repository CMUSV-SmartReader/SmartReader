package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Article;
import models.Feed;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedParser {

    private static SyndFeed getFeedInfo(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed syndFeed = input.build(new XmlReader(instream));
                return syndFeed;
            }
        } catch (ClientProtocolException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (FeedException e) {
//            e.printStackTrace();
        }
        return null;

    }

    public static List<Article> parseFeed(Feed feed) throws Exception {
        SyndFeed syndFeed = getFeedInfo(feed.xmlUrl);
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

    public static Feed parseFeedInfo(String link) throws Exception {
        SyndFeed syndFeed = getFeedInfo(link);
        return new Feed(syndFeed);

    }
}
