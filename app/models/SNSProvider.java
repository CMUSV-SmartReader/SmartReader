package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

import util.ReaderDB;
import util.SmartReaderUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Entity
public class SNSProvider extends MongoModel {

    @Id
    public ObjectId id;

    public String provider;

    @Reference
    public User user;

    @Reference
    public List<Article> articles = new ArrayList<Article>();

    public SNSProvider() {

    }

    public SNSProvider(DBObject providerDB) {
        id = new ObjectId(providerDB.get("_id").toString());
        provider = providerDB.get("popularity").toString();
    }

    public static SNSProvider existingProvider(HashMap<String, Object>condition) {
        DBCollection collection = ReaderDB.getSNSProviderCollection();
        BasicDBObject query = new BasicDBObject();
        query.putAll(condition);
        DBObject entityDB = collection.findOne(query);
        return entityDB != null ? new SNSProvider(entityDB) : null;
    }

    public static class Serializer implements JsonSerializer<SNSProvider> {
        @Override
        public JsonElement serialize(SNSProvider src, Type type,
                JsonSerializationContext ctx) {
            JsonObject providerObj = new JsonObject();
            providerObj.add("id", new JsonPrimitive(src.id.toString()));
            providerObj.add("provider", new JsonPrimitive(src.provider));
            return providerObj;
        }
    }

    public static SNSProvider findTwitterProvider(User user) {
        DBCollection collection = ReaderDB.getSNSProviderCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", user.id);
        query.put("provider", "twitter");
        DBObject entityDB = collection.findOne(query);
        if (entityDB != null) {
            return MongoModel.findEntity(entityDB.get("_id").toString(), SNSProvider.class);
        }
        else {
            return null;
        }
    }

    public List<Article> articles() {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("provider.$id", id);
        BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("publishDate", -1);
        orderBy.put("updateDate", -1);
        DBCursor cursor = articleCollection.find(query).sort(orderBy).limit(20);
        while (cursor.hasNext()) {
            Article article = new Article(cursor.next());
            article.loadIsRead(SmartReaderUtils.getCurrentUser());
            articles.add(article);
        }
        return articles;
    }

    public List<Article> articles(Date publishedBefore) {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("provider.$id", id);
        query.put("publishDate", new BasicDBObject("$lt", publishedBefore));
        BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("publishDate", -1);
        orderBy.put("updateDate", -1);
        DBCursor cursor = articleCollection.find(query).sort(orderBy).limit(20);
        while (cursor.hasNext()) {
            Article article = new Article(cursor.next());
            article.loadIsRead(SmartReaderUtils.getCurrentUser());
            articles.add(article);
        }
        return articles;
    }
}
