package models;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import util.MorphiaObject;

public class ModelTest {

    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testList() {
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            System.out.println(feed.title);
        }
    }
    
    @Test
    public void testFeed() {
        System.out.println(Feed.find("51afde5a036491fe35589d77").title);
    }
    
    @Test
    public void testFeedArticle() {
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory feedCategory : userCategories) {
            for (UserFeed userFeed : feedCategory.userFeeds) {
                System.out.println(userFeed.feed.xmlUrl);
                for (Article article : userFeed.feed.articles) {
                    System.out.println(article.title);
                }
            }
        }
    }
    
    @Test
    public void getUserCategory() {
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory userCategory : userCategories) {
            for (UserFeed userFeed : userCategory.userFeeds) {
            }
        }
    }
}
