package controllers;

import models.UserFeed;
import play.mvc.Controller;
import play.mvc.Result;

public class UserFeedController extends Controller {

    public static Result changeUserFeedOrder(String id) {
        return ok();
    }

    public static Result increasePopularity(String id) {
        UserFeed userFeed = UserFeed.findEntity(id, UserFeed.class);
        userFeed.increasePopularity();
        return ok();
    }

}
