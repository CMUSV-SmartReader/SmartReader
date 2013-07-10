package controllers;

import models.Feed;
import models.FeedCategory;
import models.MongoModel;
import models.User;
import models.UserFeed;

import org.codehaus.jackson.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import util.FeedParser;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class FeedCategoryController extends Controller {


    @SecureSocial.UserAwareAction
    public static Result addFeedCategory() {
        JsonNode dataNode = request().body().asJson().get("data");
        String name = dataNode.asText();
        User user = SmartReaderUtils.getCurrentUser();
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.user = user;
        feedCategory.name = name;
        user.addUserCategory(feedCategory);
        return ok();
    }

    @SecureSocial.UserAwareAction
    public static Result addFeedToCategory(String id) {
        try {
            FeedCategory feedCategory = MongoModel.findEntity(id, FeedCategory.class);
            User user = SmartReaderUtils.getCurrentUser();
            JsonNode dataNode = request().body().asJson().get("data");
            String xmlUrl = dataNode.asText();
            if (xmlUrl != null && xmlUrl.length() > 0) {
                Feed feed = FeedParser.parseFeedInfo(xmlUrl);
                feed = feed.createUnique();
                feed.addUser(user);
                UserFeed newUserFeed = feedCategory.createFeed(user, feed);
                Gson gson = SmartReaderUtils.builder.create();
                return ok(gson.toJson(newUserFeed));
            }
            else {
                throw new IllegalArgumentException("The url of the feed cannot be empty");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return badRequest(e.toString());
        }
    }

    public static Result moveUserFeed(String srcId, String dstId) {
        JsonNode dataNode = request().body().asJson().get("data");
        String userFeedId = dataNode.asText();
        FeedCategory srcFeedCategory = MongoModel.findEntity(srcId, FeedCategory.class);
        FeedCategory dstFeedCategory = MongoModel.findEntity(dstId, FeedCategory.class);
        UserFeed userFeed = MongoModel.findEntity(userFeedId, UserFeed.class);
        srcFeedCategory.deleteUserFeed(userFeed);
        dstFeedCategory.addUserFeed(userFeed);
        return ok();
    }

    public static Result changeFeedCategoryOrder(String id) {
        return ok();
    }

    public static Result deleteFeedCategory(String id) {
        FeedCategory feedCategory = MongoModel.findEntity(id, FeedCategory.class);
        feedCategory.delete();
        return ok();
    }

    public static Result deleteUserFeed(String id, String userFeedId) {
        FeedCategory feedCategory = MongoModel.findEntity(id, FeedCategory.class);
        UserFeed userFeed = MongoModel.findEntity(userFeedId, UserFeed.class);
        feedCategory.deleteUserFeed(userFeed);
        return ok();
    }
}
