package models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import play.libs.Scala;

import scala.Option;
import securesocial.core.AuthenticationMethod;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import securesocial.core.Identity;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import securesocial.core.UserId;
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
public class User extends MongoModel implements Identity {

    @Id
    public ObjectId id;

    public String lastName;

    public String userName;

    public String displayName;

    public String avatarUrl;

    public String firstName;

    public String fullName;
    
    public AuthenticationMethod authMethod;
    
    public String providerId;
    
    public OAuth1Info oAuth1Info;
    
    public OAuth2Info oAuth2Info;
    
    public PasswordInfo passwordInfo;

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
        if (identity.id().providerId() != null) {
            newUser.providerId = identity.id().providerId();
        }
        if (identity.authMethod() != null) {
            newUser.authMethod = identity.authMethod();
        }
        newUser.oAuth1Info = identity.oAuth1Info().getOrElse(null);
        newUser.oAuth2Info = identity.oAuth2Info().getOrElse(null);
        newUser.passwordInfo = identity.passwordInfo().getOrElse(null);
        
        newUser.create();
        try {
            GoogleReaderImporter.oAuthImportFromGoogle(newUser, identity
                    .oAuth2Info().get().accessToken());
            Akka.system().scheduler().scheduleOnce(Duration.create(0, TimeUnit.SECONDS),
                    new Runnable() {
                        @Override
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

    @Override
    public AuthenticationMethod authMethod() {
        return authMethod;
    }

    @Override
    public Option<String> avatarUrl() {
        return Scala.Option(avatarUrl);
    }

    @Override
    public Option<String> email() {
        return Scala.Option(email);
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String fullName() {
        return fullName;
    }

    @Override
    public UserId id() {
        // SecureSocial source code for UserId constructor: 
        // case class UserId(id: String, providerId: String)
        return new UserId(id.toString(), providerId);
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public Option<OAuth1Info> oAuth1Info() {
        if(oAuth1Info == null) {
            return scala.Option.apply(null);
        }
        return Scala.Option(oAuth1Info);
    }

    @Override
    public Option<OAuth2Info> oAuth2Info() {
        if(oAuth2Info == null) {
            return scala.Option.apply(null);
        }
        return Scala.Option(oAuth2Info);
    }

    @Override
    public Option<PasswordInfo> passwordInfo() {
        if(passwordInfo == null) {
            return scala.Option.apply(null);
        }
        return Scala.Option(passwordInfo);
    }

}
