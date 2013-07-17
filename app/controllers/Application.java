package controllers;

import java.util.Date;
import java.util.List;

import models.Article;
import models.Feed;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import util.SmartReaderUtils;
import views.html.main;

import com.google.gson.Gson;
public class Application extends Controller {

    /**
     * This action only gets called if the user is logged in.
     *
     * @return
     */
    @SecureSocial.SecuredAction
    public static Result index() {
        return ok(main.render());
    }

    @SecureSocial.UserAwareAction
    public static Result userAware() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        final String userName = user != null ? user.fullName() : "guest";
        return ok("Hello " + userName + ", you are seeing a public page");
    }

    @SecureSocial.SecuredAction(authorization = WithProvider.class, params = {"twitter"})
    public static Result onlyTwitter() {
        return ok("You are seeing this because you logged in using Twitter");
    }

    @SecureSocial.UserAwareAction
    public static Result crawl() {
        Identity userId = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User user = User.getUserFromIdentity(userId);
        user.crawl();
        return ok();
    }

    @SecureSocial.UserAwareAction
    public static Result getFeed(String id) {
        String publishDateStr = request().getQueryString("publishDate");
        Gson gson = SmartReaderUtils.builder.create();
        if (publishDateStr == null) {
            List<Article> articles = Feed.articlesInFeed(id);
            return ok(gson.toJson(articles));
        }
        else {
            long publishDateLong = Long.parseLong(publishDateStr);
            Date publishDate = new Date(publishDateLong);
            List<Article> articles = Feed.articlesInFeed(id, publishDate);
            return ok(gson.toJson(articles));
        }
    }

    @SecureSocial.UserAwareAction
    public static Result getCategories() {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.allFeedCategoriesWithFeed()));
    }


    public static Result twitterCallback() {
        RequestToken token = new RequestToken(session().get("token"), session().get("tokenSec"));
        String verifier = request().getQueryString("oauth_verifier");
        User user = SmartReaderUtils.getCurrentUser();
        try {
            AccessToken accessToken = SmartReaderUtils.getTwitter().getOAuthAccessToken(token, verifier);
            user.updateAccessToken(accessToken.getToken(), accessToken.getTokenSecret());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return ok();
    }

    public static Result twitterLogin() {
        try {
            RequestToken twitterRequestToken = SmartReaderUtils.getTwitter().getOAuthRequestToken();
            String token = twitterRequestToken.getToken();
            String tokenSecret = twitterRequestToken.getTokenSecret();
            session().put("token", token);
            session().put("tokenSec", tokenSecret);
            String authorizationUrl = twitterRequestToken.getAuthorizationURL();
            return redirect(authorizationUrl);
        } catch (TwitterException e) {

        }
        return ok();
    }

}
