package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.SNSProvider;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import util.SmartReaderUtils;

import com.google.gson.Gson;

public class SocialController extends Controller {

    @SecureSocial.UserAwareAction
    public static Result twitterStatus() {
        List<String> response = new ArrayList<String>();
        User user = SmartReaderUtils.getCurrentUser();
        try {
            ResponseList<twitter4j.Status> statusList = user.getTwitter().getHomeTimeline();
            for (twitter4j.Status status : statusList) {
                response.add(status.getText());
            }
        } catch(TwitterException e) {
            e.printStackTrace();
        }
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(response));
    }

    @SecureSocial.UserAwareAction
    public static Result allProviders() {
        Gson gson = SmartReaderUtils.builder.create();
        User user = SmartReaderUtils.getCurrentUser();
        return ok(gson.toJson(user.providers()));
    }

    public static Result articles(String id) {
        String publishDateStr = request().getQueryString("publishDate");
        Gson gson = SmartReaderUtils.builder.create();
        SNSProvider provider = SNSProvider.findEntity(id, SNSProvider.class);
        if (publishDateStr == null) {
            return ok(gson.toJson(provider.articles()));
        }
        else {
            long publishDateLong = Long.parseLong(publishDateStr);
            Date publishDate = new Date(publishDateLong);
            return ok(gson.toJson(provider.articles(publishDate)));
        }
    }
}
