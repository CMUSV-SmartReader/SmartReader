package controllers;

import java.io.IOException;

import models.Feed;
import models.User;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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

    // example for twitter
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

    public static Result getFeeds() throws JsonGenerationException, JsonMappingException, IOException {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        return ok(new ObjectMapper().writeValueAsString(user.feeds));
    }

    public static Result getFeed(String id) throws JsonGenerationException, JsonMappingException, IOException {
        Feed feed = Feed.findWithArticle(id);
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(feed));
    }

    public static Result getCategories() throws JsonGenerationException, JsonMappingException, IOException {
        Identity identity = SecureSocial.currentUser();
        User user = User.findByEmail(identity.email().get());
        Gson gson = SmartReaderUtils.builder.create();
        return ok(gson.toJson(user.allFeedCategories()));
    }

}
