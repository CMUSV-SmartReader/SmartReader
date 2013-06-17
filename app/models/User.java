package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import play.api.libs.concurrent.Promise;
import play.libs.Akka;

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

import scala.concurrent.duration.Duration;

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

    public static void initUser(Identity identity) {
        final User newUser = new User();
        newUser.email = identity.email().get();
        if (identity.avatarUrl() != null) {
            newUser.avatarUrl = identity.avatarUrl().get();
        }
        if (identity.firstName() != null) {
            newUser.firstName = identity.firstName();
        }
        if (identity.lastName() != null) {
            newUser.lastName = identity.lastName();
        }
        if (identity.fullName() != null) {
            newUser.fullName = identity.fullName();
        }
        newUser.create();
        try {
            GoogleReaderImporter.oAuthImportFromGoogle(newUser, identity
                    .oAuth2Info().get().accessToken());
            Akka.system().scheduler().scheduleOnce(Duration.create(0, TimeUnit.SECONDS),
                    new Runnable() {
                        public void run() {
                            newUser.crawl();
                        }
                    }, Akka.system().dispatcher());
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<FeedCategory> allFeedCategoriesWithFeed() {
        DBCollection feedCategoryCollection = SmartReaderUtils.db
                .getCollection("FeedCategory");
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
        for (FeedCategory feedCategory : this.feedCategories) {
            this.articles.addAll(feedCategory.crawl());
        }
        this.update();
    }

    public void addUserCategory(FeedCategory feedCategory) {
        feedCategory.create();
    }

}
