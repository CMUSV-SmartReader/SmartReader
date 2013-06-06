package models;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
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

    public Class<?> reference;

    public ObjectId referenceId;

    public Map<String, Object> data;

    @SuppressWarnings("unchecked")
    public Article(SyndEntry entry) {
        ObjectMapper m = new ObjectMapper();
        this.data = m.convertValue(entry, Map.class);
    }

    public Article() {
    }

    public static List<Article> findByFeed(Feed feed) {
        return (List<Article>) MorphiaObject.datastore.find(Article.class)
                .filter("refereceId", feed.id).get();
    }

    public void setReference(Class<?> reference, ObjectId referenceId) {
        this.reference = reference;
        this.referenceId = referenceId;
    }

    public static class Serializer implements JsonSerializer<Article> {

        @Override
        public JsonElement serialize(Article src, Type type,
                JsonSerializationContext ctx) {
            JsonObject article = new JsonObject();
            article.add("id", new JsonPrimitive(src.id.toString()));
            article.add("data", ctx.serialize(src.data));
            return article;
        }
    }
}
