package util;

import models.Feed;
import models.FeedCategory;
import models.User;
import models.UserFeed;

import org.junit.Before;
import org.junit.Test;

import com.google.code.morphia.Datastore;

public class DBUtils {

    @Before
    public void setUp() {
        MorphiaObject.setUp();
    }
    
    @Test
    public void clearDatabase() {
        Datastore ds = MorphiaObject.datastore;
        ds.delete(ds.createQuery(User.class));
        ds.delete(ds.createQuery(UserFeed.class));
        ds.delete(ds.createQuery(Feed.class));
        ds.delete(ds.createQuery(FeedCategory.class));
    }
}
