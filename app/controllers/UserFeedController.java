package controllers;

import models.User;
import models.UserFeed;
import play.mvc.Controller;
import play.mvc.Result;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class UserFeedController extends Controller {

    public static Result changeUserFeedOrder(String id) {
        return ok();
    }

    public static Result increasePopularity(String id) {
        UserFeed userFeed = UserFeed.findEntity(id, UserFeed.class);
        userFeed.increasePopularity();
        return ok();
    }

    public static Result allUserFeeds() {
        User user = SmartReaderUtils.getCurrentUser();
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.userFeeds()));
    }

    public static Result clearUpdate(String id) {
        UserFeed userFeed = UserFeed.findEntity(id, UserFeed.class);
        userFeed.clearUpdate();
        return ok();
    }

}
