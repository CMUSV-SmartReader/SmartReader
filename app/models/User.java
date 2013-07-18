package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import play.libs.Akka;
import play.libs.Scala;
import scala.Option;
import scala.concurrent.duration.Duration;
import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import securesocial.core.UserId;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import util.GoogleReaderImporter;
import util.ReaderDB;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

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

    @Transient
    public AuthenticationMethod authMethod;

    public String providerId;

    @Transient
    public OAuth1Info oAuth1Info;

    @Transient
    public OAuth2Info oAuth2Info;

    public PasswordInfo passwordInfo;

    public String twitterAccessToken;

    public String twitterAccessTokenSecret;

    @Indexed
    public String email;

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<FeedCategory> feedCategories = new ArrayList<FeedCategory>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<Feed> feeds = new ArrayList<Feed>();

    @Reference(concreteClass = ArrayList.class, lazy = true)
    public List<Article> articles = new ArrayList<Article>();

    public static User findByEmail(String email) {
        DBCollection userCollection = ReaderDB.db.getCollection("User");
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        DBObject userDB = userCollection.findOne(query);
        if (userDB != null) {
            return MongoModel.findEntity(userDB.get("_id").toString(), User.class);
        }
        return null;
    }

    public static void initUser(Identity identity) {
        final User newUser = new User();
        newUser.email = identity.email().get();
        if (identity.avatarUrl().nonEmpty()) {
            newUser.avatarUrl = identity.avatarUrl().get();
        }
        newUser.firstName = identity.firstName();
        newUser.lastName = identity.lastName();
        newUser.fullName = identity.fullName();
        if (identity.id().providerId() != null) {
            newUser.providerId = identity.id().providerId();
        }
        if (identity.authMethod() != null) {
            newUser.authMethod = identity.authMethod();
        }
        if (identity.oAuth1Info().nonEmpty()) {
            newUser.oAuth1Info = identity.oAuth1Info().getOrElse(null);
        }
        if (identity.oAuth2Info().nonEmpty()) {
            newUser.oAuth2Info = identity.oAuth2Info().getOrElse(null);
        }
        if (identity.passwordInfo().nonEmpty()) {
            newUser.passwordInfo = identity.passwordInfo().getOrElse(null);
        }

        newUser.create();
        newUser.addDefaultCategory();
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
        DBCollection feedCategoryCollection = ReaderDB.db
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
        feedCategory.user = this;
        feedCategory.create();
    }

    public void addDefaultCategory() {
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.name = "Uncategorized";
        feedCategory.user = this;
        this.addUserCategory(feedCategory);
    }

    public FeedCategory findDefaultCategory() {
        DBCollection collection = ReaderDB.getFeedCategoryCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", this.id);
        query.put("name", "Uncategorized");
        DBObject feedCategoryDB = collection.findOne(query);
        return FeedCategory.createFeedCategory(feedCategoryDB);
    }

    public void read(Article article) {
        UserArticle userArticle = UserArticle.getUserArticle(this, article);
        if (userArticle == null) {
            userArticle = new UserArticle();
            userArticle.article = article;
            userArticle.user = this;
            userArticle.create();
        }
        userArticle.isRead = true;
        userArticle.update();
        article.increasePopularity();
    }

    public void unread(Article article) {
        UserArticle userArticle = UserArticle.getUserArticle(this, article);
        if (userArticle == null) {
            userArticle = new UserArticle();
            userArticle.article = article;
            userArticle.user = this;
            userArticle.create();
        }
        userArticle.isRead = false;
        userArticle.update();
    }

    public void updateAccessToken(String token, String secret) {
        this.twitterAccessToken = token;
        this.twitterAccessTokenSecret = secret;
        this.update();
    }

    public Twitter getTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true);
        builder.setOAuthConsumerKey("BploO8qFE4tWwmdNNsE1g");
        builder.setOAuthConsumerSecret("lpcBV30Wrr1dN1RO9ehxYDhfrGUkJjqa7V0idWKcoM");
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        Twitter twitter = factory.getInstance();
        System.out.println(twitterAccessToken);
        System.out.println(twitterAccessTokenSecret);
        AccessToken token = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);
        twitter.setOAuthAccessToken(token);
        return twitter;
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

    public List<Article> userArticles() {
        List<Article> articles = new ArrayList<Article>();
        List<FeedCategory> feedCategories = this.allFeedCategoriesWithFeed();
        for (FeedCategory feedCategory : feedCategories) {
            feedCategory = FeedCategory.findEntity(feedCategory.id.toString(), FeedCategory.class);
            for (String feedId : feedCategory.userFeedsIds) {
                DBCollection collection = ReaderDB.getArticleCollection();
                BasicDBObject query = new BasicDBObject();
                query.put("feed.$id", new ObjectId(feedId));
                DBCursor articleCursor = collection.find(query);
                while (articleCursor.hasNext() && articles.size() <= 12) {
                    articles.add(new Article(articleCursor.next()));
                }
                if (articles.size() >= 12) {
                    return articles;
                }
            }
        }
        return articles;
    }

    public List<Article> recommends() {
        List<Article> recommends = new ArrayList<Article>();
        DBCollection userCollection = ReaderDB.getUserCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(this.id.toString()));
        DBObject userDB = userCollection.findOne(query);
        BasicDBList recommendsDB = (BasicDBList) userDB.get("recommends");
        if (recommendsDB != null) {
            for (int i = 0; i < recommendsDB.size(); i++) {
                DBObject articleDB = (DBObject) recommendsDB.get(i);
                DBRef articleRef = (DBRef) articleDB.get("article");
                recommends.add(new Article(articleRef.fetch()));
            }
        }
        return recommends;
    }

    public void crawlTwitter() {
        SNSProvider twitterProvider = SNSProvider.findTwitterProvider(this);
        if (twitterAccessToken != null && twitterAccessTokenSecret != null && twitterProvider != null) {
            Twitter twitter = this.getTwitter();
            try {
                ResponseList<Status> statusList = twitter.getHomeTimeline();
                for (Status status : statusList) {
                    Article article = new Article(status);
                    article.createTwitterArticle();
                    article.provider = twitterProvider;
                    article.update();
                    twitterProvider.articles.add(article);
                }
                twitterProvider.update();
            }
            catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTwitterProvider() {
        if (!existTwitterProvider()) {
            SNSProvider provider = new SNSProvider();
            provider.user = this;
            provider.provider = "twitter";
            provider.create();
        }
    }

    public boolean existTwitterProvider() {
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("provider", "twitter");
        condition.put("user.$id", this.id);
        return SNSProvider.existingProvider(condition) != null;
    }

    public List<UserFeed> userFeeds() {
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", new ObjectId(this.id.toString()));
        DBCollection userFeedCollection = ReaderDB.getUserFeedCollection();
        DBCursor cursor = userFeedCollection.find(query);
        List<UserFeed> userFeeds = new ArrayList<UserFeed>();
        while (cursor.hasNext()) {
            userFeeds.add(new UserFeed(cursor.next()));
        }
        return userFeeds;
    }

    public List<SNSProvider> providers() {
        BasicDBObject query = new BasicDBObject();
        query.put("user.$id", new ObjectId(this.id.toString()));
        DBCollection providerCollection = ReaderDB.getSNSProviderCollection();
        DBCursor cursor = providerCollection.find(query);
        List<SNSProvider> providers = new ArrayList<SNSProvider>();
        while (cursor.hasNext()) {
            providers.add(new SNSProvider(cursor.next()));
        }
        return providers;
    }

    public static void crawlSocialNetwork() {
        @SuppressWarnings("unchecked")
        List<User> allUsers = (List<User>) MongoModel.all(User.class);
        for (User user : allUsers) {
            user.crawlTwitter();
        }
    }

}
