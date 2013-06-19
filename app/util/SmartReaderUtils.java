package util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Article;
import models.Feed;
import models.FeedCategory;
import models.User;
import models.UserFeed;

import org.apache.commons.beanutils.BeanMap;

import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import com.google.gson.GsonBuilder;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class SmartReaderUtils {

    @SuppressWarnings("rawtypes")
    public List<Map> convertListObjs(List objs) {
        List<Map> results = new ArrayList<Map>();
        for (Object obj : objs) {
            results.add(new BeanMap(obj));
        }
        return results;
    }

    public static User getCurrentUser() {
        Identity identity = SecureSocial.currentUser();
        return User.findByEmail(identity.email().get());
    }

    public static GsonBuilder builder = new GsonBuilder();

    public static DB db;

    static {
        try {
            MongoClient client = new MongoClient("ec2.lydian.tw", 27017);
            db = client.getDB("thermoreader");

            SmartReaderUtils.builder.registerTypeAdapter(FeedCategory.class, new FeedCategory.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(UserFeed.class, new UserFeed.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(Feed.class, new Feed.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(Article.class, new Article.Serializer());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
