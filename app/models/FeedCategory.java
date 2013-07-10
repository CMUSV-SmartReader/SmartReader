package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import util.ReaderDB;

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

    public int order;

    @Reference(lazy=true)
    public User user;

    @Reference(concreteClass = ArrayList.class, lazy=true)
    public List<UserFeed> userFeeds = new ArrayList<UserFeed>();

    public List<String> userFeedsTitles = new ArrayList<String>();

    public List<String> userFeedsIds = new ArrayList<String>();

    public List<Map<String, String>> userFeedsInfos = new ArrayList<Map<String,String>>();

    public List<Article> crawl() {
        for(UserFeed userFeed: this.userFeeds){
            this.articles.addAll(userFeed.feed.crawl());
        }
        return this.articles;
    }

    public UserFeed createFeed(User user, Feed feed) {
        feed.createUnique();
        UserFeed userFeed = new UserFeed();
        userFeed.feed = feed;
        userFeed.user = user;
        userFeed.create();
        this.addUserFeed(userFeed);
        return userFeed;
    }

    public void addUserFeed(UserFeed userFeed) {
        this.userFeeds.add(userFeed);
        this.userFeedsIds.add(userFeed.feed.id.toString());
        this.userFeedsTitles.add(userFeed.feed.title);
        this.update();
    }

    public void deleteUserFeed(UserFeed userFeed) {
        int index = -1;
        for (int i = 0; i < this.userFeeds.size(); i++) {
            if (this.userFeeds.get(i).id.equals(userFeed.id)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            this.userFeeds.remove(index);
            this.userFeedsIds.remove(index);
            this.userFeedsTitles.remove(index);
        }
        this.update();
        userFeed.delete();
    }

    @Override
    public void delete() {
        super.delete();
    }

    @Reference(concreteClass = ArrayList.class, lazy=true)
    public List<Article> articles = new ArrayList<Article>();

    public static FeedCategory find(String categoryId) {
        return ReaderDB.datastore.get(FeedCategory.class, categoryId);
    }

    public static FeedCategory createFeedCategory(DBObject feedCategoryDB) {
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.id = new ObjectId(feedCategoryDB.get("_id").toString());
        feedCategory.name = feedCategoryDB.get("name").toString();
        BasicDBList userFeedList = (BasicDBList) feedCategoryDB.get("userFeeds");
        BasicDBList userFeedIdList = (BasicDBList) feedCategoryDB.get("userFeedsIds");
        BasicDBList userFeedTitleList = (BasicDBList) feedCategoryDB.get("userFeedsTitles");
        if (userFeedTitleList != null && userFeedIdList != null) {
            for (int i = 0; i < userFeedIdList.size(); i++) {
                HashMap<String, String> info = new HashMap<String, String>();
                DBRef userFeedRef = (DBRef) userFeedList.get(i);
                info.put("feedId", userFeedIdList.get(i).toString());
                info.put("feedTitle", userFeedTitleList.get(i).toString());
                info.put("userFeedId", userFeedRef.getId().toString());
                feedCategory.userFeedsInfos.add(info);
            }
        }
        return feedCategory;
    }

    public static class Serializer implements JsonSerializer<FeedCategory> {

        @Override
        public JsonElement serialize(FeedCategory src, Type type,
                JsonSerializationContext ctx) {
            JsonObject feedObject = new JsonObject();
            feedObject.add("id", new JsonPrimitive(src.id.toString()));
            feedObject.add("name", new JsonPrimitive(src.name));
            feedObject.add("userFeedsInfos", ctx.serialize(src.userFeedsInfos));
            return feedObject;
        }
    }

}
