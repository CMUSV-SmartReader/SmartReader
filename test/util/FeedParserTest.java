package util;

import java.util.List;

import models.Article;
import models.Feed;
import models.MongoModel;

import org.junit.Test;

public class FeedParserTest {

    @Test
    public void testParsingFeed() throws Exception {
        MorphiaObject.setUp();
        @SuppressWarnings("unchecked")
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
                System.out.println("Parsing: " + feed.xmlUrl + " " + feed.htmlUrl);
                FeedParser.parseFeed(feed.xmlUrl);
            } catch (Exception e) {
                System.out.println("Error: " + feed.xmlUrl);
            }
        }
    }
    
    @Test
    public void testParsingOneFeed() throws Exception {
        MorphiaObject.setUp();
        List<Article> articles = FeedParser.parseFeed("http://www.engadget.com/rss.xml");
        for (Article article : articles) {
            article.create();
        }
    }
}
