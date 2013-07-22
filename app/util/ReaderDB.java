package util;

import java.net.UnknownHostException;

import models.Article;
import models.Feed;
import models.FeedCategory;
import models.SNSProvider;
import models.UserFeed;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class ReaderDB {
    static public Mongo mongo;
    static public Morphia morphia;
    static public Datastore datastore;

    public static DB db;

    public static void setUp() {
        if (ReaderDB.mongo == null) {
            try {
                MongoClient client = new MongoClient(DBInfo.getDBHost(), DBInfo.getDBPort());
                ReaderDB.mongo = client;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ReaderDB.morphia = new Morphia();
            ReaderDB.datastore = ReaderDB.morphia.createDatastore(ReaderDB.mongo, DBInfo.getDBName());
            ReaderDB.datastore.ensureIndexes();
            ReaderDB.datastore.ensureCaps();
        }
    }

    static {
        try {
            MongoClient client = new MongoClient(DBInfo.getDBHost(), DBInfo.getDBPort());
            db = client.getDB(DBInfo.getDBName());
            SmartReaderUtils.builder.registerTypeAdapter(FeedCategory.class, new FeedCategory.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(UserFeed.class, new UserFeed.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(Feed.class, new Feed.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(Article.class, new Article.Serializer());
            SmartReaderUtils.builder.registerTypeAdapter(SNSProvider.class, new SNSProvider.Serializer());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static DBCollection getFeedCategoryCollection() {
        return db.getCollection("FeedCategory");
    }

    public static DBCollection getArticleCollection() {
        return db.getCollection("Article");
    }

    public static DBCollection getUserArticleCollection() {
        return db.getCollection("UserArticle");
    }

    public static DBCollection getUserCollection() {
        return db.getCollection("User");
    }

    public static DBCollection getUserFeedCollection() {
        return db.getCollection("UserFeed");
    }

    public static DBCollection getSNSProviderCollection() {
        return db.getCollection("SNSProvider");
    }

    private static class DBInfo {

        static String getDBHost() {
            String dbHost = System.getenv("READER_DB_HOST");
            return dbHost != null ? dbHost : "test.lydian.tw";
        }

        static int getDBPort() {
            String dbPort = System.getenv("READER_DB_PORT");
            return dbPort != null ? Integer.parseInt(dbPort) : 27017;
        }

        static String getDBName() {
            String dbName = System.getenv("READER_DB_NAME");
            return dbName != null ? dbName : "thermoreader-test";
        }
    }
}
