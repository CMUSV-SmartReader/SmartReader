package models;

import java.util.List;

import org.junit.Test;

import util.MorphiaObject;

public class ModelTest {

    @Test
    public void testList() {
        MorphiaObject.setUp();
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            System.out.println(feed.title);
        }
    }
}
