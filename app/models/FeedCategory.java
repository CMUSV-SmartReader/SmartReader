package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import util.MorphiaObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

@Entity
public class FeedCategory extends MongoModel {

    @Id
    public ObjectId id;

    public String name;

    @Reference(lazy=true)
    public User user;

    @Reference(concreteClass = ArrayList.class, lazy=true)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();

    public List<String> userFeedsTitles = new ArrayList<String>();

    public List<Map<String, String>> userFeedsInfos = new ArrayList<Map<String,String>>();

    public List<Article> crawl() {
        for(UserFeed userFeed: this.userFeeds){
            this.articles.addAll(userFeed.feed.crawl());
        }
        return this.articles;
    }

    public void createFeed(User user, Feed feed) {
        feed.createUnique();
        UserFeed userFeed = new UserFeed();
        userFeed.feed = feed;
        userFeed.user = user;
        userFeed.create();
        this.userFeeds.add(userFeed);
        this.userFeedsTitles.add(feed.title);
        this.update();
    }

    @Reference(concreteClass = ArrayList.class, lazy=true)
    public List<Article> articles = new ArrayList<Article>();

    public static FeedCategory find(String categoryId) {
        return MorphiaObject.datastore.get(FeedCategory.class, categoryId);
    }

    public static FeedCategory createFeedCategory(DBObject feedCategoryDB) {
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.name = feedCategoryDB.get("name").toString();
        BasicDBList userFeedList = (BasicDBList) feedCategoryDB.get("userFeeds");
        BasicDBList userFeedTitleList = (BasicDBList) feedCategoryDB.get("userFeedsTitles");
        for (int i = 0; i < userFeedList.size(); i++) {
            HashMap<String, String> info = new HashMap<String, String>();
            DBRef ref = (DBRef) userFeedList.get(i);
            info.put(ref.getId().toString(), userFeedTitleList.get(i).toString());
            feedCategory.userFeedsInfos.add(info);
        }
        return feedCategory;
    }

    public static class Serializer implements JsonSerializer<FeedCategory> {

        @Override
        public JsonElement serialize(FeedCategory src, Type type,
                JsonSerializationContext ctx) {
            JsonObject feedObject = new JsonObject();
            feedObject.add("name", new JsonPrimitive(src.name));
            feedObject.add("userFeedsInfos", ctx.serialize(src.userFeedsInfos));
            return feedObject;
        }
    }

}
