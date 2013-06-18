package controllers;

import java.util.List;

import models.Article;
import models.Feed;
import models.FeedCategory;
import models.MongoModel;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
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
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        return ok(main.render());
    }

    @SecureSocial.UserAwareAction
    public static Result userAware() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        final String userName = user != null ? user.fullName() : "guest";
        return ok("Hello " + userName + ", you are seeing a public page");
    }

    @SecureSocial.SecuredAction( authorization = WithProvider.class, params = {"twitter"})
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
        List<Article> articles = Feed.articlesInFeed(id);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(articles));
    }

    @SecureSocial.UserAwareAction
    public static Result getCategories() {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.allFeedCategoriesWithFeed()));
    }

    @SecureSocial.UserAwareAction
    public static Result addFeedCategory() {
        User user = getCurrentUser();
        FeedCategory feedCategory = new FeedCategory();
        feedCategory.user = user;
        feedCategory.name = request().queryString().get("name")[0];
        user.addUserCategory(feedCategory);
        return ok();
    }

    @SecureSocial.UserAwareAction
    public static Result addFeedToCategory(String feedCategoryId) {
        User user = getCurrentUser();
        Feed feed = new Feed();
        feed.title = request().queryString().get("title")[0];
        feed.xmlUrl = request().queryString().get("xmlUrl")[0];
        feed.htmlUrl = request().queryString().get("htmlUrl")[0];
        FeedCategory feedCategory = FeedCategory.find(feedCategoryId);
        feedCategory.createFeed(user,  feed);
        return ok();
    }

    public static Result getDuplicatedArticles(String id) {
        Article article = MongoModel.find(id, Article.class);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(article.dups));
    }

    public static Result getRecommendedArticles(String id) {
        Article article = MongoModel.find(id, Article.class);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(article.dups));
    }

    public static User getCurrentUser() {
        Identity identity = SecureSocial.currentUser();
        return User.findByEmail(identity.email().get());
    }

}
