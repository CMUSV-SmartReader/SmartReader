package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.Logger;
import util.FeedParser;
import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Entity
public class Feed extends MongoModel {

    @Id
    public ObjectId id;

    public String title;

    public String type;

    @Indexed
    public String xmlUrl;

    public String htmlUrl;

    public DateTime lastAccessedTime;
    
    @Reference(concreteClass=ArrayList.class, lazy = true)
    public List<Article> articles = new ArrayList<Article>();

    @Reference(lazy = true)
    public List<User> users = new ArrayList<User>();
    
    public static Feed find(String feedId) {
        return MorphiaObject.datastore.get(Feed.class, new ObjectId(feedId));
    }

    public static Feed findByXmlUrl(String xmlUrl) {
        return MorphiaObject.datastore.find(Feed.class).filter("xmlUrl", xmlUrl).get();
    }
    
    public List<Article> crawl() throws Exception {
        lastAccessedTime = DateTime.now();
        List<Article> articles = FeedParser.parseFeed(this);
        for (Article article : articles) {
            article.setReference(this.getClass(), this.id);
            article.create();
        }
        this.articles = articles;
        this.update();
        return articles;
    }
    
    public static void crawAll() {
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
                feed.crawl();
                Logger.debug("parse[" + feed.xmlUrl + "] success");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Logger.warn("parse [" + feed.xmlUrl + "] fail : "
                        + e.getMessage());
            }
        }
    }
    
    public static class Serializer implements JsonSerializer<Feed> {

        @Override
        public JsonElement serialize(Feed src, Type type,
                JsonSerializationContext ctx) {
            JsonObject feedObject = new JsonObject();
            feedObject.add("id", new JsonPrimitive(src.id.toString()));
            feedObject.add("title", new JsonPrimitive(src.title));
            feedObject.add("articles", ctx.serialize(src.articles));
            return feedObject;
        }
    }

}
