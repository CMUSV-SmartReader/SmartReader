package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
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
public class ArticleCategory extends MongoModel {

    @Id
    public ObjectId id;

    public String name;

    public String imgLink;

    @Reference(lazy=true)
    public User user;

    public ArticleCategory(DBObject db) {
        id = new ObjectId(db.get("_id").toString());
        name = db.get("name").toString();
        if (db.get("imgLink") != null) {
            imgLink = db.get("imgLink").toString();
        }
    }

    public ArticleCategory() {

    }

    public static class Serializer implements JsonSerializer<ArticleCategory> {

        @Override
        public JsonElement serialize(ArticleCategory src, Type type,
                JsonSerializationContext ctx) {
            JsonObject articleCategoryObj = new JsonObject();
            articleCategoryObj.add("id", new JsonPrimitive(src.id.toString()));
            articleCategoryObj.add("name", new JsonPrimitive(src.name));
            if (src.imgLink != null) {
                articleCategoryObj.add("imgLink", new JsonPrimitive(src.imgLink));
            }
            return articleCategoryObj;
        }
    }

    public static List<Article> articlesInCategory(String id) {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("feed.$id", new ObjectId(id));
        BasicDBObject orderBy = new BasicDBObject();
        DBCursor cursor = articleCollection.find(query).sort(orderBy).limit(20);
        User currentUser = SmartReaderUtils.getCurrentUser();
        while (cursor.hasNext()) {
            Article article = new Article(cursor.next());
            article.loadIsRead(currentUser);
            articles.add(article);
        }
        return articles;
    }

    public static List<Article> articlesInCategory(String id, Date publishedBefore) {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put(".$id", new ObjectId(id));
        query.put("publishDate", new BasicDBObject("$lt", publishedBefore));
        BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("publishDate", -1);
        orderBy.put("updateDate", -1);
        DBCursor cursor = articleCollection.find(query).sort(orderBy).limit(20);
        User currentUser = SmartReaderUtils.getCurrentUser();
        while (cursor.hasNext()) {
            Article article = new Article(cursor.next());
            article.loadIsRead(currentUser);
            articles.add(article);
        }
        return articles;
    }
}
