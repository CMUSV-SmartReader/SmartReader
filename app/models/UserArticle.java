package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

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
        return null;
//        DBCollection collection = ReaderDB.getFeedCategoryCollection();
//        BasicDBObject query = new BasicDBObject();
//        query.put("user.$id", this.id);
//        query.put("name", "Uncategorized");
//        DBObject feedCategoryDB = collection.findOne(query);
    }

}
