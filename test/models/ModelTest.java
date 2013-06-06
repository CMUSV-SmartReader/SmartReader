package models;

import java.util.List;

import org.junit.Test;

import util.MorphiaObject;

public class ModelTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testList() {
        MorphiaObject.setUp();
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            System.out.println(feed.title);
        }
    }
    
    @Test
    public void testFeed() {
        MorphiaObject.setUp();
        System.out.println(Feed.find("51afde5a036491fe35589d77").title);
    }
    
    @Test
    public void getUserCategory() {
        MorphiaObject.setUp();
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory userCategory : userCategories) {
            System.out.println(userCategory.name);
            for (UserFeed userFeed : userCategory.userFeeds) {
                System.out.println("--" + userFeed.feed.title);
            }
        }
    }
}
