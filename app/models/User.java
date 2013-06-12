package models;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.libs.Scala;
import scala.Option;
import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import securesocial.core.UserId;
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
public class User extends MongoModel implements Identity {

    @Id
    public ObjectId id;

    public String lastName;

    public String userName;

    public String displayName;

    public String avatarUrl;

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

    @Override
    public UserId id() {
        return null;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public String firstName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String fullName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option<String> avatarUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option<String> email() {
        return Scala.Option(email);
    }

    @Override
    public AuthenticationMethod authMethod() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option<OAuth1Info> oAuth1Info() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option<OAuth2Info> oAuth2Info() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Option<PasswordInfo> passwordInfo() {
        // TODO Auto-generated method stub
        return null;
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

    public void addUserCategory(FeedCategory feedCategory) {
        feedCategory.create();
    }

}
