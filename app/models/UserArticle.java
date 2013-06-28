package models;

import org.bson.types.ObjectId;

import util.ReaderDB;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Entity
public class UserArticle extends MongoModel {

    @Id
    public ObjectId id;

    @Reference(lazy=true)
    public User user;

    @Reference(lazy=true)
    public Article article;

    public boolean isRead;

    public Integer rate;

    public static UserArticle getUserArticle(User user, Article article) {
        DBCollection collection = ReaderDB.getUserArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", user.id);
        query.put("ariticle.$id", article.id);
        DBObject userArticleDB = collection.findOne(query);
        if (userArticleDB != null) {
            return MongoModel.find(userArticleDB.get("_id").toString(), UserArticle.class);
        }
        else {
            return null;
        }
    }

}
