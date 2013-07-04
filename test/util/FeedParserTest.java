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
        ReaderDB.setUp();
    }

    @Test
    public void testCrawlUserArticles() throws Exception{
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
                feed.crawl();
            }
            catch (Exception e) {

            }
        }
    }

    @Test
    public void testParsingFeed() throws Exception {
        @SuppressWarnings("unchecked")
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
            } catch (Exception e) {
                System.out.println("Error: " + feed.htmlUrl + " " + feed.xmlUrl);
            }
        }
    }

    @Test
    public void testParsingOneFeed() throws Exception {
        Feed feed = new Feed();
        feed.xmlUrl = "http://feedpress.me/finertech/pc";
        List<Article> articles = FeedParser.parseFeed(feed);
        for (Article article : articles) {
            System.out.println(article.link);
        }
    }
}
