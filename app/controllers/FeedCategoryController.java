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
        User user = SmartReaderUtils.getCurrentUser();
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.user = user;
        feedCategory.name = request().queryString().get("name")[0];
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
            Feed feed = FeedParser.parseFeedInfo(xmlUrl);
            feed = feed.createUnique();
            feed.addUser(user);
            feedCategory.createFeed(user, feed);
            Gson gson = SmartReaderUtils.builder.create();
            return ok(gson.toJson(feed));
        }
        catch (Exception e) {
            e.printStackTrace();
            return badRequest(e.toString());
        }

    }

    public static Result changeName(String id) {
        return ok();
    }

    public static Result changeFeedCategoryOrder(String id) {
        return ok();
    }

    public static Result deleteFeedCategory(String id) {
        return ok();
    }

    public static Result deleteUserFeed(String id, String userFeedId) {
        FeedCategory feedCategory = MongoModel.findEntity(id, FeedCategory.class);
        UserFeed userFeed = MongoModel.findEntity(userFeedId, UserFeed.class);
        feedCategory.deleteUserFeed(userFeed);
        return ok();
    }
}
