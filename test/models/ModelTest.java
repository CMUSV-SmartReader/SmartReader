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
    public void getUserCategory() {
        MorphiaObject.setUp();
        User user = User.findByEmail("seanlionheart@gmail.com");
        List<FeedCategory> userCategories = user.feedCategories;
        for (FeedCategory userCategory : userCategories) {
            System.out.println(userCategory.name);
            for (Feed feed : userCategory.feeds) {
                System.out.println("--" + feed.title);
            }
        }
    }
}
