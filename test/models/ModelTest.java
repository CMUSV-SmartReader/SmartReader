package models;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import util.MorphiaObject;
import util.SmartReaderUtils;

import com.google.gson.Gson;

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

    @Test
    public void getUserCategoryTest() {
        long time = System.currentTimeMillis();
        User user = User.findByEmail("seanlionheart@gmail.com");
        Gson gson = SmartReaderUtils.builder.create();
        System.out.println(gson.toJson(user.allFeedCategories()));
        System.out.println(System.currentTimeMillis() - time);
    }

    @Test
    public void getFeed() {
        Feed feed = Feed.findWithArticle("51b78b94036496dfa64a507f");
        for (Article article : feed.articles) {
            System.out.println(article.title);
        }
    }
}
