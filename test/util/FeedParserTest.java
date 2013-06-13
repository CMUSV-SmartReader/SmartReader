package util;

import java.util.List;

import models.Article;
import models.Feed;
import models.MongoModel;

import org.junit.Before;
import org.junit.Test;

public class FeedParserTest {


    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }

    @Test
    public void testCrawlUserArticles() throws Exception{
//        User user = User.findByEmail("seanlionheart@gmail.com");
//        user.crawl();
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            feed.crawl();
        }
    }
    @Test
    public void testParsingFeed() throws Exception {
        @SuppressWarnings("unchecked")
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
                System.out.println("Parsing: " + feed.xmlUrl + " " + feed.htmlUrl);
                FeedParser.parseFeed(feed);
            } catch (Exception e) {
                System.out.print("Error: " + feed.xmlUrl);
            }
        }
    }

    @Test
    public void testParsingOneFeed() throws Exception {
        Feed feed = new Feed();
        feed.xmlUrl = "http://www.engadget.com/rss.xml";
        List<Article> articles = FeedParser.parseFeed(feed);
        for (Article article : articles) {
            article.create();
        }
    }
}
