package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import scala.util.Random;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.DBObject;
import com.sun.syndication.feed.synd.SyndEntry;

@Entity
public class Article extends MongoModel {

    @Id
    public ObjectId id;

    @Reference
    public Feed feed;

    public String title;

    public String desc;

    public String summary;

    public String link;

    public Date publishDate;

    public Date updateDate;

    public String author;

    public boolean isRead;

    public int popularity;

    @Reference
    public List<Article> dups = new ArrayList<Article>();

    public Article(SyndEntry entry) {
        Random rand = new Random();
        this.title = entry.getTitle();
        this.link = entry.getLink();
        this.desc = entry.getDescription() != null ? entry.getDescription().getValue() : null;
        this.summary = this.desc;
        this.desc += " " + this.desc + " " + this.desc;
        this.isRead = rand.nextBoolean();
        this.popularity = rand.nextInt(5) + 1;
        this.publishDate = entry.getPublishedDate();
        this.updateDate = entry.getUpdatedDate();
        this.author = entry.getAuthor();
    }

    public Article() {

    }

    public Article(DBObject articleDB) {
        this.title = articleDB.get("title").toString();
        this.link = articleDB.get("link").toString();
        this.desc = articleDB.get("desc").toString();
        this.author = articleDB.get("author").toString();
        this.publishDate = (Date) articleDB.get("publishDate");
        this.updateDate = (Date) articleDB.get("updateDate");
    }

    public static class Serializer implements JsonSerializer<Article> {
        @Override
        public JsonElement serialize(Article src, Type type,
                JsonSerializationContext ctx) {
            JsonObject article = new JsonObject();
            if (src.id != null) {
                article.add("id", new JsonPrimitive(src.id.toString()));
            }
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
