package models;


import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

@Entity
public class UserFeed extends MongoModel {

    @Id
    public ObjectId id;

    public int order;

    public int popularity;

    @Reference(lazy = true)
    public User user;

    @Reference(lazy = true)
    public Feed feed;

    public static UserFeed createUserFeed(DBObject userFeedDB) {
        UserFeed userFeed = new UserFeed();
        userFeed.feed = Feed.createFeed(((DBRef) userFeedDB.get("feed")).fetch());
        return userFeed;
    }

    public UserFeed() {

    }

    public UserFeed(DBObject userFeedDB) {
        id = new ObjectId(userFeedDB.get("_id").toString());
        order = Integer.parseInt(userFeedDB.get("order").toString());
        popularity = Integer.parseInt(userFeedDB.get("popularity").toString());
    }

    public void increasePopularity() {
        this.popularity++;
        this.update();
    }

    public static class Serializer implements JsonSerializer<UserFeed> {

        @Override
        public JsonElement serialize(UserFeed src, Type type,
                JsonSerializationContext ctx) {
            JsonObject userFeedObj = new JsonObject();
            userFeedObj.add("id", new JsonPrimitive(src.id.toString()));
            userFeedObj.add("name", new JsonPrimitive(src.feed.title));
            userFeedObj.add("order", new JsonPrimitive(src.order));
            userFeedObj.add("popularity", new JsonPrimitive(src.popularity));
            userFeedObj.add("feed", ctx.serialize(src.feed));
            return userFeedObj;
        }
    }

}
