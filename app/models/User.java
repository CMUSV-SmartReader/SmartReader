package models;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import securesocial.core.Identity;
import util.GoogleReaderImporter;
import util.SmartReaderUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Entity
public class User extends MongoModel {

    @Id
    public ObjectId id;

    public String lastName;

    public String userName;

    public String displayName;

    public String avatarUrl;

    public String firstName;

    public String fullName;

    @Indexed
    public String email;

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<FeedCategory> feedCategories = new ArrayList<FeedCategory>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<Feed> feeds = new ArrayList<Feed>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<Article> articles = new ArrayList<Article>();

    public static User findByEmail(String email) {
        DBCollection userCollection = SmartReaderUtils.db.getCollection("User");
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        DBObject userDB = userCollection.findOne(query);
        if (userDB != null) {
            return User.createUser(userDB);
        }
        return null;
    }

    public User(Identity identity) {
        this.email = identity.email().get();
        if (identity.avatarUrl() != null) {
            this.avatarUrl = identity.avatarUrl().get();
        }
        if (identity.firstName() != null) {
            this.firstName = identity.firstName();
        }
        if (identity.lastName() != null) {
            this.lastName = identity.lastName();
        }
        if (identity.fullName() != null) {
            this.fullName = identity.fullName();
        }
        try {
            GoogleReaderImporter.oAuthImportFromGoogle(this, identity.oAuth2Info().get().accessToken());
        }
        catch (Exception e) {

        }
    }

    public static User getUserFromIdentity(Identity userId) {
        if (userId != null && userId.email() != null) {
            return User.findByEmail(userId.email().get());
        }
        return null;
    }

    public static User createUser(DBObject userDB) {
        User user = new User();
        user.id = new ObjectId(userDB.get("_id").toString());
        user.email = userDB.get("email").toString();
        return user;
    }

    public User() {
    }

    public List<FeedCategory> allFeedCategories() {
        DBCollection feedCategoryCollection = SmartReaderUtils.db.getCollection("FeedCategory");
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", new ObjectId(this.id.toString()));
        DBCursor cursor = feedCategoryCollection.find(query);
        List<FeedCategory> feedCategories = new ArrayList<FeedCategory>();
        while (cursor.hasNext()) {
            feedCategories.add(FeedCategory.createFeedCategory(cursor.next()));
        }
        return feedCategories;
    }

    public void crawl() {
        for(FeedCategory feedCategory: this.feedCategories){
            this.articles.addAll(feedCategory.crawl());
        }
        this.update();
    }

}
