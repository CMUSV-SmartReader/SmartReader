package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

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

    public Date lastAccessedTime;
    
    @Reference(lazy = true)
    public List<Article> articles = new ArrayList<Article>();

    @Reference(lazy = true)
    public List<User> users = new ArrayList<User>();
    
    public static Feed find(String feedId) {
        return MorphiaObject.datastore.get(Feed.class, new ObjectId(feedId));
    }

    public static Feed findByXmlUrl(String xmlUrl) {
        return MorphiaObject.datastore.find(Feed.class).filter("xmlUrl", xmlUrl).get();
    }
    
    public List<Article> crawl() {
        lastAccessedTime = new Date();
        try {
            List<Article> articles = FeedParser.parseFeed(this);
            for (Article article : articles) {
                article.feed = this;
                article.create();
                this.articles.add(article);
            }
            this.update();

        } catch (Exception e) {
            System.out.println(e);
        }
        return this.articles;
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
