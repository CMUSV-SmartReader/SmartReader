package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import models.Article;
import models.Feed;
import models.MongoModel;
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

import facebook4j.Facebook;
import facebook4j.FacebookException;
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

    @SecureSocial.SecuredAction
    public static Result getCategories() {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.allFeedCategoriesWithFeed()));
    }


    @SecureSocial.SecuredAction
    public static Result twitterCallback() {
        RequestToken token = new RequestToken(session().get("token"), session().get("tokenSec"));
        String verifier = request().getQueryString("oauth_verifier");
        User user = SmartReaderUtils.getCurrentUser();
        user.createTwitterProvider();
        try {
            AccessToken accessToken = SmartReaderUtils.getTwitter().getOAuthAccessToken(token, verifier);
            user.updateTwitterAccessToken(accessToken.getToken(), accessToken.getTokenSecret());
            return redirect("/");
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return ok();
    }

    @SecureSocial.SecuredAction
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

    @SecureSocial.SecuredAction
    public static Result facebookLogin() {
        Facebook facebook = SmartReaderUtils.getFacebook();
        String callbackUrl = routes.Application.facebookCallback().absoluteURL(request());
        return redirect(facebook.getOAuthAuthorizationURL(callbackUrl));
    }

    @SecureSocial.SecuredAction
    public static Result facebookCallback() {
        Facebook facebook = SmartReaderUtils.getFacebook();
        String oauthCode = request().getQueryString("code");
        User user = SmartReaderUtils.getCurrentUser();
        try {
            facebook4j.auth.AccessToken token = facebook.getOAuthAccessToken(oauthCode);
            user.updateFacebookAccessToken(token.getToken());
        } catch (FacebookException e) {
            e.printStackTrace();
        }
        return redirect("/");
    }

    public static Result randomArticles() {
        @SuppressWarnings("unchecked")
        List<Article> candidateArticles = (List<Article>) MongoModel.all(Article.class, 2000);
        List<Article> articles = new ArrayList<Article>();
        Random random = new Random();
        for(int i = 0; i < 20; i++) {
            articles.add(candidateArticles.get(random.nextInt(candidateArticles.size())));
        }
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(articles));
    }
}
