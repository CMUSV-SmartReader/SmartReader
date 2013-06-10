package models;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.morphia.annotations.Id;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Entity
public class Article extends MongoModel {

    @Id
    public ObjectId id;

    @Reference
    public Feed feed;

    public String title;
    
    public String desc;

    public String link;
    
    public Date publishDate;
    
    public Date updateDate;
    
    public String author;

    @SuppressWarnings("unchecked")
    public Article(SyndEntry entry) {
        this.title = entry.getTitle();
        this.link = entry.getLink();
        this.desc = entry.getDescription() != null ? entry.getDescription().getValue() : null;
        this.publishDate = entry.getPublishedDate();
        this.updateDate = entry.getUpdatedDate();
        this.author = entry.getAuthor();
    }

    public Article() {
    }

    public static List<Article> findByFeed(Feed feed) {
        return (List<Article>) MorphiaObject.datastore.find(Article.class)
                .filter("refereceId", feed.id).get();
    }

    public static class Serializer implements JsonSerializer<Article> {

        @Override
        public JsonElement serialize(Article src, Type type,
                JsonSerializationContext ctx) {
            JsonObject article = new JsonObject();
            article.add("id", new JsonPrimitive(src.id.toString()));
            article.add("title", new JsonPrimitive(src.title));
            article.add("desc", new JsonPrimitive(src.desc));
            article.add("link", new JsonPrimitive(src.link));
            article.add("publishDate", ctx.serialize(src.publishDate));
            article.add("updateDate", ctx.serialize(src.updateDate));
            article.add("author", new JsonPrimitive(src.author));
            return article;
        }
    }
}
