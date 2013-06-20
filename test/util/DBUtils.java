package util;

import models.Article;
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
        ReaderDB.setUp();
    }
    
    @Test
    public void clearDatabase() {
        Datastore ds = ReaderDB.datastore;
        ds.delete(ds.createQuery(User.class));
        ds.delete(ds.createQuery(UserFeed.class));
        ds.delete(ds.createQuery(Feed.class));
        ds.delete(ds.createQuery(FeedCategory.class));
        ds.delete(ds.createQuery(Article.class));
    }
    
    @Test
    public void deleteArticle() {
        Datastore ds = ReaderDB.datastore;
        ds.delete(ds.createQuery(Article.class));
    }
}
