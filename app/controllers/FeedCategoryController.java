package controllers;

import models.Feed;
import models.FeedCategory;
import models.MongoModel;
import models.User;
import models.UserFeed;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import util.SmartReaderUtils;

public class FeedCategoryController extends Controller {


    @SecureSocial.UserAwareAction
    public static Result addFeedCategory() {
        System.out.println(request().body().toString());
        User user = SmartReaderUtils.getCurrentUser();
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.user = user;
        feedCategory.name = request().queryString().get("name")[0];
        user.addUserCategory(feedCategory);
        return ok();
    }

    @SecureSocial.UserAwareAction
    public static Result addFeedToCategory(String feedCategoryId) {
        User user = SmartReaderUtils.getCurrentUser();
        Feed feed = new Feed();
        feed.title = request().queryString().get("title")[0];
        feed.xmlUrl = request().queryString().get("xmlUrl")[0];
        feed.htmlUrl = request().queryString().get("htmlUrl")[0];
        FeedCategory feedCategory = FeedCategory.find(feedCategoryId);
        feedCategory.createFeed(user, feed);
        return ok();
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
