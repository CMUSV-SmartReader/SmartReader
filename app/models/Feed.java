package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;
import util.FeedParser;
import util.ReaderDB;
import util.SmartReaderUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.syndication.feed.synd.SyndFeed;

@Entity
public class Feed extends MongoModel {

    @Id
    public ObjectId id;

    public String title;

    public String type;

    @Indexed
    public String xmlUrl;

    public String htmlUrl;

    public Date lastAccessedTime;

    public boolean hasError;

    public String errorReason;

    @Transient
    public List<Article> articles = new ArrayList<Article>();

    @Reference(lazy = true)
    public List<User> users = new ArrayList<User>();

    public Feed(DBObject feedDB) {
        id = new ObjectId(feedDB.get("_id").toString());
        title = feedDB.get("title").toString();
    }

    public Feed() {

    }

    public Feed(SyndFeed syndFeed) {
        this.title = syndFeed.getTitle();
        this.xmlUrl = syndFeed.getLink();
    }

    public Feed createUnique() {
        Feed feedEntity = Feed.findByXmlUrl(this.xmlUrl);
        if (feedEntity == null) {
            this.create();
            return this;
        }
        else {
            return feedEntity;
        }
    }

    public void addUser(User user) {
        this.users.add(user);
        this.update();
    }

    public static List<Article> articlesInFeed(String id) {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("feed.$id", new ObjectId(id));
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

    public static List<Article> articlesInFeed(String id, Date publishedBefore) {
        List<Article> articles = new ArrayList<Article>();
        DBCollection articleCollection = ReaderDB.getArticleCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("feed.$id", new ObjectId(id));
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

    public static Feed createFeed(DBObject feedDb) {
        Feed feed = new Feed();
        feed.id = new ObjectId(feedDb.get("_id").toString());
        feed.title = feedDb.get("title").toString();
        return feed;
    }

    public static Feed findByXmlUrl(String xmlUrl) {
        return ReaderDB.datastore.find(Feed.class).filter("xmlUrl", xmlUrl).get();
    }

    public void crawl() {
        lastAccessedTime = new Date();
        try {
            List<Article> articles = FeedParser.parseFeed(this);
            for (Article article : articles) {
                article.feed = this;
                article = article.createUnique();
            }
            this.hasError = false;
            this.errorReason = "";
            this.update();
            if (articles.size() > 0) {
                List<UserFeed> userFeeds = this.userFeeds();
                for (UserFeed userFeed : userFeeds) {
                    userFeed.hasUpdate = true;
                    userFeed.update();
                }
            }
        } catch (Exception e) {
            this.hasError = true;
            this.errorReason = e.getMessage();
            this.update();
            throw new RuntimeException(e);
        }
    }

    public static void crawlAll() {
        @SuppressWarnings("unchecked")
        List<Feed> feeds = (List<Feed>) MongoModel.all(Feed.class);
        for (Feed feed : feeds) {
            try {
                feed.crawl();
                Logger.info("parse[" + feed.xmlUrl + "] success");
            } catch (Exception e) {
                Logger.warn("parse [" + feed.xmlUrl + "] fail : "
                        + e.getMessage());
            }
        }
    }

    public List<UserFeed> userFeeds() {
        List<UserFeed> userFeeds = new ArrayList<UserFeed>();
        DBCollection userFeedsCollection = ReaderDB.getUserFeedCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("feed.$id", id);
        DBCursor cursor = userFeedsCollection.find(query);
        while (cursor.hasNext()) {
            userFeeds.add(MongoModel.findEntity(cursor.next().get("_id").toString(), UserFeed.class));
        }
        return userFeeds;
    }

    public static class Serializer implements JsonSerializer<Feed> {
        @Override
        public JsonElement serialize(Feed src, Type type,
                JsonSerializationContext ctx) {
            JsonObject feedObject = new JsonObject();
            feedObject.add("id", new JsonPrimitive(src.id.toString()));
            feedObject.add("title", new JsonPrimitive(src.title));
            feedObject.add("articles", ctx.serialize(src.articles));
            return feedObject;
        }
    }
}
